package cl.ravenhill.plascevo
package examples

import evolution.config.{PopulationSize, SurvivalRate}
import evolution.engines.GeneticAlgorithm
import genetics.Genotype
import genetics.chromosomes.BooleanChromosome
import genetics.genes.BooleanGene

import cl.ravenhill.plascevo.limits.MaxGenerations
import cl.ravenhill.plascevo.operators.alteration.crossover.UniformCrossover
import cl.ravenhill.plascevo.operators.alteration.mutation.BitFlipMutator
import cl.ravenhill.plascevo.operators.selection.{RouletteWheelSelector, TournamentSelector}
import cl.ravenhill.plascevo.ranking.{FitnessMaxRanker, IndividualRanker}

import scala.util.Random

object OneMax {
    def count(genotype: Genotype[Boolean, BooleanGene]): Double = {
        genotype.flatten().count(_ == true)
    }

    def main(args: Array[String]): Unit = { 
        given PopulationSize = PopulationSize(100)
        given SurvivalRate = SurvivalRate(0.7)
        given Random = Random()
        
        val engine = GeneticAlgorithm.of(
            count,
            Genotype.of(
                BooleanChromosome.builder()
                    .withSize(50)
                    .withTrueRate(0.15)
            )
        )
            .withParentSelector(RouletteWheelSelector())
            .withSurvivorSelector(TournamentSelector())
            .addAlterer(BitFlipMutator())
            .addAlterer(UniformCrossover())
            .addLimit(MaxGenerations(100))
            .build
        println(engine.toString)
    }
}
