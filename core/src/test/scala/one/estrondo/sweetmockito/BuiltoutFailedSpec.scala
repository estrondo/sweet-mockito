package one.estrondo.sweetmockito.outro

import scala.util.Try
import scala.util.Failure
import java.io.FileOutputStream
import org.mockito.ArgumentMatchers
import scala.util.Success
import one.estrondo.sweetmockito.SweetMockito
import one.estrondo.sweetmockito.Answer

class BuiltoutFailedSpec extends one.estrondo.sweetmockito.Spec:

  trait Foo:
    def failAsTry(arg: String): Try[String]
    def failAsEither(value: String): Either[String, Boolean]

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
        .whenF2(mock.failAsEither("42"))
        .thenFail("Ouch!")

      assert(mock.failAsEither("42") == Left("Ouch!"))
    }

    it("it should answer for scala.util.Try") {
      val mock = SweetMockito[Foo]
      SweetMockito
        .whenF(mock.failAsTry(ArgumentMatchers.any()))
        .thenAnswer { invocation =>
          Answer.failed(IllegalArgumentException(s"Ouch ${invocation.getArgument(0)}!"))
        }

      inside(mock.failAsTry("99")) { case Failure(exception: IllegalArgumentException) =>
        assert(exception.getMessage() == "Ouch 99!")
      }
    }

    it("it should answer for scala.util.Either") {
      val mock = SweetMockito[Foo]
      SweetMockito
        .whenF2(mock.failAsEither(ArgumentMatchers.any()))
        .thenAnswer { invocation =>
          Answer.failed(s"Ouch ${invocation.getArgument(0)}!")
        }

      assert(mock.failAsEither("22") == Left("Ouch 22!"))
    }
  }
