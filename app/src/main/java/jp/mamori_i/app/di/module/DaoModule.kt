package jp.mamori_i.app.di.module

import jp.mamori_i.app.data.database.MIJDatabase
import org.koin.dsl.module

val daoModule = module {
    factory { get<MIJDatabase>().tempUserIdDao() }
    factory { get<MIJDatabase>().traceDataDao() }
    factory { get<MIJDatabase>().deepContactUserDao() }
}