package one.estrondo.sweetmockito

import scala.util.Try
import scala.util.Success
import org.mockito.ArgumentMatchers
import scala.util.Failure

class BuiltoutSucceedSpec extends Spec:

  case class Message(value: String)

  trait Foo:
    def returnAsTry(param: Int): Try[String]
    def returnAsEither(param: String): Either[String, Message]
    def returnAsOption(param: String): Option[Message]
    def returnAsVector(param: String): Vector[Message]

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
        .thenReturn(Message("Earth is OK!"))

      assert(mock.returnAsEither("Earth") == Right(Message("Earth is OK!")))
    }

    it("it should answer for scala.util.Try") {
      val mock    = SweetMockito[Foo]
      val illegal = IllegalArgumentException("It's different of 99!")
      SweetMockito
        .whenF(mock.returnAsTry(ArgumentMatchers.any()))
        .thenAnswer { invocation =>
          Answer.succeed(s"Ok ${invocation.getArgument(0)}!")
        }

      assert(mock.returnAsTry(99) == Success("Ok 99!"))
    }

    it("it should answer for scala.util.Either") {
      val mock    = SweetMockito[Foo]
      val illegal = IllegalArgumentException("It's different of 99!")
      SweetMockito
        .whenF2(mock.returnAsEither(ArgumentMatchers.any()))
        .thenAnswer { invocation =>
          Answer.succeed(Message(s"Ok ${invocation.getArgument(0)}!"))
        }

      assert(mock.returnAsEither("99") == Right(Message("Ok 99!")))
    }

    it("it should work for scala.Option") {
      val mock = SweetMockito[Foo]
      SweetMockito
        .whenF(mock.returnAsOption("Earth"))
        .thenReturn(Message("Earth is OK!"))

      assert(mock.returnAsOption("Earth") == Some(Message("Earth is OK!")))
    }

    it("it should return a None for a scala.Option") {
      val mock = SweetMockito[Foo]
      SweetMockito
        .whenF(mock.returnAsOption("Pluto"))
        .thenEmpty()

      assert(mock.returnAsOption("Pluto") == None)
    }

    it("it should work for any collection (for instance scala.collection.Vector)") {
      val l = List(1, 23, 2)
      val c = l.toVector

      val mock = SweetMockito[Foo]

      SweetMockito
        .whenF(mock.returnAsVector("Earth"))
        .thenReturn(Message("Earth is OK!"))

      assert(mock.returnAsVector("Earth") == Vector(Message("Earth is OK!")))
    }

    it("it should return an Empty for any collection (not for Maps)") {
      val mock = SweetMockito[Foo]
      SweetMockito
        .whenF(mock.returnAsVector("Pluto"))
        .thenEmpty()

      assert(mock.returnAsVector("Pluto") == Nil)
    }
  }
