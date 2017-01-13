package org

import cats.Monad
import cats.implicits._

import scala.util.Either.RightProjection

package object secret_sharing {
  // used for scala 2.11.8 compatibility
  implicit def eitherToRight[A, B](either: Either[A, B]): RightProjection[A, B] = either.right

  implicit def eitherMonad[Err] = new Monad[Either[Err, ?]]{
    def flatMap[A, B](fa: Either[Err, A])(f: (A) => Either[Err, B]): Either[Err, B] = fa.right.flatMap(f)
    def tailRecM[A, B](a: A)(f: (A) => Either[Err, Either[A, B]]): Either[Err, B] = f(a) match {
      case Right(Right(b)) => Either.right(b)
      case Right(Left(a)) => tailRecM(a)(f)
      case l@Left(_) => l.rightCast[B]
    }
    def pure[A](x: A): Either[Err, A] = Either.right(x)
  }
}
