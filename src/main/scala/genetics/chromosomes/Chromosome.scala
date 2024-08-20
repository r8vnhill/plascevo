package cl.ravenhill.plascevo
package genetics.chromosomes

import genetics.genes.Gene
import mixins.Mappable
import repr.Representation

/** Represents a chromosome in an evolutionary algorithm.
 *
 * A `Chromosome` is a specialized [[Representation]] that encapsulates a sequence of genes, where each gene represents
 * a distinct feature or component of the chromosome. The `Chromosome` trait provides methods for accessing and
 * manipulating the genes, as well as for verifying the chromosome's integrity, flattening its structure, and folding
 * its features into a single result.
 *
 * @tparam T The type of value stored by the genes within the chromosome.
 * @tparam G The type of gene contained in the chromosome, which must extend [[Gene]].
 *
 * @example
 * {{{
 * // Example implementation of a binary chromosome with Boolean genes
 * case class BinaryChromosome(genes: Seq[BinaryGene]) extends Chromosome[Boolean, BinaryGene] {
 *   override def duplicateWithGenes(genes: Seq[BinaryGene]): BinaryChromosome = BinaryChromosome(genes)
 * }
 *
 * val chromosome = BinaryChromosome(Seq(BinaryGene(true), BinaryGene(false), BinaryGene(true)))
 * val flattened = chromosome.flatten()
 * // flattened: Seq[Boolean] = Seq(true, false, true)
 * }}}
 */
trait Chromosome[T, G <: Gene[T, G]] extends Representation[T, G] {

    /** Returns the sequence of genes that make up the chromosome.
     *
     * @return A sequence of genes that comprise the chromosome.
     */
    def genes: Seq[G]

    /** Returns the size of the chromosome, which is the number of genes it contains.
     *
     * @return The number of genes in the chromosome.
     */
    override def size: Int = genes.size

    /** Flattens the chromosome into a sequence of values by flattening each gene it contains.
     *
     * @return A sequence containing all the values from the genes in the chromosome.
     */
    override def flatten(): Seq[T] = genes.flatMap(_.flatten())

    /** Verifies the integrity of the chromosome by ensuring that all genes are valid.
     *
     * @return `true` if all genes in the chromosome are valid, `false` otherwise.
     */
    override def verify(): Boolean = genes.forall(_.verify())

    /** Folds the genes of the chromosome from the right, combining them with an initial value.
     *
     * The `foldRight` method processes the genes from the last gene to the first, applying the binary operation `f`
     * to combine each gene's value with the accumulated result. This is particularly useful when the order of
     * processing matters, such as when constructing a result that depends on the genes being combined in reverse order.
     *
     * @param initial The initial value to start the folding operation.
     * @param f       A binary operation that takes the current gene value and the accumulated result, and returns the
     *                new accumulated value.
     * @tparam U The type of the accumulated value and the final result.
     * @return The final result of folding the genes from the right.
     *
     * @example
     * {{{
     * // Example: Using foldRight to concatenate the string values of genes in reverse order
     * case class StringGene(value: String) extends Gene[String, StringGene] {
     *   override def duplicateWithValue(value: String): StringGene = StringGene(value)
     *   override def flatten(): Seq[String] = Seq(value)
     *   override def foldRight[U](initial: U)(f: (String, U) => U): U = f(value, initial)
     *   override def foldLeft[U](initial: U)(f: (U, String) => U): U = f(initial, value)
     * }
     *
     * val chromosome = new Chromosome[String, StringGene] {
     *   override def genes: Seq[StringGene] = Seq(StringGene("A"), StringGene("B"), StringGene("C"))
     *   override def duplicateWithGenes(genes: Seq[StringGene]): Chromosome[String, StringGene] = this
     * }
     *
     * val concatenated = chromosome.foldRight("")(_ + _)
     * // concatenated: String = "CBA"
     * }}}
     */
    override def foldRight[U](initial: U)(f: (T, U) => U): U =
        genes.foldRight(initial)((gene, acc) => gene.foldRight(acc)(f))


    /** Folds the genes of the chromosome from the left, combining them with an initial value.
     *
     * The `foldLeft` method processes the genes from the first gene to the last, applying the binary operation `f`
     * to combine the accumulated result with each gene's value. This approach is useful when the order of operations
     * should be preserved, such as when accumulating values sequentially or building up a result incrementally.
     *
     * @param initial The initial value to start the folding operation.
     * @param f       A binary operation that takes the current accumulated value and the next gene's value, and
     *                returns the updated accumulated value.
     * @tparam U The type of the accumulated value and the final result.
     * @return The final result of folding the genes from the left.
     *
     * @example
     * {{{
     * // Example: Using foldLeft to concatenate the string values of genes in order
     * case class StringGene(value: String) extends Gene[String, StringGene] {
     *   override def duplicateWithValue(value: String): StringGene = StringGene(value)
     *   override def flatten(): Seq[String] = Seq(value)
     *   override def foldRight[U](initial: U)(f: (String, U) => U): U = f(value, initial)
     *   override def foldLeft[U](initial: U)(f: (U, String) => U): U = f(initial, value)
     * }
     *
     * val chromosome = new Chromosome[String, StringGene] {
     *   override def genes: Seq[StringGene] = Seq(StringGene("A"), StringGene("B"), StringGene("C"))
     *   override def duplicateWithGenes(genes: Seq[StringGene]): Chromosome[String, StringGene] = this
     * }
     *
     * val concatenated = chromosome.foldLeft("")(_ + _)
     * // concatenated: String = "ABC"
     * }}}
     */
    override def foldLeft[U](initial: U)(f: (U, T) => U): U =
        genes.foldLeft(initial)((acc, gene) => gene.foldLeft(acc)(f))

    /** Maps the genes of the chromosome to a new chromosome using the provided function.
     *
     * The `map` method applies the function `f` to each gene in the chromosome, producing a new chromosome with the
     * resulting genes. This is useful when you need to transform or mutate the genes within a chromosome, such as
     * applying a mutation function or modifying gene values.
     *
     * @param f A function that transforms each gene into a new gene.
     * @return A new chromosome containing the transformed genes.
     *
     * @example
     * {{{
     * // Example: Applying a function to increase the value of each gene in the chromosome by 1
     * case class IntGene(value: Int) extends Gene[Int, IntGene] {
     *   override def duplicateWithValue(value: Int): IntGene = IntGene(value)
     *   override def flatten(): Seq[Int] = Seq(value)
     *   override def foldRight[U](initial: U)(f: (Int, U) => U): U = f(value, initial)
     *   override def foldLeft[U](initial: U)(f: (U, Int) => U): U = f(initial, value)
     * }
     *
     * val chromosome = new Chromosome[Int, IntGene] {
     *   override def genes: Seq[IntGene] = Seq(IntGene(1), IntGene(2), IntGene(3))
     *   override def duplicateWithGenes(genes: Seq[IntGene]): Chromosome[Int, IntGene] = this
     * }
     *
     * val incrementedChromosome = chromosome.map(gene => gene.duplicateWithValue(gene.value + 1))
     * // incrementedChromosome contains genes with values [2, 3, 4]
     * }}}
     */
    def map(f: G => G): Chromosome[T, G] = duplicateWithGenes(genes.map(f))


    /** Creates a duplicate of the chromosome with the specified sequence of genes.
     *
     * The `duplicateWithGenes` method must be implemented by subclasses to return a new instance of the chromosome
     * with the provided genes.
     *
     * @param genes The sequence of genes for the new chromosome.
     * @return A new chromosome with the specified genes.
     */
    def duplicateWithGenes(genes: Seq[G]): Chromosome[T, G]

    /** Accesses the gene at the specified index.
     *
     * @param index The index of the gene to retrieve.
     * @return The gene at the specified index.
     */
    def apply(index: Int): G = genes(index)
}
