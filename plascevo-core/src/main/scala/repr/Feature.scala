package cl.ravenhill.plascevo
package repr

import mixins.{FlatMappable, Foldable, Verifiable}

/** Represents an atomic feature in an evolutionary algorithm.
 *
 * In the context of evolutionary algorithms, a feature is the smallest unit that contributes to the structure of a
 * solution. For example, in a genetic algorithm, a gene would be considered a feature, while a chromosome could be
 * viewed as a collection of such features. The `Feature` trait provides a unified interface for these components,
 * ensuring they can be duplicated with new values, verified, and manipulated through various operations.
 *
 * This trait is typically implemented by classes that represent individual elements within evolutionary algorithms,
 * such as genes, decision variables, or other fundamental units that compose a solution.
 *
 * @tparam T The type of the value contained within the feature.
 * @tparam F The specific type of the feature itself, which must extend [[Feature]].
 *
 * @example
 * {{{
 * class MyGene(val value: Int) extends Feature[Int, MyGene] {
 *   override def duplicateWithValue(value: Int): MyGene = new MyGene(value)
 *
 *   override def verify(): Boolean = value >= 0  // Example verification logic
 *
 *   override def flatten(): Seq[Int] = Seq(value)  // Flattening returns a sequence with the single value
 *
 *   override def foldLeft[U](initial: U)(f: (U, Int) => U): U = f(initial, value)
 *
 *   override def foldRight[U](initial: U)(f: (Int, U) => U): U = f(value, initial)
 * }
 * }}}
 */
trait Feature[T, F <: Feature[T, F]] extends Verifiable with FlatMappable[T] with Foldable[T] {

    /** The value contained within the feature.
     *
     * This value represents the core data or decision variable that the feature contributes to the evolutionary
     * process.
     */
    def value: T

    /** Creates a duplicate of this feature with a new specified value.
     *
     * This method allows for the creation of a new instance of the feature, retaining the same characteristics but with
     * a different value. This is particularly useful in operations such as mutation, where a feature needs to be
     * altered while preserving the structure of the solution.
     *
     * @param value The new value for the duplicated feature.
     * @return A new feature instance with the specified value.
     */
    def duplicateWithValue(value: T): F
}
