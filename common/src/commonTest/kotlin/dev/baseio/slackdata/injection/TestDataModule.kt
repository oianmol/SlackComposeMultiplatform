package dev.baseio.slackdata.injection

import dev.baseio.database.SlackDB
import dev.baseio.grpc.GrpcCalls
import dev.baseio.grpc.IGrpcCalls
import dev.baseio.security.RsaEcdsaKeyManager
import dev.baseio.slackdata.localdata.testDbConnection
import dev.baseio.slackdata.readBinaryResource
import org.koin.dsl.module

val testDataModule = module {
    single {
        SlackDB.invoke(testDbConnection())
    }

    single<IGrpcCalls> {
        GrpcCalls("192.168.1.9", skKeyValueData = get())
    }

    single {
        RsaEcdsaKeyManager(
            senderVerificationKey = readBinaryResource("sender_verification_key.dat"),
            chainId = "1"
        )
    }
}
