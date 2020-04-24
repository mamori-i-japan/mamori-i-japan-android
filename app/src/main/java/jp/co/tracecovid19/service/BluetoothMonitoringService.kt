package jp.co.tracecovid19.service

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import jp.co.tracecovid19.BuildConfig
import jp.co.tracecovid19.bluetooth.BLEAdvertiser
import jp.co.tracecovid19.bluetooth.gatt.ACTION_RECEIVED_STREETPASS
import jp.co.tracecovid19.bluetooth.gatt.STREET_PASS
import jp.co.tracecovid19.data.database.tracedata.TraceDataEntity
import jp.co.tracecovid19.data.repository.trase.TraceRepository
import jp.co.tracecovid19.idmanager.TempIdManager
import jp.co.tracecovid19.logger.DebugLogger
import jp.co.tracecovid19.notifications.NotificationTemplates
import jp.co.tracecovid19.streetpass.ConnectionRecord
import jp.co.tracecovid19.streetpass.StreetPassScanner
import jp.co.tracecovid19.streetpass.StreetPassServer
import jp.co.tracecovid19.streetpass.StreetPassWorker
import jp.co.tracecovid19.util.BLEUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import pub.devrel.easypermissions.EasyPermissions
import java.lang.ref.WeakReference
import kotlin.coroutines.CoroutineContext

class BluetoothMonitoringService : Service(), CoroutineScope {

    companion object {
        private const val TAG = "BTMService"

        private const val NOTIFICATION_ID = BuildConfig.SERVICE_FOREGROUND_NOTIFICATION_ID
        private const val CHANNEL_ID = BuildConfig.SERVICE_FOREGROUND_CHANNEL_ID
        const val CHANNEL_SERVICE = BuildConfig.SERVICE_FOREGROUND_CHANNEL_NAME
        const val COMMAND_KEY = "${BuildConfig.APPLICATION_ID}_CMD"

        const val PENDING_ACTIVITY = 5
        const val PENDING_SCAN_REQ_CODE = 7
        const val PENDING_ADVERTISE_REQ_CODE = 8
        const val PENDING_HEALTH_CHECK_CODE = 9
        const val PENDING_WIZARD_REQ_CODE = 10

        val connectionTimeout: Long = BuildConfig.CONNECTION_TIMEOUT
        val maxQueueTime: Long = BuildConfig.MAX_QUEUE_TIME
        val blacklistDuration: Long = BuildConfig.BLACKLIST_DURATION
        val healthCheckInterval: Long = BuildConfig.HEALTH_CHECK_INTERVAL
        val advertisingDuration: Long = BuildConfig.ADVERTISING_DURATION
        val scanDuration: Long = BuildConfig.SCAN_DURATION
        val scanInterval: Long = BuildConfig.SCAN_INTERVAL
        val advertisingGap: Long = BuildConfig.ADVERTISING_INTERVAL
        val infiniteAdvertising = BuildConfig.INFINITE_ADVERTISING
        val infiniteScanning = BuildConfig.INFINITE_SCANNING

        val useBlacklist = true
    }

    // TODO: ServiceクラスなのでViewModel化が難しい
    private val traceRepository: TraceRepository by inject()
    private val tempIdManager: TempIdManager by inject()

    private var mNotificationManager: NotificationManager? = null

    private lateinit var serviceUUID: String

    private var streetPassServer: StreetPassServer? = null
    private var streetPassScanner: StreetPassScanner? = null
    private var advertiser: BLEAdvertiser? = null

    var worker: StreetPassWorker? = null

    private val streetPassReceiver = StreetPassReceiver()
    private val bluetoothStatusReceiver = BluetoothStatusReceiver()

    private var job: Job = Job()

    private lateinit var commandHandler: CommandHandler

    private lateinit var localBroadcastManager: LocalBroadcastManager

    private var notificationShown: NOTIFICATION_STATE? = null

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job
    
    override fun onCreate() {
        localBroadcastManager = LocalBroadcastManager.getInstance(this)
        setup()
    }

    private fun setup() {
        commandHandler = CommandHandler(WeakReference(this))

        DebugLogger.log(TAG, "Creating service - BluetoothMonitoringService")
        serviceUUID = BuildConfig.BLE_SSID

        worker = StreetPassWorker(this.applicationContext, tempIdManager)

        unregisterReceivers()
        registerReceivers()

        setupNotifications()
    }

    fun teardown() {
        streetPassServer?.tearDown()
        streetPassServer = null

        streetPassScanner?.stopScan()
        streetPassScanner = null

        commandHandler.removeCallbacksAndMessages(null)

        BLEUtil.cancelNextScan(this.applicationContext)
        BLEUtil.cancelNextAdvertise(this.applicationContext)
    }

    private fun setupNotifications() {

        mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Android O requires a Notification Channel.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = CHANNEL_SERVICE
            // Create the channel for the notification
            val mChannel =
                NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_LOW)
            mChannel.enableLights(false)
            mChannel.enableVibration(true)
            mChannel.vibrationPattern = longArrayOf(0L)
            mChannel.setSound(null, null)
            mChannel.setShowBadge(false)

            // Set the Notification Channel for the Notification Manager.
            mNotificationManager!!.createNotificationChannel(mChannel)
        }
    }

    private fun notifyLackingThings(override: Boolean = false) {
        if (notificationShown != NOTIFICATION_STATE.LACKING_THINGS || override) {
            var notif =
                NotificationTemplates.lackingThingsNotification(this.applicationContext, CHANNEL_ID)
            startForeground(NOTIFICATION_ID, notif)
            notificationShown = NOTIFICATION_STATE.LACKING_THINGS
        }
    }

    private fun notifyRunning(override: Boolean = false) {
        if (notificationShown != NOTIFICATION_STATE.RUNNING || override) {
            var notif =
                NotificationTemplates.getRunningNotification(this.applicationContext, CHANNEL_ID)
            startForeground(NOTIFICATION_ID, notif)
            notificationShown = NOTIFICATION_STATE.RUNNING
        }
    }

    private fun hasLocationPermissions(): Boolean {
        val perms = BLEUtil.getRequiredPermissions()
        return EasyPermissions.hasPermissions(this.applicationContext, *perms)
    }

    private fun hasWritePermissions(): Boolean {
        val perms = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        return EasyPermissions.hasPermissions(this.applicationContext, *perms)
    }

    private fun isBluetoothEnabled(): Boolean {
        var btOn = false
        val bluetoothAdapter: BluetoothAdapter? by lazy(LazyThreadSafetyMode.NONE) {
            val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
            bluetoothManager.adapter
        }

        bluetoothAdapter?.let {
            btOn = it.isEnabled
        }
        return btOn
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        DebugLogger.log(TAG, "Service onStartCommand")

        //check for permissions
        if (!hasLocationPermissions() || !isBluetoothEnabled()) {
            DebugLogger.log(
                TAG,
                "location permission: ${hasLocationPermissions()} bluetooth: ${isBluetoothEnabled()}"
            )
            notifyLackingThings()
            return START_STICKY
        }

        intent?.let {
            val cmd = intent.getIntExtra(COMMAND_KEY, Command.INVALID.index)
            runService(Command.findByValue(cmd))

            return START_STICKY
        }

        if (intent == null) {
            Log.e(TAG, "WTF? Nothing in intent @ onStartCommand")
//            Utils.startBluetoothMonitoringService(applicationContext)
            commandHandler.startBluetoothMonitoringService()
        }

        // Tells the system to not try to recreate the service after it has been killed.
        return START_STICKY
    }

    fun runService(cmd: Command?) {

        var doWork = true
        DebugLogger.log(TAG, "Command is:${cmd?.string}")

        //check for permissions
        if (!hasLocationPermissions() || !isBluetoothEnabled()) {
            DebugLogger.log(
                TAG,
                "location permission: ${hasLocationPermissions()} bluetooth: ${isBluetoothEnabled()}"
            )
            notifyLackingThings()
            return
        }

        //show running foreground notification if its not showing that
        notifyRunning()

        when (cmd) {
            Command.ACTION_START -> {
                setupService()
                BLEUtil.scheduleNextHealthCheck(this.applicationContext, healthCheckInterval)
                actionStart()
            }

            Command.ACTION_SCAN -> {
                scheduleScan()

                if (doWork) {
                    actionScan()
                }
            }

            Command.ACTION_ADVERTISE -> {
                scheduleAdvertisement()
                if (doWork) {
                    actionAdvertise()
                }
            }

            Command.ACTION_STOP -> {
                actionStop()
            }

            Command.ACTION_SELF_CHECK -> {
                BLEUtil.scheduleNextHealthCheck(this.applicationContext, healthCheckInterval)
                if (doWork) {
                    actionHealthCheck()
                }
            }

            else -> DebugLogger.log(TAG, "Invalid / ignored command: $cmd. Nothing to do")
        }
    }

    private fun actionStop() {
        stopForeground(true)
        stopSelf()
        Log.w(TAG, "Service Stopping")
    }

    private fun actionHealthCheck() {
        performHealthCheck()
    }

    private fun actionStart() {
        DebugLogger.log(TAG, "Action Start")
        setupCycles()
    }

    private fun actionScan() {
        performScan()
    }

    private fun actionAdvertise() {
        setupAdvertiser()
        if (isBluetoothEnabled()) {
            advertiser?.startAdvertising(advertisingDuration)
        } else {
            Log.w(TAG, "Unable to start advertising, bluetooth is off")
        }
    }

    private fun setupService() {
        streetPassServer =
            streetPassServer ?: StreetPassServer(this.applicationContext, serviceUUID, tempIdManager)
        setupScanner()
        setupAdvertiser()
    }

    private fun setupScanner() {
        streetPassScanner = streetPassScanner ?: StreetPassScanner(
            this,
            serviceUUID,
            scanDuration
        )
    }

    private fun setupAdvertiser() {
        advertiser = advertiser ?: BLEAdvertiser(serviceUUID)
    }

    private fun setupCycles() {
        setupScanCycles()
        setupAdvertisingCycles()
    }

    private fun setupScanCycles() {
        commandHandler.scheduleNextScan(0)
    }

    private fun setupAdvertisingCycles() {
        commandHandler.scheduleNextAdvertise(0)
    }

    private fun performScan() {
        setupScanner()
        startScan()
    }

    private fun scheduleScan() {
        if (!infiniteScanning) {
            commandHandler.scheduleNextScan(scanInterval)
        }
    }

    private fun scheduleAdvertisement() {
        if (!infiniteAdvertising) {
            commandHandler.scheduleNextAdvertise(advertisingDuration + advertisingGap)
        }
    }

    private fun startScan() {

        if (isBluetoothEnabled()) {

            streetPassScanner?.let { scanner ->
                if (!scanner.isScanning()) {
                    scanner.startScan()
                } else {
                    Log.e(TAG, "Already scanning!")
                }
            }
        } else {
            Log.w(TAG, "Unable to start scan - bluetooth is off")
        }
    }

    private fun performHealthCheck() {

        DebugLogger.log(TAG, "Performing self diagnosis")

        if (!hasLocationPermissions() || !isBluetoothEnabled()) {
            DebugLogger.log(TAG, "no location permission")
            notifyLackingThings(true)
            return
        }

        notifyRunning(true)

        //ensure our service is there
        setupService()

        if (!infiniteScanning) {
            if (!commandHandler.hasScanScheduled()) {
                Log.w(TAG, "Missing Scan Schedule - rectifying")
                commandHandler.scheduleNextScan(100)
            } else {
                Log.w(TAG, "Scan Schedule present")
            }
        } else {
            Log.w(TAG, "Should be operating under infinite scan mode")
        }

        if (!infiniteAdvertising) {
            if (!commandHandler.hasAdvertiseScheduled()) {
                Log.w(TAG, "Missing Advertise Schedule - rectifying")
                commandHandler.scheduleNextAdvertise(100)
            } else {
                Log.w(
                    TAG,
                    "Advertise Schedule present. Should be advertising?:  ${advertiser?.shouldBeAdvertising
                        ?: false}. Is Advertising?: ${advertiser?.isAdvertising ?: false}"
                )
            }
        } else {
            Log.w(TAG, "Should be operating under infinite advertise mode")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        DebugLogger.log(TAG, "BluetoothMonitoringService destroyed - tearing down")
        stopService()
        DebugLogger.log(TAG, "BluetoothMonitoringService destroyed")
    }

    private fun stopService() {
        teardown()
        unregisterReceivers()

        worker?.terminateConnections()
        worker?.unregisterReceivers()

        job.cancel()
    }


    private fun registerReceivers() {
        val recordAvailableFilter = IntentFilter(ACTION_RECEIVED_STREETPASS)
        localBroadcastManager.registerReceiver(streetPassReceiver, recordAvailableFilter)

        val bluetoothStatusReceivedFilter = IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
        registerReceiver(bluetoothStatusReceiver, bluetoothStatusReceivedFilter)

        DebugLogger.log(TAG, "Receivers registered")
    }

    private fun unregisterReceivers() {
        try {
            localBroadcastManager.unregisterReceiver(streetPassReceiver)
        } catch (e: Throwable) {
            Log.w(TAG, "streetPassReceiver is not registered?")
        }

        try {
            unregisterReceiver(bluetoothStatusReceiver)
        } catch (e: Throwable) {
            Log.w(TAG, "bluetoothStatusReceiver is not registered?")
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    inner class BluetoothStatusReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.let {
                val action = intent.action
                if (action == BluetoothAdapter.ACTION_STATE_CHANGED) {
                    var state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1)

                    when (state) {
                        BluetoothAdapter.STATE_TURNING_OFF -> {
                            DebugLogger.log(TAG, "BluetoothAdapter.STATE_TURNING_OFF")
                            notifyLackingThings()
                            teardown()
                        }
                        BluetoothAdapter.STATE_OFF -> {
                            DebugLogger.log(TAG, "BluetoothAdapter.STATE_OFF")
                        }
                        BluetoothAdapter.STATE_TURNING_ON -> {
                            DebugLogger.log(TAG, "BluetoothAdapter.STATE_TURNING_ON")
                        }
                        BluetoothAdapter.STATE_ON -> {
                            DebugLogger.log(TAG, "BluetoothAdapter.STATE_ON")
                            BLEUtil.startBluetoothMonitoringService(this@BluetoothMonitoringService.applicationContext)
                        }
                    }
                }
            }
        }
    }

    inner class StreetPassReceiver : BroadcastReceiver() {

        private val TAG = "StreetPassReceiver"

        override fun onReceive(context: Context, intent: Intent) {

            if (ACTION_RECEIVED_STREETPASS == intent.action) {
                var connRecord: ConnectionRecord = intent.getParcelableExtra(STREET_PASS)
                DebugLogger.service(TAG, "+-+- StreetPass received: $connRecord")

                if (connRecord.id.isNotEmpty()) {
                    launch (Dispatchers.IO) {
                        val entity = TraceDataEntity(
                            tempId = connRecord.id,
                            timestamp = System.currentTimeMillis(),
                            rssi = connRecord.rssi,
                            txPower = connRecord.txPower
                        )
                        traceRepository.insertTraceData(entity)
                        DebugLogger.service(TAG, "Saving TraceDataEntity: ${entity.toString()}")
                    }
                }
            }
        }
    }

    enum class Command(val index: Int, val string: String) {
        INVALID(-1, "INVALID"),
        ACTION_START(0, "START"),
        ACTION_SCAN(1, "SCAN"),
        ACTION_STOP(2, "STOP"),
        ACTION_ADVERTISE(3, "ADVERTISE"),
        ACTION_SELF_CHECK(4, "SELF_CHECK");

        companion object {
            private val types = values().associateBy { it.index }
            fun findByValue(value: Int) = types[value]
        }
    }

    enum class NOTIFICATION_STATE() {
        RUNNING,
        LACKING_THINGS
    }
}