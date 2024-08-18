package cl.ravenhill.plascevo
package evolution.executors

/** A factory class for creating instances of `ConstructorExecutor`.
 *
 * The `ConstructorExecutorFactory` class provides a mechanism for creating instances of `ConstructorExecutor[T]`.
 * It allows the creation strategy to be customized by assigning a different `creator` function, which returns a
 * specific implementation of `ConstructorExecutor[T]`. By default, it uses the `SequentialConstructor` to create
 * instances.
 *
 * @tparam T The type of elements that the `ConstructorExecutor` will handle.
 */
class ConstructorExecutorFactory[T] {

    /** A function that creates instances of `ConstructorExecutor[T]`.
     *
     * This variable holds a function that returns an instance of `ConstructorExecutor[T]`. By default, it creates
     * instances of `SequentialConstructor[T]`. The function can be reassigned to provide different creation strategies.
     *
     * @return A new instance of `ConstructorExecutor[T]`.
     */
    var creator: Unit => ConstructorExecutor[T] = { _ => SequentialConstructor[T]() }
}
