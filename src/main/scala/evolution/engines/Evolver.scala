package cl.ravenhill.plascevo
package evolution.engines

import evolution.config.EvolutionConfiguration
import evolution.states.EvolutionState
import listeners.EvolutionListener
import repr.{Feature, Representation}

/** A trait that defines the core behavior of an evolutionary algorithm.
 *
 * The `Evolver` trait encapsulates the logic for evolving a population through generations, using a specified
 * configuration that includes limits and listeners. It manages the evolutionary process by iterating through
 * generations until one or more stopping conditions are met.
 *
 * @param evolutionConfiguration The configuration for the evolutionary process, including limits and listeners.
 * @tparam T The type of value stored by the feature.
 * @tparam F The kind of feature stored in the representation, which must implement [[Feature]].
 * @tparam R The type of representation used by the individual, which must implement [[Representation]].
 * @tparam S The type of the evolutionary state, which must extend [[EvolutionState]].
 */
trait Evolver[
    T,
    F <: Feature[T, F],
    R <: Representation[T, F],
    S <: EvolutionState[T, F, R]
](
    evolutionConfiguration: EvolutionConfiguration[T, F, R, S]
) {

    /** The current state of the evolutionary process.
     *
     * This variable holds the current state, which is updated as the process evolves through generations. It is
     * protected, allowing subclasses to access and modify the state as needed.
     */
    protected var currentState: S = evolutionConfiguration.initialState

    /** The listeners for the evolutionary process.
     *
     * These listeners monitor and respond to events during the evolution, such as the start and end of generations.
     */
    private val listeners = evolutionConfiguration.listeners

    /** The limits for the evolutionary process.
     *
     * These limits define the stopping conditions for the evolution. The process will continue until one or more of
     * these conditions are met.
     */
    private val limits = evolutionConfiguration.limits

    /** Evolves the current state through generations until a stopping condition is met.
     *
     * This method manages the entire evolutionary process. It notifies listeners at the start and end of the evolution,
     * as well as at the start and end of each generation. The process continues to iterate through generations until
     * one of the limits is reached.
     *
     * @return The final state after the evolutionary process has completed.
     */
    def evolve(): S = {
        listeners.foreach(_.onEvolutionStart(currentState))
        limits.foreach(_.listener.onEvolutionStart(currentState))

        while (!limits.exists(_.apply(currentState))) {
            listeners.foreach(_.onGenerationStart(currentState))
            limits.foreach(_.listener.onGenerationStart(currentState))
            currentState = iterateGeneration(currentState)
            listeners.foreach(_.onGenerationEnd(currentState))
            limits.foreach(_.listener.onGenerationEnd(currentState))
        }

        listeners.foreach(_.onEvolutionEnd(currentState))
        limits.foreach(_.listener.onEvolutionEnd(currentState))

        currentState
    }

    /** Iterates through a single generation of the evolutionary process.
     *
     * This method is responsible for the logic of evolving the population from one generation to the next. Subclasses
     * should implement this method to define the specifics of the generation iteration.
     *
     * @param state The current state of the evolutionary process.
     * @return The updated state after one generation has been processed.
     */
    protected def iterateGeneration(state: S): S
}
