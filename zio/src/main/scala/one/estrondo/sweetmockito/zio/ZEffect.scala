package one.estrondo.sweetmockito.zio

import zio.ZIO
import one.estrondo.sweetmockito.Effect
import one.estrondo.sweetmockito.Effect2

given [E, A]: Effect2[[X, Y] =>> ZIO[Any, X, Y], E, A] = new Effect2:
  override def failed(error: => E): ZIO[Any, E, A]  = ZIO.fail(error)
  override def succeed(value: => A): ZIO[Any, E, A] = ZIO.succeed(value)
