package cl.ravenhill.plascevo
package evolution.engines

import evolution.config.EvolutionConfiguration
import evolution.states.EvolutionState
import limits.Limit
import listeners.EvolutionListener
import ranking.IndividualRanker
import repr.{Feature, Representation}

trait Evolver[
    T,
    F <: Feature[T, F],
    R <: Representation[T, F],
    S <: EvolutionState[T, F, R, S],
    L <: EvolutionListener[T, F, R, S]
](evolutionConfiguration: EvolutionConfiguration[T, F, R, S, L]) {
    protected val listeners: Seq[L] = evolutionConfiguration.listeners

    protected val limits: Seq[Limit[T, F, R, S, L]] = evolutionConfiguration.limits

    private var currentState: S = evolutionConfiguration.initialState

    protected val allListeners: Seq[EvolutionListener[T, F, R, S]] = listeners ++ limits.map(_.listener)

    /** Evolves the current state through generations until a stopping condition is met.
     *
     * This method manages the entire evolutionary process. It notifies listeners at the start and end of the evolution,
     * as well as at the start and end of each generation. The process continues to iterate through generations until
     * one of the limits is reached.
     *
     * @return The final state after the evolutionary process has completed.
     */
    def evolve(): S = {
        allListeners.foreach(_.onEvolutionStart(currentState))

        while (!limits.exists(_.apply(currentState))) {
            allListeners.foreach(_.onGenerationStart(currentState))
            currentState = iterateGeneration(currentState)
            allListeners.foreach(_.onGenerationEnd(currentState))
        }

        allListeners.foreach(_.onEvolutionEnd(currentState))

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
