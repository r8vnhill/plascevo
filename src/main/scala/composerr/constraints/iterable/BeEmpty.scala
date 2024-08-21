package cl.ravenhill.plascevo
package composerr.constraints.iterable

/**
 * A constraint that validates whether an iterable collection is empty.
 *
 * The `BeEmpty` class extends the `IterableConstraint[T]` trait to provide a specific constraint for validating 
 * that an iterable collection of type `T` is empty. This constraint can be applied to any collection that implements 
 * the `Iterable[T]` trait, and it ensures that the collection contains no elements.
 *
 * @tparam T The type of elements contained within the iterable collection.
 *
 * @example
 * {{{
 * val beEmpty = BeEmpty[Int]()
 * val numbers = List.empty[Int]
 * constrained {
 *     "The list must be empty" in { numbers must beEmpty }
 * }
 *
 * val nonEmptyNumbers = List(1, 2, 3)
 * constrained {
 *     "The list must not be empty" in { nonEmptyNumbers mustNot beEmpty }
 * }
 * }}}
 */
class BeEmpty[T] extends IterableConstraint[T] {
    
    /**
     * Validates whether the given iterable collection is empty.
     *
     * This method checks if the input iterable collection is empty by returning `true` if the collection contains 
     * no elements, and `false` otherwise.
     *
     * @return `true` if the iterable collection is empty, `false` otherwise.
     */
    override val validator: Iterable[T] => Boolean = _.isEmpty
}
