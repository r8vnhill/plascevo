package cl.ravenhill.plascevo
package genetics

import genetics.chromosomes.{Chromosome, ChromosomeBuilder}
import genetics.genes.Gene
import repr.Representation


/** Represents a genotype in an evolutionary algorithm, consisting of a sequence of chromosomes.
 *
 * The `Genotype` class encapsulates the genetic representation of an individual in an evolutionary algorithm. It is
 * composed of a sequence of chromosomes, each of which holds a collection of genes. The genotype serves as the basis
 * for operations such as mutation, crossover, and selection in genetic algorithms.
 *
 * While the constructor of the `Genotype` class is public to provide users with flexibility in how they use the
 * framework, the recommended approach for creating instances of `Genotype` is through the [[of]] factory method.
 * This factory method ensures a more structured and consistent creation process, aligning with best practices
 * within the framework.
 *
 * @param chromosomes The sequence of chromosomes that make up the genotype.
 * @tparam T The type of value stored by the genes within the chromosomes.
 * @tparam G The type of gene contained within the chromosomes, which must extend [[Gene]].
 * @example
 * {{{
 * case class IntGene(value: Int) extends Gene[Int, IntGene] {
 *   override def duplicateWithValue(value: Int): IntGene = IntGene(value)
 *   override def flatten(): Seq[Int] = Seq(value)
 *   override def foldRight[U](initial: U)(f: (Int, U) => U): U = f(value, initial)
 *   override def foldLeft[U](initial: U)(f: (U, Int) => U): U = f(initial, value)
 * }
 *
 * val chromosome1 = Chromosome(Seq(IntGene(1), IntGene(2), IntGene(3)))
 * val chromosome2 = Chromosome(Seq(IntGene(4), IntGene(5), IntGene(6)))
 * val genotype = Genotype(Seq(chromosome1, chromosome2))
 *
 * // Access the first chromosome
 * val firstChromosome = genotype(0)
 *
 * // Flatten the genotype to get a sequence of gene values
 * val flattened = genotype.flatten()
 * // flattened: Seq[Int] = Seq(1, 2, 3, 4, 5, 6)
 *
 * // Verify the integrity of the genotype
 * val isValid = genotype.verify()
 * // isValid: Boolean = true
 * }}}
 * @note Although the constructor is public, it is generally recommended to use the [[of]] factory method for creating
 *       `Genotype` instances to ensure consistent and structured creation within the framework.
 */
case class Genotype[T, G <: Gene[T, G]](chromosomes: Seq[Chromosome[T, G]]) extends Representation[T, G] {

    /** Returns the number of chromosomes in the genotype.
     *
     * @return The number of chromosomes.
     */
    override def size: Int = chromosomes.size

    /** Flattens the genotype into a sequence of values from all its chromosomes.
     *
     * @return A sequence containing all the values from the genes in the chromosomes.
     */
    override def flatten(): Seq[T] = chromosomes.flatMap(_.flatten())

    /** Verifies the integrity of the genotype by checking all its chromosomes.
     *
     * @return `true` if all chromosomes are valid, `false` otherwise.
     */
    override def verify(): Boolean = chromosomes.forall(_.verify())

    /** Accesses the chromosome at the specified index.
     *
     * @param index The index of the chromosome to access.
     * @return The chromosome at the specified index.
     * @throws IndexOutOfBoundsException if the index is out of range.
     */
    def apply(index: Int): Chromosome[T, G] = chromosomes(index)

    /** Folds the values of the genes in the genotype from the left, combining them with an initial value.
     *
     * @param initial The initial value to start the folding operation.
     * @param f       A binary operation that combines the accumulated value with each gene's value.
     * @tparam U The type of the accumulated value and the final result.
     * @return The final result of folding the gene values from the left.
     */
    override def foldLeft[U](initial: U)(f: (U, T) => U): U = flatten().foldLeft(initial)(f)

    /** Folds the values of the genes in the genotype from the right, combining them with an initial value.
     *
     * @param initial The initial value to start the folding operation.
     * @param f       A binary operation that combines each gene's value with the accumulated result.
     * @tparam U The type of the accumulated value and the final result.
     * @return The final result of folding the gene values from the right.
     */
    override def foldRight[U](initial: U)(f: (T, U) => U): U = flatten().foldRight(initial)(f)

    /** Zips this genotype with another genotype, returning a sequence of paired chromosomes.
     *
     * @param other The genotype to zip with.
     * @return A sequence of pairs, where each pair contains chromosomes from both genotypes at corresponding positions.
     */
    def zip(other: Genotype[T, G]): Seq[(Chromosome[T, G], Chromosome[T, G])] = chromosomes.zip(other.chromosomes)

    /** Zips the chromosomes of this genotype with their indices.
     *
     * @return A sequence of pairs, where each pair contains a chromosome and its index.
     */
    def zipWithIndex: Seq[(Chromosome[T, G], Int)] = chromosomes.zipWithIndex

    /** Returns a string representation of the genotype.
     *
     * The format of the string depends on the current `Domain.toStringMode`:
     * - `ToStringMode.SIMPLE`: Chromosomes are listed in a simple format.
     * - Other modes: Chromosomes are displayed in a more detailed format.
     *
     * @return A string representation of the genotype.
     */
    override def toString: String = Domain.toStringMode match
        case ToStringMode.SIMPLE => chromosomes.mkString("[", ", ", "]")
        case _ => s"Genotype(chromosomes=$chromosomes)"
}

/** Factory object for creating and building `Genotype` instances.
 *
 * The `Genotype` object provides a factory method for constructing a `GenotypeBuilder` with a specified sequence of
 * chromosome builders. This is useful in evolutionary algorithms where you need to create a genotype consisting of
 * multiple chromosomes, each constructed using a different builder.
 *
 * @see [[of]]
 */
object Genotype {

    /** Creates a `GenotypeBuilder` using the provided chromosome builders.
     *
     * The `of` method initializes a `GenotypeBuilder` with the given sequence of chromosome builders. Each builder
     * is added to the genotype builder, which can then be used to construct a `Genotype` instance.
     *
     * @param chromosomes A sequence of chromosome builders to be added to the genotype builder.
     * @tparam T The type of value stored by the genes within the chromosomes.
     * @tparam G The type of gene contained within the chromosomes, which must extend [[Gene]].
     * @return A `GenotypeBuilder` initialized with the provided chromosome builders.
     *
     * @example
     * {{{
     * // Example: Creating a genotype with two chromosome builders
     * case class IntGene(value: Int) extends Gene[Int, IntGene] {
     *   override val generator: (Int, Random) => Int = (v, _) => v + 1
     *   override def duplicateWithValue(value: Int): IntGene = IntGene(value)
     *   override def flatten(): Seq[Int] = Seq(value)
     *   override def foldRight[U](initial: U)(f: (Int, U) => U): U = f(value, initial)
     *   override def foldLeft[U](initial: U)(f: (U, Int) => U): U = f(initial, value)
     * }
     *
     * val chromosomeBuilder1 = new ChromosomeBuilder[Int, IntGene] {
     *   override def build()(using random: Random): Chromosome[Int, IntGene] =
     *     Chromosome(Seq.fill(10)(IntGene(random.nextInt(100))))
     * }
     *
     * val chromosomeBuilder2 = new ChromosomeBuilder[Int, IntGene] {
     *   override def build()(using random: Random): Chromosome[Int, IntGene] =
     *     Chromosome(Seq.fill(5)(IntGene(random.nextInt(50))))
     * }
     *
     * val genotypeBuilder = Genotype.of(chromosomeBuilder1, chromosomeBuilder2)
     * val genotype = genotypeBuilder.build()
     * // genotype: Genotype[Int, IntGene] containing two chromosomes with specified genes
     * }}}
     */
    def of[T, G <: Gene[T, G]](chromosomes: ChromosomeBuilder[T, G]*): GenotypeBuilder[T, G] = {
        val builder = GenotypeBuilder[T, G]()
        chromosomes.foreach(builder.addChromosome)
        builder
    }
}
