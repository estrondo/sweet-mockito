package one.estrondo.sweetmockito

import scala.util.Try
import scala.util.Failure
import java.io.FileOutputStream

class BuiltoutFailedSpec extends Spec:

  trait Foo:
    def failAsTry(arg: String): Try[String]
    def failAsEither(value: Int): Either[String, Boolean]

  describe("When mocking a failure.") {
    it("it should work for scala.util.Try") {
      val mock      = SweetMockito[Foo]
      val exception = IllegalArgumentException("I'm marcian!")

      SweetMockito
        .whenF(mock.failAsTry("earth"))
        .thenFail(exception)

      assert(mock.failAsTry("earth") == Failure(exception))
    }

    it("it should work for scala.util.Either") {
      val mock = SweetMockito[Foo]

      SweetMockito
        .whenF2(mock.failAsEither(42))
        .thenFail("Ouch!")

      assert(mock.failAsEither(42) == Left("Ouch!"))
    }
  }
