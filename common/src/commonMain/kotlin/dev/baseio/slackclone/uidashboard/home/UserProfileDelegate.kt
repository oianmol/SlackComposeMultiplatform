package dev.baseio.slackclone.uidashboard.home

import dev.baseio.slackclone.uidashboard.vm.UserProfileDelegate
import ViewModel
class UserProfileVM(
  private val userProfileDelegate: UserProfileDelegate
) :
  ViewModel(), UserProfileDelegate by userProfileDelegate{
    init {
      getCurrentUser(viewModelScope)
    }
  }