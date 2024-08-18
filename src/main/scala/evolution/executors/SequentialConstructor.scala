package cl.ravenhill.plascevo
package evolution.executors

/** A class that provides a sequential constructor for creating sequences of elements.
 *
 * The `SequentialConstructor` class extends the `ConstructorExecutor` trait and implements a method for creating a
 * sequence of elements of a specified size. Each element in the sequence is initialized to the same value, provided by
 * the `init` parameter.
 *
 * @tparam T The type of elements in the sequence.
 */
class SequentialConstructor[T] extends ConstructorExecutor[T] {

    /** Constructs a sequence of elements of a specified size, all initialized to the same value.
     *
     * This method creates a sequence of elements of type `T` with the specified size. Each element in the sequence is
     * initialized to the value provided by the `init` parameter. The size must be non-negative.
     *
     * @param size The size of the sequence to be created. Must be non-negative.
     * @param init The value used to initialize each element in the sequence.
     * @return A sequence of elements of type `T`, all initialized to the `init` value.
     * @throws IllegalArgumentException if the size is negative.
     */
    override def apply(size: Int, init: (index: Int) => T): Seq[T] = {
        require(size >= 0, "Size must be non-negative.")
        (0 until size).map(init)
    }
}
