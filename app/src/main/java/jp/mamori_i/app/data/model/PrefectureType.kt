package jp.mamori_i.app.data.model

import jp.mamori_i.app.ui.SelectText

enum class PrefectureType(val rawValue: Int): SelectText.SelectTextItem {
    Hokkaido(1),
    Aomori(2),
    Iwate(3),
    Miyagi(4),
    Akita(5),
    Yamagata(6),
    Fukushima(7),
    Ibaraki(8),
    Tochigi(9),
    Gunma(10),
    Saitama(11),
    Chiba(12),
    Tokyo(13),
    Kanagawa(14),
    Nigata(15),
    Toyama(16),
    Ishikawa(17),
    Fukui(18),
    Yamanashi(19),
    Nagano(20),
    Gifu(21),
    Shizuoka(22),
    Aichi(23),
    Mie(24),
    Shiga(25),
    Kyoto(26),
    Osaka(27),
    Hyogo(28),
    Nara(29),
    Wakayama(30),
    Tottori(31),
    Shimane(32),
    Okayama(33),
    Hiroshima(34),
    Yamaguchi(35),
    Tokushima(36),
    Kagawa(37),
    Ehime(38),
    Kouchi(39),
    Fukuoka(40),
    Saga(41),
    Nagasaki(42),
    Kumamoto(43),
    Ooita(44),
    Miyazaki(45),
    Kagoshima(46),
    Okinawa(47),
    Other(999);

    override fun description(): String {
        return when (this) {
            Hokkaido -> "北海道"
            Aomori -> "青森県"
            Iwate -> "岩手県"
            Miyagi -> "宮城県"
            Akita -> "秋田県"
            Yamagata -> "山形県"
            Fukushima -> "福島県"
            Ibaraki -> "茨城県"
            Tochigi -> "栃木県"
            Gunma -> "群馬県"
            Saitama -> "埼玉県"
            Chiba -> "千葉県"
            Tokyo -> "東京都"
            Kanagawa -> "神奈川県"
            Nigata -> "新潟県"
            Toyama -> "富山県"
            Ishikawa -> "石川県"
            Fukui -> "福井県"
            Yamanashi -> "山梨県"
            Nagano -> "長野県"
            Gifu -> "岐阜県"
            Shizuoka -> "静岡県"
            Aichi -> "愛知県"
            Mie -> "三重県"
            Shiga -> "滋賀県"
            Kyoto -> "京都府"
            Osaka -> "大阪府"
            Hyogo -> "兵庫県"
            Nara -> "奈良県"
            Wakayama -> "和歌山県"
            Tottori -> "鳥取県"
            Shimane -> "島根県"
            Okayama -> "岡山県"
            Hiroshima -> "広島県"
            Yamaguchi -> "山口県"
            Tokushima -> "徳島県"
            Kagawa -> "香川県"
            Ehime -> "愛媛県"
            Kouchi -> "高知県"
            Fukuoka -> "福岡県"
            Saga -> "佐賀県"
            Nagasaki -> "長崎県"
            Kumamoto -> "熊本県"
            Ooita -> "大分県"
            Miyazaki -> "宮崎県"
            Kagoshima -> "鹿児島県"
            Okinawa -> "沖縄県"
            Other -> "その他"
        }
    }

    companion object {
        fun create(rawValue: Int?): PrefectureType {
            values().firstOrNull {
                it.rawValue == rawValue
            }?.let {
                return it
            }
            return Other
        }

        fun selectableValues(): Array<PrefectureType> {
            return values().filter{ it != Other }.toTypedArray()
        }
    }
}