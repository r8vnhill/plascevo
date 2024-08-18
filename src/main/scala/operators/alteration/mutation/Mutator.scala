package cl.ravenhill.plascevo
package operators.alteration.mutation

import evolution.states.EvolutionState
import genetics.Genotype
import genetics.chromosomes.Chromosome
import genetics.genes.Gene
import operators.alteration.Alterer
import utils.===

/** A trait representing a mutator in a genetic algorithm, responsible for mutating individuals and chromosomes.
 *
 * The `Mutator` trait extends the `Alterer` trait and provides functionality to mutate individuals and chromosomes
 * within a genetic algorithm. The mutation process is controlled by the `individualRate` and `chromosomeRate`,
 * which determine the probability of mutating an individual or a chromosome, respectively.
 * 
 * @tparam T The type of value stored by the gene.
 * @tparam G The type of gene contained within the genotype, which must extend [[Gene]].
 */
trait Mutator[T, G <: Gene[T, G]] extends Alterer[T, G, Genotype[T, G]] {

    /**
     * The probability of mutating an individual within the population. Implementers must ensure that this value is
     * between 0.0 and 1.0.
     */
    val individualRate: Double
    
    /**
     * The probability of mutating a chromosome within an individual. Implementers must ensure that this value is
     * between 0.0 and 1.0.
     */
    val chromosomeRate: Double
    
    /** Applies mutation to the population during the evolutionary process.
     *
     * The `apply` method is responsible for mutating individuals in the population based on the specified
     * `individualRate`. It ensures that the output size matches the population size and performs mutations on
     * individuals and chromosomes as dictated by the mutation rates.
     *
     * @param state      The current evolutionary state, containing the population to be mutated.
     * @param outputSize The size of the output population after mutation, which must match the current population size.
     * @param buildState A function to build the new evolutionary state from the mutated population.
     * @return A new evolutionary state with the mutated population.
     * @throws IllegalArgumentException if `outputSize` does not match the population size.
     *
     *                                  <h3>Example:</h3>
     * @example
     * {{{
     * val mutator = new CustomMutator[Double, SimpleGene](individualRate = 0.1, chromosomeRate = 0.05)
     * val newState = mutator.apply(currentState, outputSize = currentState.population.size, newStateBuilder)
     * }}}
     */
    override def apply[S <: EvolutionState[T, G, Genotype[T, G], S]](
        state: S,
        outputSize: Int,
        buildState: Seq[Individual[T, G, Genotype[T, G]]] => S
    ): S = {
        require(outputSize == state.population.size, "Output size must match the population size.")
        if (individualRate === 0) {
            state
        } else {
            state.map(
                if (Domain.random.nextDouble() > individualRate) then identity else mutateIndividual
            )
        }
    }

    /** Mutates an individual by potentially mutating each chromosome based on the `chromosomeRate`.
     *
     * The `mutateIndividual` method applies the `mutateChromosome` method to each chromosome within the individual with
     * a probability defined by `chromosomeRate`. If a chromosome is not mutated, it is left unchanged.
     *
     * @param individual The individual to be mutated.
     * @return A new `Individual` instance with potentially mutated chromosomes.
     */
    def mutateIndividual(individual: Individual[T, G, Genotype[T, G]]): Individual[T, G, Genotype[T, G]] =
        Individual(
            Genotype(
                individual.representation.chromosomes.map(
                    if (Domain.random.nextDouble() > chromosomeRate) then identity else mutateChromosome
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
    def mutateChromosome(chromosome: Chromosome[T, G]): Chromosome[T, G]
}
