package cl.ravenhill.plascevo
package repr

import mixins.{FlatMappable, Foldable, Verifiable}

/** Represents a location in the search space within an evolutionary algorithm.
 *
 * The `Representation` trait defines a structured collection of features (`Feature`) that together form a candidate
 * solution in an evolutionary algorithm. In this context, the representation serves as a point or location in the
 * search space, where each feature contributes to the position of the solution within that space.
 *
 * This trait provides fundamental operations for verifying, flattening, and folding the collection of features, as
 * well as methods to determine the size and whether the collection is empty. By implementing this trait, you define how
 * a solution's position in the search space is represented and manipulated.
 *
 * In genetic algorithms, for example, a representation might be a chromosome, where each gene is a feature that
 * contributes to the overall position of the individual in the search space.
 *
 * @tparam T The type of value held by the features within the representation.
 * @tparam F The type of feature contained within the representation, which must extend [[Feature]].
 *
 * @example
 * {{{
 * class MyChromosome(val genes: Seq[MyGene]) extends Representation[Int, MyGene] {
 *   override def size: Int = genes.size
 *
 *   override def verify(): Boolean = genes.forall(_.verify())
 *
 *   override def flatten(): Seq[Int] = genes.flatMap(_.flatten())
 *
 *   override def foldLeft[U](initial: U)(f: (U, Int) => U): U = 
 *       genes.foldLeft(initial)((acc, gene) => gene.foldLeft(acc)(f))
 *
 *   override def foldRight[U](initial: U)(f: (Int, U) => U): U = 
 *       genes.foldRight(initial)((gene, acc) => gene.foldRight(acc)(f))
 * }
 * }}}
 */
trait Representation[T, F <: Feature[T, F]] extends Verifiable with FlatMappable[T] with Foldable[T] {

    /** Returns the number of features within this representation.
     *
     * The `size` method provides the total count of features (or collections of features) contained within the
     * representation. This is particularly useful for understanding the complexity or dimensionality of the candidate
     * solution and its position in the search space.
     *
     * @return The number of features in the representation.
     */
    def size: Int

    /** Checks if the representation contains any features.
     *
     * The `isEmpty` method returns `true` if the representation has no features, and `false` otherwise. This is a
     * convenience method that checks whether the `size` is zero.
     *
     * @return `true` if the representation is empty, `false` otherwise.
     */
    def isEmpty: Boolean = size == 0
}
