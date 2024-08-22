package cl.ravenhill.plascevo
package genetics.genes.numeric

import cl.ravenhill.composerr.constrained
import cl.ravenhill.composerr.constraints.iterable.BeEmpty

import scala.util.Random

/** Represents a gene with a double-precision floating-point value in an evolutionary algorithm.
 *
 * The `DoubleGene` class is a concrete implementation of the [[NumericGene]] trait, specifically designed for handling
 * double values within an evolutionary algorithm. This gene type is associated with a numeric range and a predicate
 * that can be used to enforce constraints on the gene's value. It supports operations such as mutation, averaging, and
 * conversion to different numeric types.
 *
 * @param value     The double-precision floating-point value stored by the gene.
 * @param range     The inclusive range within which the gene's value is allowed to vary. The default range is from
 *                  `Double.MinValue` to `Double.MaxValue`.
 * @param predicate A function that determines whether a generated or mutated value is acceptable. The default predicate
 *                  always returns `true`, allowing any value within the specified range.
 * @example
 * {{{
 * // Example of creating a DoubleGene with a specific range and predicate
 * val gene = DoubleGene(0.5, range = (0.0, 1.0)) { value => value >= 0.0 && value <= 1.0 }
 * val mutatedGene = gene.mutate()
 * val averageGene = gene.average(Seq(DoubleGene(0.2), DoubleGene(0.8)))
 * }}}
 */
case class DoubleGene(
    override val value: Double,
    range: (Double, Double) = (Double.MinValue, Double.MaxValue)
)(
    override val predicate: Double => Boolean = { _ =>
        true
    }
) extends NumericGene[Double, DoubleGene] {

    /** Calculates the average value of a sequence of `DoubleGene` instances.
     *
     * The `average` method computes the average of the values in the provided sequence of genes and returns a new
     * `DoubleGene` with this average value. The calculation is performed with floating-point precision.
     *
     * @param genes A sequence of `DoubleGene` instances to average.
     * @return A new `DoubleGene` with the average value.
     * @throws IllegalArgumentException if the sequence of genes is empty.
     */
    override def average(genes: Seq[DoubleGene]): DoubleGene = {
        constrained {
            "Cannot calculate the average of an empty sequence of genes." | {
                genes mustNot BeEmpty()
            }
        }
        duplicateWithValue(
            genes.foldLeft(value / (genes.size + 1)) { case (acc, gene) =>
                acc + gene.toDouble / (genes.size + 1)
            }
        )
    }

    /** Generates a random double-precision floating-point value within the gene's range.
     *
     * The `generator` method produces a new double value within the specified range using the provided random number
     * generator. This method is typically used for creating initial gene values or for mutation operations.
     *
     * @param random An implicit random number generator used to produce the value.
     * @return A randomly generated double value within the gene's range.
     */
    override def generate(using random: Random): Double = random.nextDouble() * (range._2 - range._1) + range._1

    /** Creates a new `DoubleGene` with the specified value.
     *
     * The `duplicateWithValue` method allows for creating a new `DoubleGene` instance with a different value while
     * retaining the same range and predicate. This method is commonly used in evolutionary operations where gene
     * values need to be modified.
     *
     * @param value The new value for the duplicated gene.
     * @return A new `DoubleGene` instance with the specified value.
     */
    override def duplicateWithValue(value: Double): DoubleGene = {
        DoubleGene(value, range)(predicate)
    }

    /** Converts the gene's value to an integer.
     *
     * The `toInt` method returns the integer representation of this gene's value, providing a way to directly access
     * the value for operations that require an integer.
     *
     * @return The integer representation of the gene's value.
     */
    override def toInt: Int = value.toInt

    /** Returns the gene's value as a double.
     *
     * The `toDouble` method returns the double-precision floating-point value stored by the gene.
     *
     * @return The double value of the gene.
     */
    override def toDouble: Double = value
}
