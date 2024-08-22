package cl.ravenhill.plascevo
package genetics.genes.numeric

import genetics.genes.Gene
import mixins.Filterable

import scala.util.Random

/** A trait representing a numeric gene in an evolutionary algorithm.
 *
 * The `NumericGene` trait extends both the [[Gene]] and [[Filterable]] traits, providing additional functionalities
 * specific to numeric genes in evolutionary algorithms. It supports filtering based on predicates, mutation with
 * constraints, and conversion to numeric types such as `Int` and `Double`. This trait is typically used for genes
 * that represent numeric values, offering methods to filter, mutate, average, and convert these values.
 *
 * @tparam T The type of numeric value stored by the gene.
 * @tparam G The type of the gene itself, which must extend `NumericGene`.
 * @example
 * {{{
 * // Example implementation of a NumericGene for integers
 * case class IntegerGene(value: Int, generator: Random => Int) extends NumericGene[Int, IntegerGene] {
 *   override def duplicateWithValue(value: Int): IntegerGene = copy(value = value)
 *
 *   override def filter(predicate: Int => Boolean): Seq[Int] = Seq(value).filter(predicate)
 *
 *   override def average(genes: Seq[IntegerGene]): Int =
 *       if (genes.isEmpty) 0 else genes.map(_.value).sum / genes.size
 *
 *   override def toInt: Int = value
 *
 *   override def toDouble: Double = value.toDouble
 * }
 * }}}
 */
trait NumericGene[T, G <: NumericGene[T, G]] extends Gene[T, G] with Filterable[T] {

    /** Mutates the gene's value, ensuring it satisfies the given predicate.
     *
     * The `mutate` method generates new values for the gene using the `generator` function until a value is found
     * that satisfies the predicate. This ensures that the mutation respects any constraints imposed by the predicate.
     *
     * @param random An implicit random number generator used for the mutation process.
     * @return A new instance of the gene with a mutated value.
     */
    final override def mutate()(using random: Random): G = {
        var newValue = generate(using random)
        while (!predicate(newValue)) {
            newValue = generate(using random)
        }
        duplicateWithValue(newValue)
    }


    /** Computes the average value of a sequence of genes.
     *
     * The `average` method calculates the average of the values stored in the given sequence of genes. This method
     * can be useful for operations that require an aggregated numeric value across multiple genes.
     *
     * @param genes A sequence of genes of the same type.
     * @return The average value of the genes in the sequence.
     */
    def average(genes: Seq[G]): G

    /** Converts the gene's value to an integer.
     *
     * The `toInt` method provides a way to convert the gene's value to an integer, which can be useful for
     * certain types of operations or comparisons.
     *
     * @return The integer representation of the gene's value.
     */
    def toInt: Int

    /** Converts the gene's value to a double.
     *
     * The `toDouble` method provides a way to convert the gene's value to a double, allowing for more precise
     * numeric operations or comparisons.
     *
     * @return The double representation of the gene's value.
     */
    def toDouble: Double

    /** Verifies if the gene's value satisfies the predicate.
     *
     * The `verify` method checks whether the current value of the gene satisfies the defined predicate. This can
     * be used to ensure that the gene's value meets certain constraints or conditions.
     *
     * @return `true` if the value satisfies the predicate, `false` otherwise.
     */
    final override def verify(): Boolean = predicate(value)

    /** Returns a string representation of the gene.
     *
     * The `toString` method provides a string representation of the gene, with formatting depending on the
     * current `ToStringMode` in the [[Domain]] object. In `SIMPLE` mode, it returns the value as a string;
     * otherwise, it includes the class name and the value.
     *
     * @return The string representation of the gene.
     */
    override def toString: String = Domain.toStringMode match {
        case ToStringMode.SIMPLE => value.toString
        case _ => s"${getClass.getSimpleName}(value=$value)"
    }
}
