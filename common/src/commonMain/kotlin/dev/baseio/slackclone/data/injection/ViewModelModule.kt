package dev.baseio.slackclone.data.injection

import dev.baseio.slackclone.uichat.chatthread.SendMessageDelegate
import dev.baseio.slackclone.uichat.chatthread.SendMessageDelegateImpl
import dev.baseio.slackclone.uidashboard.vm.UserProfileDelegate
import dev.baseio.slackclone.uidashboard.vm.UserProfileDelegateImpl
import dev.baseio.slackclone.uionboarding.QrCodeDelegate
import dev.baseio.slackclone.uionboarding.QrCodeDelegateImpl
import org.koin.dsl.module

val viewModelDelegateModule = module {
  single<UserProfileDelegate> {
    UserProfileDelegateImpl(getKoin().get(), getKoin().get())
  }
  single<SendMessageDelegate> {
    SendMessageDelegateImpl(get(), get(), get())
  }
  single<QrCodeDelegate> {
    QrCodeDelegateImpl(get(), get())
  }
}
