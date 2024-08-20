package cl.ravenhill.plascevo
package evolution.config

import genetics.GenotypeBuilder
import genetics.genes.Gene
import repr.{Feature, Representation, RepresentationFactory}

case class GeneticPopulationConfiguration[T, G <: Gene[T, G]](
    genotypeBuilder: GenotypeBuilder[T, G],
    populationSize: Int
)
