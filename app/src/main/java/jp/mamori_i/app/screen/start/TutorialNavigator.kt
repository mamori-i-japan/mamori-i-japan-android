package jp.mamori_i.app.screen.start


interface TutorialNavigator {

    enum class TutorialPageType {
        Tutorial1
    }

    fun goToNext(pageType: TutorialPageType)
}