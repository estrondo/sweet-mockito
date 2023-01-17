package one.estrondo.sweetmockito

import scala.reflect.ClassTag
import org.mockito.Mockito
import org.mockito.stubbing.OngoingStubbing
import org.mockito.invocation.InvocationOnMock
import org.mockito.Mock

object SweetMockito:

  def apply[T: ClassTag]: T =
    Mockito.mock(summon[ClassTag[T]].runtimeClass.asInstanceOf[Class[T]])

  def whenF[F[_], A](invocation: => F[A]): SweetMockitoF[F, A] =
    new SweetMockitoF(invocation)

  def whenF2[F[_, _], E, A](invocation: => F[E, A]): SweetMockitoF2[F, E, A] =
    new SweetMockitoF2(invocation)
