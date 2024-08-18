package cl.ravenhill.plascevo
package evolution.engines

import genetics.{Genotype, GenotypeFactory}

import cl.ravenhill.plascevo.genetics.genes.Gene
import cl.ravenhill.plascevo.operators.selection.{Selector, TournamentSelector}

class GeneticAlgorithmFactory[T, G <: Gene[T, G]](
    val fitnessFunction: Genotype[T, G] => Double,
    val genotypeFactory: GenotypeFactory[T, G],
) {
    private var _populationSize: Int = GeneticAlgorithmFactory.defaultPopulationSize

    def populationSize: Int = _populationSize

    def populationSize_=(size: Int): Unit = {
        require(size > 0, "Population size must be greater than 0")
        _populationSize = size
    }

    private var _survivalRate: Double = GeneticAlgorithmFactory.defaultSurvivalRate

    def survivalRate: Double = _survivalRate
    
    def survivalRate_=(rate: Double): Unit = {
        require(rate >= 0.0 && rate <= 1.0, "Survival rate must be between 0.0 and 1.0")
        _survivalRate = rate
    }
}

object GeneticAlgorithmFactory {
    val defaultPopulationSize: Int = 100
    val defaultSurvivalRate: Double = 0.5
    def defaultParentSelector[T, G <: Gene[T, G]]: Selector[T, G, Genotype[T, G]] = TournamentSelector()
    def defaultOffspringSelector[T, G <: Gene[T, G]]: Selector[T, G, Genotype[T, G]] = TournamentSelector()
}
