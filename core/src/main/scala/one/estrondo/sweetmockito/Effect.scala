package one.estrondo.sweetmockito

import scala.util.Success
import scala.util.Try
import scala.util.Failure

object Effect:

  inline transparent def apply[F[_], E](using inline effect: Effect[F, E]): Effect[F, E] = effect

  given [E <: Throwable]: Effect[Try, E] = new Effect:

    override def failed[A, B <: E](error: => B): Try[A] = Failure(error)

    override def succeed[A](value: => A): Try[A] = Success(value)

trait Effect[F[_], E]:

  def failed[A, B <: E](error: => B): F[A]

  def succeed[A](value: => A): F[A]
