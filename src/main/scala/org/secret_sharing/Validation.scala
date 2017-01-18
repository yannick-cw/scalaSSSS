package org.secret_sharing

private[secret_sharing] object Validation {

  sealed trait ShareError { def msg: String }
  case class InvalidInput(msg: String) extends ShareError

  def validateSecret(secret: Array[Byte], inHash: Array[Byte]): Either[ShareError, Unit] =
    if (SHA256.encrypt(secret) sameElements inHash) Right()
    else Left(InvalidInput(s"Hash of secret does not match up with hash of input shares (${secret.toList} : ${inHash.toList})"))

  def validateShares(shares: List[Share]): Either[ShareError, Unit] =
    if (shares.isEmpty) Left(InvalidInput(s"empty shares"))
    else if (shares.exists(!_.hash.sameElements(shares.head.hash))) Left(InvalidInput(s"all shares have to have the same hash"))
    else if (shares.exists(_.primeUsed != shares.head.primeUsed)) Left(InvalidInput(s"all shares have to have the same prime"))
    else Right(())

  def validateIn(requiredParts: Int, totalParts: Int, secret: Array[Byte]): Either[ShareError, Unit] =
    if (requiredParts > totalParts) Left(InvalidInput(s"requiredParts must be less than totalParts ($requiredParts > $totalParts)"))
    else if (requiredParts < 1) Left(InvalidInput(s"requiredParts ($requiredParts) must be bigger 0"))
    else if (totalParts < 1) Left(InvalidInput(s"totalParts ($totalParts) must be bigger 0"))
    else if (secret.isEmpty) Left(InvalidInput(s"secret ($totalParts) must be bigger 0"))
    else Right(())

}
