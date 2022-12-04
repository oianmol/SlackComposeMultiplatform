package dev.baseio.slackdata.injection

import dev.baseio.database.SlackDB
import dev.baseio.grpc.GrpcCalls
import dev.baseio.grpc.IGrpcCalls
import dev.baseio.slackdata.localdata.testDbConnection
import org.koin.dsl.module

val testDataModule = module {
    single {
        SlackDB.invoke(testDbConnection())
    }

    single<IGrpcCalls> {
        GrpcCalls( skKeyValueData = get(), port = 8083)
    }
}
