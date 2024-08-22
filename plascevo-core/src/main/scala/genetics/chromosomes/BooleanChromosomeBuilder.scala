package cl.ravenhill.plascevo
package genetics.chromosomes

import exceptions.InvalidProbabilityException
import genetics.genes.{BooleanGene, FalseGene, TrueGene}

import cl.ravenhill.composerr.constrained
import cl.ravenhill.composerr.constraints.doubles.BeInRange

import scala.util.Random

/** A builder class for constructing `BooleanChromosome` instances in a genetic algorithm.
 *
 * The `BooleanChromosomeBuilder` class extends the `ChromosomeBuilder` trait and provides additional configuration
 * options specific to boolean chromosomes. This includes the ability to set the `trueRate`, which controls the
 * likelihood of each gene being `true`. The builder pattern allows for a flexible and fluent interface for constructing
 * chromosomes with custom configurations.
 */
class BooleanChromosomeBuilder extends ChromosomeBuilder[Boolean, BooleanGene] {

    /** A private variable holding the probability that a gene in the chromosome will be `true`.
     *
     * The `_trueRate` variable controls the likelihood of each gene being `true` when the chromosome is created. It
     * must be a value between 0.0 and 1.0, inclusive. The default value is 0.5, meaning there's an equal chance of a
     * gene being `true` or `false`.
     */
    private var _trueRate: Double = 0.5

    /** Sets the true rate for the chromosome.
     *
     * This method allows the user to specify the probability that a gene in the chromosome will be `true`. The method
     * returns the builder instance to enable method chaining.
     *
     * @param trueRate The desired true rate, which must be between 0.0 and 1.0.
     * @return The current instance of the `BooleanChromosomeBuilder` for method chaining.
     * @throws IllegalArgumentException if `trueRate` is not between 0.0 and 1.0.
     */
    def withTrueRate(trueRate: Double): BooleanChromosomeBuilder = {
        constrained {
            "True rate must be between 0.0 and 1.0." ~ (
                trueRate must BeInRange(0.0, 1.0),
                InvalidProbabilityException(_)
            )
        }
        _trueRate = trueRate
        this
    }

    /** Builds and returns a `BooleanChromosome` instance based on the current configuration.
     *
     * The `build` method constructs a `BooleanChromosome` using the specified `trueRate`, size, and executor. Each gene
     * in the chromosome is created with a probability of being `true` based on the `trueRate`.
     *
     * @return A new `BooleanChromosome` instance with the configured boolean genes.
     * @throws NoSuchElementException if the size of the chromosome has not been set.
     */
    override def build()(using random: Random): Chromosome[Boolean, BooleanGene] = {
        BooleanChromosome(
            _executor(
                _size.get,
                _ => if (random.nextDouble() < _trueRate) then TrueGene else FalseGene
            )
        )
    }
}
