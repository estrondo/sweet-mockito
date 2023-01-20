package one.estrondo.sweetmockito

import scala.util.Success
import scala.util.Try
import scala.util.Failure
import scala.collection.Factory

object Effect:

  inline transparent def apply[F[_], E, A](using inline effect: Effect[F, E, A]): Effect[F, E, A] = effect

  given [E <: Throwable, A]: Effect[Try, E, A] = new Effect:

    override def failed(error: => E): Try[A] = Failure(error)

    override def succeed(value: => A): Try[A] = Success(value)

  given [E <: Nothing, I]: IterableEffect[Option, E, I] = IterableEffect.option

  given [F[_], E <: Nothing, A](using Factory[A, F[A]]): IterableEffect[F, E, A] = IterableEffect.fromFactory

trait Effect[F[_], E, A]:

  def failed(error: => E): F[A]

  def succeed(value: => A): F[A]
