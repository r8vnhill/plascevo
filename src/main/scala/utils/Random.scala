package cl.ravenhill.plascevo
package utils

import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.util.Random

/** Generates a sequence of unique random indices within a specified range.
 *
 * The `nIndices` method produces a sequence of `size` unique indices within the range `[start, end)`. The indices are
 * selected randomly and are guaranteed to be unique within the resulting sequence. This method is useful in scenarios
 * where you need to sample a specific number of distinct elements from a collection by their indices.
 *
 * @param size  The number of indices to generate.
 * @param end   The exclusive upper bound of the range from which to generate indices.
 * @param start The inclusive lower bound of the range from which to generate indices. Defaults to `0`.
 * @param random An implicit `Random` instance used for generating random numbers.
 * @return A sorted sequence of unique indices within the range `[start, end)`.
 * @throws IllegalArgumentException If `size` is negative, greater than the size of the range, or if `start` is not less
 *                                  than `end`.
 * @example
 * {{{
 * given Random = new Random()
 * val indices = nIndices(3, 10)
 * // indices: Seq[Int] = Seq(2, 5, 7)  // Example output
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

/** Generates a sequence of indices within a specified range, selected based on a given probability.
 *
 * The `pIndices` method produces a sequence of indices from the range `[start, end)`, where each index is included in
 * the result with a probability specified by `pickProbability`. This method is useful in scenarios where you need to
 * randomly sample indices from a range with a certain likelihood.
 *
 * @param pickProbability The probability with which each index is selected. Must be within the range `[0.0, 1.0]`.
 * @param end             The exclusive upper bound of the range from which to generate indices.
 * @param start           The inclusive lower bound of the range from which to generate indices. Defaults to `0`.
 * @param random          An implicit `Random` instance used for generating random numbers.
 * @return A sequence of indices from the specified range `[start, end)` where each index is selected with the given
 *         probability.
 * @throws IllegalArgumentException If `pickProbability` is not within the range `[0.0, 1.0]`, or if `start` is not
 *                                  less than or equal to `end - 1`.
 * @example
 * {{{
 * given Random = new Random()
 * val indices = pIndices(0.3, 10)
 * // indices: Seq[Int] = Seq(1, 3, 7)  // Example output where some indices are selected
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

/** Generates a sequence of randomly selected subsets from a given sequence of elements.
 *
 * The `subsets` method returns a sequence of subsets, each containing a random selection of elements from the input
 * sequence. The selection process can be either exclusive or non-exclusive, based on the `exclusive` parameter.
 *
 * - If `exclusive` is `Exclusivity.Exclusive`, each element can only be used once across all subsets. The size of the
 *   input sequence must be a multiple of the subset size.
 * - If `exclusive` is `Exclusivity.NonExclusive`, elements can be used in multiple subsets.
 *
 * The `size` parameter specifies the number of elements in each subset. It must be at least 1 and at most the size of
 * the input sequence. The `limit` parameter sets the maximum number of subsets to generate. If not provided, the 
 * default value is `Int.MaxValue`.
 *
 * Each element in the input sequence is guaranteed to be included in at least one subset if `exclusivity` is set to
 * `Exclusivity.Exclusive`.
 *
 * @param elements  The input sequence of elements from which to generate subsets.
 * @param size      The number of elements in each subset.
 * @param exclusive Specifies whether the selection should be exclusive (no repeated elements) or non-exclusive
 *                  (repeated elements allowed).
 * @param limit     The maximum number of subsets to generate. Defaults to `Int.MaxValue`.
 * @param random    An implicit `Random` instance used for shuffling and selecting elements.
 * @tparam T The type of elements in the sequence.
 * @return A sequence of randomly generated subsets.
 * @throws IllegalArgumentException If the input parameters are invalid, such as an invalid subset size or 
 *                                  exclusivity settings.
 * @example
 * {{{
 * given random: Random = new Random()
 *
 * // Generate three exclusive subsets of size two from a sequence of integers:
 * val elements = Seq(1, 2, 3, 4, 5, 6)
 * val subsets = subsets(elements, size = 2, exclusive = Exclusivity.Exclusive, limit = 3)
 * // subsets: Seq(Seq(2, 6), Seq(4, 3), Seq(5, 1))
 *
 * // Generate four non-exclusive subsets of size three from a sequence of strings:
 * val elements = Seq("cat", "dog", "fish", "bird", "hamster")
 * val subsets = subsets(elements, size = 3, exclusive = Exclusivity.NonExclusive, limit = 4)
 * // subsets: Seq(Seq("hamster", "fish", "dog"), Seq("bird", "fish", "hamster"),
 * //              Seq("cat", "dog", "fish"), Seq("hamster", "cat", "dog"))
 *
 * // Generate two exclusive subsets of size four from a sequence of characters:
 * val elements = Seq('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l')
 * val subsets = subsets(elements, size = 4, exclusive = Exclusivity.Exclusive, limit = 2)
 * // subsets: Seq(Seq('h', 'j', 'l', 'f'), Seq('b', 'g', 'a', 'e'))
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

/** Creates a non-exclusive subset of elements from a sequence.
 *
 * The `createNonExclusiveSubset` method generates a subset of the specified `size` from the provided `elements`
 * sequence. This method allows for the possibility of selecting the same element multiple times across different
 * subsets (non-exclusive selection). The first element of the subset is always taken from the `remainingElements`,
 * ensuring that each element is used at least once. The subsequent elements are chosen randomly from the entire
 * `elements` sequence.
 *
 * The `remainingElements` sequence is modified during this process by removing elements that are used in the subset.
 *
 * @param elements          The original sequence of elements from which to select the subset.
 * @param remainingElements A mutable `ListBuffer` of elements that have not yet been used in the current subset.
 * @param size              The number of elements to include in the subset.
 * @param random            An implicit `Random` instance used for selecting random elements.
 * @tparam T The type of elements in the sequence.
 * @return A `ListBuffer` containing the selected subset of elements.
 * @throws NoSuchElementException If the `remainingElements` or `elements` sequence does not have enough elements
 *                                to create a subset of the specified size.
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
