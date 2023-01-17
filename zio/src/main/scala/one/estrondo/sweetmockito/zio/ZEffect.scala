package one.estrondo.sweetmockito.zio

import zio.ZIO
import one.estrondo.sweetmockito.Effect
import one.estrondo.sweetmockito.Effect2

type ZEffect2[E, A] = ZIO[Any, E, A]

given [E]: Effect2[ZEffect2, E] = new Effect2:

  override def failed[A, B <: E](error: => B): ZIO[Any, E, A] = ZIO.fail(error)

  override def succeed[A](value: => A): ZIO[Any, E, A] = ZIO.succeed(value)
