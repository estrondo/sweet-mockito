package one.estrondo.sweetmockito

object Effect2:

  inline transparent def apply[F[_, _], E](using inline effect2: Effect2[F, E]): Effect2[F, E] = effect2

  given [E]: Effect2[Either, E] = new Effect2:
    override def failed[A, B <: E](error: => B): Either[E, A] = Left(error)

    override def succeed[A](value: => A): Either[E, A] = Right(value)

trait Effect2[F[_, _], E]:

  def failed[A, B <: E](error: => B): F[E, A]

  def succeed[A](value: => A): F[E, A]
