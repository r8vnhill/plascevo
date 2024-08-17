package cl.ravenhill.plascevo
package ranking

import repr.{Representation, SimpleFeature, simpleFeatureGen, simpleRepresentationGen}

import org.scalacheck.{Arbitrary, Gen}

class IndividualRankerTest extends AbstractPlascevoTest:

    "An individual ranker" - {
        "should have an ordering that works accordingly to the apply method" in {
            forAll(
                individualGen(simpleRepresentationGen(simpleFeatureGen(Arbitrary.arbInt.arbitrary))),
                individualGen(simpleRepresentationGen(simpleFeatureGen(Arbitrary.arbInt.arbitrary))),
                Gen.oneOf(FitnessMaxRanker[Int, SimpleFeature[Int], Representation[Int, SimpleFeature[Int]]],
                    FitnessMinRanker[Int, SimpleFeature[Int], Representation[Int, SimpleFeature[Int]]])
            ) { (individual1, individual2, ranker) =>
                val ordering = ranker.ordering
                val result = ordering.compare(individual1, individual2)
                val expected = ranker(individual1, individual2)
                result shouldBe expected
            }
        }

        "when applied to compare two individuals" - {

            "should return 1 if the first individual is better than the second" in {
                forAll(
                    homogeneousOrderedPairGen(
                        Arbitrary.arbDouble.arbitrary,
                        strict = true,
                        reversed = true
                    ).flatMap { case (a, b) => generateIndividualsPair(a, b) }
                ) { case (individual1, individual2) =>
                    val ranker = FitnessMaxRanker[
                        Int, SimpleFeature[Int], Representation[Int, SimpleFeature[Int]]
                    ]
                    ranker(individual1, individual2) shouldBe 1
                }
            }

            "should return -1 if the first individual is worse than the second" in {
                forAll(
                    homogeneousOrderedPairGen(
                        Arbitrary.arbDouble.arbitrary,
                        strict = true,
                        reversed = true
                    ).flatMap { case (a, b) => generateIndividualsPair(a, b) }
                ) { case (individual1, individual2) =>
                    val ranker = FitnessMinRanker[
                        Int, SimpleFeature[Int], Representation[Int, SimpleFeature[Int]]
                    ]
                    ranker(individual1, individual2) shouldBe -1
                }
            }

            "should return 0 if the individuals are equal" in {
                forAll(
                    Arbitrary.arbDouble.arbitrary.flatMap { a => generateIndividualsPair(a, a) }
                ) { case (individual1, individual2) =>
                    val ranker = FitnessMaxRanker[
                        Int, SimpleFeature[Int], Representation[Int, SimpleFeature[Int]]
                    ]
                    ranker(individual1, individual2) shouldBe 0
                }
            }
        }

        "can sort a population of individuals according to the sorting criteria" in {
            forAll(
                populationGen(individualGen(simpleRepresentationGen(simpleFeatureGen(Arbitrary.arbInt.arbitrary)))),
                Gen.oneOf(FitnessMaxRanker[Int, SimpleFeature[Int], Representation[Int, SimpleFeature[Int]]],
                    FitnessMinRanker[Int, SimpleFeature[Int], Representation[Int, SimpleFeature[Int]]]
                )
            ) { (population, ranker) =>
                val sorted = ranker.sort(population)
                sorted.zipWithNext { case (i1, i2) =>
                    ranker(i1, i2) should be >= 0
                }
            }
        }

        "should have a fitness transform method that returns the same list" in {
            forAll(Gen.listOf(nonNaNDoubleGen())) { fitnesses =>
                val ranker = FitnessMaxRanker[Int, SimpleFeature[Int], Representation[Int, SimpleFeature[Int]]]
                val transformed = ranker.fitnessTransform(fitnesses)
                transformed shouldBe fitnesses
            }
        }
    }
    
    def generateIndividualsPair(
        a: Double, b: Double
    ): Gen[
        (Individual[Int, SimpleFeature[Int], Representation[Int, SimpleFeature[Int]]],
            Individual[Int, SimpleFeature[Int], Representation[Int, SimpleFeature[Int]]])
    ] = for {
        i1 <- individualGen(
            simpleRepresentationGen(simpleFeatureGen(Arbitrary.arbInt.arbitrary)),
            Gen.const(a)
        )
        i2 <- individualGen(
            simpleRepresentationGen(simpleFeatureGen(Arbitrary.arbInt.arbitrary)),
            Gen.const(b)
        )
    } yield (i1, i2)

