package cl.ravenhill.plascevo
package evolution.states

import ranking.{FitnessMaxRanker, FitnessMinRanker, IndividualRanker}
import repr.*

import org.scalacheck.{Arbitrary, Gen}

class EvolutionStateTest extends AbstractPlascevoTest:
    "An EvolutionState" - {
        "should have a size property that" - {
            "returns the number of individuals in the population" in {
                forAll(
                    evolutionStateAndPopulationGen(
                        populationGen(
                            individualGen(simpleRepresentationGen(simpleFeatureGen(Arbitrary.arbInt.arbitrary)))
                        ),
                        Gen.oneOf(
                            FitnessMaxRanker[Int, SimpleFeature[Int], Representation[Int, SimpleFeature[Int]]],
                            FitnessMinRanker[Int, SimpleFeature[Int], Representation[Int, SimpleFeature[Int]]]
                        )
                    )
                ) { (evolutionState, population) =>
                    evolutionState should have size (population.size)
                }
            }
        }

        "when checking for emptiness" - {
            "should return true if the population is empty" in {
                forAll(
                    evolutionStateAndPopulationGen(
                        populationGen(
                            individualGen(simpleRepresentationGen(simpleFeatureGen(Arbitrary.arbInt.arbitrary))),
                            0,
                            0
                        ),
                        Gen.oneOf(
                            FitnessMaxRanker[Int, SimpleFeature[Int], Representation[Int, SimpleFeature[Int]]],
                            FitnessMinRanker()
                        )
                    )
                ) { case (evolutionState, _) =>
                    evolutionState should be(empty)
                }
            }

            "should return false if the population is not empty" in {
                forAll(
                    evolutionStateAndPopulationGen(
                        populationGen(
                            individualGen(simpleRepresentationGen(simpleFeatureGen(Arbitrary.arbInt.arbitrary))),
                            1
                        ),
                        Gen.oneOf(
                            FitnessMaxRanker[Int, SimpleFeature[Int], Representation[Int, SimpleFeature[Int]]],
                            FitnessMinRanker()
                        )
                    )
                ) { case (evolutionState, _) =>
                    evolutionState should not be empty
                }
            }
        }

        "can be mapped to another state" in {
            forAll(
                evolutionStateAndPopulationGen(
                    populationGen(
                        individualGen(simpleRepresentationGen(simpleFeatureGen(Arbitrary.arbInt.arbitrary)))
                    ),
                    Gen.oneOf(
                        FitnessMaxRanker[Int, SimpleFeature[Int], Representation[Int, SimpleFeature[Int]]],
                        FitnessMinRanker[Int, SimpleFeature[Int], Representation[Int, SimpleFeature[Int]]]
                    )
                )
            ) { case (evolutionState, _) =>
                val mapped = evolutionState.map(ind => ind.copy(fitness = ind.fitness + 1))
                mapped.population should contain theSameElementsAs
                    evolutionState.population.map(ind => ind.copy(fitness = ind.fitness + 1))
            }
        }

        "can be folded to the left" in {
            forAll(
                evolutionStateAndPopulationGen(
                    populationGen(
                        individualGen(simpleRepresentationGen(simpleFeatureGen(Arbitrary.arbInt.arbitrary)))
                    ),
                    Gen.oneOf(
                        FitnessMaxRanker[Int, SimpleFeature[Int], Representation[Int, SimpleFeature[Int]]],
                        FitnessMinRanker[Int, SimpleFeature[Int], Representation[Int, SimpleFeature[Int]]]
                    )
                )
            ) { case (evolutionState, _) =>
                val folded = evolutionState.foldLeft(0)(_ + _)
                val expected = evolutionState.population.map(_.representation.flatten().sum).sum
                folded should be(expected)
            }
        }

        "can be folded to the right" in {
            forAll(
                evolutionStateAndPopulationGen(
                    populationGen(
                        individualGen(simpleRepresentationGen(simpleFeatureGen(Arbitrary.arbInt.arbitrary)))
                    ),
                    Gen.oneOf(
                        FitnessMaxRanker[Int, SimpleFeature[Int], Representation[Int, SimpleFeature[Int]]],
                        FitnessMinRanker[Int, SimpleFeature[Int], Representation[Int, SimpleFeature[Int]]]
                    )
                )
            ) { case (evolutionState, _) =>
                val folded = evolutionState.foldRight(0)(_ + _)
                val expected = evolutionState.population.map(_.representation.flatten().sum).sum
                folded should be(expected)
            }
        }

        "can be flattened" in {
            forAll(
                evolutionStateAndPopulationGen(
                    populationGen(
                        individualGen(simpleRepresentationGen(simpleFeatureGen(Arbitrary.arbInt.arbitrary)))
                    ),
                    Gen.oneOf(
                        FitnessMaxRanker[Int, SimpleFeature[Int], Representation[Int, SimpleFeature[Int]]],
                        FitnessMinRanker[Int, SimpleFeature[Int], Representation[Int, SimpleFeature[Int]]]
                    )
                )
            ) { case (evolutionState, _) =>
                val flattened = evolutionState.flatten()
                val expected = evolutionState.population.flatMap(_.representation.flatten())
                flattened should contain theSameElementsAs expected
            }
        }
    }

    private def evolutionStateAndPopulationGen[T, F <: Feature[T, F], R <: Representation[T, F]](
        populationGen: Gen[Population[T, F, R]],
        rankerGen: Gen[IndividualRanker[T, F, R]],
        generationGen: Gen[Int] = positiveIntGen()
    ): Gen[(EvolutionState[T, F, R], Population[T, F, R])] =
        for
            population <- populationGen
            ranker <- rankerGen
            generation <- generationGen
        yield
            (new SimpleState[T, F, R](population, ranker, generation), population)
