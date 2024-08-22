package cl.ravenhill.plascevo
package operators.alteration.crossover

import evolution.states.EvolutionState
import genetics.Genotype
import genetics.chromosomes.Chromosome
import genetics.genes.Gene
import operators.alteration.Alterer
import utils.{Exclusivity, pIndices, subsets}

import scala.collection.mutable.ListBuffer
import scala.util.Random

/** A trait representing a crossover operation in a genetic algorithm.
 *
 * The `Crossover` trait extends the `Alterer` trait and defines the behavior for performing crossover operations in
 * genetic algorithms. Crossover operations combine genetic material from multiple parent genotypes to produce offspring
 * genotypes. The number of parents, offspring, and the rate at which chromosomes are exchanged between parents are
 * configurable parameters.
 *
 * @tparam T The type of value stored by the gene.
 * @tparam G The type of gene contained within the genotype, which must extend [[Gene]].
 */
trait Crossover[T, G <: Gene[T, G]] extends Alterer[T, G, Genotype[T, G]] {

    /** The number of offspring to produce from the crossover operation.
     *
     * This value defines how many offspring genotypes are generated from the crossover of parent genotypes.
     */
    val numOffspring: Int

    /** The number of parents involved in the crossover operation.
     *
     * This value defines how many parent genotypes are required for the crossover operation.
     */
    val numParents: Int

    /** The rate at which chromosomes are exchanged between parents during crossover.
     *
     * The `chromosomeRate` defines the probability of selecting a chromosome from the parent genotypes to be included
     * in the offspring. Must be a value between 0.0 and 1.0.
     */
    val chromosomeRate: Double

    /** The exclusivity rule applied when selecting parents for crossover.
     *
     * The `exclusivity` parameter defines whether the selection of parents is exclusive or non-exclusive, determining
     * whether the same parent can be selected multiple times for crossover.
     */
    val exclusivity: Exclusivity

    /** Applies the crossover operation to the population during the evolutionary process.
     *
     * The `apply` method selects subsets of individuals from the population, performs crossover operations on them, and
     * generates offspring genotypes. The number of offspring generated is determined by the `numOffspring` parameter,
     * and the method ensures that the output population size matches the specified `outputSize`.
     *
     * @param state      The current evolutionary state, containing the population to be altered.
     * @param outputSize The desired size of the output population after the crossover operation.
     * @param buildState A function to build the new evolutionary state from the recombined population.
     * @return A new evolutionary state with the recombined population.
     *
     *         <h3>Example:</h3>
     * @example
     * {{{
     * val crossover = new SimpleCrossover[Double, SimpleGene](
     *   numOffspring = 2,
     *   numParents = 2,
     *   chromosomeRate = 0.5,
     *   exclusivity = Exclusivity.Exclusive
     * )
     * val newState = crossover.apply(currentState, outputSize = 10, newStateBuilder)
     * }}}
     */
    override def apply[S <: EvolutionState[T, G, Genotype[T, G], S]](
        state: S,
        outputSize: Int,
        buildState: Seq[Individual[T, G, Genotype[T, G]]] => S
    )(using random: Random, equalityThreshold: Double): S = {

        // Select a subset of individuals to recombine using the provided probability and other parameters
        val parents = subsets(state.population, numParents, exclusivity)
        // Recombine the selected individuals to produce offspring
        val recombined = ListBuffer.empty[Individual[T, G, Genotype[T, G]]]
        while (recombined.size < outputSize) {
            val selectedParents = parents(Random.nextInt(parents.size)).map(_.representation)
            crossover(selectedParents).foreach { genotype =>
                recombined += Individual(genotype)
            }
        }

        state.withPopulation(recombined.take(outputSize).toSeq)
    }

    /** Performs the crossover operation on a sequence of parent genotypes.
     *
     * The `crossover` method takes a sequence of parent genotypes, exchanges chromosomes between them based on the
     * `chromosomeRate`, and produces a sequence of offspring genotypes. The method ensures that the number of parents
     * matches `numParents` and that all genotypes have the same number of chromosomes.
     *
     * @param parentGenotypes The sequence of parent genotypes to be recombined.
     * @param random          An implicit `Random` instance used to determine random selections during the crossover process.
     * @return A sequence of offspring genotypes produced from the crossover operation.
     * @throws IllegalArgumentException if the number of parent genotypes does not match `numParents` or if the genotypes
     *                                  do not have the same number of chromosomes.
     */
    def crossover(parentGenotypes: Seq[Genotype[T, G]])(using random: Random): Seq[Genotype[T, G]] = {
        require(
            parentGenotypes.size == numParents,
            s"The number of inputs (${parentGenotypes.size}) must be equal to the number of parents ($numParents)"
        )
        require(
            parentGenotypes.map(_.size).toSet.size == 1,
            "Genotypes must have the same number of chromosomes"
        )
        parentGenotypes.zipWithIndex.foreach { case (genotype, index) =>
            require(
                genotype.size > 0,
                s"The number of chromosomes in parent $index must be greater than 0"
            )
        }

        val parentGenotypeSize = parentGenotypes.head.size // Number of chromosomes in each parent genotype
        val chromosomeIndices = pIndices(chromosomeRate, parentGenotypeSize)

        val chromosomes = chromosomeIndices.map(index => parentGenotypes.map(_(index)))

        val offspringChromosomes = chromosomes.map(crossoverChromosomes).transpose

        offspringChromosomes.map { offspring =>
            var i = 0
            Genotype(parentGenotypes.head.zipWithIndex.map { case (chromosome, index) =>
                if (chromosomeIndices.contains(index)) {
                    val result = offspring(i)
                    i += 1
                    result
                } else {
                    chromosome
                }
            })
        }
    }

    /** Performs the crossover operation on a sequence of chromosomes.
     *
     * The `crossoverChromosomes` method takes a sequence of chromosomes from multiple parent genotypes and produces a
     * sequence of offspring chromosomes. The method is responsible for defining the specific crossover strategy used
     * to combine genetic material from the parent chromosomes.
     *
     * @param chromosomes The sequence of chromosomes to be recombined.
     * @param random      An implicit `Random` instance used to determine random selections during the crossover process.
     * @return A sequence of offspring chromosomes produced from the crossover operation.
     */
    def crossoverChromosomes(chromosomes: Seq[Chromosome[T, G]])(using random: Random): Seq[Chromosome[T, G]]
}
