package org

import scala.util.Either.RightProjection

package object secret_sharing {
  // used for scala 2.11.8 compatibility
  implicit def eitherToRight[A, B](either: Either[A, B]): RightProjection[A, B] = either.right
}
