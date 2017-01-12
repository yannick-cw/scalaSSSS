package org.secret_sharing

private[secret_sharing] case class Polynomial(coefficients: List[BigInt]) {
  def f(x: BigInt): BigInt =
    coefficients.zipWithIndex.foldLeft(BigInt(0)) { case(y, (coefficient, i)) =>
      y + coefficient * x.pow(i)
    }
}
