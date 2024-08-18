package cl.ravenhill.plascevo
package evolution.engines

import evolution.config.{EvolutionConfiguration, GeneticPopulationConfiguration, SelectionConfiguration}
import evolution.states.{EvolutionState, GeneticEvolutionState}
import genetics.genes.Gene
import genetics.{Genotype, GenotypeFactory}
import operators.selection.Selector

/** A trait that represents a gene-based evolutionary algorithm.
 *
 * The `GeneBasedEvolutionaryAlgorithm` trait provides the foundational structure for implementing an evolutionary
 * algorithm where the evolution is based on genes. It extends the `Evolver` trait and encapsulates configurations for
 * the population and selection processes. The algorithm leverages these configurations to guide the evolutionary
 * process, including the creation of genotypes and the selection of parents and offspring.
 *
 * @param populationConfiguration The configuration for the population, including the genotype factory and population
 *                                size.
 * @param selectionConfiguration  The configuration for selection, including the survival rate, parent selector, and
 *                                offspring selector.
 * @tparam T The type of value stored by the genes within the genotypes.
 * @tparam G The type of gene that the genotypes hold, which must extend [[Gene]].
 */
abstract class AbstractGeneBasedEvolutionaryAlgorithm[T, G <: Gene[T, G]](
    populationConfiguration: GeneticPopulationConfiguration[T, G],
    selectionConfiguration: SelectionConfiguration[T, G, Genotype[T, G]],
    evolutionConfiguration: EvolutionConfiguration[T, G, Genotype[T, G], GeneticEvolutionState[T, G]]
) extends Evolver[
    T,
    G,
    Genotype[T, G],
    GeneticEvolutionState[T, G]
](evolutionConfiguration) {

    /** The factory used to create genotypes within the evolutionary algorithm.
     *
     * This factory is derived from the population configuration and is responsible for generating the genotypes that
     * make up the individuals in the population.
     */
    protected val genotypeFactory: GenotypeFactory[T, G] = populationConfiguration.representation

    /** The size of the population in the evolutionary algorithm.
     *
     * This value is derived from the population configuration and determines the number of individuals in each
     * generation.
     */
    protected val populationSize: Int = populationConfiguration.populationSize

    /** The rate at which individuals survive to the next generation.
     *
     * This value is derived from the selection configuration and represents the proportion of the population
     * that survives to the next generation. It should be a value between 0.0 and 1.0.
     */
    protected val survivalRate: Double = selectionConfiguration.survivalRate

    /** The selector used to choose parents for reproduction in the evolutionary process.
     *
     * This selector is derived from the selection configuration and is responsible for selecting individuals from the
     * current population to act as parents for generating the next generation.
     */
    val parentSelector: Selector[T, G, Genotype[T, G]] = selectionConfiguration.parentSelector

    /** The selector used to choose offspring for inclusion in the next generation.
     *
     * This selector is derived from the selection configuration and is responsible for selecting the best offspring to
     * include in the next generation, ensuring the continuation of the evolutionary process.
     */
    val offspringSelector: Selector[T, G, Genotype[T, G]] = selectionConfiguration.offspringSelector
}
