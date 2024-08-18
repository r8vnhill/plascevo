package cl.ravenhill.plascevo
package listeners.records

import repr.{Feature, Representation}

/** A case class that represents the complete record of an evolutionary process.
 *
 * The `EvolutionRecord` class tracks the entire sequence of generations involved in an evolutionary algorithm.
 * It also records the initialization phase, capturing timing and other relevant details.
 *
 * @param generations A sequence of `GenerationRecord` instances, each representing a generation in the evolutionary
 *                    process.
 * @tparam T The type of value stored by the feature.
 * @tparam F The kind of feature stored in the representation, which must implement [[Feature]].
 * @tparam R The type of representation used by the individual, which must implement [[Representation]].
 */
case class EvolutionRecord[T, F <: Feature[T, F], R <: Representation[T, F]](
    generations: Seq[GenerationRecord[T, F, R]] = Seq.empty[GenerationRecord[T, F, R]]
):

    /** A record of the initialization phase of the evolutionary process.
     *
     * The `InitializationRecord` class extends `TimedRecord` to include timing information specific to the
     * initialization phase, which occurs before any generations are processed. This phase typically involves setting up
     * the initial population and any necessary parameters.
     */
    val initialization = InitializationRecord()

    /** A class that records the timing and details of the initialization phase.
     *
     * The `InitializationRecord` class captures timing information and other relevant data related to the
     * initialization of the evolutionary process. It extends `TimedRecord`, allowing it to track the start time and
     * duration of the initialization phase.
     */
    class InitializationRecord extends TimedRecord

