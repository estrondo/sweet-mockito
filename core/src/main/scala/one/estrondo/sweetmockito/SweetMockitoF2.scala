package one.estrondo.sweetmockito

import scala.reflect.ClassTag
import org.mockito.Mockito
import org.mockito.stubbing.OngoingStubbing
import org.mockito.invocation.InvocationOnMock
import org.mockito.Mock

class SweetMockitoF2[F[_, _], E, A](invocation: => F[E, A]):

  def thenAnswer[E1, A1](
      fn: InvocationOnMock => Answer[E1, A1]
  )(using AnswerF2[F, E1, A1], E1 <:< E, A1 <:< A): this.type =
    Mockito
      .when(invocation)
      .thenAnswer(AnswerF2[F, E1, A1](fn))
    this

  def thenFail[E1](error: => E1)(using Effect2[F, E1, A], E1 <:< E): this.type =
    Mockito
      .when(invocation)
      .thenReturn(Effect2[F, E1, A].failed(error))
    this

  def thenReturn[A1](value: => A1)(using Effect2[F, Nothing, A1], A1 <:< A): this.type =
    Mockito
      .when(invocation)
      .thenReturn(Effect2[F, Nothing, A1].succeed(value))
    this
