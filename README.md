## Scala -- Shamir's Secret Sharing Scheme
[![](https://jitpack.io/v/yannick-cw/scalaSSSS.svg)](https://jitpack.io/#yannick-cw/scalaSSSS)
[![Build Status](https://travis-ci.org/yannick-cw/scalaSSSS.svg?branch=master)](https://travis-ci.org/yannick-cw/scalaSSSS)

This is a scala implementation of [Shamir's Secret Sharing Scheme](https://en.wikipedia.org/wiki/Shamir's_Secret_Sharing) algorithm.
See the original [pdf](https://cs.jhu.edu/~sdoshi/crypto/papers/shamirturing.pdf)

### Concept

Imagine you do not want to leave the password for launching all your countries nukes in the hand of the president.
Instead you decide the secret password `I was elected to lead not to read` gets distributed to
the president, vice president, first lady, influential lobbyist and general of the army.
Now you want to allow the nuke whenever 3 of the 5 decide so.

That's what SSSS (Shamir's Secret Sharing Scheme) does.
The secret gets split up in n parts and whenever you have at least k of the n parts you can
restore the original secret. k - 1 parts of the secret do not help you in any way.

### Basic Usage

Add to your `build.sbt`

```scala
    resolvers += "jitpack" at "https://jitpack.io"
    
    // scala versions
    libraryDependencies += "com.github.yannick-cw" % "scalaSSSS_2.11" % "0.1.1"	
    libraryDependencies += "com.github.yannick-cw" % "scalaSSSS_2.12" % "0.1.1"	
```

```scala
import SSSS._

val secret = "I was elected to lead not to read"
val secretShares: Either[ShareError, List[Share]] =
  shares(secret = secret, requiredParts = 3, totalParts = 5)

val eitherSecret: Either[ShareError, String] = 
  secretShares.flatMap(shares => combine(shares.take(3)))

eitherSecret.foreach(println)
// prints "I was elected to lead not to read"
  ```

The secret can be restored with any number of shares between `requiredParts` to `totalParts` and
the ordering does not matter.

The resulting Share case class:
```scala
case class Share(x: BigInt, y: BigInt, hash: Array[Byte], primeUsed: String)
```

The share case class carries all information needed for recombination.
The `hash` is a [sha-256](https://de.wikipedia.org/wiki/SHA-2) hash and can be used to reidentify shares belonging to the same
secret. Furthermore it is used to verify that a valid secret was restored.
The `primeUsed` is a `BigInt` prime needed for [security](https://en.wikipedia.org/wiki/Shamir's_Secret_Sharing#Solution) reason.

#### Input restraints

* `requiredParts` must be less than `totalParts`
* `requiredParts` and `totalParts` must be bigger 0
* `List[Share]` must be non empty
* `List[Share]` all shares must have the same `hash`
* `List[Share]` all shares must have the `primeUsed`

### Limitations

Currently the maximum support input size is 4096 bit.
This translates to a maximum of 512 Characters.
This limit is given by the maximum pre generated prime number with 4100 bit.

If you feel like hitting this limit I'd recommend encrypting your secret and sharing the
private key via SSSS.

There are no limitations on the input size of `requiredParts` but it might take a while for values
`>> 1000`.

### More Usage

Additionally you can pass in a `scala.util.Random` if you'd like to supply your own.
Furthermore the `share` and `combine` methods support `String`, `Array[Byte]` and `BigInt`
as input and output.
