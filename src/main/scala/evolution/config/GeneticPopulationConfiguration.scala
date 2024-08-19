package cl.ravenhill.plascevo
package evolution.config

import repr.{Feature, Representation, RepresentationFactory}

import cl.ravenhill.plascevo.genetics.GenotypeBuilder
import cl.ravenhill.plascevo.genetics.genes.Gene

/** A configuration class for defining the parameters of a genetic population in an evolutionary algorithm.
 *
 * The `GeneticPopulationConfiguration` case class encapsulates the configuration needed to create a population of
 * genotypes within a genetic algorithm. It includes a factory for creating genotypes and a size parameter that defines
 * the number of individuals in the population.
 *
 * @param representation The `GenotypeFactory` used to create instances of `Genotype[T, G]` for each individual in the
 *                       population.
 * @param populationSize  The size of the population, i.e., the number of individuals to be created. Must be a positive
 *                        integer.
 * @tparam T The type of value stored by the genes within the genotypes.
 * @tparam G The type of gene that the genotypes hold, which must extend [[Gene]].
 */
case class GeneticPopulationConfiguration[T, G <: Gene[T, G]](
    representation: GenotypeBuilder[T, G],
    populationSize: PopulationSize
)
