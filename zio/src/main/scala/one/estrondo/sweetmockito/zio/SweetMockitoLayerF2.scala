package one.estrondo.sweetmockito.zio

import one.estrondo.sweetmockito.SweetMockito
import one.estrondo.sweetmockito.SweetMockitoF
import org.mockito.stubbing.OngoingStubbing
import zio.RIO
import zio.Tag
import zio.ZIO
import org.mockito.invocation.InvocationOnMock
import one.estrondo.sweetmockito.Effect2

class SweetMockitoLayerF2[M: Tag, F[_, _], E, A](invocation: M => F[E, A]):

  def thenFail[B](error: => B)(using Effect2[F, B], B <:< E): RIO[M, this.type] =
    ZIO.serviceWith { mock =>
      SweetMockito
        .whenF2(invocation(mock))
        .thenFail(error)
      this
    }

  def thenReturn[B](value: => B)(using Effect2[F, Nothing], B <:< A): RIO[M, this.type] =
    ZIO.serviceWith[M] { mock =>
      SweetMockito
        .whenF2(invocation(mock))
        .thenReturn(value)
      this
    }
