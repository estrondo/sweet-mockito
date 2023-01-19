package one.estrondo.sweetmockito

object Effect2:

  inline transparent def apply[F[_, _], E, A](using inline effect2: Effect2[F, E, A]): Effect2[F, E, A] = effect2

  given [E, A]: Effect2[Either, E, A] = new Effect2:

    override def failed(error: => E): Either[E, A] = Left(error)

    override def succeed(value: => A): Either[E, A] = Right(value)

trait Effect2[F[_, _], E, A]:

  def failed(error: => E): F[E, A]

  def succeed(value: => A): F[E, A]
