package one.estrondo.sweetmockito

import scala.util.CommandLineParser.FromString.given_FromString_Int
import org.mockito.invocation.InvocationOnMock
import org.mockito.stubbing.{Answer => MockitoAnswer}
import java.util.Objects
import scala.util.Try
import scala.util.Success
import scala.util.Failure

object Answer:

  def succeed[E, A](value: => A): Answer[E, A] = Succeed(value)

  def failed[E, A](error: => E): Answer[E, A] = Failed(error)

  case class Succeed[E, A](value: A) extends Answer[E, A]
  case class Failed[E, A](error: E)  extends Answer[E, A]

sealed trait Answer[E, A]

object AnswerF2:

  inline transparent def apply[F[_, _], E, A](using inline answer: AnswerF2[F, E, A]): AnswerF2[F, E, A] = answer

  given [F[_, _], E, A](using effect2: Effect2[F, E, A]): AnswerF2[F, E, A] = new AnswerF2(effect2)

class AnswerF2[F[_, _], E, A](effect2: Effect2[F, E, A]):

  def apply(fn: InvocationOnMock => Answer[E, A]): MockitoAnswer[F[E, A]] = new MockitoAnswer:
    override def answer(invocation: InvocationOnMock): F[E, A] =
      fn(invocation) match
        case Answer.Succeed(value) => effect2.succeed(value)
        case Answer.Failed(error)  => effect2.failed(error)

object AnswerF:

  inline transparent def apply[F[_], E, A](using inline answer: AnswerF[F, E, A]): AnswerF[F, E, A] = answer

  given [F[_], E, A](using effect: Effect[F, E, A]): AnswerF[F, E, A] = new AnswerF(effect)

class AnswerF[F[_], E, A](effect: Effect[F, E, A]):

  def apply(fn: InvocationOnMock => Answer[E, A]): MockitoAnswer[F[A]] = new MockitoAnswer:

    override def answer(invocation: InvocationOnMock): F[A] =
      fn(invocation) match
        case Answer.Succeed(value) => effect.succeed(value)
        case Answer.Failed(error)  => effect.failed(error)
