/*
 * Copyright (c) 2024, Ignacio Slater M.
 * 2-Clause BSD License.
 */

package cl.ravenhill.plascevo
package operators.crossover

import utils.Exclusivity

import cl.ravenhill.plascevo.genetics.genes.Gene

import scala.util.Random

/** A crossover operator that selects genes uniformly at random from multiple parents.
 *
 * The `UniformCrossover` class extends the `CombineCrossover` trait and implements a crossover strategy where each gene
 * in the offspring is selected uniformly at random from the corresponding genes in the parent chromosomes. This method
 * allows for a high degree of genetic diversity in the offspring by randomly selecting genetic material from multiple
 * parents.
 *
 * @param numParents The number of parents involved in the crossover operation. Defaults to
 *                   `UniformCrossover.defaultNumParents`.
 * @param chromosomeRate The probability of selecting a chromosome from the parent chromosomes for recombination. Must
 *                       be in the range `[0.0, 1.0]`. Defaults to `UniformCrossover.defaultChromosomeRate`.
 * @param exclusivity The exclusivity rule applied when selecting parents for crossover. Defaults to
 *                    `UniformCrossover.defaultExclusivity`.
 * @param random An implicit `Random` instance used for random selection during the crossover process.
 * @tparam T The type of value stored by the genes.
 * @tparam G The type of gene contained within the genotype, which must extend [[Gene]].
 *
 * <h3>Example:</h3>
 * @example
 * {{{
 * given random: Random = new Random()
 * val crossover = new UniformCrossover[Double, SimpleGene](
 *   numParents = 3,
 *   chromosomeRate = 0.7,
 *   exclusivity = Exclusivity.Exclusive
 * )
 * val offspring = crossover.crossoverChromosomes(parentChromosomes)
 * }}}
 */
class UniformCrossover[T, G <: Gene[T, G]](
    numParents: Int = UniformCrossover.defaultNumParents,
    chromosomeRate: Double = UniformCrossover.defaultChromosomeRate,
    exclusivity: Exclusivity = UniformCrossover.defaultExclusivity
)(using random: Random) extends CombineCrossover[T, G](
    combiner = { genes => genes(random.nextInt(genes.size)) },
    chromosomeRate = chromosomeRate,
    geneRate = 1,
    numParents = numParents,
    exclusivity = exclusivity
)

/** Companion object for `UniformCrossover` providing default values for crossover parameters.
 *
 * The `UniformCrossover` object defines default values for the number of parents, chromosome rate, and exclusivity.
 * These defaults can be used when creating instances of `UniformCrossover` without specifying custom values.
 */
object UniformCrossover {

    /** The default number of parents involved in the crossover operation. */
    val defaultNumParents: Int = 2

    /** The default probability of selecting a chromosome from the parent chromosomes for recombination. */
    val defaultChromosomeRate: Double = 0.5

    /** The default exclusivity rule applied when selecting parents for crossover. */
    val defaultExclusivity: Exclusivity = Exclusivity.NonExclusive
}
