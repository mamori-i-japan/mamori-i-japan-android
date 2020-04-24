package jp.co.tracecovid19.streetpass

import android.bluetooth.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Handler
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import jp.co.tracecovid19.BuildConfig
import jp.co.tracecovid19.bluetooth.BLE
import jp.co.tracecovid19.bluetooth.BLEReadPayload
import jp.co.tracecovid19.bluetooth.BLEWritePayload
import jp.co.tracecovid19.bluetooth.gatt.ACTION_DEVICE_PROCESSED
import jp.co.tracecovid19.bluetooth.gatt.CONNECTION_DATA
import jp.co.tracecovid19.bluetooth.gatt.DEVICE_ADDRESS
import jp.co.tracecovid19.idmanager.TempIdManager
import jp.co.tracecovid19.logger.DebugLogger
import jp.co.tracecovid19.service.BluetoothMonitoringService
import jp.co.tracecovid19.service.BluetoothMonitoringService.Companion.blacklistDuration
import jp.co.tracecovid19.service.BluetoothMonitoringService.Companion.maxQueueTime
import jp.co.tracecovid19.service.BluetoothMonitoringService.Companion.useBlacklist
import jp.co.tracecovid19.util.BLEUtil
import kotlinx.coroutines.runBlocking
import java.util.*
import java.util.concurrent.PriorityBlockingQueue

class StreetPassWorker(val context: Context, val tempIdManager: TempIdManager) {

    companion object {
        private const val TAG = "StreetPassWorker"
    }

    private val workQueue: PriorityBlockingQueue<Work> = PriorityBlockingQueue(5, Collections.reverseOrder<Work>())
    private val blacklist: MutableList<BlacklistEntry> = Collections.synchronizedList(ArrayList())

    private val scannedDeviceReceiver = ScannedDeviceReceiver()
    private val blacklistReceiver = BlacklistReceiver()
    private val serviceUUID: UUID = UUID.fromString(BuildConfig.BLE_SSID)
    private val characteristicV2: UUID = UUID.fromString(BuildConfig.V2_CHARACTERISTIC_ID)

    private val bluetoothManager =
        context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager

    private lateinit var timeoutHandler: Handler
    private lateinit var queueHandler: Handler
    private lateinit var blacklistHandler: Handler

    private var currentWork: Work? = null
    private var localBroadcastManager: LocalBroadcastManager =
        LocalBroadcastManager.getInstance(context)

    val onWorkTimeoutListener = object : Work.OnWorkTimeoutListener {
        override fun onWorkTimeout(work: Work) {

            if (!isCurrentlyWorkedOn(work.device.address)) {
                DebugLogger.log(TAG, "Work already removed. Timeout ineffective??.")
            }

            Log.e(
                TAG,
                "Work timed out for ${work.device.address} @ ${work.connectable.rssi} queued for ${work.checklist.started.timePerformed - work.timeStamp}ms"
            )
            Log.e(
                TAG,
                "${work.device.address} work status: ${work.checklist}."
            )

            //connection never formed - don't need to disconnect
            if (!work.checklist.connected.status) {
                Log.e(TAG, "No connection formed for ${work.device.address}")
                if (work.device.address == currentWork?.device?.address) {
                    currentWork = null
                }

                try {
                    work.gatt?.close()
                } catch (e: Exception) {
                    Log.e(
                        TAG,
                        "Unexpected error while attempting to close clientIf to ${work.device.address}: ${e.localizedMessage}"
                    )
                }

                finishWork(work)
            }
            //the connection is still there - might be stuck / work in progress
            else if (work.checklist.connected.status && !work.checklist.disconnected.status) {

                if (work.checklist.readCharacteristic.status || work.checklist.writeCharacteristic.status || work.checklist.skipped.status) {
                    Log.e(TAG, "Connected but did not disconnect in time for ${work.device.address}")

                    try {
                        work.gatt?.disconnect()
                        //disconnect callback won't get invoked
                        if (work.gatt == null) {
                            currentWork = null
                            finishWork(work)
                        }
                    } catch (e: Throwable) {
                        Log.e(
                            TAG,
                            "Failed to clean up work, bluetooth state likely changed or other device's advertiser stopped: ${e.localizedMessage}"
                        )
                    }

                } else {
                    Log.e(
                        TAG,
                        "Connected but did nothing for ${work.device.address}"
                    )

                    try {
                        work.gatt?.disconnect()
                        //disconnect callback won't get invoked
                        if (work.gatt == null) {
                            currentWork = null
                            finishWork(work)
                        }
                    } catch (e: Throwable) {
                        Log.e(
                            TAG,
                            "Failed to clean up work, bluetooth state likely changed or other device's advertiser stopped: ${e.localizedMessage}"
                        )
                    }
                }
            }

            //all other edge cases? - disconnected
            else {
                Log.e(
                    TAG,
                    "Disconnected but callback not invoked in time. Waiting.: ${work.device.address}: ${work.checklist}"
                )
            }
        }
    }

    init {
        prepare()
    }

    private fun prepare() {
        val deviceAvailableFilter = IntentFilter(ACTION_DEVICE_SCANNED)
        localBroadcastManager.registerReceiver(scannedDeviceReceiver, deviceAvailableFilter)

        val deviceProcessedFilter = IntentFilter(ACTION_DEVICE_PROCESSED)
        localBroadcastManager.registerReceiver(blacklistReceiver, deviceProcessedFilter)

        timeoutHandler = Handler()
        queueHandler = Handler()
        blacklistHandler = Handler()
    }

    fun isCurrentlyWorkedOn(address: String?): Boolean {
        return currentWork?.let {
            it.device.address == address
        } ?: false
    }

    fun addWork(work: Work): Boolean {
        DebugLogger.log(TAG, "addWork")
        //if it's our current work. ignore
        if (isCurrentlyWorkedOn(work.device.address)) {
            DebugLogger.log(TAG, "${work.device.address} is being worked on, not adding to queue")
            return false
        }

        //if its in blacklist - check for both mac address and manu data?
        //devices seem to cache manuData. needs further testing. temporarily disabling.
        if (useBlacklist) {
            if (blacklist.any { it.uniqueIdentifier == work.device.address }) {
                DebugLogger.log(TAG, "${work.device.address} is in blacklist, not adding to queue")
                return false
            }
        }

        //if we haven't seen this device yet
        if (workQueue.none { it.device.address == work.device.address }) {
            workQueue.offer(work)
            queueHandler.postDelayed({
                if (workQueue.contains(work)) {
                    val result = workQueue.remove(work)
                    DebugLogger.log(TAG, "Work for ${work.device.address} removed from queue? : $result")
                    DebugLogger.central(TAG, "Work for ${work.device.address} removed from queue? : $result")
                }
            }, maxQueueTime)
            return true
        }
        //this gadget is already in the queue, we can use the latest rssi and txpower? replace the entry
        else {

            DebugLogger.log(TAG, "${work.device.address} is already in work queue")
            DebugLogger.central(TAG, "${work.device.address} is already in work queue")

            val prevWork = workQueue.find { it.device.address == work.device.address }
            val removed = workQueue.remove(prevWork)
            val added = workQueue.offer(work)

            DebugLogger.log(TAG, "Queue entry updated - removed: ${removed}, added: $added")

            return false
        }
    }

    fun doWork() {
        //check the status of the current work item
        if (currentWork != null) {
            DebugLogger.central(TAG, "doWork ${currentWork?.device?.name}(${currentWork?.device?.address})")
            DebugLogger.log(
                TAG,
                "Already trying to connect to: ${currentWork?.device?.address}"
            )
            //devices may reset their bluetooth before the disconnection happens properly and disconnect is never called.
            //handle that situation here

            //if the job was finished or timed out but was not removed
            val timedout = System.currentTimeMillis() > currentWork?.timeout ?: 0
            if (currentWork?.finished == true || timedout) {

                Log.w(
                    TAG,
                    "Handling erroneous current work for ${currentWork?.device?.address} : - finished: ${currentWork?.finished
                        ?: false}, timedout: $timedout"
                )
                //check if there is, for some reason, an existing connection
                if (currentWork != null) {
                    if (bluetoothManager.getConnectedDevices(BluetoothProfile.GATT).contains(
                            currentWork?.device
                        )
                    ) {
                        Log.w(
                            TAG,
                            "Disconnecting dangling connection to ${currentWork?.device?.address}"
                        )
                        currentWork?.gatt?.disconnect()
                    }
                } else {
                    doWork()
                }
            }

            return
        }

        if (workQueue.isEmpty()) {
            DebugLogger.log(TAG, "Queue empty. Nothing to do.")
            return
        }

        DebugLogger.log(TAG, "Queue size: ${workQueue.size}")

        var workToDo: Work? = null
        val now = System.currentTimeMillis()

        while (workToDo == null && workQueue.isNotEmpty()) {
            workToDo = workQueue.poll()
            workToDo?.let { work ->
                if (now - work.timeStamp > maxQueueTime) {
                    Log.w(
                        TAG,
                        "Work request for ${work.device.address} too old. Not doing"
                    )
                    workToDo = null
                }
            }
        }

        workToDo?.let { currentWorkOrder ->

            val device = currentWorkOrder.device

            if (useBlacklist) {
                if (blacklist.any { it.uniqueIdentifier == device.address }) {
                    Log.w(TAG, "Already worked on ${device.address}. Skip.")
                    doWork()
                    return
                }
            }

            val alreadyConnected = getConnectionStatus(device)
            DebugLogger.central(TAG, "Already connected to ${device.address} : $alreadyConnected")

            if (alreadyConnected) {
                //this might mean that the other device is currently connected to this device's local gatt server
                //skip. we'll rely on the other party to do a write
                currentWorkOrder.checklist.skipped.status = true
                currentWorkOrder.checklist.skipped.timePerformed = System.currentTimeMillis()
                finishWork(currentWorkOrder)
            } else {

                currentWorkOrder.let {

                    val gattCallback = CentralGattCallback(it)
                    DebugLogger.log(
                        TAG,
                        "Starting work - connecting to device: ${device.address} @ ${it.connectable.rssi} ${System.currentTimeMillis() - it.timeStamp}ms ago"
                    )
                    currentWork = it

                    try {
                        it.checklist.started.status = true
                        it.checklist.started.timePerformed = System.currentTimeMillis()

                        it.startWork(context, gattCallback)

                        val connecting = it.gatt?.connect() ?: false

                        if (!connecting) {
                            Log.e(
                                TAG,
                                "Alamak! not connecting to ${it.device.address}??"
                            )

                            //bail and do the next job
                            Log.e(TAG, "Moving on to next task")
                            currentWork = null
                            doWork()
                            return

                        } else {
                            DebugLogger.log(
                                TAG,
                                "Connection to ${it.device.address} attempt in progress"
                            )
                        }

                        timeoutHandler.postDelayed(
                            it.timeoutRunnable,
                            BluetoothMonitoringService.connectionTimeout
                        )
                        it.timeout =
                            System.currentTimeMillis() + BluetoothMonitoringService.connectionTimeout

                        DebugLogger.log(TAG, "Timeout scheduled for ${it.device.address}")
                    } catch (e: Throwable) {
                        Log.e(
                            TAG,
                            "Unexpected error while attempting to connect to ${device.address}: ${e.localizedMessage}"
                        )
                        Log.e(TAG, "Moving on to next task")
                        currentWork = null
                        doWork()
                        return
                    }
                }
            }
        }

        if (workToDo == null) {
            DebugLogger.log(TAG, "No outstanding work")
        }

    }

    private fun getConnectionStatus(device: BluetoothDevice): Boolean {

        val connectedDevices = bluetoothManager.getDevicesMatchingConnectionStates(
            BluetoothProfile.GATT,
            intArrayOf(BluetoothProfile.STATE_CONNECTED)
        )
        return connectedDevices.contains(device)
    }

    fun finishWork(work: Work) {

        DebugLogger.central(TAG, "finishWork ${work.device.name}(${work.device.address})")
        if (work.finished) {
            DebugLogger.log(
                TAG,
                "Work on ${work.device.address} already finished and closed"
            )
            return
        }

        if (work.isCriticalsCompleted()) {
            BLEUtil.broadcastDeviceProcessed(context, work.device.address)
        }

        DebugLogger.log(
            TAG,
            "Work on ${work.device.address} stopped in: ${work.checklist.disconnected.timePerformed - work.checklist.started.timePerformed}"
        )

        DebugLogger.log(
            TAG,
            "Work on ${work.device.address} completed?: ${work.isCriticalsCompleted()}. Connected in: ${work.checklist.connected.timePerformed - work.checklist.started.timePerformed}. connection lasted for: ${work.checklist.disconnected.timePerformed - work.checklist.connected.timePerformed}. Status: ${work.checklist}"
        )

        timeoutHandler.removeCallbacks(work.timeoutRunnable)
        DebugLogger.log(TAG, "Timeout removed for ${work.device.address}")

        work.finished = true
        doWork()
    }

    inner class CentralGattCallback(private val work: Work) : BluetoothGattCallback() {

        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {

            DebugLogger.central(TAG, "----- ConnectionStateChange")
            gatt?.let {
                when (newState) {
                    BluetoothProfile.STATE_CONNECTED -> {
                        DebugLogger.log(TAG, "Connected to other GATT server - ${gatt.device.address}")
                        DebugLogger.central(TAG, "${gatt.device.name}(${gatt.device.address}) is connected")

                        //get a fast connection?
                        gatt.requestConnectionPriority(BluetoothGatt.CONNECTION_PRIORITY_BALANCED)
                        gatt.requestMtu(512)

                        work.checklist.connected.status = true
                        work.checklist.connected.timePerformed = System.currentTimeMillis()
                    }

                    BluetoothProfile.STATE_DISCONNECTED -> {
                        DebugLogger.log(
                            TAG,
                            "Disconnected from other GATT server - ${gatt.device.address}"
                        )
                        DebugLogger.central(TAG, "${gatt.device.name}(${gatt.device.address}) is disconnected")
                        work.checklist.disconnected.status = true
                        work.checklist.disconnected.timePerformed = System.currentTimeMillis()

                        //remove timeout runnable if its still there
                        timeoutHandler.removeCallbacks(work.timeoutRunnable)
                        DebugLogger.log(TAG, "Timeout removed for ${work.device.address}")

                        //remove job from list of current work - if it is the current work
                        if (work.device.address == currentWork?.device?.address) {
                            this@StreetPassWorker.currentWork = null
                        }
                        gatt.close()
                        finishWork(work)
                    }

                    else -> {
                        DebugLogger.log(TAG, "Connection status for ${gatt.device.address}: $newState")
                        DebugLogger.central(TAG, "${gatt.device.name}(${gatt.device.address}) newState = $newState")
                        endWorkConnection(gatt)
                    }
                }
            }
        }

        override fun onMtuChanged(gatt: BluetoothGatt?, mtu: Int, status: Int) {

            if (!work.checklist.mtuChanged.status) {

                work.checklist.mtuChanged.status = true
                work.checklist.mtuChanged.timePerformed = System.currentTimeMillis()

                DebugLogger.log(
                    TAG,
                    "${gatt?.device?.address} MTU is $mtu. Was change successful? : ${status == BluetoothGatt.GATT_SUCCESS}"
                )

                gatt?.let {
                    val discoveryOn = gatt.discoverServices()
                    DebugLogger.log(
                        TAG,
                        "Attempting to start service discovery on ${gatt.device.address}: $discoveryOn"
                    )
                }
            }
        }

        // New services discovered
        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            when (status) {
                BluetoothGatt.GATT_SUCCESS -> {
                    DebugLogger.log(
                        TAG,
                        "Discovered ${gatt.services.size} services on ${gatt.device.address}"
                    )
                    DebugLogger.central(TAG, "${gatt.device.name}(${gatt.device.address}) is discovered")

                    val service = gatt.getService(serviceUUID)

                    service?.let {

                        //select characteristicUUID to read from
                        val characteristic = service.getCharacteristic(characteristicV2)

                        if (characteristic != null) {
                            val readSuccess = gatt.readCharacteristic(characteristic)
                            DebugLogger.log(
                                TAG,
                                "Attempt to read characteristic of our service on ${gatt.device.address}: $readSuccess"
                            )
                        } else {
                            Log.e(
                                TAG,
                                "WTF? ${gatt.device.address} does not have our characteristic"
                            )
                            endWorkConnection(gatt)
                        }
                    }

                    if (service == null) {
                        Log.e(
                            TAG,
                            "WTF? ${gatt.device.address} does not have our service"
                        )
                        endWorkConnection(gatt)
                    }
                }
                else -> {
                    Log.w(TAG, "No services discovered on ${gatt.device.address}")
                    DebugLogger.central(TAG, "No services discovered on ${gatt.device.name}(${gatt.device.address})")
                    endWorkConnection(gatt)
                }
            }
        }

        // data read from a perhipheral
        //I am a central
        override fun onCharacteristicRead(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            status: Int
        ) {

            DebugLogger.log(TAG, "Read Status: $status")
            DebugLogger.central(TAG, " Characteristic read from ${gatt.device.name}(${gatt.device.address})")
            when (status) {
                BluetoothGatt.GATT_SUCCESS -> {
                    DebugLogger.log(
                        TAG,
                        "Characteristic read from ${gatt.device.address}: ${characteristic.getStringValue(
                            0
                        )}"
                    )

                    DebugLogger.log(
                        TAG,
                        "onCharacteristicRead: ${work.device.address} - [${work.connectable.rssi}]"
                    )

                    if (BLE.supportsCharUUID(characteristic.uuid)) {
                        try {
                            val dataBytes = characteristic.value
                            val blePayload = BLEReadPayload.fromPayload(dataBytes)

                            val connectionRecord = ConnectionRecord(
                                BLEType.CENTRAL,
                                blePayload.i,
                                work.connectable.rssi,
                                work.connectable.transmissionPower
                            )
                            //if the deserializing was a success, connectionRecord will not be null, save it
                            connectionRecord?.let {
                                BLEUtil.broadcastStreetPassReceived(
                                    context,
                                    connectionRecord
                                )
                                DebugLogger.central(TAG, it.toString())
                                DebugLogger.central(TAG, "read data = ${dataBytes.toString(Charsets.UTF_8)}")
                            }
                        } catch (e: Throwable) {
                            Log.e(TAG, "Failed to process read payload - ${e.message}")
                        }

                    }
                    work.checklist.readCharacteristic.status = true
                    work.checklist.readCharacteristic.timePerformed = System.currentTimeMillis()
                }
                else -> {
                    Log.w(
                        TAG,
                        "Failed to read characteristics from ${gatt.device.address}: $status"
                    )
                }
            }

            // attempt to do a write
            if (BLE.supportsCharUUID(characteristic.uuid)) {
                // TODO: I/F決めてデータを作成(work.connectable.rssiやwork.connectable.transmissionPowerを送信するはず
                val tempUserId = runBlocking {
                    tempIdManager.getTempUserId(System.currentTimeMillis())
                }
                val payload = BLEWritePayload(tempUserId.tempId, work.connectable.rssi, work.connectable.transmissionPower)
                characteristic.value = payload.getPayload()
                val writeSuccess = gatt.writeCharacteristic(characteristic)
                DebugLogger.central(TAG, "write data = ${String(characteristic.value, Charsets.UTF_8)}")
                DebugLogger.central(TAG, "Attempt to write characteristic to our service on ${gatt.device.address}: $writeSuccess")
                // TODO: writeした場合、どうやったらendWorkConnection(gatt)が呼ばれて、接続完了になるのか？
            } else {
                DebugLogger.central(TAG, "Not writing to ${gatt.device.address}. Characteristic ${characteristic.uuid} is not supported")
                endWorkConnection(gatt)
            }
        }

        override fun onCharacteristicWrite(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            status: Int
        ) {

            when (status) {
                BluetoothGatt.GATT_SUCCESS -> {
                    Log.i(TAG, "Characteristic wrote successfully")
                    work.checklist.writeCharacteristic.status = true
                    work.checklist.writeCharacteristic.timePerformed =
                        System.currentTimeMillis()
                }
                else -> {
                    Log.i(TAG, "Failed to write characteristics: $status")
                }
            }
            endWorkConnection(gatt)
        }

        private fun endWorkConnection(gatt: BluetoothGatt) {
            DebugLogger.log(TAG, "Ending connection with: ${gatt.device.address}")
            DebugLogger.central(TAG, "----- end connection with ${gatt.device.name}(${gatt.device.address})")
            gatt.disconnect()
        }
    }

    fun terminateConnections() {
        DebugLogger.log(TAG, "Cleaning up worker.")

        currentWork?.gatt?.disconnect()
        currentWork = null

        timeoutHandler.removeCallbacksAndMessages(null)
        queueHandler.removeCallbacksAndMessages(null)
        blacklistHandler.removeCallbacksAndMessages(null)

        workQueue.clear()
        blacklist.clear()
    }

    fun unregisterReceivers() {
        try {
            localBroadcastManager.unregisterReceiver(blacklistReceiver)
        } catch (e: Throwable) {
            Log.e(TAG, "Unable to close receivers: ${e.localizedMessage}")
        }

        try {
            localBroadcastManager.unregisterReceiver(scannedDeviceReceiver)
        } catch (e: Throwable) {
            Log.e(TAG, "Unable to close receivers: ${e.localizedMessage}")
        }
    }

    inner class BlacklistReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            if (ACTION_DEVICE_PROCESSED == intent.action) {
                intent.getStringExtra(DEVICE_ADDRESS)?.let {
                    DebugLogger.central(TAG, "+-+- Adding to blacklist: $it")
                    val entry = BlacklistEntry(it, System.currentTimeMillis())
                    blacklist.add(entry)
                    blacklistHandler.postDelayed({
                        val result = blacklist.remove(entry)
                        DebugLogger.central(
                            TAG,
                            "blacklist for ${entry.uniqueIdentifier} removed? : $result"
                        )
                    }, blacklistDuration)
                }
            }
        }
    }

    inner class ScannedDeviceReceiver : BroadcastReceiver() {

        private val TAG = "ScannedDeviceReceiver"

        override fun onReceive(context: Context?, intent: Intent?) {

            intent?.let {
                if (ACTION_DEVICE_SCANNED == intent.action) {
                    //get data from extras
                    val device: BluetoothDevice? =
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    val connectable: ConnectablePeripheral? =
                        intent.getParcelableExtra(CONNECTION_DATA)

                    val devicePresent = device != null
                    val connectablePresent = connectable != null

                    DebugLogger.log(
                        TAG,
                        "Device received: ${device?.address}. Device present: $devicePresent, Connectable Present: $connectablePresent"
                    )

                    device?.let {
                        connectable?.let {
                            val work = Work(device, connectable, onWorkTimeoutListener)
                            if (addWork(work)) {
                                doWork()
                            }
                        }
                    }
                }
            }
        }
    }
}
