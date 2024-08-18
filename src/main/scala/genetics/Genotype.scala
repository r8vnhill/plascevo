package cl.ravenhill.plascevo
package genetics

import genetics.chromosomes.{Chromosome, ChromosomeBuilder}
import genetics.genes.Gene
import repr.Representation

/** A case class that represents a genotype in a genetic algorithm.
 *
 * The `Genotype` class extends the `Representation` trait, encapsulating a sequence of chromosomes. It provides
 * various functionalities, including flattening the genotype into a sequence of values, verifying the integrity of the
 * chromosomes, and folding operations over the values in the genotype. The class also supports custom string
 * representations based on the current `Domain.toStringMode`.
 *
 * @param chromosomes The sequence of chromosomes that make up the genotype.
 * @tparam T The type of value stored by the genes within the chromosomes.
 * @tparam G The type of gene that the chromosomes hold, which must extend [[Gene]].
 */
case class Genotype[T, G <: Gene[T, G]](chromosomes: Seq[Chromosome[T, G]]) extends Representation[T, G] {

    /** Returns the number of chromosomes in the genotype.
     *
     * The size is defined as the number of chromosomes contained within the genotype.
     *
     * @return The number of chromosomes in the genotype.
     */
    override def size: Int = chromosomes.size

    /** Flattens the genotype into a sequence of values.
     *
     * This method returns a sequence of values by flattening all the chromosomes within the genotype. Each chromosome's
     * values are included in the resulting sequence.
     *
     * @return A sequence containing the values of all genes in the genotype.
     */
    override def flatten(): Seq[T] = chromosomes.flatMap(_.flatten())

    /** Verifies the integrity of the genotype.
     *
     * This method checks that all chromosomes within the genotype are valid by verifying each chromosome. A genotype is
     * considered valid if all of its chromosomes are valid.
     *
     * @return `true` if all chromosomes are valid, `false` otherwise.
     */
    override def verify(): Boolean = chromosomes.forall(_.verify())

    /** Retrieves the chromosome at the specified index.
     *
     * @param index The index of the chromosome to retrieve.
     * @return The chromosome at the specified index.
     */
    def apply(index: Int): Chromosome[T, G] = chromosomes(index)

    /** Folds the values of the genotype from the left.
     *
     * This method flattens the genotype into a sequence of values and then applies a binary operation sequentially
     * from the left (beginning) of the sequence to the right (end), combining the values into a single result.
     *
     * @param initial The initial value to start the folding operation.
     * @param f       A binary operation that takes the current accumulated value and the next value, and returns the
     *                new accumulated value.
     * @tparam U The type of the accumulated value and the result.
     * @return The result of folding the values of the genotype from the left.
     */
    override def foldLeft[U](initial: U)(f: (U, T) => U): U = flatten().foldLeft(initial)(f)

    /** Folds the values of the genotype from the right.
     *
     * This method flattens the genotype into a sequence of values and then applies a binary operation sequentially
     * from the right (end) of the sequence to the left (beginning), combining the values into a single result.
     *
     * @param initial The initial value to start the folding operation.
     * @param f       A binary operation that takes the next value and the current accumulated value, and returns the
     *                new accumulated value.
     * @tparam U The type of the accumulated value and the result.
     * @return The result of folding the values of the genotype from the right.
     */
    override def foldRight[U](initial: U)(f: (T, U) => U): U = flatten().foldRight(initial)(f)

    /** Returns a sequence of chromosomes paired with their corresponding indices.
     *
     * The `zipWithIndex` method pairs each chromosome in the genotype with its corresponding index in the sequence.
     * This can be useful for iterating over chromosomes when both the chromosome and its position in the genotype are
     * needed.
     *
     * @return A sequence of tuples, where each tuple contains a `Chromosome[T, G]` and its corresponding index as an
     *         `Int`.
     *
     * <h3>Example:</h3>
     * @example
     * {{{
     * val chromosomes = genotype.zipWithIndex
     * // Example result: Seq((chromosome1, 0), (chromosome2, 1), (chromosome3, 2))
     * }}}
     */
    def zipWithIndex: Seq[(Chromosome[T, G], Int)] = chromosomes.zipWithIndex

    /** Returns a string representation of the genotype.
     *
     * The format of the string is determined by the current [[Domain.toStringMode]]:
     * - `ToStringMode.SIMPLE`: A comma-separated list of chromosomes enclosed in square brackets.
     * - `ToStringMode.DEFAULT`: A detailed string representation including the name and chromosomes.
     *
     * @return The string representation of the genotype.
     */
    override def toString: String = Domain.toStringMode match
        case ToStringMode.SIMPLE => chromosomes.mkString("[", ", ", "]")
        case _ => s"Genotype(chromosomes=$chromosomes)"
}

/** Companion object for the `Genotype` class, providing factory methods for creating genotype builders.
 *
 * The `Genotype` object includes methods that facilitate the creation of `GenotypeBuilder` instances by specifying the
 * chromosomes that make up the genotype. This allows for a flexible and fluent interface when constructing genotypes
 * for use in genetic algorithms.
 */
object Genotype {

    /** Creates a `GenotypeBuilder` initialized with the specified chromosome builders.
     *
     * The `of` method constructs a `GenotypeBuilder` and initializes it with the provided `ChromosomeBuilder`
     * instances. This method allows users to specify the chromosomes that will be part of the genotype, providing a
     * flexible way to define the structure of the genotype before it is built.
     *
     * @param chromosomes A variable number of `ChromosomeBuilder` instances that define the chromosomes of the
     *                    genotype.
     * @tparam T The type of value stored by the genes in the chromosomes.
     * @tparam G The type of gene contained within the chromosomes, which must extend [[Gene]].
     * @return A `GenotypeBuilder[T, G]` initialized with the specified chromosomes.
     *
     * <h3>Example:</h3>
     * @example
     * {{{
     * val chromosome1 = new BooleanChromosomeBuilder().withSize(10)
     * val chromosome2 = new BooleanChromosomeBuilder().withSize(15)
     * val genotypeBuilder = Genotype.of(chromosome1, chromosome2)
     * val genotype = genotypeBuilder.build()
     * }}}
     */
    def of[T, G <: Gene[T, G]](chromosomes: ChromosomeBuilder[T, G]*): GenotypeBuilder[T, G] = {
        val builder = GenotypeBuilder[T, G]()
        chromosomes.foreach(builder.addChromosome)
        builder
    }
}
