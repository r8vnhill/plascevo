package cl.ravenhill.plascevo

import repr.{Feature, Representation}

/** A type alias representing a population of individuals in an evolutionary algorithm.
 *
 * A population is a sequence of individuals, each with a specific representation and fitness value.
 * 
 * @example
 * {{{
 * val population: Population[Int, SimpleFeature[Int], Representation[Int, SimpleFeature[Int]]] = Seq(
 *     ...
 * )
 * }}}
 */
type Population[T, F <: Feature[T, F], R <: Representation[T, F]] = Seq[Individual[T, F, R]]

extension[T, F <: Feature[T, F], R <: Representation[T, F]](population: Population[T, F, R]) {

    /** Retrieves the fitness values of all individuals in the population.
     *
     * This method maps over the population, extracting the fitness value of each individual and returning a list of
     * fitness values.
     *
     * @return A list of fitness values corresponding to the individuals in the population.
     * @example
     * {{{
     * val population: Population[Int, SimpleFeature[Int], Representation[Int, SimpleFeature[Int]]] = Seq(
     *     Individual(representation1, 0.5),
     *     Individual(representation2, 0.75),
     *     Individual(representation3, 0.25),
     * )
     * val fitnessValues: List[Double] = population.fitness
     * // List(0.5, 0.75, 0.25, ...)
     * }}}
     */
    def fitness: List[Double] = population.map(_.fitness).toList
}