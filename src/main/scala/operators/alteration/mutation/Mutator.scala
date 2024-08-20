package cl.ravenhill.plascevo
package operators.alteration.mutation

import evolution.states.EvolutionState
import genetics.Genotype
import genetics.chromosomes.Chromosome
import genetics.genes.Gene
import operators.alteration.Alterer
import utils.Numeric.=~

import scala.util.Random

/** Represents a mutator in an evolutionary algorithm.
 *
 * A `Mutator` is a specialized alterer used in evolutionary algorithms to introduce random variations in individuals
 * by mutating their chromosomes. This process is essential for maintaining diversity within the population, allowing
 * the algorithm to explore the search space more effectively. The `Mutator` trait extends the [[Alterer]] trait and
 * provides mechanisms for mutating individuals and their chromosomes based on specified mutation rates.
 *
 * @tparam T The type of value stored by the features within the individual.
 * @tparam G The type of gene contained within the genotype, which must extend [[Gene]].
 *
 * @example
 * {{{
 * // Example usage of a simple mutator that flips bits in a binary chromosome
 * class SimpleBitFlipMutator extends Mutator[Boolean, SimpleGene] {
 *   override val individualRate: Double = 0.1
 *   override val chromosomeRate: Double = 0.05
 *
 *   override def mutateChromosome(chromosome: Chromosome[Boolean, SimpleGene])
 *       (using random: Random): Chromosome[Boolean, SimpleGene] = {
 *     chromosome.duplicateWithGenes(
 *       chromosome.genes.map { gene =>
 *         if (random.nextDouble() < chromosomeRate) gene.duplicateWithValue(!gene.value) else gene
 *       }
 *     )
 *   }
 * }
 * }}}
 */
trait Mutator[T, G <: Gene[T, G]] extends Alterer[T, G, Genotype[T, G]] {

    /** The probability of mutating an individual within the population.
     *
     * The `individualRate` defines how likely it is for each individual to be selected for mutation during the
     * evolutionary process. A higher value increases the chances of mutation, whereas a lower value reduces it.
     */
    val individualRate: Double

    /** The probability of mutating a chromosome within an individual.
     *
     * The `chromosomeRate` defines the likelihood that a specific chromosome within an individual will be mutated.
     * This value is used to determine which chromosomes are altered during the mutation process.
     */
    val chromosomeRate: Double

    /** Applies the mutation operator to the current evolutionary state, producing a new state with mutated individuals.
     *
     * The `apply` method is responsible for mutating individuals in the population based on the specified
     * `individualRate`. It ensures that the output size matches the population size and applies the mutation process to
     * each individual and their chromosomes as dictated by the mutation rates.
     *
     * @param state      The current evolutionary state containing the population of individuals.
     * @param outputSize The size of the output population after mutation, which must match the current population size.
     * @param buildState A function to build the new evolutionary state from the mutated population.
     * @param random An implicit random number generator used for stochastic operations.
     * @param equalityThreshold An implicit threshold for determining equality between features.
     * @tparam S The type of the evolutionary state, which must extend [[EvolutionState]].
     * @return A new evolutionary state with the mutated population.
     * @throws IllegalArgumentException if `outputSize` does not match the population size.
     */
    final override def apply[S <: EvolutionState[T, G, Genotype[T, G], S]](
        state: S,
        outputSize: Int,
        buildState: Seq[Individual[T, G, Genotype[T, G]]] => S
    )(using random: Random, equalityThreshold: Double): S = {
        require(outputSize == state.population.size, "Output size must match the population size.")
        if (individualRate =~ 0) {
            state
        } else {
            state.map(
                if (random.nextDouble() > individualRate) then identity else mutateIndividual
            )
        }
    }

    /** Mutates an individual by potentially mutating each chromosome based on the `chromosomeRate`.
     *
     * The `mutateIndividual` method applies the `mutateChromosome` method to each chromosome within the individual
     * with a probability defined by `chromosomeRate`. If a chromosome is not mutated, it is left unchanged.
     *
     * @param individual The individual to be mutated.
     * @return A new `Individual` instance with potentially mutated chromosomes.
     */
    final def mutateIndividual(individual: Individual[T, G, Genotype[T, G]])
        (using random: Random): Individual[T, G, Genotype[T, G]] =
        Individual(
            Genotype(
                individual.representation.chromosomes.map(
                    if (random.nextDouble() > chromosomeRate) then identity else mutateChromosome
                )
            )
        )

    /** Abstract method to define how a chromosome is mutated.
     *
     * The `mutateChromosome` method must be implemented by concrete classes extending the `Mutator` trait. It defines
     * the specific mutation process for a chromosome.
     *
     * @param chromosome The chromosome to be mutated.
     * @return A new `Chromosome[T, G]` instance representing the mutated chromosome.
     */
    def mutateChromosome(chromosome: Chromosome[T, G])(using random: Random): Chromosome[T, G]
}
