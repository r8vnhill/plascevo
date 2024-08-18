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
 * @param genes The sequence of genes that make up the chromosome.
 * @tparam T The type of value stored by the genes.
 * @tparam G The type of gene that the chromosome holds, which must extend [[Gene]].
 */
trait Chromosome[T, G <: Gene[T, G]](genes: Seq[G]) extends Representation[T, G] with Mappable[G] {

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

    def duplicateWithGenes(genes: Seq[G]): Chromosome[T, G]
}
