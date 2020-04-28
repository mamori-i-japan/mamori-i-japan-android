package jp.mamori_i.app.data.exception

// Exceptionがthrowされない処理のエラーハンドリングの際など、自身で例外を生成したい時に使用する
// Exceptionのハンドリングがしきれずに、処理を固定化させたい時などにも使用して良い
class MIJException(val reason: Reason): Throwable() {
    enum class Reason {
        Network,
        Parse,
        Auth,
        Other
    }
}