package org.secret_sharing

import java.nio.charset.StandardCharsets.UTF_8
import org.secret_sharing.Validation._
import scala.util.Random

trait SSSSOps[Error, Share] {

  // creates the shares or returns an error, if input is invalid
  def shares(secret: Array[Byte], requiredParts: Int, totalParts: Int, random: Random): Either[Error, List[Share]]
  // creates combined string or returns an error, if shares are from different secrets
  def combineToBigInt(shares: List[Share]): Either[Error, BigInt]

  // this ensures the encrypted shares for the same secret are always the same
  // But opens gate to dictionary attacks
  def unsafeShares(secret: Array[Byte], requiredParts: Int, totalParts: Int): Either[Error, List[Share]] =
    shares(secret, requiredParts, totalParts, new Random(BigInt(secret).toLong))

  def shares(secret: Array[Byte], requiredParts: Int, totalParts: Int): Either[Error, List[Share]] =
    shares(secret, requiredParts, totalParts, Random)

  def shares(secret: String, requiredParts: Int, totalParts: Int, random: Random): Either[Error, List[Share]] =
    shares(secret.getBytes(UTF_8), requiredParts, totalParts, random)
  def shares(secret: String, requiredParts: Int, totalParts: Int): Either[Error, List[Share]] =
    shares(secret, requiredParts, totalParts, Random)
  def unsafeShares(secret: String, requiredParts: Int, totalParts: Int): Either[Error, List[Share]] =
    unsafeShares(secret.getBytes(UTF_8), requiredParts, totalParts)

  def shares(secret: BigInt, requiredParts: Int, totalParts: Int, random: Random): Either[Error, List[Share]] =
    shares(secret.toByteArray, requiredParts, totalParts, random)
  def shares(secret: BigInt, requiredParts: Int, totalParts: Int): Either[Error, List[Share]] =
    shares(secret, requiredParts, totalParts, Random)
  def unsafeShares(secret: BigInt, requiredParts: Int, totalParts: Int): Either[Error, List[Share]] =
    unsafeShares(secret.toByteArray, requiredParts, totalParts)

  def combine(shares: List[Share]): Either[Error, String] =
    combineToBigInt(shares).map(bigInt => new String(bigInt.toByteArray, UTF_8))

  def combineToBytes(shares: List[Share]): Either[Error, Array[Byte]] =
    combineToBigInt(shares).map(_.toByteArray)
}

object SSSS extends SSSSOps[ShareError, Share] {
  def shares(secret: Array[Byte], requiredParts: Int, totalParts: Int, random: Random): Either[ShareError, List[Share]] =
    for {
      _ <- validateIn(requiredParts, totalParts, secret)
      prime <- BigPrimes.extractPrime(secret)
    } yield Shares(secret, requiredParts, totalParts, prime, random).get

  def combineToBigInt(shares: List[Share]): Either[ShareError, BigInt] =
    for {
      _ <- validateShares(shares)
      secret = LaGrangeInterpolation.coefficient0(shares)
      _ <- validateSecret(secret.toByteArray, shares.head.hash.toArray)
    } yield secret
}


