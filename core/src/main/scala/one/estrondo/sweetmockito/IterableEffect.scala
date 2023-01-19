package one.estrondo.sweetmockito

import scala.collection.Factory
import scala.collection.IterableFactory

object IterableEffect:

  inline transparent def apply[F[_], E, I](using inline effect: IterableEffect[F, E, I]): IterableEffect[F, E, I] =
    effect

  given option[E <: Nothing, A]: IterableEffect[Option, E, A] = new IterableEffect:

    override def empty(): Option[A] = None

    override def failed(error: => E): Option[A] = None

    override def succeed(value: => A): Option[A] = Some(value)

  given fromFactory[F[_], E, A](using factory: Factory[A, F[A]]): IterableEffect[F, E, A] =
    new IterableEffect:
      override def empty(): F[A]              = factory.fromSpecific(Nil)
      override def failed(error: => E): F[A]  = factory.fromSpecific(Nil)
      override def succeed(value: => A): F[A] = factory.fromSpecific(Seq(value))

trait IterableEffect[F[_], E, A] extends Effect[F, E, A]:

  def empty(): F[A]
