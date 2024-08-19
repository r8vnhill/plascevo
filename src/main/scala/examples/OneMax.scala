package cl.ravenhill.plascevo
package examples

import evolution.engines.GeneticAlgorithm
import genetics.Genotype
import genetics.chromosomes.BooleanChromosome
import genetics.genes.BooleanGene
import limits.MaxGenerations
import operators.alteration.crossover.UniformCrossover
import operators.alteration.mutation.BitFlipMutator
import operators.selection.{RouletteWheelSelector, TournamentSelector}

import scala.util.Random

object OneMax {
    def count(genotype: Genotype[Boolean, BooleanGene]): Double = {
        genotype.flatten().count(_ == true)
    }

    def main(args: Array[String]): Unit = {
        given random: Random = new Random()

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
            .build()
        println(engine.toString)
    }
}
