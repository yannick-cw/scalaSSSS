package org.secret_sharing

object SHA256 {
  import java.security.MessageDigest
  def encrypt(bytes: Array[Byte]): Array[Byte] = MessageDigest.getInstance("SHA-256").digest(bytes)
}
