package dev.baseio.slackclone.data.injection

import dev.baseio.slackclone.chatmessaging.chatthread.SendMessageDelegate
import dev.baseio.slackclone.chatmessaging.chatthread.SendMessageDelegateImpl
import dev.baseio.slackclone.dashboard.vm.UserProfileDelegate
import dev.baseio.slackclone.dashboard.vm.UserProfileDelegateImpl
import dev.baseio.slackclone.onboarding.QrCodeDelegate
import dev.baseio.slackclone.onboarding.QrCodeDelegateImpl
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
