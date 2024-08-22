package cl.ravenhill.plascevo
package operators.selection

import ranking.IndividualRanker
import repr.{Feature, Representation}

import scala.util.Random

/** A selector that implements tournament selection in an evolutionary or evolutionary algorithm.
 *
 * The `TournamentSelector` class extends the `Selector` trait and implements a tournament-based selection strategy. In
 * each tournament, a subset of individuals is randomly selected from the population, and the best individual according
 * to the provided ranker is chosen. This process is repeated until the desired number of individuals is selected.
 *
 * @param tournamentSize The number of individuals participating in each tournament. Must be greater than 0.
 * @tparam T The type of value stored by the feature.
 * @tparam F The type of feature that the individuals' representations hold, which must extend [[Feature]].
 * @tparam R The type of representation used by the individual, which must extend [[Representation]].
 */
case class TournamentSelector[T, F <: Feature[T, F], R <: Representation[T, F]](
    tournamentSize: Int = TournamentSelector.defaultTournamentSize
) extends Selector[T, F, R] {
    require(tournamentSize > 0, "Tournament size must be greater than 0.")

    /** Selects a subset of individuals from the population using tournament selection.
     *
     * This method selects a specified number of individuals from the population by performing a series of tournaments.
     * In each tournament, a group of individuals is randomly selected, and the best individual according to the
     * provided ranker is chosen. The process is repeated until the desired number of individuals is selected.
     *
     * @param population The population from which individuals are selected.
     * @param count      The number of individuals to select.
     * @param ranker     The `IndividualRanker` used to evaluate and compare individuals within the tournament.
     * @return A sequence of selected individuals.
     */
    override def select(
        population: Population[T, F, R],
        count: Int,
        ranker: IndividualRanker[T, F, R]
    )(using random: Random, equalityThreshold: Double): Seq[Individual[T, F, R]] = {
        (0 until count).map { _ =>
            // Initialize variables for the tournament selection
            var maxIndividual: Option[Individual[T, F, R]] = None

            // Iterate through the individuals in the tournament
            val selectedIndividuals = Iterator.continually(population(random.nextInt(population.size)))
                .take(tournamentSize)

            selectedIndividuals.foreach { individual =>
                maxIndividual match {
                    case Some(currentMax) =>
                        // Compare the current max with the new individual
                        if (ranker.compare(currentMax, individual) < 0) {
                            maxIndividual = Some(individual)
                        }
                    case None =>
                        // Set the first individual as the current max
                        maxIndividual = Some(individual)
                }
            }

            maxIndividual.get
        }
    }
}

/** Companion object for `TournamentSelector` providing default values.
 *
 * This object contains the default tournament size used when creating instances of `TournamentSelector`.
 */
object TournamentSelector {
    /** The default size of the tournament (number of participants). */
    val defaultTournamentSize: Int = 3
}
