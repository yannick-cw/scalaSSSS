package org.secret_sharing

import org.scalacheck.Prop.{BooleanOperators, forAll}
import org.scalacheck.Properties

class HashMd5Spec extends Properties("Md5") {

  import HashMd5.md5

  property("be unique for different inputs") = forAll { as: List[Array[Byte]] =>
    (as.map(_.toList).distinct.length == as.length) ==>
      (as.map(md5(_).toList).distinct.length == as.length)
  }
}
