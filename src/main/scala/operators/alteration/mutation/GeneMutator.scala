package cl.ravenhill.plascevo
package operators.alteration.mutation

import genetics.genes.Gene

/** A trait representing a mutator for genes in a genetic algorithm.
 *
 * The `GeneMutator` trait extends the `Mutator` trait and focuses specifically on mutating individual genes within a
 * genetic algorithm. It introduces an additional `geneRate` parameter, which determines the probability of mutating
 * each gene within a chromosome.
 *
 * @tparam T The type of value stored by the gene.
 * @tparam G The type of gene contained within the genotype, which must extend [[Gene]].
 */
trait GeneMutator[T, G <: Gene[T, G]] extends Mutator[T, G] {

    /** The probability of mutating an individual gene within a chromosome.
     *
     * Implementers must ensure that this value is between 0.0 and 1.0.
     */
    val geneRate: Double

    /** Mutates an individual gene.
     *
     * The `mutateGene` method defines how a gene is mutated. This method must be implemented by concrete classes that
     * extend the `GeneMutator` trait. The mutation process is applied to each gene with a probability defined by
     * `geneRate`.
     *
     * @param gene The gene to be mutated.
     * @return A new instance of the gene after mutation.
     */
    def mutateGene(gene: G): G
}
