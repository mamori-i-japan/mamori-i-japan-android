package jp.co.tracecovid19.util


class InputValidateUtil {
    companion object {
        fun validatePhoneNumber(value: String): String? {
            // TODO ローカライズ
            val errorMessage = "正しい電話番号の桁数で入力してください。(仮)"

            // 正しい電話番号の形式じゃない
            val regex = Regex(pattern = "^\\d{11}\$")
            val matched = regex.containsMatchIn(input = value)
            if (!matched) {
                return errorMessage
            }

            return null
        }
    }
}