package dev.baseio.slackserver

import dev.baseio.slackserver.communications.PNChannel
import dev.baseio.slackserver.communications.PNChannelMember
import dev.baseio.slackserver.communications.PNMessages
import dev.baseio.slackserver.communications.PNSender
import dev.baseio.slackserver.data.impl.*
import dev.baseio.slackserver.data.models.SkChannel
import dev.baseio.slackserver.data.models.SkChannelMember
import dev.baseio.slackserver.data.models.SkMessage
import dev.baseio.slackserver.data.sources.*
import dev.baseio.slackserver.services.AuthenticationDelegate
import dev.baseio.slackserver.services.AuthenticationDelegateImpl
import dev.baseio.slackserver.services.IQrCodeGenerator
import dev.baseio.slackserver.services.QrCodeGenerator
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.koin.java.KoinJavaComponent
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

val dataSourcesModule = module {
    single {
        val client = KMongo.createClient(connectionString = System.getenv("MONGODB_URL"))
        client.getDatabase("slackDB").coroutine //normal java driver usage 
    }
    factory<WorkspaceDataSource> { WorkspaceDataSourceImpl(get()) }
    factory<UsersDataSource> { UsersDataSourceImpl(get()) }

    factory<ChannelMemberDataSource> {
        ChannelMemberDataSourceImpl(get())
    }
    factory<ChannelsDataSource> {
        ChannelsDataSourceImpl(get(), getKoin().get())
    }
    factory<MessagesDataSource> {
        MessagesDataSourceImpl(get())
    }
    factory<AuthDataSource> {
        AuthDataSourceImpl(get())
    }
    factory<AuthenticationDelegate> {
        AuthenticationDelegateImpl(KoinJavaComponent.getKoin().get(), KoinJavaComponent.getKoin().get())
    }
    factory<IQrCodeGenerator> { QrCodeGenerator() }

    factory<UserPushTokenDataSource> {
        UserPushTokenDataSourceImpl(get())
    }
    factory<PNSender<SkChannel>>(named(SkChannel::class.java.name)) {
        PNChannel(get(), get(), get())
    }
    factory<PNSender<SkMessage>>(named(SkMessage::class.java.name)) {
        PNMessages(get(), get(), get())
    }
    factory<PNSender<SkChannelMember>>(named(SkChannelMember::class.java.name)) {
        PNChannelMember(get(), get())
    }
}
