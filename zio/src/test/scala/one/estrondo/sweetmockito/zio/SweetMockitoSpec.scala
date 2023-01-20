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
import zio.test.TestResult

object SweetMockitoSpec extends ZIOSpecDefault:

  case class Message(message: String)

  trait Foo:

    def tellMe(name: String): Task[Message]

    def canIPass(name: String): IO[String, Message]

  def spec: Spec[TestEnvironment & Scope, Any] =
    suite("SweetMockito with ZIO")(
      suite("Simple behaviour")(
        test("It should mock a succeed result.") {
          val mock = SweetMockito[Foo]
          SweetMockito
            .whenF2(mock.tellMe("Einstein"))
            .thenReturn(Message("You were smart!"))

          for result <- mock.tellMe("Einstein")
          yield assertTrue(result == Message("You were smart!"))
        },
        test("It should mock a failed result.") {
          val mock          = SweetMockito[Foo]
          val expectedError = IOException("You? Maybe... look at that bird!")
          SweetMockito
            .whenF2(mock.tellMe("Me"))
            .thenFail(expectedError)

          for exit <- mock.tellMe("Me").exit
          yield assert(exit)(Assertion.fails(Assertion.equalTo(expectedError)))
        },
        test("It should mock with dynamic answers.") {
          val mock          = SweetMockito[Foo]
          val expectedError = IOException("You're a machine, not a man!")
          SweetMockito
            .whenF2(mock.tellMe(ArgumentMatchers.any()))
            .thenAnswer { invocation =>
              invocation.getArgument[String](0) match
                case "Morpheus"    => Answer.succeed(Message("A good leader"))
                case "Agent Smith" => Answer.failed(expectedError)
                case value         => Answer.succeed(Message(s"Who are you $value?"))
            }

          for
            morpheus   <- mock.tellMe("Morpheus").exit
            agentSmith <- mock.tellMe("Agent Smith").exit
            neo        <- mock.tellMe("Neo").exit
          yield TestResult.all(
            assert(morpheus)(Assertion.succeeds(Assertion.equalTo(Message("A good leader")))),
            assert(agentSmith)(Assertion.fails(Assertion.equalTo(expectedError))),
            assert(neo)(Assertion.succeeds(Assertion.equalTo(Message("Who are you Neo?"))))
          )
        }
      ),
      suite("Simple behaviour with ZLayers")(
        test("It should mock a succeed layer.") {
          for
            _      <- SweetMockitoLayer[Foo]
                        .whenF2(_.canIPass("Michael Jackson"))
                        .thenReturn(Message("Yes! Please take this money."))
            mock   <- ZIO.service[Foo]
            result <- mock.canIPass("Michael Jackson")
          yield assertTrue(result == Message("Yes! Please take this money."))

        },
        test("It should mock a failed layer.") {
          for
            _    <- SweetMockitoLayer[Foo]
                      .whenF2(_.canIPass("This is my friend"))
                      .thenFail("I don't think so, I've never seen him!")
            mock <- ZIO.service[Foo]
            exit <- mock.canIPass("This is my friend").exit
          yield assert(exit)(Assertion.fails(Assertion.equalTo("I don't think so, I've never seen him!")))
        },
        test("It should mock a layer with dynamic answers.") {
          for
            _     <- SweetMockitoLayer[Foo]
                       .whenF2(_.canIPass(ArgumentMatchers.any()))
                       .thenAnswer { invocation =>
                         invocation.getArgument[String](0) match
                           case "Dr. Spock"   => Answer.succeed(Message("Long live and prosper, please go ahead."))
                           case "Darth Vader" => Answer.failed("Nooooooo!")
                           case other         => Answer.failed(s"$other, you're not Spock!")
                       }
            mock  <- ZIO.service[Foo]
            spock <- mock.canIPass("Dr. Spock").exit
            vader <- mock.canIPass("Darth Vader").exit
            data  <- mock.canIPass("Data").exit
          yield TestResult.all(
            assert(spock)(Assertion.succeeds(Assertion.equalTo(Message("Long live and prosper, please go ahead.")))),
            assert(vader)(Assertion.fails(Assertion.equalTo("Nooooooo!"))),
            assert(data)(Assertion.fails(Assertion.equalTo("Data, you're not Spock!")))
          )
        }
      ).provideSomeLayer(ZLayer.succeed(SweetMockito[Foo]))
    )
