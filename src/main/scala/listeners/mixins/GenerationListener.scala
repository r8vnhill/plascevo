package cl.ravenhill.plascevo
package listeners.mixins

import evolution.states.EvolutionState
import repr.{Feature, Representation}

/** A trait for listening to events during the evolutionary process.
 *
 * The `GenerationListener` trait defines hooks that can be implemented to perform actions at the start and end of each
 * generation during the evolutionary process. This allows for custom behaviors or logging to be injected into the
 * algorithm's lifecycle.
 *
 * @tparam T The type of value stored by the feature.
 * @tparam F The kind of feature stored in the representation, which must implement [[Feature]].
 * @tparam R The type of representation used by the individual, which must implement [[Representation]].
 * @tparam S The type of the evolutionary state, which must extend [[EvolutionState]].
 */
trait GenerationListener[T, F <: Feature[T, F], R <: Representation[T, F], S <: EvolutionState[T, F, R, S]]:

    /** Called at the start of a generation.
     *
     * This method is a hook that is invoked at the beginning of each generation. Subclasses can override this
     * method to perform custom actions such as logging, monitoring, or modifying the state before the generation
     * begins.
     *
     * @param state The current state of the evolutionary process at the start of the generation.
     */
    def onGenerationStart(state: S): Unit = ()

    /** Called at the end of a generation.
     *
     * This method is a hook that is invoked at the end of each generation. Subclasses can override this
     * method to perform custom actions such as logging, monitoring, or analyzing the state after the generation ends.
     *
     * @param state The current state of the evolutionary process at the end of the generation.
     */
    def onGenerationEnd(state: S): Unit = ()
