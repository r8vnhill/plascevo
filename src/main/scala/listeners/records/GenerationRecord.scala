package cl.ravenhill.plascevo
package listeners.records

import repr.{Feature, Representation}

/** A case class that represents the record of a generation in an evolutionary algorithm.
 *
 * The `GenerationRecord` class tracks the details of a specific generation, including its index, the duration of
 * various phases (such as alteration, evaluation, and selection), and the population involved. It extends `TimedRecord`
 * to include timing information for the entire generation and its phases.
 *
 * @param generation The index of the generation. Must be non-negative.
 * @tparam T The type of value stored by the feature.
 * @tparam F The kind of feature stored in the representation, which must implement [[Feature]].
 * @tparam R The type of representation used by the individual, which must implement [[Representation]].
 */
case class GenerationRecord[T, F <: Feature[T, F], R <: Representation[T, F]](
    generation: Int
) extends TimedRecord {
    require(generation >= 0, "Generation must be non-negative")

    /** A private variable that tracks the steady state of the generation.
     *
     * The steady state represents the number of generations in which no significant changes occurred. It is initialized
     * to zero and must be non-negative.
     */
    private var _steady = 0

    /** Retrieves the current steady state value.
     *
     * @return The number of steady generations.
     */
    def steady: Int = _steady

    /** Sets the steady state value.
     *
     * @param value The new steady state value. Must be non-negative.
     * @throws IllegalArgumentException if the value is negative.
     */
    def steady_=(value: Int): Unit = {
        require(value >= 0, "Steady must be non-negative")
        _steady = value
    }

    /** A class that records the timing of the alteration phase within a generation.
     *
     * The `AlterationRecord` class extends `TimedRecord` to include timing information specific to the alteration phase
     * of the evolutionary process.
     */
    class AlterationRecord extends TimedRecord

    /** A class that records the timing of the evaluation phase within a generation.
     *
     * The `EvaluationRecord` class extends `TimedRecord` to include timing information specific to the evaluation phase
     * of the evolutionary process.
     */
    class EvaluationRecord extends TimedRecord

    /** A class that records the timing of the selection phase within a generation.
     *
     * The `SelectionRecord` class extends `TimedRecord` to include timing information specific to the selection phase
     * of the evolutionary process.
     */
    class SelectionRecord extends TimedRecord

    /** A case class that records the parent and offspring populations within a generation.
     *
     * The `PopulationRecord` class extends `TimedRecord` to include timing information and records the parent and
     * offspring populations involved in a specific generation.
     *
     * @param parents  The sequence of parent individuals in the generation.
     * @param offspring The sequence of offspring individuals in the generation.
     */
    case class PopulationRecord(
        parents: Seq[IndividualRecord[T, F, R]],
        offspring: Seq[IndividualRecord[T, F, R]]
    ) extends TimedRecord
}
