package dev.baseio.slackdata.injection

import dev.baseio.database.SlackDB
import dev.baseio.grpc.GrpcCalls
import dev.baseio.grpc.IGrpcCalls
import dev.baseio.slackdata.DriverFactory
import dev.baseio.slackdata.network.FakeGrpcCalls
import org.koin.dsl.module

val testDataModule = module {
    single {
        SlackDB.invoke(DriverFactory().createInMemorySqlDriver(SlackDB.Schema))
    }

    single<IGrpcCalls> {
        GrpcCalls(skKeyValueData = get(), address = "localhost")
    }
}