package cl.ravenhill.plascevo

import repr.{Feature, Representation}

/** A type alias representing a population of individuals in an evolutionary algorithm.
 *
 * A population is a sequence of individuals, each with a specific representation and fitness value.
 */
type Population[T, F <: Feature[T, F], R <: Representation[T, F]] = Seq[Individual[T, F, R]]

extension[T, F <: Feature[T, F], R <: Representation[T, F]](population: Population[T, F, R])

    /** Retrieves the fitness values of all individuals in the population.
     *
     * This method maps over the population, extracting the fitness value of each individual and returning a list of
     * fitness values.
     *
     * @return A list of fitness values corresponding to the individuals in the population.
     */
    def fitness: List[Double] = population.map(_.fitness).toList
