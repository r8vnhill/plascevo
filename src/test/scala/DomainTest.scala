package cl.ravenhill.plascebo

import org.scalacheck.{Gen, Shrink}

class DomainTest extends AbstractPlascevoTest {
    override def afterEach(): Unit = {
        Domain.random = scala.util.Random
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
                    val thrown = intercept[IllegalArgumentException] {
                        Domain.equalityThreshold = value
                    }
                    thrown.getMessage should endWith (s"The equality threshold ($value) must be greater than or equal to zero")
                    Domain.equalityThreshold shouldBe Domain.DEFAULT_EQUALITY_THRESHOLD
                }
            }
        }
    }
}
