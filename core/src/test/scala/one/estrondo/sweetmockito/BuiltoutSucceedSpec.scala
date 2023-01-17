package one.estrondo.sweetmockito

import scala.util.Try
import java.time.ZonedDateTime
import scala.util.Success

class BuiltoutSucceedSpec extends Spec:

  trait Foo:
    def returnAsTry(param: Int): Try[String]
    def returnAsEither(param: String): Either[String, Boolean]

  describe("When mocking a returning.") {
    it("it should work for scala.util.Try") {
      val mock = SweetMockito[Foo]
      SweetMockito
        .whenF(mock.returnAsTry(10))
        .thenReturn("Ok!")

      assert(mock.returnAsTry(10) == Success("Ok!"))
    }

    it("it should work for scala.util.Either") {
      val mock = SweetMockito[Foo]
      SweetMockito
        .whenF2(mock.returnAsEither("Earth"))
        .thenReturn(true)

      assert(mock.returnAsEither("Earth") == Right(true))
    }
  }
