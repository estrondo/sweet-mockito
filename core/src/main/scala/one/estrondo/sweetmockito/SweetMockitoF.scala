package one.estrondo.sweetmockito

import scala.reflect.ClassTag
import org.mockito.Mockito
import org.mockito.stubbing.OngoingStubbing
import org.mockito.invocation.InvocationOnMock
import org.mockito.Mock

class SweetMockitoF[F[_], A](invocation: => F[A]):

  def thenAnswer[E, A1](fn: InvocationOnMock => Answer[E, A1])(using AnswerF[F, E, A1], A1 <:< A): this.type =
    Mockito
      .when(invocation)
      .thenAnswer(AnswerF[F, E, A1](fn))
    this

  def thenEmpty()(using IterableEffect[F, Nothing, A]): this.type =
    Mockito
      .when(invocation)
      .thenReturn(IterableEffect[F, Nothing, A].empty())
    this

  def thenFail[E](error: => E)(using Effect[F, E, A]): this.type =
    Mockito
      .when(invocation)
      .thenReturn(Effect[F, E, A].failed(error))
    this

  def thenFailWith[E](fn: InvocationOnMock => E)(using Effect[F, E, A]): this.type =
    Mockito
      .when(invocation)
      .thenAnswer { invocationOnMock =>
        Effect[F, E, A].failed(fn(invocationOnMock))
      }
    this

  def thenReturn[A1](value: => A1)(using Effect[F, Nothing, A1], A1 <:< A): this.type =
    Mockito
      .when(invocation)
      .thenReturn(Effect[F, Nothing, A1].succeed(value))
    this

  def thenReturnWith[A1](fn: InvocationOnMock => A1)(using Effect[F, Nothing, A1], A1 <:< A): this.type =
    Mockito
      .when(invocation)
      .thenAnswer { invocationOnMock =>
        Effect[F, Nothing, A1].succeed(fn(invocationOnMock))
      }
    this
