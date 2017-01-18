package org.secret_sharing

import scala.util.Random

// k := min shares needed to decrypt secret
// n := shares provided
private[secret_sharing] case class Shares(
  secret: Array[Byte], k: Int, n: Int, prime: BigInt, rnd: Random) {

  private val poly = Polynomial(coefficients)

  // coefficients has k length with k-1 random values
  private def coefficients: List[BigInt] =
    BigInt(secret) :: (1 until k).map(_ => BigInt(prime.bitLength - 1, rnd)).toList

  def get: List[Share] =
    (1 to n).par.map(i => Share(x = i, y = poly.f(i).mod(prime), SHA256.encrypt(secret).toList, prime.toString)).toList
}

// md5 encrypted hash of secret to verify joining back together + prime to be used for re-adding
case class Share(x: BigInt, y: BigInt, hash: List[Byte], primeUsed: String)










