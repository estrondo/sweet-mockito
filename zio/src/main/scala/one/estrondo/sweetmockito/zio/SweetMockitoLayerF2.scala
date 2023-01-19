package one.estrondo.sweetmockito.zio

import one.estrondo.sweetmockito.SweetMockito
import one.estrondo.sweetmockito.SweetMockitoF
import org.mockito.stubbing.OngoingStubbing
import zio.RIO
import zio.Tag
import zio.ZIO
import org.mockito.invocation.InvocationOnMock
import one.estrondo.sweetmockito.Effect2
import one.estrondo.sweetmockito.{Answer, AnswerF2}

class SweetMockitoLayerF2[M: Tag, F[_, _], E, A](invocation: M => F[E, A]):

  def thenAnswer[E1, A1](
      fn: InvocationOnMock => Answer[E1, A1]
  )(using AnswerF2[F, E1, A1], E1 <:< E, A1 <:< A): RIO[M, this.type] =
    ZIO.serviceWith { mock =>
      SweetMockito
        .whenF2(invocation(mock))
        .thenAnswer(fn)
      this
    }

  def thenFail[E1](error: => E1)(using Effect2[F, E1, A], E1 <:< E): RIO[M, this.type] =
    ZIO.serviceWith { mock =>
      SweetMockito
        .whenF2(invocation(mock))
        .thenFail(error)
      this
    }

  def thenReturn[A1](value: => A1)(using Effect2[F, Nothing, A1], A1 <:< A): RIO[M, this.type] =
    ZIO.serviceWith { mock =>
      SweetMockito
        .whenF2(invocation(mock))
        .thenReturn(value)
      this
    }
