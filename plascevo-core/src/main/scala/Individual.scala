package cl.ravenhill.plascevo

import mixins.{FlatMappable, Foldable, Verifiable}
import repr.{Feature, Representation}

import java.util.Objects

/** Represents an individual in an evolutionary algorithm, characterized by its representation and fitness value.
 *
 * The `Individual` class encapsulates a candidate solution in an evolutionary algorithm. Each individual is defined by
 * its representation (the specific collection of features) and its associated fitness value, which measures the quality
 * or suitability of the solution. The fitness value defaults to `Double.NaN`, indicating that the individual has not
 * been evaluated yet. The class also provides various operations to work with the individual's features, including
 * verification, folding, and flattening.
 *
 * @param representation The representation of the individual, encapsulating its features.
 * @param fitness        The fitness value of the individual, which defaults to `Double.NaN` if not provided, indicating
 *                       that the individual has not been evaluated.
 * @tparam T The type of value stored by the features.
 * @tparam F The type of feature contained in the representation, which must implement [[Feature]].
 * @tparam R The type of representation used by the individual, which must implement [[Representation]].
 * @example
 * {{{
 * val genotype: Genotype[Int, SimpleGene] = ...
 * val fitness = 420
 * val individual = Individual(genotype, fitness)
 * }}}
 */
case class Individual[T, F <: Feature[T, F], R <: Representation[T, F]](
    representation: R,
    fitness: Double = Double.NaN
) extends Verifiable with FlatMappable[T] with Foldable[T] {

    /** Lazily evaluates and returns the size of the individual, representing the number of features it contains.
     *
     * The `size` of the individual corresponds to the number of features or components within its representation. For
     * instance, in a binary string representation, this might be the number of bits; in a matrix representation, it
     * could represent the number of rows, columns, or elements, depending on the specific implementation. The size is
     * computed only when first accessed, and the result is cached for subsequent accesses.
     */
    lazy val size: Int = representation.size


    /** Verifies the validity of the individual.
     *
     * This method checks whether the representation is valid and ensures that the fitness value is not `NaN`.
     *
     * @return `true` if the individual is valid, `false` otherwise.
     */
    override def verify(): Boolean = representation.verify() && !fitness.isNaN

    /** Folds the features of the individual from the left, combining them with an initial value.
     *
     * The `foldLeft` method processes the individual's features sequentially from the left (first feature) to the right
     * (last feature). Starting with the provided `initial` value, it applies the binary operation `f` to combine the
     * current accumulated result with each feature in turn. This operation allows you to reduce or transform the
     * features into a single result, such as calculating a sum, product, or constructing a new data structure.
     *
     * @param initial The initial value for the folding operation, which serves as the starting point.
     * @param f       A binary function that takes the current accumulated value and the next feature, and returns the
     *                updated accumulated value.
     * @tparam U The type of the accumulated value and the final result.
     * @return The final result of folding the features from the left using the provided function `f`.
     * @example
     * {{{
     * // Suppose we have an individual with a vector of integers as its representation
     * // [1, 2, 3, 4, 5]
     * val individual: Individual[Int, SimpleGene, Genotype[Int, SimpleGene]] = ...
     * val sum = individual.foldLeft(0)(_ + _)
     * // The sum will be 15, which is the result of adding all the features together: 1 + 2 + 3 + 4 + 5
     * }}}
     */
    override def foldLeft[U](initial: U)(f: (U, T) => U): U = representation.foldLeft(initial)(f)

    /** Folds the features of the individual from the right, combining them with an initial value.
     *
     * The `foldRight` method processes the individual's features sequentially from the right (last feature) to the left
     * (first feature). Starting with the provided `initial` value, it applies the binary operation `f` to combine each
     * feature with the current accumulated result. This operation is particularly useful when the combination operation
     * benefits from processing elements in reverse order or when constructing certain data structures.
     *
     * @param initial The initial value for the folding operation, which serves as the starting point.
     * @param f       A binary function that takes the current feature and the accumulated value, and returns the
     *                updated accumulated value.
     * @tparam U The type of the accumulated value and the final result.
     * @return The final result of folding the features from the right using the provided function `f`.
     * @example
     * {{{
     * // Consider a scenario where we want to build a list from the individual's features.
     * // If we use foldLeft, the list will be built in reverse order, requiring an additional reversal step.
     * // Using foldRight, however, allows us to build the list in the correct order directly.
     *
     * val individual: Individual[Int, SimpleGene, Genotype[Int, SimpleGene]] = ...
     * val featureList = individual.foldRight(List.empty[Int])(_ :: _)
     * // featureList will contain the features in the correct order: [1, 2, 3, 4, 5]
     *
     * // With foldLeft, we would need to reverse the list after folding:
     * val reversedList = individual.foldLeft(List.empty[Int])((acc, elem) => elem :: acc).reverse
     * // This approach is less efficient because of the additional reversal step.
     * }}}
     */
    override def foldRight[U](initial: U)(f: (T, U) => U): U = representation.foldRight(initial)(f)


    /** Flattens the individual's features into a single sequence.
     *
     * The `flatten` method combines all the features contained within the individual's representation into a single
     * sequence. This is particularly useful when dealing with complex representations, such as matrices or nested
     * structures, where you need to reduce the representation to a simple, linear sequence of elements.
     *
     * @return A sequence containing all the features from the individual's representation in a linear order.
     * @example
     * {{{
     * // Consider an individual whose representation is a 2x2 matrix of integers:
     * val matrixRepresentation: MatrixRepresentation[Int] = MatrixRepresentation(Seq(
     *   Seq(1, 2),
     *   Seq(3, 4)
     * ))
     * val individual = Individual(matrixRepresentation)
     *
     * // Flattening the individual will result in a single sequence containing all elements:
     * val flattened = individual.flatten()
     * // flattened: Seq[Int] = Seq(1, 2, 3, 4)
     * }}}
     */
    override def flatten(): Seq[T] = representation.flatten()

    /** Checks if the individual's fitness has been evaluated.
     *
     * @return `true` if the fitness value is not `NaN`, `false` otherwise.
     */
    def isEvaluated: Boolean = !fitness.isNaN

    /** Provides a string representation of the individual.
     *
     * The format of the string is determined by the current [[Domain.toStringMode]]:
     * - `ToStringMode.DEFAULT`: `"Individual(representation = ..., fitness = ...)"`
     * - `ToStringMode.SIMPLE`: `"representation -> fitness"`
     *
     * @return A string representing the individual.
     */
    override def toString: String = Domain.toStringMode match
        case ToStringMode.DEFAULT => s"Individual(representation = $representation, fitness = $fitness)"
        case ToStringMode.SIMPLE => s"$representation -> $fitness"

    /** Compares this individual with another object for equality.
     *
     * Two individuals are considered equal if their representations are equal, regardless of their fitness values.
     *
     * @param obj The object to compare with.
     * @return `true` if the other object is an `Individual` with an equal representation, `false` otherwise.
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
}
