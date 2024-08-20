package cl.ravenhill.plascevo
package operators.selection

import evolution.states
import evolution.states.{SimpleState, evolutionStateGen}
import ranking.{FitnessMaxRanker, FitnessMinRanker, IndividualRanker}
import repr.*

import org.scalacheck.{Arbitrary, Gen}

import scala.util.Random

class SelectorTest extends AbstractPlascevoTest {
    given Random = Random()
    given Double = 1e-6

    "A Selector" - {
        "should throw an exception when selecting from an empty population" in {
            forAll(
                simpleSelectorGen[Int, SimpleFeature[Int], Representation[Int, SimpleFeature[Int]]](),
                emptyStateGen(),
                Gen.chooseNum(1, 10))
            { (
                selector: Selector[Int, SimpleFeature[Int], Representation[Int, SimpleFeature[Int]]],
                state: SimpleState[Int, SimpleFeature[Int], Representation[Int, SimpleFeature[Int]]],
                outputSize: Int
            ) =>
                val ex = intercept[IllegalArgumentException] {
                    selector.select(state.population, outputSize, state.ranker)
                }
                ex.getMessage should include("Cannot select individuals from an empty population.")
            }
        }

        "when selecting from a given state" - {
            "should select the expected number of individuals" in {
                forAll(
                    simpleSelectorGen[Int, SimpleFeature[Int], Representation[Int, SimpleFeature[Int]]](),
                    nonEmptyStateGen().flatMap(state => positiveIntGen(state.size).map(size => state -> size))
                ) { case (selector, (state, outputSize)) =>
                    val selected = selector.select(state.population, outputSize, state.ranker)
                    selected should have size outputSize
                }
            }

            "should select the individuals with the highest fitness" in {
                forAll(
                    simpleSelectorGen[Int, SimpleFeature[Int], Representation[Int, SimpleFeature[Int]]](),
                    nonEmptyStateGen().flatMap(state => positiveIntGen(state.size).map(size => state -> size))
                ) { case (selector, (state, outputSize)) =>
                    val selected = selector.select(state.population, outputSize, state.ranker)
                    val ranked = state.ranker.sort(state.population)
                    selected should contain theSameElementsInOrderAs ranked.take(outputSize)
                }
            }
        }
    }
}


/** Generates a simple selector for use in evolutionary algorithms.
 *
 * The `simpleSelectorGen` function creates a generator (`Gen`) that produces a `Selector` instance. This selector
 * selects the top individuals from a population based on their rank, as determined by a provided `IndividualRanker`.
 * The selected population is of a specified size, containing the highest-ranking individuals.
 *
 * @tparam T The type of value held by the features in the individuals.
 * @tparam F The type of the feature, which must extend `Feature[T, F]`.
 * @tparam R The type of the representation, which must extend `Representation[T, F]`.
 * @return A generator that produces a `Selector[T, F, R]` which selects the top individuals from the population based
 *         on their rank.
 */
private def simpleSelectorGen[T, F <: Feature[T, F], R <: Representation[T, F]](): Gen[Selector[T, F, R]] = {
    new Selector[T, F, R] {
        override def select(
            population: Population[T, F, R], outputSize: Int,
            ranker: IndividualRanker[T, F, R]
        )(using random: Random, equalityThreshold: Double) = ranker.sort(population).take(outputSize)
    }
}

/** Generates an empty evolutionary state for testing purposes.
 *
 * The `emptyStateGen` function produces a generator (`Gen`) that creates an instance of `SimpleState` with an empty
 * population. This is useful for testing scenarios where an evolutionary algorithm's state needs to be initialized
 * with no individuals.
 *
 * The state is generated with a population size of `0` and uses a simple ranker selected from a set of predefined
 * rankers.
 *
 * @return A generator that produces an empty `SimpleState[Int, SimpleFeature[Int], Representation[Int,SimpleFeature[Int]]]`.
 */
private def emptyStateGen(): Gen[SimpleState[Int, SimpleFeature[Int], Representation[Int, SimpleFeature[Int]]]] =
    evolutionStateGen(
        populationGen(individualGen(simpleRepresentationGen(simpleFeatureGen(Arbitrary.arbInt.arbitrary))), 0, 0),
        simpleRankerGen()
    )

/** Generates a non-empty evolutionary state for testing purposes.
 *
 * The `nonEmptyStateGen` function produces a generator (`Gen`) that creates an instance of `SimpleState` with a
 * non-empty population. This is useful for testing scenarios where an evolutionary algorithm's state needs to be
 * initialized with a set of individuals.
 *
 * The state is generated with a population size between `1` and `10`, using a simple ranker selected from a set of
 * predefined rankers.
 *
 * @return A generator that produces a `SimpleState[Int, SimpleFeature[Int], Representation[Int, SimpleFeature[Int]]]`
 *         with a population containing between `1` and `10` individuals.
 */
private def nonEmptyStateGen(): Gen[SimpleState[Int, SimpleFeature[Int], Representation[Int, SimpleFeature[Int]]]] =
    evolutionStateGen(
        populationGen(
            individualGen(simpleRepresentationGen(simpleFeatureGen(Arbitrary.arbInt.arbitrary))),
            1,
            10
        ),
        simpleRankerGen()
    )

/** Generates a simple ranker for use in evolutionary algorithms.
 *
 * The `simpleRankerGen` function produces a generator (`Gen`) that selects one of two predefined rankers:
 * `FitnessMaxRanker` or `FitnessMinRanker`. These rankers are used to evaluate and rank individuals within a population
 * based on their fitness, either maximizing or minimizing the fitness value.
 *
 * @return A generator that produces either a `FitnessMaxRanker` or a `FitnessMinRanker` for use in evolutionary
 *         algorithms.
 */
private def simpleRankerGen(): Gen[IndividualRanker[Int, SimpleFeature[Int], Representation[Int, SimpleFeature[Int]]]] =
    Gen.oneOf(
        FitnessMaxRanker[Int, SimpleFeature[Int], Representation[Int, SimpleFeature[Int]]],
        FitnessMinRanker[Int, SimpleFeature[Int], Representation[Int, SimpleFeature[Int]]]
    )
