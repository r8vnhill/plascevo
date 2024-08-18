package cl.ravenhill.plascevo
package genetics.genes

import scala.util.Random

/** A sealed trait representing a boolean gene in a genetic algorithm.
 *
 * The `BooleanGene` trait is a specialized type of gene that stores a `Boolean` value. It provides a default generator
 * function that randomly generates boolean values and defines two concrete instances: `True` and `False`. These
 * instances represent the possible values that a `BooleanGene` can hold. The trait also includes a method for
 * duplicating the gene with a specified boolean value.
 */
sealed trait BooleanGene extends Gene[Boolean, BooleanGene] {

    /** A generator function that produces a random boolean value.
     *
     * This function takes the current value and a `Random` instance as input, but it only uses the `Random` instance
     * to generate a new boolean value, ignoring the current value.
     */
    override val generator: (Boolean, Random) => Boolean = (_, random) => random.nextBoolean()

    /** Duplicates the gene with a specified boolean value.
     *
     * This method returns a new instance of `BooleanGene` with the given value. If the value is `true`, it returns the
     * `True` instance; otherwise, it returns the `False` instance.
     *
     * @param value The boolean value for the new gene.
     * @return A `BooleanGene` instance with the specified value.
     */
    override def duplicateWithValue(value: Boolean): BooleanGene = if value then True else False

    /** An object representing a `BooleanGene` with a value of `true`.
     *
     * The `True` object is a singleton instance of `BooleanGene` that always holds the value `true`.
     */
    case object True extends BooleanGene {
        override val value: Boolean = true
    }

    /** An object representing a `BooleanGene` with a value of `false`.
     *
     * The `False` object is a singleton instance of `BooleanGene` that always holds the value `false`.
     */
    case object False extends BooleanGene {
        override val value: Boolean = false
    }
}
