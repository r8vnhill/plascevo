package cl.ravenhill.plascevo
package genetics.chromosomes

import genetics.genes.Gene
import repr.Representation

/** A trait that represents a chromosome in a genetic algorithm.
 *
 * The `Chromosome` trait extends the `Representation` trait, encapsulating a sequence of genes. It provides
 * functionality for verifying the integrity of the chromosome, flattening the genes into a sequence of values, and
 * determining the size of the chromosome.
 *
 * @param genes The sequence of genes that make up the chromosome.
 * @tparam T The type of value stored by the genes.
 * @tparam G The type of gene that the chromosome holds, which must extend [[Gene]].
 */
trait Chromosome[T, G <: Gene[T, G]](val genes: Seq[Gene[T, G]]) extends Representation[T, G]:

    /** Returns the size of the chromosome.
     *
     * The size is defined as the number of genes within the chromosome.
     *
     * @return The number of genes in the chromosome.
     */
    override def size: Int = genes.size

    /** Flattens the chromosome into a sequence of values.
     *
     * This method returns a sequence of values by flattening all the genes within the chromosome. Each gene's value is
     * included in the resulting sequence.
     *
     * @return A sequence containing the values of all genes in the chromosome.
     */
    override def flatten(): Seq[T] = genes.flatMap(_.flatten())

    /** Verifies the integrity of the chromosome.
     *
     * This method checks that all genes within the chromosome are valid by verifying each gene. A chromosome is
     * considered valid if all of its genes are valid.
     *
     * @return `true` if all genes are valid, `false` otherwise.
     */
    override def verify(): Boolean = genes.forall(_.verify())
