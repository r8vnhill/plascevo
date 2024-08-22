package cl.ravenhill.plascevo
package evolution.executors.construction

/** A trait that represents an executor for constructing sequences of elements.
 *
 * The `ConstructorExecutor` trait defines an interface for creating sequences of elements of type `T`. It allows for
 * the construction of a sequence of a specified size, where each element is initialized using a function that takes the
 * index of the element and returns the corresponding value.
 *
 * @tparam T The type of elements in the sequence.
 */
trait ConstructorExecutor[T] {

    /** Constructs a sequence of elements of a specified size.
     *
     * This method creates a sequence of elements of type `T`, where each element is initialized using the provided
     * `init` function. The `init` function takes the index of the element in the sequence and returns the corresponding
     * value.
     *
     * @param size The size of the sequence to be created.
     * @param init A function that takes an index and returns the corresponding element of type `T`.
     * @return A sequence of elements of type `T` with the specified size.
     */
    def apply(size: Int, init: (index: Int) => T): Seq[T]
}
