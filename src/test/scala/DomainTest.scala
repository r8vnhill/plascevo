package cl.ravenhill.plascevo

import cl.ravenhill.plascevo.{AbstractPlascevoTest, Domain, repr}
import org.scalacheck.{Gen, Shrink}

class DomainTest extends AbstractPlascevoTest {
    override def afterEach(): Unit = {
        Domain.random = scala.util.Random
        Domain.equalityThreshold = Domain.DEFAULT_EQUALITY_THRESHOLD
    }

    "The Domain" - {
        "should have an equality threshold tat" - {
            "starts at the default value" in {
                Domain.equalityThreshold shouldBe Domain.DEFAULT_EQUALITY_THRESHOLD
            }

            "can be set to a positive value" in {
                forAll(Gen.posNum[Double]) { value =>
                    Domain.equalityThreshold = value
                    Domain.equalityThreshold shouldBe value
                }
            }

            "can be set to zero" in {
                Domain.equalityThreshold = 0.0
                Domain.equalityThreshold shouldBe 0.0
            }

            "cannot be set to a negative value" in {
                given Shrink[Double] = Shrink.shrinkAny

                forAll(Gen.chooseNum(Double.MinValue, -Double.MinPositiveValue).suchThat(_ < 0)) { value =>
                    whenever(value < 0) {
                        val thrown = intercept[IllegalArgumentException] {
                            Domain.equalityThreshold = value
                        }
                        thrown.getMessage should endWith(s"The equality threshold ($value) must be greater than or equal to zero")
                        Domain.equalityThreshold shouldBe Domain.DEFAULT_EQUALITY_THRESHOLD
                    }
                }
            }
        }

        "should have a random number generator that" - {
            "is a scala.util.Random instance by default" in {
                Domain.random shouldBe a[scala.util.Random]
            }

            "can be set to a custom instance" in {
                val random = new scala.util.Random(42)
                Domain.random = random
                Domain.random shouldBe theSameInstanceAs(random)
            }
        }
    }
}
