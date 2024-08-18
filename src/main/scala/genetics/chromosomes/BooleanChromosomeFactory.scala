package cl.ravenhill.plascevo
package genetics.chromosomes

import genetics.genes.{BooleanGene, FalseGene, TrueGene}

/** A factory class for creating `BooleanChromosome` instances in a genetic algorithm.
 *
 * The `BooleanChromosomeFactory` class extends the `ChromosomeFactory` trait and is responsible for producing
 * chromosomes composed of boolean genes. It allows the configuration of a `trueRate`, which controls the likelihood
 * of a gene being `true` when the chromosome is created.
 */
class BooleanChromosomeFactory extends ChromosomeFactory[Boolean, BooleanGene] {

    /** The probability that a gene in the chromosome will be `true`.
     *
     * This value represents the likelihood of each gene being `true` when the chromosome is created. It must be a value
     * between 0.0 and 1.0, inclusive. The default value is 0.5, meaning there's an equal chance of a gene being `true`
     * or `false`.
     */
    var trueRate: Double = 0.5

    /** Creates a new `BooleanChromosome` with a sequence of boolean genes.
     *
     * This method generates a chromosome where each gene has a probability of being `true` based on the `trueRate`.
     * The size of the chromosome is determined by the `size` property of the factory, and each gene is created using
     * the executor provided by the `ChromosomeFactory` trait.
     *
     * @return A `BooleanChromosome` instance with the generated boolean genes.
     * @throws IllegalArgumentException if `trueRate` is not between 0.0 and 1.0.
     */
    override def make(): Chromosome[Boolean, BooleanGene] = {
        require(trueRate >= 0.0 && trueRate <= 1.0, "True rate must be between 0.0 and 1.0.")
        BooleanChromosome(
            executor(
                size.get,
                _ => if (Domain.random.nextDouble() < trueRate) then TrueGene else FalseGene
            )
        )
    }
}
