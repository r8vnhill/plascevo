package cl.ravenhill.plascevo
package listeners.records

import repr.{Feature, Representation}

/** A case class that represents a record of an individual in an evolutionary algorithm.
 *
 * The `IndividualRecord` class encapsulates the representation and fitness of an individual, providing a simplified,
 * serializable structure. It also includes a method to convert the record back into an `Individual` instance.
 *
 * @param representation The representation of the individual, encapsulating its features.
 * @param fitness        The fitness value of the individual.
 * @tparam T The type of value stored by the feature.
 * @tparam F The kind of feature stored in the representation, which must implement [[Feature]].
 * @tparam R The type of representation used by the individual, which must implement [[Representation]].
 */
case class IndividualRecord[T, F <: Feature[T, F], R <: Representation[T, F]](
    representation: R,
    fitness: Double
):

    /** Converts this record into an `Individual`.
     *
     * This method creates a new `Individual` instance using the representation and fitness stored in this record.
     *
     * @return An `Individual` with the same representation and fitness as this record.
     */
    def toIndividual(): Individual[T, F, R] = Individual(representation, fitness)

/** Companion object for `IndividualRecord` providing utility methods. */
object IndividualRecord:

    /** Creates an `IndividualRecord` from an `Individual`.
     *
     * This method extracts the representation and fitness from the given `Individual` and wraps them
     * in a new `IndividualRecord`.
     *
     * @param individual The individual to convert into a record.
     * @tparam T The type of value stored by the feature.
     * @tparam F The kind of feature stored in the representation, which must implement [[Feature]].
     * @tparam R The type of representation used by the individual, which must implement [[Representation]].
     * @return An `IndividualRecord` containing the representation and fitness of the given individual.
     */
    def fromIndividual[T, F <: Feature[T, F], R <: Representation[T, F]](
        individual: Individual[T, F, R]
    ): IndividualRecord[T, F, R] = IndividualRecord(individual.representation, individual.fitness)
