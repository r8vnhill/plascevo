package cl.ravenhill.plascevo
package operators.alteration.mutation

import genetics.genes.Gene

/** A mutator class that performs bit-flip mutations on boolean genes in a genetic algorithm.
 *
 * The `BitFlipMutator` class extends the `GeneMutator` trait and provides an implementation for mutating boolean genes
 * by flipping their values (i.e., changing `true` to `false` and vice versa). The mutation process is controlled by
 * three rates: `individualRate`, `chromosomeRate`, and `geneRate`, which define the probabilities of mutating
 * individuals, chromosomes, and genes, respectively.
 *
 * @param individualRate The probability of mutating an individual within the population. Must be between 0.0 and 1.0.
 * @param chromosomeRate The probability of mutating a chromosome within an individual. Must be between 0.0 and 1.0.
 * @param geneRate The probability of mutating an individual gene within a chromosome. Must be between 0.0 and 1.0.
 * @tparam G The type of gene, which must extend [[Gene]] and hold a `Boolean` value.
 *
 * @throws IllegalArgumentException if any of the rates are outside the range of 0.0 to 1.0.
 *
 * <h2>Usage:</h2>
 *
 * <h3>Example 1: Basic Bit-Flip Mutation</h3>
 *
 * @example
 * {{{
 * val mutator = new BitFlipMutator[SimpleGene](
 *   individualRate = 0.2,
 *   chromosomeRate = 0.5,
 *   geneRate = 0.1
 * )
 * val mutatedIndividual = mutator.mutateIndividual(individual)
 * }}}
 */
class BitFlipMutator[G <: Gene[Boolean, G]](
    override val individualRate: Double = BitFlipMutator.defaultIndividualRate,
    override val chromosomeRate: Double = BitFlipMutator.defaultChromosomeRate,
    override val geneRate: Double = BitFlipMutator.defaultGeneRate
) extends GeneMutator[Boolean, G] {

    // Validates that the mutation rates are within the allowed range of 0.0 to 1.0
    require(geneRate >= 0.0 && geneRate <= 1.0, "Gene mutation rate must be between 0.0 and 1.0.")
    require(individualRate >= 0.0 && individualRate <= 1.0, "Individual mutation rate must be between 0.0 and 1.0.")
    require(chromosomeRate >= 0.0 && chromosomeRate <= 1.0, "Chromosome mutation rate must be between 0.0 and 1.0.")

    /** Mutates a boolean gene by flipping its value.
     *
     * The `mutateGene` method overrides the abstract method in `GeneMutator` and performs a bit-flip mutation,
     * which inverts the boolean value of the gene (`true` becomes `false` and `false` becomes `true`).
     *
     * @param gene The boolean gene to be mutated.
     * @return A new instance of the gene with its value flipped.
     */
    override def mutateGene(gene: G): G = gene.duplicateWithValue(!gene.value)
}

/** Companion object for the `BitFlipMutator` class, providing default mutation rates.
 *
 * The `BitFlipMutator` object defines default values for the mutation rates used in the `BitFlipMutator` class. These
 * default rates can be used as standard settings when creating instances of `BitFlipMutator` if no specific rates are
 * provided.
 */
object BitFlipMutator {

    /** The default probability of mutating an individual within the population.
     *
     * The `defaultIndividualRate` is set to 0.5, meaning there is a 50% chance that any given individual will be
     * mutated.
     */
    val defaultIndividualRate: Double = 0.5

    /** The default probability of mutating a chromosome within an individual.
     *
     * The `defaultChromosomeRate` is set to 0.5, meaning there is a 50% chance that any given chromosome within an
     * individual will be mutated.
     */
    val defaultChromosomeRate: Double = 0.5

    /** The default probability of mutating a gene within a chromosome.
     *
     * The `defaultGeneRate` is set to 0.5, meaning there is a 50% chance that any given gene within a chromosome will
     * be mutated.
     */
    val defaultGeneRate: Double = 0.5
}
