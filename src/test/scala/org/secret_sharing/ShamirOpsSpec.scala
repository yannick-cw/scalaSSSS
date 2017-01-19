package org.secret_sharing

import org.scalacheck.{Gen, Properties}
import org.scalacheck.Prop.{BooleanOperators, forAll}
import scala.util.Random.{shuffle, nextInt}

class ShamirOpsSpec extends Properties("ShamirOps") {

  import SSSS._

  val posInt = Gen.choose(1, 100)

  property("combine(create(secret)) == secret") =
    forAll(Gen.alphaStr, posInt, posInt) { (secret: String, k: Int, n: Int) =>
      (secret.nonEmpty && k <= n) ==>
        (shares(secret, k, n)
          .flatMap(shares => combine(shuffle(shares))) == Right(secret))
    }

  property("always return n shares") =
    forAll(Gen.alphaStr, posInt, posInt) { (secret: String, k: Int, n: Int) =>
      (secret.nonEmpty && k <= n) ==>
        (shares(secret, k, n).getOrElse(List.empty).length == n)
    }

  property("always be able to combine with k shares") =
    forAll(Gen.alphaStr, posInt, posInt) { (secret: String, k: Int, n: Int) =>
      (secret.nonEmpty && k <= n) ==>
        (shares(secret, k, n)
          .flatMap(shares => combine(shares.take(k))) == Right(secret))
    }

  property("always be able to combine with k shuffled shares") =
    forAll(Gen.alphaStr, posInt, posInt) { (secret: String, k: Int, n: Int) =>
      (secret.nonEmpty && k <= n) ==>
        (shares(secret, k, n)
          .flatMap{shares => combine(shuffle(shares).take(k))} == Right(secret))
    }

  property("never be able to combine with k-1 shares") =
    forAll(Gen.alphaStr, posInt, posInt) { (secret: String, k: Int, n: Int) =>
      (secret.nonEmpty && k <= n) ==>
        shares(secret, k, n)
          .flatMap(shares => combine(shuffle(shares).take(k - 1))).isLeft
    }

  property("always reject empty secret") =
    forAll(posInt, posInt) { (k: Int, n: Int) =>
      (k <= n) ==>
        shares("", k, n)
          .flatMap(shares => combine(shuffle(shares).take(k - 1))).isLeft
    }

  property("never able create shares for to long input") =
    forAll { secret: String =>
      (secret.getBytes.length * 8 > 4096) ==>
        shares(secret, 5, 10).isLeft
    }

  property("reject if hash of one share is different") =
    forAll(Gen.alphaStr, posInt, posInt) { (secret: String, k: Int, n: Int) =>
      (secret.nonEmpty && k <= n && n > 1) ==>
        shares(secret, k, n)
          .flatMap { shares =>
            val randomShare = nextInt(shares.length)
            val modifiedShares = shares.zipWithIndex.map {
              case(share, index) => if(index == randomShare) share.copy(hash = share.hash.tail) else share}
            combine(modifiedShares)
          }.isLeft
    }

  property("reject if prime of one share is different") =
    forAll(Gen.alphaStr, posInt, posInt) { (secret: String, k: Int, n: Int) =>
      (secret.nonEmpty && k <= n && n > 1) ==>
        shares(secret, k, n)
          .flatMap { shares =>
            val randomShare = nextInt(shares.length)
            val modifiedShares = shares.zipWithIndex.map {
              case(share, index) => if(index == randomShare) share.copy(primeUsed = "10") else share}
            combine(modifiedShares)
          }.isLeft
    }

  property("be able to decrypt from same shares from different creations") =
    forAll(Gen.alphaStr, posInt, posInt) { (secret: String, k: Int, n: Int) =>
      (secret.nonEmpty && k <= n && n > 1) ==> combine(
        shuffle((1 to 100).flatMap(_ => unsafeShares(secret, k, n).get).toList.distinct).take(k)
      ).isRight
    }
}

