package one.estrondo.sweetmockito

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.Inside

abstract class Spec extends AnyFunSpec, Matchers, Inside
