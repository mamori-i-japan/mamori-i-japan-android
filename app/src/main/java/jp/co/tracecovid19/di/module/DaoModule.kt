package jp.co.tracecovid19.di.module

import jp.co.tracecovid19.data.database.TraceCovid19Database
import org.koin.dsl.module

val daoModule = module {
    factory { get<TraceCovid19Database>().tempUserIdDao() }
    factory { get<TraceCovid19Database>().traceDataDao() }
    factory { get<TraceCovid19Database>().deepContactUserDao() }
}