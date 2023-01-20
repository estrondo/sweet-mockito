# sweet-mockito


| Scaladex   | Scaladoc   |  Maven  |
| :--------: | :--------: | :-----: |
| [![sweet-mockito Scala version support](https://index.scala-lang.org/estrondo/sweet-mockito/sweet-mockito/latest.svg)](https://index.scala-lang.org/estrondo/sweet-mockito/sweet-mockito) | [![javadoc](https://javadoc.io/badge2/one.estrondo/sweet-mockito/1.0.0/javadoc.svg)](https://javadoc.io/doc/one.estrondo/sweet-mockito/1.0.0) | [![Maven Central](https://img.shields.io/maven-central/v/one.estrondo/sweet-mockito_3.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22one.estrondo%22%20AND%20a:%22sweet-mockito_3%22) |

A small library to help you (or not) with [Mockito](https://site.mockito.org/) + [Scala 3](https://scala-lang.org/) + [ZIO](https://zio.dev/) (I think it is possible with [Cats Effect](https://typelevel.org/cats-effect/) as well).

I have decided to publish it because it helped me with a small project that I have been working on.

The following examples use ZIO, Try, Either and Option as Effect types.

## Installing

Add the following dependencies in your sbt build, remember for now **it's available only for Scala 3**.

```scala
    "one.estrondo" %% "sweet-mockito" % <version> % Test,
    "one.estrondo" %% "sweet-mockito-zio" % <version> % Test
```

## Returning succeed and failed ZIO effects.

First, use these imports:

```scala
import one.estrondo.sweetmockito.SweetMockito
import one.estrondo.sweetmockito.zio.given // Remember, it's necessary for Scala 3.
```

Trait and class used by the examples:

```scala

  trait Foo:
    def tellMe(name: String): Task[Message]
    def canIPass(name: String): IO[String, Message]

  case class Message(message: String)

```

### Mocking success results

```scala
val mock = SweetMockito[Foo]
SweetMockito
  .whenF2(mock.tellMe("Einstein")) // there is a method called whenF, but for ZIO use whenF2.
  .thenReturn(Message("You were smart!"))

for result <- mock.tellMe("Einstein")
yield assertTrue(result == Message("You were smart!"))

```

### Mocking failures (not dying)

```scala
val mock          = SweetMockito[Foo]
val expectedError = IOException("You? Maybe... look at that bird!")
SweetMockito
  .whenF2(mock.tellMe("Me"))
  .thenFail(expectedError)

for exit <- mock.tellMe("Me").exit
yield assert(exit)(Assertion.fails(Assertion.equalTo(expectedError)))
```

### If you need more control, you can do this

In this case, you want to run some code to answer who is calling the mocked method, for this, you have to inform SweetMockit what kind the answer will be returned using `Answer.succeed` or `Answer.failed`. Please import this object:

```scala
import one.estrondo.sweetmockito.Answer
```

Here is an example:

```scala
val mock          = SweetMockito[Foo]
val expectedError = IOException("You're a machine, not a man!")

SweetMockito
  .whenF2(mock.tellMe(ArgumentMatchers.any())) // As you can see, here I'm using ArgumentMatchers from Mockito.
  .thenAnswer { invocation => // Here invocation is an InvocationOnMock from Mockito too.
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
```

## Mocking ZIO Services

It is quite similar, but do need to import this:

```scala
import one.estrondo.sweetmockito.zio.SweetMockitoLayer
```

### Mocking success services

```scala
for
  _      <- SweetMockitoLayer[Foo]
              .whenF2(_.canIPass("Michael Jackson")) // Remember, use whenF2 for ZIO.
              .thenReturn(Message("Yes! Please take this money."))
  mock   <- ZIO.service[Foo]
  result <- mock.canIPass("Michael Jackson")

yield assertTrue(result == Message("Yes! Please take this money."))
```

### Mocking failed services

```scala
for
  _    <- SweetMockitoLayer[Foo]
            .whenF2(_.canIPass("This is my friend"))
            .thenFail("I don't think so, I've never seen him!")
  mock <- ZIO.service[Foo]
  exit <- mock.canIPass("This is my friend").exit

yield assert(exit)(Assertion.fails(Assertion.equalTo("I don't think so, I've never seen him!")))
```

### If you need more control over mocking services, you will do this:

Import:

```scala
import one.estrondo.sweetmockito.Answer
```

Example:

```scala
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
```

## Bonus! Mocking with scala.util.Try, scala.util.Either, scala.Option and Iterable classes.

Imports that you need:

```scala
import one.estrondo.sweetmockito.SweetMockito
import one.estrondo.sweetmockito.Answer // Just if you need .thenAnswer method, there are examples bellow.
```

These examples came from my testing codes and I'm using [ScalaTest](https://www.scalatest.org).

### scala.util.Try

For scala.util.Try (maybe in coming support for Cats Effect) you have to call `whenF` instead `whenF2` used by `Either` and ZIO mocking.

Success example:

```scala
val mock = SweetMockito[Foo]
SweetMockito
  .whenF(mock.returnAsTry(10))
  .thenReturn("Ok!")

assert(mock.returnAsTry(10) == Success("Ok!"))
```

Failure example:

```scala
val mock      = SweetMockito[Foo]
val exception = IllegalArgumentException("I'm marcian!")

SweetMockito
  .whenF(mock.failAsTry("earth"))
  .thenFail(exception)

assert(mock.failAsTry("earth") == Failure(exception))
```

### scala.either.Either

Right example:

```scala
val mock = SweetMockito[Foo]
SweetMockito
  .whenF2(mock.returnAsEither("Earth"))
  .thenReturn(Message("Earth is OK!"))

assert(mock.returnAsEither("Earth") == Right(Message("Earth is OK!")))
```

Left example:

```scala
val mock = SweetMockito[Foo]
SweetMockito
  .whenF2(mock.failAsEither("42"))
  .thenFail("Ouch!")

assert(mock.failAsEither("42") == Left("Ouch!"))
```

### scala.Option

Some example:

```scala
val mock = SweetMockito[Foo]
SweetMockito
  .whenF(mock.returnAsOption("Earth"))
  .thenReturn(Message("Earth is OK!"))

assert(mock.returnAsOption("Earth") == Some(Message("Earth is OK!")))
```

None example, for this you can use the method `thenEmpty` which is available for some types.

```scala
val mock = SweetMockito[Foo]
SweetMockito
  .whenF(mock.returnAsOption("Pluto"))
  .thenEmpty()

assert(mock.returnAsOption("Pluto") == None)
```

### Iterable classes

Here I tested only `scala.collection.immutable.Vector`. SweetMockito looks for a `given` (implicit) `scala.collection.Factory` for the type that you want to return in your mock. So, any type that has this Factory available SweetMockito could be used for it.

One element:

```scala
val mock = SweetMockito[Foo]

SweetMockito
  .whenF(mock.returnAsVector("Earth"))
  .thenReturn(Message("Earth is OK!"))

assert(mock.returnAsVector("Earth") == Vector(Message("Earth is OK!")))
```

Multiple elements. I'm sorry! I forgot! That will be available soon.

Empty vector, like Option you can return an empty `Iterable`.

```scala
val mock = SweetMockito[Foo]
SweetMockito
  .whenF(mock.returnAsVector("Pluto"))
  .thenEmpty()

assert(mock.returnAsVector("Pluto") == Nil)
```

## You are welcomed to contribute.

If you have any suggestions or more ideas for features, please contact me here in github.
