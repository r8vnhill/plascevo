/*
 * Copyright (c) 2024, Ignacio Slater M.
 * 2-Clause BSD License.
 */

package cl.ravenhill.plascevo
package operators.crossover

import utils.Exclusivity
import utils.Exclusivity.NonExclusive

import cl.ravenhill.plascevo.genetics.chromosomes.Chromosome
import cl.ravenhill.plascevo.genetics.genes.Gene

import scala.util.Random

/** A crossover operator that combines genes from multiple parents using a specified combiner function.
 *
 * The `CombineCrossover` class implements a crossover strategy where genes from multiple parent chromosomes are
 * combined to produce a single offspring chromosome. The combination of genes is controlled by a custom combiner
 * function and probabilities for chromosome and gene selection. The number of parents and the exclusivity of selection
 * are also configurable.
 *
 * @param combiner       The function used to combine genes from the parent chromosomes.
 * @param chromosomeRate The probability of selecting a chromosome from the parent chromosomes for recombination. Must
 *                       be in the range `[0.0, 1.0]`. Defaults to [[CombineCrossover.defaultChromosomeRate]].
 * @param geneRate       The probability of selecting a gene from the parent chromosomes for recombination. Must be in
 *                       the range `[0.0, 1.0]`. Defaults to `CombineCrossover.defaultGeneRate`.
 * @param numParents     The number of parents involved in the crossover operation. Must be greater than or equal to 2.
 *                       Defaults to `CombineCrossover.defaultNumParents`.
 * @param exclusivity    The exclusivity rule applied when selecting parents for crossover. Defaults to
 *                       `CombineCrossover.defaultExclusivity`.
 * @tparam T The type of value stored by the genes.
 * @tparam G The type of gene contained within the genotype, which must extend [[Gene]].
 * @throws IllegalArgumentException if `chromosomeRate` or `geneRate` are outside the range `[0.0, 1.0]`, or if
 *                                  `numParents` is less than 2.
 *
 *                                  <h3>Example:</h3>
 * @example
 * {{{
 * val combiner: Seq[SimpleGene] => SimpleGene = genes => SimpleGene(genes.map(_.value).sum / genes.size)
 * val crossover = new CombineCrossover[Double, SimpleGene](
 *   combiner = combiner,
 *   chromosomeRate = 0.8,
 *   geneRate = 0.5,
 *   numParents = 3,
 *   exclusivity = Exclusivity.Exclusive
 * )
 * val offspring = crossover.crossoverChromosomes(parentChromosomes)
 * }}}
 */
class CombineCrossover[T, G <: Gene[T, G]](
    val combiner: Seq[G] => G,
    override val chromosomeRate: Double = CombineCrossover.defaultChromosomeRate,
    val geneRate: Double = CombineCrossover.defaultGeneRate,
    override val numParents: Int = CombineCrossover.defaultNumParents,
    override val exclusivity: Exclusivity = CombineCrossover.defaultExclusivity
) extends Crossover[T, G] {

    /** The number of offspring produced by this crossover operation. */
    override val numOffspring: Int = 1

    require(
        chromosomeRate >= 0.0 && chromosomeRate <= 1.0,
        s"The chromosome rate ($chromosomeRate) must be in the range [0.0, 1.0]"
    )
    require(
        geneRate >= 0.0 && geneRate <= 1.0,
        s"The gene rate ($geneRate) must be in the range [0.0, 1.0]"
    )
    require(
        numParents >= 2,
        s"The number of parents ($numParents) must be greater than or equal to 2"
    )

    /** Recombines the chromosomes from the selected parents to produce offspring chromosomes.
     *
     * The `crossoverChromosomes` method combines genes from the parent chromosomes using the specified `combiner`
     * function. The method selects chromosomes and genes based on the `chromosomeRate` and `geneRate` probabilities.
     * The result is a sequence of offspring chromosomes, where each chromosome is created by combining genes from the
     * parents.
     *
     * @param chromosomes The sequence of parent chromosomes to be recombined.
     * @param random      An implicit `Random` instance used for random selections during the crossover process.
     * @return A sequence of offspring chromosomes produced by combining the parent chromosomes.
     * @throws IllegalArgumentException if the number of chromosomes does not match `numParents` or if the chromosomes
     *                                  do not have the same length.
     */
    override def crossoverChromosomes(chromosomes: Seq[Chromosome[T, G]])(using random: Random): Seq[Chromosome[T, G]] =
        Seq(chromosomes.head.duplicateWithGenes(combine(chromosomes)))

    /** Combines the genes from the selected parent chromosomes.
     *
     * The `combine` method takes genes from the parent chromosomes and combines them using the specified `combiner`
     * function. The selection of genes is controlled by the `geneRate` probability. This method ensures that the
     * resulting offspring chromosome contains the combined genetic material from the parents.
     *
     * @param chromosomes The sequence of parent chromosomes from which genes are to be combined.
     * @param random      An implicit `Random` instance used for random selections during the gene combination process.
     * @return A sequence of genes representing the combined genetic material of the parent chromosomes.
     * @throws IllegalArgumentException if the number of parent chromosomes does not match `numParents` or if the
     *                                  chromosomes do not have the same length.
     */
    private def combine(chromosomes: Seq[Chromosome[T, G]])(using random: Random): Seq[G] = {
        require(
            chromosomes.size == numParents,
            s"Number of inputs (${chromosomes.size}) must equal the number of parents ($numParents)"
        )
        require(chromosomes.map(_.size).toSet.size == 1, "All chromosomes must have the same length")

        List.tabulate(chromosomes.head.size) { i =>
            if (random.nextDouble() < geneRate) {
                combiner(chromosomes.map(chromosome => chromosome(i)))
            } else {
                chromosomes.head(i)
            }
        }
    }
}

/** Companion object for `CombineCrossover` providing default values for crossover parameters.
 *
 * The `CombineCrossover` object defines default values for chromosome rate, gene rate, number of parents, and
 * exclusivity. These defaults can be used when creating instances of `CombineCrossover` without specifying custom
 * values.
 */
object CombineCrossover {
    /** The default probability of selecting a chromosome from the parent chromosomes for recombination. */
    val defaultChromosomeRate: Double = 1.0

    /** The default probability of selecting a gene from the parent chromosomes for recombination. */
    val defaultGeneRate: Double = 1.0

    /** The default number of parents involved in the crossover operation. */
    val defaultNumParents: Int = 2

    /** The default exclusivity rule applied when selecting parents for crossover. */
    val defaultExclusivity: Exclusivity = NonExclusive
}
