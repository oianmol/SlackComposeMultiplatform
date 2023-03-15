package dev.baseio.extensions

import kotlinx.cinterop.addressOf
import kotlinx.cinterop.pin
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.Foundation.create
import platform.posix.memcpy


inline fun ByteArray.toData(offset: Int = 0, length: Int = size - offset): NSData {
  require(offset + length <= size) { "offset + length > size" }
  if (isEmpty()) return NSData()
  val pinned = pin()
  return NSData.create(pinned.addressOf(offset), length.toULong()) { _, _ -> pinned.unpin() }
}

fun NSData.toByteArrayFromNSData(): ByteArray {
  val size = length.toInt()
  val bytes = ByteArray(size)

  if (size > 0) {
    bytes.usePinned { pinned ->
      memcpy(pinned.addressOf(0), this.bytes, this.length)
    }
  }

  return bytes
}