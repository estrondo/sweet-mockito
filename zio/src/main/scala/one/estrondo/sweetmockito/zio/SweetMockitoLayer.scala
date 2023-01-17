package one.estrondo.sweetmockito.zio

import one.estrondo.sweetmockito.SweetMockito
import one.estrondo.sweetmockito.SweetMockitoF
import org.mockito.stubbing.OngoingStubbing
import zio.RIO
import zio.Tag
import zio.ZIO
import org.mockito.invocation.InvocationOnMock

object SweetMockitoLayer:

  def apply[M: Tag]: SweetMockitoLayer[M] = new SweetMockitoLayer()

class SweetMockitoLayer[M: Tag]:

  def whenF2[F[_, _], E, A](invocation: M => F[E, A]): SweetMockitoLayerF2[M, F, E, A] =
    new SweetMockitoLayerF2(invocation)
