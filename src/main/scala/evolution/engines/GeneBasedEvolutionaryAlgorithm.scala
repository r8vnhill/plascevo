package cl.ravenhill.plascevo
package evolution.engines

import evolution.states.EvolutionState
import genetics.Genotype
import genetics.genes.Gene

trait GeneBasedEvolutionaryAlgorithm[T, G <: Gene[T, G]]
    extends Evolver[T, G, Genotype[T, G], EvolutionState[T, G, Genotype[T, G]]]:

    val populationSize: Int

    val survivalRate: Double

