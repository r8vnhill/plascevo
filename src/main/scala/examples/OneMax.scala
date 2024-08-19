package cl.ravenhill.plascevo
package examples

import evolution.engines.GeneticAlgorithm
import genetics.Genotype
import genetics.chromosomes.BooleanChromosome
import genetics.genes.BooleanGene
import limits.{MaxGenerations, TargetFitness}
import operators.alteration.crossover.UniformCrossover
import operators.alteration.mutation.BitFlipMutator
import operators.selection.{RouletteWheelSelector, TournamentSelector}

import cl.ravenhill.plascevo.listeners.summary.EvolutionSummary

import scala.util.Random

object OneMax {
    def count(genotype: Genotype[Boolean, BooleanGene]): Double = {
        genotype.flatten().count(_ == true)
    }

    def main(args: Array[String]): Unit = {
        given Random = new Random()
        given equalityThreshold: Double = 0.0001

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
            .addListener(c => EvolutionSummary(c))
            .build()
        engine.evolve()
        engine.listeners.foreach(_.display)
    }
}
