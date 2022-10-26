package dev.baseio.slackclone.data.injection

import dev.baseio.slackclone.uichat.chatthread.SendMessageDelegate
import dev.baseio.slackclone.uichat.chatthread.SendMessageDelegateImpl
import dev.baseio.slackclone.uidashboard.vm.UserProfileDelegate
import dev.baseio.slackclone.uidashboard.vm.UserProfileDelegateImpl
import org.koin.core.qualifier.Qualifier
import org.koin.core.qualifier.QualifierValue
import org.koin.dsl.module

val viewModelDelegateModule = module {
    single<UserProfileDelegate> {
        UserProfileDelegateImpl(getKoin().get(), getKoin().get())
    }
    single<SendMessageDelegate> {
        SendMessageDelegateImpl(get(), get(), get())
    }
}
