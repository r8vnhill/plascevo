package cl.ravenhill.plascevo
package limits

import evolution.states.EvolutionState
import listeners.{EvolutionListener, ListenerConfiguration}
import repr.{Feature, Representation}

/** A class that applies a limiting condition to an evolutionary process.
 *
 * The `Limit` class encapsulates an `EvolutionListener` and a predicate that determines whether a limit has been
 * reached during the evolutionary process. The limit is defined by applying the predicate to the listener and the
 * current evolutionary state.
 *
 * @param listener  The `EvolutionListener` that monitors the evolutionary process.
 * @param predicate A function that takes an `EvolutionListener` and the current evolutionary state `S`, and returns
 *                  a `Boolean` indicating whether the limit condition has been met.
 * @tparam T The type of value stored by the feature.
 * @tparam F The kind of feature stored in the representation, which must implement [[Feature]].
 * @tparam R The type of representation used by the individual, which must implement [[Representation]].
 * @tparam S The type of the evolutionary state, which must extend [[EvolutionState]].
 */
class Limit[T, F <: Feature[T, F], R <: Representation[T, F], S <: EvolutionState[T, F, R]](
    val listener: EvolutionListener[T, F, R, S],
    val predicate: (EvolutionListener[T, F, R, S], S) => Boolean
) {

    /** Applies the limiting condition to the current state.
     *
     * This method evaluates the predicate using the provided listener and state to determine if the limit has been
     * reached.
     *
     * @param state The current evolutionary state.
     * @return `true` if the limit condition is met, `false` otherwise.
     */
    def apply(state: S): Boolean = predicate(listener, state)
}

/** A function that creates a limiting condition for an evolutionary process.
 *
 * The `limit` function constructs a `Limit` object using a builder function and a predicate. The builder function
 * creates an `EvolutionListener` based on the provided `ListenerConfiguration`, and the predicate defines the condition
 * that determines whether the limit has been reached.
 *
 * @param builder   A function that takes a `ListenerConfiguration` and returns an `EvolutionListener`.
 * @param predicate A function that takes an `EvolutionListener` and the current evolutionary state `S`, and returns
 *                  a `Boolean` indicating whether the limit condition has been met.
 * @tparam T The type of value stored by the feature.
 * @tparam F The kind of feature stored in the representation, which must implement [[Feature]].
 * @tparam R The type of representation used by the individual, which must implement [[Representation]].
 * @tparam S The type of the evolutionary state, which must extend [[EvolutionState]].
 * @return A function that takes a `ListenerConfiguration` and returns a `Limit` object.
 */
def limit[T, F <: Feature[T, F], R <: Representation[T, F], S <: EvolutionState[T, F, R]](
    builder: ListenerConfiguration[T, F, R] => EvolutionListener[T, F, R, S],
    predicate: (EvolutionListener[T, F, R, S], S) => Boolean
): ListenerConfiguration[T, F, R] => Limit[T, F, R, S] = {
    listenerConfiguration => new Limit(builder(listenerConfiguration), predicate)
}
