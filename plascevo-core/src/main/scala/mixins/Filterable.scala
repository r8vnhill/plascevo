package cl.ravenhill.plascevo
package mixins

/** A trait representing a filterable entity in an evolutionary algorithm.
 *
 * The `Filterable` trait provides a mechanism to filter elements based on a predicate function. It is designed to be
 * implemented by classes that need to perform filtering operations on their internal values or features.
 *
 * The trait defines a `predicate` that determines whether an element should be included in the filtered result.
 *
 * @tparam T The type of value that this trait filters.
 */
trait Filterable[T] {

    /** The predicate used to determine if a value should be included in the filtered result.
     *
     * The `predicate` is a function that takes a value of type `T` and returns `true` if the value meets the condition,
     * or `false` otherwise.
     */
    val predicate: T => Boolean
}
