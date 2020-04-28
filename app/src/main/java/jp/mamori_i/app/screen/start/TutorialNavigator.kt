package jp.mamori_i.app.screen.start


interface TutorialNavigator {

    enum class TutorialPageType {
        Tutorial1,
        Tutorial2,
        Tutorial3
    }

    fun goToNext(pageType: TutorialPageType)
}