package one.estrondo.sweetmockito

import scala.reflect.ClassTag
import org.mockito.Mockito
import org.mockito.stubbing.OngoingStubbing
import org.mockito.invocation.InvocationOnMock
import org.mockito.Mock

class SweetMockitoF2[F[_, _], E, A](invocation: => F[E, A]):

  def thenFail[B](error: => B)(using Effect2[F, B], B <:< E): this.type =
    Mockito
      .when(invocation)
      .thenReturn(Effect2[F, B].failed(error))
    this

  def thenReturn[B](value: => B)(using Effect2[F, Nothing], B <:< A): this.type =
    Mockito
      .when(invocation)
      .thenReturn(Effect2[F, Nothing].succeed(value))
    this
