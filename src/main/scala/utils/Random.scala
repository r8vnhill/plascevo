package cl.ravenhill.plascevo
package utils

import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.util.Random

/** Generates a sequence of unique random indices within a specified range.
 *
 * The `nIndices` method returns a sequence of unique random integers within the range defined by the `start`
 * (inclusive) and `end` (exclusive) parameters. The number of indices generated is specified by the `size` parameter.
 * The resulting indices are returned in sorted order.
 *
 * @param size   The number of indices to generate. Must be greater than or equal to 0 and less than or equal to the
 *               size of the range (`end - start`).
 * @param end    The end of the range (exclusive) within which indices are generated. Must be greater than the `start`
 *               index.
 * @param start  The start of the range (inclusive) within which indices are generated. Defaults to 0. Must be greater
 *               than or equal to 0.
 * @param random An implicit `Random` instance used for generating random indices.
 * @return A sorted sequence of unique random indices within the specified range.
 * @throws IllegalArgumentException if the input parameters do not meet the validation criteria.
 *
 *                                  <h3>Example 1: Generating Random Indices</h3>
 * @example
 * {{{
 * given random: Random = new Random()
 * val randomIndices = nIndices(size = 3, end = 10)
 * // Example result: Seq(1, 4, 7)
 * }}}
 *
 * <h3>Example 2: Generating Random Indices with a Custom Start</h3>
 * @example
 * {{{
 * given random: Random = new Random()
 * val randomIndices = nIndices(size = 5, end = 20, start = 10)
 * // Example result: Seq(10, 12, 15, 17, 19)
 * }}}
 */
def nIndices(size: Int, end: Int, start: Int = 0)(using random: Random): Seq[Int] = {
    require(size >= 0, s"The size ($size) must be greater than or equal to 0")
    require(size <= (end - start), s"The size ($size) must be at most the size of the range (${end - start})")
    require(end >= 0, s"The end index ($end) must be greater than or equal to 0")
    require(start >= 0, s"The start index ($start) must be greater than or equal to 0")
    require(start < end, s"The start index ($start) must be less than the end index ($end)")

    val remainingIndices = ListBuffer.range(start, end)
    Seq.fill(size) {
        remainingIndices.remove(random.nextInt(remainingIndices.size))
    }.sorted
}

/** Generates a sequence of indices based on a specified probability.
 *
 * The `pIndices` method returns a sequence of indices within the specified range `[start, end)`, where each index is
 * included in the result based on the provided `pickProbability`. The method filters the indices by generating a random
 * number for each and comparing it against the `pickProbability`. If the random number is less than the
 * `pickProbability`, the index is included in the result.
 *
 * @param pickProbability The probability of picking each index. Must be in the range `[0.0, 1.0]`.
 * @param end The end of the range (exclusive) within which indices are considered. Must be greater than or equal to 0.
 * @param start The start of the range (inclusive) within which indices are considered. Defaults to 0. Must be greater
 *              than or equal to 0.
 * @param random An implicit `Random` instance used to determine whether each index is picked.
 * @return A sequence of indices selected based on the specified probability.
 * @throws IllegalArgumentException if the input parameters do not meet the validation criteria.
 *
 * <h3>Example 1: Generating Indices with 50% Probability</h3>
 * @example
 * {{{
 * given random: Random = new Random()
 * val indices = pIndices(pickProbability = 0.5, end = 10)
 * // Example result: Seq(1, 3, 7, 8)
 * }}}
 *
 * <h3>Example 2: Generating Indices with 30% Probability and Custom Start</h3>
 * @example
 * {{{
 * given random: Random = new Random()
 * val indices = pIndices(pickProbability = 0.3, end = 20, start = 10)
 * // Example result: Seq(10, 14, 17)
 * }}}
 */
def pIndices(pickProbability: Double, end: Int, start: Int = 0)(using random: Random): Seq[Int] = {
    require(
        pickProbability >= 0.0 && pickProbability <= 1.0,
        s"The pick probability ($pickProbability) must be in the range [0.0, 1.0]"
    )
    require(start <= end - 1, s"The start ($start) must be less than or equal to the end ($end - 1)")
    require(end >= 0, s"The end index ($end) must be greater than or equal to 0")
    require(start >= 0, s"The start index ($start) must be greater than or equal to 0")

    (start until end).filter { _ => random.nextDouble() < pickProbability }
}

/** Generates a sequence of subsets from a given sequence of elements.
 *
 * The `subsets` method creates a specified number of subsets from the input sequence of elements. The method can
 * generate either exclusive or non-exclusive subsets, depending on the value of the `exclusive` parameter. Exclusive
 * subsets ensure that elements are not repeated across subsets, while non-exclusive subsets allow the same element to
 * appear in multiple subsets. The method also supports an optional limit on the number of subsets generated.
 *
 * @param elements The input sequence of elements from which subsets will be generated.
 * @param size The desired size of each subset. Must be greater than 0.
 * @param exclusive The exclusivity rule to apply when generating subsets: `Exclusive` or `NonExclusive`.
 * @param limit An optional limit on the number of subsets to generate. Defaults to `Int.MaxValue`.
 * @param random An implicit `Random` instance used for random selection.
 * @tparam T The type of elements in the sequence.
 * @return A sequence of subsets, each represented as a `Seq[T]`.
 * @throws IllegalArgumentException if the input parameters do not meet the validation criteria.
 *
 * <h3>Example 1: Generating Exclusive Subsets</h3>
 * @example
 * {{{
 * given random: Random = new Random()
 * val elements = Seq(1, 2, 3, 4, 5)
 * val subsets = subsets(elements, size = 2, exclusive = Exclusivity.Exclusive)
 * // Example result: Seq(Seq(1, 2), Seq(3, 4))
 * }}}
 *
 * <h3>Example 2: Generating Non-Exclusive Subsets</h3>
 * @example
 * {{{
 * given random: Random = new Random()
 * val elements = Seq(1, 2, 3, 4, 5)
 * val subsets = subsets(elements, size = 3, exclusive = Exclusivity.NonExclusive, limit = 3)
 * // Example result: Seq(Seq(1, 2, 3), Seq(1, 4, 5), Seq(2, 3, 4))
 * }}}
 */
def subsets[T](elements: Seq[T], size: Int, exclusive: Exclusivity, limit: Int = Int.MaxValue)
    (using random: Random): Seq[Seq[T]] = {
    validateSubsetInput(elements, size, exclusive, limit)

    val subsets = mutable.ListBuffer[ListBuffer[T]]()
    val remainingElements = mutable.ListBuffer(elements *)
    random.shuffle(remainingElements)

    var i = 0
    while (remainingElements.nonEmpty && i < limit) {
        exclusive match
            case Exclusivity.Exclusive =>
                val subset = remainingElements.take(size)
                subsets += subset
                remainingElements.dropInPlace(size)
            case _ =>
                val subset = createNonExclusiveSubset(elements, remainingElements, size)
                subsets += subset
        i += 1
    }

    subsets.map(_.toSeq).toSeq
}

/** Creates a non-exclusive subset of elements from a list.
 *
 * The `createNonExclusiveSubset` method generates a subset of a specified size from the given list of elements. The
 * selection process is non-exclusive, meaning that the same element can appear multiple times in the subset. The first
 * element of the subset is always the first unused element from the list, while the remaining elements are chosen
 * randomly from the list. Once an element is selected, it is removed from the list of remaining elements.
 *
 * @param elements          The original list of elements from which the subset is to be created.
 * @param remainingElements A mutable list buffer of remaining elements that have not yet been used in the subset.
 *                          This list is updated as elements are selected.
 * @param size              The desired size of the subset.
 * @param random            An implicit `Random` instance used for random selection.
 * @tparam T The type of elements in the list.
 * @return A `List[T]` representing the non-exclusive subset of elements.
 *
 *         <h3>Example:</h3>
 * @example
 * {{{
 * given random: Random = new Random()
 * val elements = List(1, 2, 3, 4, 5)
 * val remainingElements = ListBuffer(1, 2, 3, 4, 5)
 * val subset = createNonExclusiveSubset(elements, remainingElements, size = 3)
 * // Example result: List(1, 4, 2)
 * }}}
 */
private def createNonExclusiveSubset[T](
    elements: Seq[T],
    remainingElements: ListBuffer[T],
    size: Int
)(using random: Random): ListBuffer[T] = {
    ListBuffer.tabulate(size) { i =>
        if (i == 0) {
            // The first element of the subset is always the first unused element.
            remainingElements.remove(0)
        } else {
            // The rest of the elements are chosen randomly from the list of elements.
            val randomElement = elements(random.nextInt(elements.size))
            // Since the element was used, it is removed from the list of remaining elements.
            remainingElements -= randomElement
            randomElement
        }
    }
}

/** Validates the parameters for selecting a subset of elements from a sequence.
 *
 * The `validateSubsetInfo` method checks various conditions to ensure that the parameters provided for selecting a
 * subset of elements from a sequence are valid. This includes verifying that the sequence is not empty, the subset size
 * is positive, and that the selection adheres to the exclusivity and limit constraints.
 *
 * @param elements    The sequence of elements from which a subset is to be selected.
 * @param size        The size of the subset to be selected. Must be greater than 0.
 * @param exclusivity The exclusivity rule to be applied, either `Exclusive` or `NonExclusive`.
 * @param limit       The maximum number of subsets that can be selected. Must be non-negative.
 * @throws IllegalArgumentException if the sequence is empty, the subset size is invalid, or the exclusivity and limit
 *                                  constraints are not met.
 */
private def validateSubsetInput(elements: Seq[?], size: Int, exclusivity: Exclusivity, limit: Int): Unit = {
    if (elements.isEmpty) {
        throw new IllegalArgumentException("Cannot select a subset from an empty sequence.")
    } else {
        require(size > 0, "Subset size must be greater than 0.")
        exclusivity match
            case Exclusivity.Exclusive => {
                require(
                    size <= elements.size,
                    "Subset size must be less than or equal to the number of elements."
                )
                if (size != 0) {
                    require(
                        elements.size % size == 0,
                        "The number of elements must be divisible by the subset size."
                    )
                }
            }
            case _ =>
        require(limit >= 0, "The limit on the number of subsets must be non-negative.")
    }
}
