package cl.ravenhill.plascevo
package operators.selection

import ranking.IndividualRanker
import repr.{Feature, Representation}
import utils.Sorting.Unsorted
import utils.{Sorting, incremental, sub}
import utils.Numeric.=~

import scala.util.Random

/** A selector class implementing the roulette wheel selection mechanism for evolutionary algorithms.
 *
 * The `RouletteWheelSelector` class extends the `Selector` trait and implements the roulette wheel selection algorithm,
 * which is commonly used in evolutionary algorithms. This selector picks individuals from the population based on their
 * fitness, with a higher probability assigned to individuals with higher fitness values.
 *
 * @param sorted An instance of the `Sorting` enum, indicating whether the population should be sorted based on fitness
 *               before selection. The default value is `Unsorted`, meaning the population is used as-is.
 * @tparam T The type of value stored by the feature.
 * @tparam F The type of feature contained within the representation, which must extend [[Feature]].
 * @tparam R The type of representation used by the individual, which must extend [[Representation]].
 */
case class RouletteWheelSelector[T, F <: Feature[T, F], R <: Representation[T, F]](sorted: Sorting = Unsorted)
    extends Selector[T, F, R] {

    /** Calculates the selection probabilities for each individual in the population based on their fitness.
     *
     * The `probabilities` method transforms the fitness values of the population into a sequence of probabilities. Each
     * individual's probability is proportional to its fitness, with adjustments made to handle cases where fitness
     * values might be negative, zero, or non-finite (NaN or Infinity). The method ensures that all probabilities sum to
     * 1, or assigns equal probability if the total fitness is not valid.
     *
     * @param population The population of individuals from which to select.
     * @param ranker     The `IndividualRanker` used to evaluate and compare individuals.
     * @return A sequence of probabilities corresponding to each individual in the population.
     */
    def calculateProbabilities(population: Population[T, F, R], ranker: IndividualRanker[T, F, R])
        (using equalityThreshold: Double): Seq[Double] = {
        val fitnessValues = ranker.fitnessTransform(population.fitness)
        val adjustedFitness = (fitnessValues sub scala.math.min(fitnessValues.min, 0.0)).toBuffer
        val totalFitness = adjustedFitness.sum
        if (totalFitness.isNaN || totalFitness.isInfinity || totalFitness =~ 0.0) {
            Seq.fill(population.size)(1.0 / population.size)
        } else {
            for (case (_, i) <- adjustedFitness.zipWithIndex) {
                adjustedFitness(i) = adjustedFitness(i) / totalFitness
            }
            adjustedFitness.toSeq
        }
    }

    /** Selects individuals from the population based on the roulette wheel selection mechanism.
     *
     * The `select` method chooses a specified number of individuals from the population according to the probabilities
     * computed by the `probabilities` method. If the `sorted` parameter is set to `Sorted`, the population is first
     * sorted based on fitness before selection.
     *
     * @param population The population of individuals from which to select.
     * @param outputSize The number of individuals to select.
     * @param ranker     The `IndividualRanker` used to evaluate and compare individuals.
     * @return A `Population[T, F, R]` containing the selected individuals.
     */
    override def select(
        population: Population[T, F, R], outputSize: Int,
        ranker: IndividualRanker[T, F, R]
    )(using random: Random, equalityThreshold: Double): Population[T, F, R] = {
        val pop = sorted match {
            case Unsorted => population
            case _ => ranker.sort(population)
        }
        val probabilities = calculateProbabilities(pop, ranker).incremental
        (0 until outputSize).map { _ =>
            val r = random.nextDouble()
            val index = probabilities.indexWhere(_ > r)
            pop(index)
        }
    }
}
