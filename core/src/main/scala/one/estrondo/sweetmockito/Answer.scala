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

  given [F[_, _], E, A](using effect2: Effect2[F, E]): AnswerF2[F, E, A] = new AnswerF2(effect2)

class AnswerF2[F[_, _], E, A](effect2: Effect2[F, E]):

  def apply(fn: InvocationOnMock => Answer[E, A]): MockitoAnswer[Any] = new MockitoAnswer:
    override def answer(invocation: InvocationOnMock): Any =
      fn(invocation) match
        case Answer.Succeed(value) => effect2.succeed(value)
        case Answer.Failed(error)  => effect2.failed(error)
