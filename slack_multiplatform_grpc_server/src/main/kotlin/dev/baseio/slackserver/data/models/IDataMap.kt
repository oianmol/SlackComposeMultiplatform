package dev.baseio.slackserver.data.models

interface IDataMap {
  fun provideMap():Map<String,String>
}