package one.estrondo.sweetmockito.zio

import zio.test.ZIOSpecDefault
import zio.Scope
import zio.test.Spec
import zio.test.TestEnvironment
import zio.{ZIO, Task}
import one.estrondo.sweetmockito.SweetMockito
import zio.test.{assertTrue, assert, Assertion}
import java.time.ZonedDateTime
import java.io.IOException
import zio.IO
import zio.ZLayer
import one.estrondo.sweetmockito.Answer
import org.mockito.ArgumentMatchers

object SweetMockitoSpec extends ZIOSpecDefault:

  trait SomeTrait:

    def tellMe(a: Int, b: String): Task[String]

    def validate(value: Int): IO[String, Long]

    def filter(input: String): Task[Option[String]]

  def spec: Spec[TestEnvironment & Scope, Any] =
    suite("SweetMockito with ZIO")(
      suite("Simple behaviour")(
        test("It should mock a succeed Task.") {
          val mock = SweetMockito[SomeTrait]
          SweetMockito
            .whenF2(mock.tellMe(100, "100"))
            .thenReturn("Woohoo!")

          for result <- mock.tellMe(100, "100")
          yield assertTrue(result == "Woohoo!")
        },
        test("It should mock a failed Task.") {
          val mock          = SweetMockito[SomeTrait]
          val expectedError = IOException("@@@")
          SweetMockito
            .whenF2(mock.tellMe(42, "42"))
            .thenFail(expectedError)

          for exit <- mock.tellMe(42, "42").exit
          yield assert(exit)(Assertion.fails(Assertion.equalTo(expectedError)))
        },
        test("It should mock an IO.") {
          val mock          = SweetMockito[SomeTrait]
          val expectedError = "I hate these numbers: 17, 22!"
          SweetMockito
            .whenF2(mock.validate(22))
            .thenFail(expectedError)

          for exit <- mock.validate(22).exit
          yield assert(exit)(Assertion.fails(Assertion.equalTo(expectedError)))
        },
        test("It should mock an Task[Option] with Task[None].") {
          val mock = SweetMockito[SomeTrait]
          SweetMockito
            .whenF2(mock.filter("Mars"))
            .thenReturn(None)

          for result <- mock.filter("Mars")
          yield assertTrue(result.isEmpty)
        }
      ),
      suite("Simple behaviour with ZLayers")(
        test("It should mock a succeed layer.") {
          for
            _      <- SweetMockitoLayer[SomeTrait]
                        .whenF2(_.tellMe(77, "66"))
                        .thenReturn("Yes!")
            mock   <- ZIO.service[SomeTrait]
            result <- mock.tellMe(77, "66")
          yield assertTrue(result == "Yes!")

        },
        test("It should mock a failed layer.") {
          for
            _    <- SweetMockitoLayer[SomeTrait]
                      .whenF2(_.validate(17))
                      .thenFail("You again?")
            mock <- ZIO.service[SomeTrait]
            exit <- mock.validate(17).exit
          yield assert(exit)(Assertion.fails(Assertion.equalTo("You again?")))
        },
        test("It should mock with a answer.") {
          for
            _    <- SweetMockitoLayer[SomeTrait]
                      .whenF2(_.validate(ArgumentMatchers.any()))
                      .thenAnswer { invocation =>
                        if invocation.getArgument[Int](0) == 17 then Answer.failed("Please, stop!")
                        else Answer.succeed(13L)
                      }
            mock <- ZIO.service[SomeTrait]
            ok   <- mock.validate(13).exit
            stop <- mock.validate(17).exit
          yield assert(ok)(Assertion.succeeds(Assertion.equalTo(13L))) && assert(stop)(
            Assertion.fails(Assertion.equalTo("Please, stop!"))
          )
        }
      ).provideSomeLayer(ZLayer.succeed(SweetMockito[SomeTrait]))
    )
