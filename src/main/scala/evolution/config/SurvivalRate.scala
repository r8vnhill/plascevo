package cl.ravenhill.plascevo
package evolution.config

/** A case class representing the survival rate in a genetic algorithm.
 *
 * The `SurvivalRate` case class is used to define the survival rate in a genetic algorithm, which determines the
 * proportion of individuals in the population that will survive to the next generation. This parameter plays a critical
 * role in controlling the selection pressure within the algorithm, affecting how aggressively less-fit individuals are
 * removed from the population.
 *
 * @param rate The survival rate, expressed as a value between `0.0` and `1.0`, where `0.0` means no individuals
 *             survive, and `1.0` means all individuals survive.
 *
 * @throws IllegalArgumentException if `rate` is not within the range `[0.0, 1.0]`.
 *
 * <h3>Example:</h3>
 * @example
 * {{{
 * val survivalRate = SurvivalRate(0.5) // 50% of the population will survive to the next generation
 * }}}
 */
case class SurvivalRate(rate: Double) {
    require(rate >= 0.0 && rate <= 1.0, "Survival rate must be between 0.0 and 1.0")
}
