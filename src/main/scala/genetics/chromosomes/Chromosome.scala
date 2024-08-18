package cl.ravenhill.plascevo
package genetics.chromosomes

import genetics.genes.Gene
import mixins.Mappable
import repr.Representation

/** A trait that represents a chromosome in a genetic algorithm.
 *
 * The `Chromosome` trait extends the `Representation` trait, encapsulating a sequence of genes. It provides
 * functionality for verifying the integrity of the chromosome, flattening the genes into a sequence of values, and
 * determining the size of the chromosome.
 *
 * @tparam T The type of value stored by the genes.
 * @tparam G The type of gene that the chromosome holds, which must extend [[Gene]].
 */
trait Chromosome[T, G <: Gene[T, G]] extends Representation[T, G] with Mappable[G] {

    /** The sequence of genes that make up the chromosome. */
    val genes: Seq[G]
    
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

    /** Folds the values of the chromosome's genes from the right.
     *
     * This method applies a binary operation to the values of the genes and an initial value, effectively folding the
     * values from the right. The operation is applied sequentially from the last gene to the first.
     *
     * @param initial The initial value to start the folding operation.
     * @param f       A binary operation that takes a gene's value and the current accumulated value, and returns the
     *                new accumulated value.
     * @tparam U The type of the accumulated value and the result.
     * @return The result of folding the values of the chromosome's genes from the right.
     */
    override def foldRight[U](initial: U)(f: (T, U) => U): U =
        genes.foldRight(initial)((gene, acc) => gene.foldRight(acc)(f))

    /** Folds the values of the chromosome's genes from the left.
     *
     * This method applies a binary operation to an initial value and the values of the genes, effectively folding the
     * values from the left. The operation is applied sequentially from the first gene to the last.
     *
     * @param initial The initial value to start the folding operation.
     * @param f       A binary operation that takes the current accumulated value and a gene's value, and returns the
     *                new accumulated value.
     * @tparam U The type of the accumulated value and the result.
     * @return The result of folding the values of the chromosome's genes from the left.
     */
    override def foldLeft[U](initial: U)(f: (U, T) => U): U =
        genes.foldLeft(initial)((acc, gene) => gene.foldLeft(acc)(f))

    /** Applies a transformation function to each gene in the chromosome.
     *
     * The `map` method applies the provided transformation function `f` to each gene in the `genes` sequence of the
     * chromosome. It returns a sequence of results produced by applying the function to each gene.
     *
     * @param f The transformation function to apply to each gene. The function takes a gene of type `G` and returns a
     *          value of type `U`.
     * @tparam U The type of elements in the resulting sequence.
     * @return A sequence of elements of type `U` resulting from applying the transformation function to each gene in
     *         the chromosome.
     */
    override def map[U](f: G => U): Seq[U] = genes.map(f)

    /** Creates a new chromosome with the specified sequence of genes.
     *
     * The `duplicateWithGenes` method creates a new instance of a chromosome using the provided sequence of genes. This
     * method is useful for creating modified versions of an existing chromosome while preserving its other attributes.
     *
     * @param genes The sequence of genes to be included in the new chromosome.
     * @return A new `Chromosome[T, G]` instance containing the specified genes.
     *
     *         <h3>Example:</h3>
     * @example
     * {{{
     * val newGenes = Seq(gene1, gene2, gene3)
     * val newChromosome = chromosome.duplicateWithGenes(newGenes)
     * }}}
     */
    def duplicateWithGenes(genes: Seq[G]): Chromosome[T, G]

    /** Retrieves the gene at the specified index in the chromosome.
     *
     * The `apply` method provides access to the gene at a given index within the chromosome. This allows for easy
     * retrieval of specific genes by their position in the sequence.
     *
     * @param index The index of the gene to retrieve.
     * @return The gene at the specified index in the chromosome.
     * @throws IndexOutOfBoundsException if the index is out of range (i.e., `index < 0` or `index >= genes.size`).
     *
     *                                   <h3>Example:</h3>
     * @example
     * {{{
     * val gene = chromosome(1)
     * // Example result: gene2 (the gene at index 1)
     * }}}
     */
    def apply(index: Int): G = genes(index)
}
