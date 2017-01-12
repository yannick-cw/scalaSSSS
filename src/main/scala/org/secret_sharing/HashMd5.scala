package org.secret_sharing

object HashMd5 {
  import java.security.MessageDigest
  def md5(bytes: Array[Byte]): Array[Byte] = MessageDigest.getInstance("MD5").digest(bytes)
}
