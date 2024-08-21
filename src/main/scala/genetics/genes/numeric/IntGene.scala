package cl.ravenhill.plascevo
package genetics.genes.numeric

import composerr.constrained
import composerr.constraints.iterable.BeEmpty

import java.util.Objects
import scala.util.Random

/** Represents a gene with an integer value in an evolutionary algorithm.
 *
 * The `IntGene` class is a concrete implementation of the [[NumericGene]] trait, specifically designed for handling
 * integer values within an evolutionary algorithm. This gene type is associated with a numeric range and a predicate
 * that can be used to enforce constraints on the gene's value. It supports operations such as mutation, averaging, and
 * conversion to different numeric types.
 *
 * @param value     The integer value stored by the gene.
 * @param range     The inclusive range within which the gene's value is allowed to vary. The default range is from
 *                  `Int.MinValue` to `Int.MaxValue`.
 * @param predicate A function that determines whether a generated or mutated value is acceptable. The default predicate
 *                  always returns `true`, allowing any value within the specified range.
 * @example
 * {{{
 * // Example of creating an IntGene with a specific range and predicate
 * val gene = IntGene(10, range = (0, 100)) { value => value % 2 == 0 }
 * val mutatedGene = gene.mutate()
 * val averageGene = gene.average(Seq(IntGene(5), IntGene(15)))
 * }}}
 */
case class IntGene(
    override val value: Int,
    range: (Int, Int) = (Int.MinValue, Int.MaxValue)
)(
    override val predicate: Int => Boolean = { _ =>
        true
    }
) extends NumericGene[Int, IntGene] {

    /** Creates a new `IntGene` with the specified value.
     *
     * The `duplicateWithValue` method allows for creating a new `IntGene` instance with a different value while
     * retaining the same range and predicate. This method is commonly used in evolutionary operations where gene
     * values need to be modified.
     *
     * @param value The new value for the duplicated gene.
     * @return A new `IntGene` instance with the specified value.
     */
    override def duplicateWithValue(value: Int): IntGene = {
        IntGene(value, range)(predicate)
    }

    /** Generates a random integer value within the gene's range.
     *
     * The `generator` method produces a new integer value within the specified range using the provided random number
     * generator. This method is typically used for creating initial gene values or for mutation operations.
     *
     * @param random An implicit random number generator used to produce the value.
     * @return A randomly generated integer value within the gene's range.
     */
    override def generate(using random: Random): Int = random.between(range._1, range._2)

    /** Calculates the average value of a sequence of `IntGene` instances.
     *
     * The `average` method computes the average of the values in the provided sequence of genes and returns a new
     * `IntGene` with this average value. The calculation is performed with floating-point precision before being
     * rounded to the nearest integer.
     *
     * @param genes A sequence of `IntGene` instances to average.
     * @return A new `IntGene` with the average value.
     * @throws IllegalArgumentException if the sequence of genes is empty.
     */
    override def average(genes: Seq[IntGene]): IntGene = {
        constrained {
            "Cannot calculate the average of an empty sequence of genes." | {
                genes mustNot BeEmpty()
            }
        }
        duplicateWithValue(
            genes.foldLeft(value.toDouble / (genes.size + 1)) { case (acc, gene) =>
                acc + gene.toDouble / (genes.size + 1)
            }.round.toInt
        )
    }

    /** Converts the gene's value to an integer.
     *
     * The `toInt` method returns the integer value stored by this gene, providing a way to directly access the value
     * for operations that require an integer.
     *
     * @return The integer representation of the gene's value.
     */
    override def toInt: Int = value

    /** Converts the gene's value to a double.
     *
     * The `toDouble` method converts the gene's value to a double, allowing for more precise numeric operations.
     *
     * @return The double representation of the gene's value.
     */
    override def toDouble: Double = value.toDouble

    /**
     * Compares this IntGene object with the specified object for equality.
     *
     * @param obj The object to be compared for equality.
     * @return true if the specified object is an instance of IntGene and has the same value, false otherwise.
     */
    override def equals(obj: Any): Boolean = obj match {
        case other: IntGene => value == other.value
        case _ => false
    }

    /**
     * Returns the hash code value for this IntGene object.
     * The hash code is computed based on the class of the object and its value.
     *
     * @return the hash code value for this IntGene object
     */
    override def hashCode(): Int = Objects.hash(classOf[IntGene], value)
}
