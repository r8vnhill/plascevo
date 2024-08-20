package cl.ravenhill.plascevo
package examples

import evolution.engines.GeneticAlgorithm
import genetics.Genotype
import genetics.chromosomes.BooleanChromosome
import genetics.genes.BooleanGene
import limits.{MaxGenerations, TargetFitness}
import listeners.summary.EvolutionSummary
import operators.alteration.crossover.UniformCrossover
import operators.alteration.mutation.BitFlipMutator
import operators.selection.{RouletteWheelSelector, TournamentSelector}

import scala.util.Random

object OneMax {
    def count(genotype: Genotype[Boolean, BooleanGene]): Double = {
        genotype.flatten().count(_ == true)
    }

    def main(args: Array[String]): Unit = {
        given Random = new Random()

        given equalityThreshold: Double = 1e-6

        Domain.toStringMode = ToStringMode.SIMPLE
        
        val engine = GeneticAlgorithm.of(
                count,
                Genotype.of(
                    BooleanChromosome.builder()
                        .withSize(50)
                        .withTrueRate(0.15)
                )
            )
            .withPopulationSize(100)
            .withParentSelector(RouletteWheelSelector())
            .withSurvivorSelector(TournamentSelector())
            .addAlterer(BitFlipMutator())
            .addAlterer(UniformCrossover(chromosomeRate = 0.6))
            .addLimit(MaxGenerations(100))
            .addLimit(TargetFitness(50))
            .addListener(EvolutionSummary(_))
            .build()
        engine.evolve()
        engine.listeners.foreach(_.display)
    }
}
