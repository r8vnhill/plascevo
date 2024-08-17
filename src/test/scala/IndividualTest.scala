package cl.ravenhill.plascevo

import repr.{Feature, IsValidRepresentation, Representation, SimpleFeature, simpleFeatureGen, simpleRepresentationGen}

import cl.ravenhill.individualGen
import cl.ravenhill.plascevo.matchers.beValid
import cl.ravenhill.plascevo.{Domain, Individual, ToStringMode, repr}
import org.scalacheck.{Arbitrary, Gen}

class IndividualTest extends AbstractPlascevoTest:
    override def beforeEach(): Unit = {
        Domain.toStringMode = ToStringMode.SIMPLE
    }

    "An Individual instance" - {
        "should have a representation property that is set according to the constructor" in {
            forAll(
                simpleRepresentationGen(simpleFeatureGen(Arbitrary.arbInt.arbitrary)),
                Arbitrary.arbDouble.arbitrary
            ) { (representation, fitness) =>
                val individual = Individual(representation, fitness)
                individual.representation shouldEqual representation
            }
        }

        "should have a fitness property that is set according to the constructor" in {
            forAll(
                simpleRepresentationGen(simpleFeatureGen(Arbitrary.arbInt.arbitrary)),
                arbNonNaNDouble()
            ) { (representation, fitness) =>
                val individual = Individual(representation, fitness)
                individual.fitness shouldEqual fitness
            }
        }

        "should have a size property that is equal to the size of the representation" in {
            forAll(simpleRepresentationGen(simpleFeatureGen(Arbitrary.arbInt.arbitrary))) { representation =>
                val individual = Individual(representation, 0.0)
                individual.size shouldEqual representation.size
            }
        }

        "when verifying" - {
            "should return true if the representation is valid" in {
                forAll(
                    individualGen(
                        simpleRepresentationGen(
                            simpleFeatureGen(Arbitrary.arbInt.arbitrary),
                            Gen.const(IsValidRepresentation.VALID)
                        ),
                        Arbitrary.arbDouble.arbitrary
                    )
                ) { individual =>
                    individual should beValid
                }
            }

            "should return false if the representation is invalid" in {
                forAll(
                    individualGen(
                        simpleRepresentationGen(
                            simpleFeatureGen(Arbitrary.arbInt.arbitrary)
                        ),
                        Gen.const(Double.NaN)
                    )
                ) { individual =>
                    individual shouldNot beValid
                }
            }
        }

        "can be flattened" in {
            forAll(
                arbIndividualAndFlattenedRepresentation(simpleFeatureGen(Arbitrary.arbInt.arbitrary))
            ) { case (individual, flattened) =>
                individual.flatten() shouldEqual flattened
            }
        }

        "when checking if an individual is evaluated" - {
            "should return true if the fitness is not NaN" in {
                forAll(
                    simpleRepresentationGen(simpleFeatureGen(Arbitrary.arbInt.arbitrary)),
                    arbNonNaNDouble()
                ) { (representation, fitness) =>
                    val individual = Individual(representation, fitness)
                    individual.isEvaluated shouldBe true
                }
            }

            "should return false if the fitness is NaN" in {
                forAll(
                    simpleRepresentationGen(simpleFeatureGen(Arbitrary.arbInt.arbitrary)),
                    Gen.const(Double.NaN)
                ) { (representation, fitness) =>
                    val individual = Individual(representation, fitness)
                    individual.isEvaluated shouldBe false
                }
            }
        }
    }

/** Generates an `Individual` instance along with its flattened representation.
 *
 * The `arbIndividualAndFlattenedRepresentation` method produces a generator (`Gen`) that creates a tuple consisting of
 * an `Individual` and a corresponding flattened list of values. The `Individual` is composed of a `Representation`
 * constructed from a list of `SimpleFeature` instances. This is useful for testing scenarios where you need to validate
 * the correctness of the flattening and folding operations in a `Representation`.
 *
 * @param feature A generator for `SimpleFeature` instances, each holding a value of type `T`.
 * @tparam T The type of value held by the features and used within the representation.
 * @return A generator that produces a tuple containing an `Individual` and a flattened list of values.
 */
private def arbIndividualAndFlattenedRepresentation[T](
    feature: Gen[SimpleFeature[T]]
): Gen[(Individual[T, SimpleFeature[T], Representation[T, SimpleFeature[T]]], List[T])] = for {
    size <- Gen.chooseNum(0, 10)
    elementsAndFlattened <- Gen.listOfN(size, Gen.listOf(feature))
} yield {
    val flattened = elementsAndFlattened.flatten.map(_.value)

    val representation = new Representation[T, SimpleFeature[T]] {
        override val size: Int = flattened.size

        override def flatten(): List[T] = flattened

        override def foldLeft[U](initial: U)(f: (U, T) => U): U = flattened.foldLeft(initial)(f)

        override def foldRight[U](initial: U)(f: (T, U) => U): U = flattened.foldRight(initial)(f)
    }

    Individual(representation) -> flattened
}
