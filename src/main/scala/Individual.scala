package cl.ravenhill.plascevo

import mixins.{FlatMappable, Foldable, Verifiable}
import repr.{Feature, Representation}

import java.util.Objects

/** Represents an individual in an evolutionary algorithm, defined by its representation and fitness.
 *
 * The `Individual` class encapsulates the representation of a candidate solution and its fitness value. It supports
 * various operations including verification, folding, and flattening, and provides lazy evaluation of its size. The
 * fitness value defaults to `Double.NaN`, indicating that the individual has not been evaluated.
 *
 * @param representation The representation of the individual, encapsulating its features.
 * @param fitness        The fitness value of the individual, which defaults to `Double.NaN` if not provided.
 * @tparam T The type of value stored by the feature.
 * @tparam F The kind of feature stored in the representation, which must implement [[Feature]].
 * @tparam R The type of representation used by the individual, which must implement [[Representation]].
 */
case class Individual[T, F <: Feature[T, F], R <: Representation[T, F]](
    val representation: R,
    val fitness: Double = Double.NaN
) extends Verifiable with FlatMappable[T] with Foldable[T]:

    /** The size of the individual, representing the number of features in its representation.
     *
     * This value is lazily evaluated and corresponds to the size of the underlying representation.
     */
    lazy val size: Int = representation.size

    /** Verifies the integrity of the individual.
     *
     * This method checks that the representation is valid and that the fitness value is not `NaN`.
     *
     * @return `true` if the individual is valid, `false` otherwise.
     */
    override def verify(): Boolean = representation.verify() && !fitness.isNaN

    /** Folds the individual's features from the left.
     *
     * @param initial The initial value to start the folding operation.
     * @param f       A binary operation that takes the current accumulated value and the next feature, and returns the
     *                new accumulated value.
     * @tparam U      The type of the accumulated value and the result.
     * @return The result of folding the features from the left.
     */
    override def foldLeft[U](initial: U)(f: (U, T) => U): U = representation.foldLeft(initial)(f)

    /** Folds the individual's features from the right.
     *
     * @param initial The initial value to start the folding operation.
     * @param f       A binary operation that takes the next feature and the current accumulated value, and returns the
     *                new accumulated value.
     * @tparam U      The type of the accumulated value and the result.
     * @return The result of folding the features from the right.
     */
    override def foldRight[U](initial: U)(f: (T, U) => U): U = representation.foldRight(initial)(f)

    /** Flattens the individual's features into a list.
     *
     * @return A list containing all the features in the individual's representation.
     */
    override def flatten(): Seq[T] = representation.flatten()

    /** Checks if the individual's fitness has been evaluated.
     *
     * @return `true` if the fitness is not `NaN`, `false` otherwise.
     */
    def isEvaluated: Boolean = !fitness.isNaN

    /** Returns a string representation of the individual.
     *
     * The format of the string is determined by the current [[Domain.toStringMode]]:
     * - `ToStringMode.DEFAULT`: "Individual(representation = ..., fitness = ...)"
     * - `ToStringMode.SIMPLE`: "... -> ..."
     *
     * @return The string representation of the individual.
     */
    override def toString: String = Domain.toStringMode match
        case ToStringMode.DEFAULT => s"Individual(representation = $representation, fitness = $fitness)"
        case ToStringMode.SIMPLE => s"$representation -> $fitness"

    /** Compares this individual with another object for equality.
     *
     * Two individuals are considered equal if their representations are equal.
     *
     * @param obj The object to compare with.
     * @return `true` if the other object is an individual with an equal representation, `false` otherwise.
     */
    override def equals(obj: Any): Boolean = obj match
        case that: Individual[_, _, _] => representation == that.representation
        case _ => false

    /** Returns a hash code value for the individual.
     *
     * The hash code is computed based on the class and the representation.
     *
     * @return The hash code value.
     */
    override def hashCode(): Int = Objects.hash(classOf[Individual[?, ?, ?]], representation)
