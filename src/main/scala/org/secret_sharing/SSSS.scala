package org.secret_sharing

import java.nio.charset.StandardCharsets.UTF_8

import cats.Monad
import cats.implicits._
import org.secret_sharing.Validation._

import scala.util.Random

abstract class SSSSOps[F[_]](implicit monadF: Monad[F]) {

  // creates the shares or returns an error, if input is invalid
  def shares(secret: Array[Byte], requiredParts: Int, totalParts: Int, random: Random): F[List[Share]]
  // creates combined string or returns an error, if shares are from different secrets
  def combineToBigInt(shares: List[Share]): F[BigInt]

  def shares(secret: Array[Byte], requiredParts: Int, totalParts: Int): F[List[Share]] =
    shares(secret, requiredParts, totalParts, Random)

  def shares(secret: String, requiredParts: Int, totalParts: Int, random: Random): F[List[Share]] =
    shares(secret.getBytes(UTF_8), requiredParts, totalParts, random)
  def shares(secret: String, requiredParts: Int, totalParts: Int): F[List[Share]] =
    shares(secret, requiredParts, totalParts, Random)

  def shares(secret: BigInt, requiredParts: Int, totalParts: Int, random: Random): F[List[Share]] =
    shares(secret.toByteArray, requiredParts, totalParts, random)
  def shares(secret: BigInt, requiredParts: Int, totalParts: Int): F[List[Share]] =
    shares(secret, requiredParts, totalParts, Random)

  def combine(shares: List[Share]): F[String] =
    combineToBigInt(shares).map(bigInt => new String(bigInt.toByteArray, UTF_8))

  def combineToBytes(shares: List[Share]): F[Array[Byte]] =
    combineToBigInt(shares).map(_.toByteArray)
}

object SSSS extends SSSSOps[Either[ShareError, ?]] {

  def shares(secret: Array[Byte], requiredParts: Int, totalParts: Int, random: Random): Either[ShareError, List[Share]] =
    for {
      _ <- validateIn(requiredParts, totalParts)
      prime <- BigPrimes.extractPrime(secret)
    } yield Shares(secret, requiredParts, totalParts, prime, random).get

  def combineToBigInt(shares: List[Share]): Either[ShareError, BigInt] =
    for {
      _ <- validateShares(shares)
      secret = LaGrangeInterpolation.coefficient0(shares)
      _ <- validateSecret(secret.toByteArray, shares.head.hash)
    } yield secret
}


