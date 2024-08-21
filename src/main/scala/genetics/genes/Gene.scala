package cl.ravenhill.plascevo
package genetics.genes

import repr.Feature

import scala.util.Random

/** A trait representing a gene in an evolutionary algorithm.
 *
 * The `Gene` trait extends the [[Feature]] trait and provides additional functionality specific to genes in
 * evolutionary algorithms. A gene typically represents a single unit of information in the genetic representation
 * of an individual. This trait includes methods for generating new values, mutating the gene, and basic operations
 * like flattening and folding its value.
 *
 * @tparam T The type of value stored by the gene.
 * @tparam G The type of the gene itself, which must extend `Gene`.
 *
 * @example
 * {{{
 * case class IntGene(value: Int) extends Gene[Int, IntGene] {
 *   override def generator(using random: Random): Int = random.nextInt(100)
 *   override def duplicateWithValue(value: Int): IntGene = copy(value = value)
 * }
 *
 * given Random = new Random()
 * val gene = IntGene(42)
 * val mutatedGene = gene.mutate() // Generates a new IntGene with a randomly generated value
 * }}}
 */
trait Gene[T, G <: Gene[T, G]] extends Feature[T, G] {

    /** Generates a new value for the gene using a random number generator.
     *
     * The `generator` method is used to produce a new value for the gene, typically during the mutation process.
     * The method operates within the context of an implicit random number generator, allowing for stochastic
     * generation of values.
     *
     * @param random An implicit random number generator.
     * @return A new value of type `T` generated randomly.
     */
    def generate(using random: Random): T

    /** Mutates the gene by generating a new value and returning a new instance with this value.
     *
     * The `mutate` method applies the `generator` method to produce a new value and then creates a new instance of the
     * gene with this value. The mutation process is stochastic, relying on the provided random number generator.
     *
     * @param random An implicit random number generator.
     * @return A new instance of the gene with a mutated value.
     */
    def mutate()(using random: Random): G = duplicateWithValue(generate(using random))

    /** Flattens the gene into a sequence containing its value.
     *
     * The `flatten` method returns a sequence containing the gene's value. Since a gene typically represents a single
     * unit of information, the resulting sequence will contain exactly one element.
     *
     * @return A sequence containing the gene's value.
     */
    final override def flatten(): Seq[T] = Seq(value)

    /** Folds the gene's value from the right, combining it with an initial value.
     *
     * The `foldRight` method processes the gene's value and combines it with the initial value using the provided
     * binary operation `f`. Since the gene contains a single value, this operation is straightforward.
     *
     * @param initial The initial value to combine with the gene's value.
     * @param f       A binary operation that combines the gene's value with the accumulated result.
     * @tparam U The type of the accumulated value and the final result.
     * @return The result of combining the gene's value with the initial value using `f`.
     */
    final override def foldRight[U](initial: U)(f: (T, U) => U): U = f(value, initial)

    /** Folds the gene's value from the left, combining it with an initial value.
     *
     * The `foldLeft` method processes the gene's value and combines it with the initial value using the provided
     * binary operation `f`. Since the gene contains a single value, this operation is straightforward.
     *
     * @param initial The initial value to combine with the gene's value.
     * @param f       A binary operation that combines the accumulated value with the gene's value.
     * @tparam U The type of the accumulated value and the final result.
     * @return The result of combining the gene's value with the initial value using `f`.
     */
    final override def foldLeft[U](initial: U)(f: (U, T) => U): U = f(initial, value)
}
