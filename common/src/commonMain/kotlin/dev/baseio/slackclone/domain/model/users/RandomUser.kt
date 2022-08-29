package dev.baseio.slackclone.domain.model.users

data class RandomUser(val firstName: String, val lastName: String, val picUrl: String = ""){
  fun name() = firstName.plus(" $lastName")
}
