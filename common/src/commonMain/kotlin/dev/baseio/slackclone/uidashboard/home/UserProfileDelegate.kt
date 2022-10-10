package dev.baseio.slackclone.uidashboard.home

import ViewModel
import dev.baseio.slackclone.uidashboard.vm.UserProfileDelegate

class UserProfileVM(
  private val userProfileDelegate: UserProfileDelegate
) :
  ViewModel(), UserProfileDelegate by userProfileDelegate{
    init {
      getCurrentUser(viewModelScope)
    }
  }