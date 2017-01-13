package org.secret_sharing

import org.scalacheck.Prop.{BooleanOperators, forAll}
import org.scalacheck.Properties

class HashSpec extends Properties("Md5") {

  import SHA256.encrypt

  property("be unique for different inputs") = forAll { as: List[Array[Byte]] =>
    (as.map(_.toList).distinct.length == as.length) ==>
      (as.map(encrypt(_).toList).distinct.length == as.length)
  }
}
