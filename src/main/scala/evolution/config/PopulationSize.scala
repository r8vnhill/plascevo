package cl.ravenhill.plascevo
package evolution.config

/** A case class representing the size of a population in a genetic algorithm.
 *
 * The `PopulationSize` case class is used to define the size of the population in a genetic algorithm. The population
 * size determines the number of individuals that will be evolved in each generation of the algorithm. This parameter
 * is crucial as it affects the diversity of the population and the algorithm's ability to explore the solution space.
 *
 * @param size The number of individuals in the population. Must be a positive integer.
 *
 * @throws IllegalArgumentException if `size` is not a positive integer.
 *
 * <h3>Example:</h3>
 * @example
 * {{{
 * val populationSize = PopulationSize(100)
 * }}}
 */
case class PopulationSize(size: Int) {
    require(size > 0, "Population size must be a positive integer")
}
