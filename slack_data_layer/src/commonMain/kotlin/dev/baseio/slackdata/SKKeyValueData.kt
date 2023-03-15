package dev.baseio.slackdata

expect class SKKeyValueData {
  fun save(key:String,value:String)
  fun get(key: String):String?
  fun clear()
}