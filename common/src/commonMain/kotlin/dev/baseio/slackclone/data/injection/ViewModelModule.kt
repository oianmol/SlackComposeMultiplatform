package dev.baseio.slackclone.data.injection

import dev.baseio.slackclone.navigation.SlackScreens
import dev.baseio.slackclone.uichannels.SlackChannelVM
import dev.baseio.slackclone.uichannels.createsearch.CreateChannelVM
import dev.baseio.slackclone.uichannels.createsearch.SearchChannelsVM
import dev.baseio.slackclone.uichannels.directmessages.MessageViewModel
import dev.baseio.slackclone.uichat.chatthread.ChatScreenVM
import dev.baseio.slackclone.uichat.newchat.NewChatThreadVM
import dev.baseio.slackclone.uidashboard.vm.DashboardVM
import dev.baseio.slackclone.uidashboard.vm.SideNavVM
import dev.baseio.slackclone.uidashboard.home.HomeScreenVM
import dev.baseio.slackclone.uidashboard.home.UserProfileVM
import dev.baseio.slackclone.uidashboard.vm.UserProfileDelegate
import dev.baseio.slackclone.uidashboard.vm.UserProfileDelegateImpl
import dev.baseio.slackclone.uionboarding.GettingStartedVM
import dev.baseio.slackclone.uionboarding.vm.EmailInputVM
import dev.baseio.slackclone.uionboarding.vm.WorkspaceCreateVM
import dev.baseio.slackclone.uionboarding.vm.WorkspaceInputVM
import org.koin.core.qualifier.Qualifier
import org.koin.core.qualifier.QualifierValue
import org.koin.dsl.ScopeDSL
import org.koin.dsl.module

val viewModelDelegateModule = module {
  single<UserProfileDelegate> {
    UserProfileDelegateImpl(getKoin().get(), getKoin().get())
  }
}

val viewModelModule = module {
  scope<SlackScreens.CreateWorkspace> {
    scoped {
      WorkspaceCreateVM(getKoin().get())
    }
  }
  scope<SlackScreens.Home> {
    scoped {
      HomeScreenVM(
        getKoin().get(),
        getKoin().get(),
        getKoin().get(),
        getKoin().get(),
        getKoin().get(),
        getKoin().get(),
        getKoin().get()
      )
    }
  }
  scope<SlackScreens.You> {
    scoped {
      UserProfileVM(get())
    }
  }
  scope<SlackScreens.GettingStarted> {
    scoped { GettingStartedVM() }
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
      MessageViewModel(
        getKoin().get(),
        getKoin().get()
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
    scoped { SideNavVM(getKoin().get(), getKoin().get(), get(), get()) }
    scoped { DashboardVM(get(), get()) }

    scoped {
      ChatScreenVM(getKoin().get(), getKoin().get(), getKoin().get(), getKoin().get(), getKoin().get())
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
  scoped(qualifier = StarredChatsQualifier) {
    SlackChannelVM(
      getKoin().get(),
      getKoin().get(),
      getKoin().get()
    )
  }
  scoped(qualifier = DirectChatsQualifier) {
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
}


object RecentChatsQualifier : Qualifier {
  override val value: QualifierValue
    get() = "RecentChatsQualifier"
}

object StarredChatsQualifier : Qualifier {
  override val value: QualifierValue
    get() = "StarredChatsQualifier"
}

object DirectChatsQualifier : Qualifier {
  override val value: QualifierValue
    get() = "DirectChatsQualifier"
}

object AllChatsQualifier : Qualifier {
  override val value: QualifierValue
    get() = "AllChatsQualifier"
}