/*
 * Copyright (c) 2024, Ignacio Slater M.
 * 2-Clause BSD License.
 */

package cl.ravenhill.plascevo
package assertions


/** Provides the current thread's `ErrorCollector` instance.
 *
 * The `errorCollector` method returns the `ErrorCollector` instance associated with the current thread. This instance
 * is obtained from a thread-local variable, ensuring that each thread has its own isolated `ErrorCollector` for
 * managing errors. This isolation is crucial in concurrent or parallel processing scenarios, where different threads
 * may encounter different errors that need to be managed independently.
 *
 * @return The `ErrorCollector` instance for the current thread.
 */
def errorCollector: ErrorCollector = threadLocalErrorCollector.get()

/** A thread-local variable that holds an instance of `ThreadLocalErrorCollector`. */
private val threadLocalErrorCollector = ThreadLocal.withInitial(() => new ThreadLocalErrorCollector())

/** A thread-local error collector that extends `BasicErrorCollector`.
 *
 * The `ThreadLocalErrorCollector` class provides a mechanism for managing and collecting errors within a thread-local
 * context. This allows for maintaining separate error states in different threads, which can be useful in concurrent
 * or parallel processing scenarios. The class extends the `BasicErrorCollector` to inherit basic error collection
 * functionality, such as storing errors, managing collection mode, and handling context clues.
 *
 * This class includes a method to create a copy of the current state, allowing for duplication of the error collector
 * with all the accumulated errors, mode, and clues intact.
 */
private class ThreadLocalErrorCollector extends BasicErrorCollector {

    /** Creates a copy of the current `ThreadLocalErrorCollector`.
     *
     * The `copy` method creates a new instance of `ThreadLocalErrorCollector` and copies the current state, including
     * all collected failures, the error collection mode, and any context clues, into the new instance. This can be
     * useful when you need to preserve the state of the error collector while creating a new instance for further use.
     *
     * @return A new instance of `ThreadLocalErrorCollector` with the same state as the original.
     */
    def copy(): ThreadLocalErrorCollector = {
        val result = new ThreadLocalErrorCollector()
        result.failures ++= this.failures
        result.mode = this.mode
        result.clues ++= this.clues
        result
    }
}
