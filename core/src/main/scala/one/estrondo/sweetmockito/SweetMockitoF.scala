package one.estrondo.sweetmockito

import scala.reflect.ClassTag
import org.mockito.Mockito
import org.mockito.stubbing.OngoingStubbing
import org.mockito.invocation.InvocationOnMock
import org.mockito.Mock

class SweetMockitoF[F[_], A](invocation: => F[A]):

  def thenFail[E](error: => E)(using Effect[F, E]): this.type =
    Mockito
      .when(invocation)
      .thenReturn(Effect[F, E].failed(error))
    this

  def thenFailWith[E](fn: InvocationOnMock => E)(using Effect[F, E]): this.type =
    Mockito
      .when(invocation)
      .thenAnswer { invocationOnMock =>
        Effect[F, E].failed(fn(invocationOnMock))
      }
    this

  def thenReturn[B](value: => B)(using Effect[F, Nothing], B <:< A): this.type =
    Mockito
      .when(invocation)
      .thenReturn(Effect[F, Nothing].succeed(value))
    this

  def thenReturnWith[B](fn: InvocationOnMock => B)(using Effect[F, Nothing], B <:< A): this.type =
    Mockito
      .when(invocation)
      .thenAnswer { invocationOnMock =>
        Effect[F, Nothing].succeed(fn(invocationOnMock))
      }
    this
