package cl.ravenhill.plascevo
package evolution

import evolution.states.EvolutionState
import repr.{Feature, Representation}

/** A class that intercepts the evolutionary process, allowing for custom actions before and after evolution steps.
 *
 * The `EvolutionInterceptor` class provides a mechanism to modify the evolutionary state before and after key steps in
 * the evolutionary process. It takes two functions: one to modify the state before a step (`before`) and one to modify
 * the state after a step (`after`).
 *
 * @param before A function that takes the current evolutionary state and returns a potentially modified state before
 *               the evolution step.
 * @param after  A function that takes the current evolutionary state and returns a potentially modified state after the
 *               evolution step.
 * @tparam T The type of value stored by the feature.
 * @tparam F The type of feature contained within the representation, which must extend [[Feature]].
 * @tparam R The type of representation used by the individual, which must extend [[Representation]].
 * @tparam S The type of the evolutionary state, which must extend [[EvolutionState]].
 */
class EvolutionInterceptor[T, F <: Feature[T, F], R <: Representation[T, F], S <: EvolutionState[T, F, R, S]](
    val before: S => S,
    val after: S => S
)

/** Companion object for `EvolutionInterceptor` providing factory methods for creating interceptors.
 *
 * The `EvolutionInterceptor` object includes utility methods to create interceptors that can modify
 * the evolutionary state before and/or after key steps in the evolution process.
 */
object EvolutionInterceptor {

    /** Creates an identity `EvolutionInterceptor` that does not modify the state.
     *
     * This interceptor returns the state unchanged both before and after the evolution step, effectively acting as a
     * no-op interceptor.
     *
     * @tparam T The type of value stored by the feature.
     * @tparam F The type of feature contained within the representation, which must extend [[Feature]].
     * @tparam R The type of representation used by the individual, which must extend [[Representation]].
     * @tparam S The type of the evolutionary state, which must extend [[EvolutionState]].
     * @return An `EvolutionInterceptor` that returns the state unchanged.
     */
    def identity[
        T,
        F <: Feature[T, F],
        R <: Representation[T, F],
        S <: EvolutionState[T, F, R, S]
    ]: EvolutionInterceptor[T, F, R, S] =
        new EvolutionInterceptor[T, F, R, S](before = s => s, after = s => s)

    /** Creates an `EvolutionInterceptor` that modifies the state only before the evolution step.
     *
     * This interceptor applies the provided `before` function to the state before the evolution step, leaving the state
     * unchanged after the step.
     *
     * @param before A function to modify the state before the evolution step.
     * @tparam T The type of value stored by the feature.
     * @tparam F The type of feature contained within the representation, which must extend [[Feature]].
     * @tparam R The type of representation used by the individual, which must extend [[Representation]].
     * @tparam S The type of the evolutionary state, which must extend [[EvolutionState]].
     * @return An `EvolutionInterceptor` that modifies the state before the evolution step.
     */
    def before[
        T,
        F <: Feature[T, F],
        R <: Representation[T, F],
        S <: EvolutionState[T, F, R, S]
    ](before: S => S): EvolutionInterceptor[T, F, R, S] =
        new EvolutionInterceptor[T, F, R, S](before = before, after = s => s)

    /** Creates an `EvolutionInterceptor` that modifies the state only after the evolution step.
     *
     * This interceptor applies the provided `after` function to the state after the evolution step, leaving the state
     * unchanged before the step.
     *
     * @param after A function to modify the state after the evolution step.
     * @tparam T The type of value stored by the feature.
     * @tparam F The type of feature contained within the representation, which must extend [[Feature]].
     * @tparam R The type of representation used by the individual, which must extend [[Representation]].
     * @tparam S The type of the evolutionary state, which must extend [[EvolutionState]].
     * @return An `EvolutionInterceptor` that modifies the state after the evolution step.
     */
    def after[
        T,
        F <: Feature[T, F],
        R <: Representation[T, F],
        S <: EvolutionState[T, F, R, S]
    ](after: S => S): EvolutionInterceptor[T, F, R, S] =
        new EvolutionInterceptor[T, F, R, S](before = s => s, after = after)
}
