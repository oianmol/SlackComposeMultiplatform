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

/*val viewModelModule = module {
  scope<SlackScreens.Home> {
    scoped {
      HomeScreenComponent(
        getKoin().get(),
      )
    }
  }
  scope<SlackScreens.You> {
    scoped {
      UserProfileComponent(get(), componentContext)
    }
  }
  scope<SlackScreens.GettingStarted> {
    scoped { GettingStartedComponent(componentContext = get(), get()) }
  }
  scope<SlackScreens.WorkspaceInputUI> {
    scoped {
      WorkspaceInputVM(get(), get(), get())
    }
  }
  scope<SlackScreens.EmailAddressInputUI> {
    scoped {
      EmailInputVM(getKoin().get(), getKoin().get())
    }
  }

  scope<SlackScreens.DMs> {
    scoped {
      DirectMessagesComponent(
        getKoin().get(),
        getKoin().get(),
        componentContext
      )
    }
  }

  scope<SlackScreens.Home> {
    slackChannelVMScoped()
  }

  scope<SlackScreens.CreateNewDM> {
    scoped {
      NewChatThreadVM(
        getKoin().get(),
        getKoin().get(),
        getKoin().get(),
        getKoin().get(),
        getKoin().get()
      )
    }
  }

  scope<SlackScreens.CreateChannelsScreen> {
    scoped {
      SearchChannelsVM(
        getKoin().get(),
        getKoin().get(),
        getKoin().get(),
        getKoin().get()
      )
    }
  }

  scope<SlackScreens.CreateNewChannel> {
    scoped {
      CreateChannelVM(getKoin().get(), getKoin().get())
    }
  }

  scope<SlackScreens.Dashboard> {
    scoped { SideNavComponent(getKoin().get(), getKoin().get(), get()) }
    scoped { DashboardComponent(get(), get(), get(), get(), get(), get(), get(), get(), get()) }

    scoped {
      ChatScreenComponent(
        getKoin().get(), getKoin().get(), getKoin().get(), getKoin().get(),
        getKoin().get()
      )
    }

    slackChannelVMScoped()
  }

}

private fun ScopeDSL.slackChannelVMScoped() {
  scoped(qualifier = RecentChatsQualifier) {
    SlackChannelVM(
      getKoin().get(),
      getKoin().get(),
      getKoin().get()
    )
  }
  scoped(qualifier = AllChatsQualifier) {
    SlackChannelVM(
      getKoin().get(),
      getKoin().get(),
      getKoin().get()
    )
  }
}*/


object RecentChatsQualifier : Qualifier {
  override val value: QualifierValue
    get() = "RecentChatsQualifier"
}

object AllChatsQualifier : Qualifier {
  override val value: QualifierValue
    get() = "AllChatsQualifier"
}