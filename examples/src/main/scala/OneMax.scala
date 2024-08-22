package cl.ravenhill.plascevo

/*
 * Copyright (c) 2024, Ignacio Slater M.
 * 2-Clause BSD License.
 */

import evolution.engines.GeneticAlgorithm
import evolution.executors.construction.{AkkaConcurrentConstructor, SequenceActor}
import genetics.Genotype
import genetics.chromosomes.BooleanChromosome
import genetics.genes.BooleanGene
import limits.{MaxGenerations, TargetFitness}
import listeners.plotter.EvolutionPlotter
import listeners.summary.EvolutionSummary
import operators.selection.{RouletteWheelSelector, TournamentSelector}

import akka.actor.typed.ActorSystem
import cl.ravenhill.plascevo.operators.crossover.UniformCrossover
import cl.ravenhill.plascevo.operators.mutation.BitFlipMutator

import scala.util.Random

/** An example of a genetic algorithm implementation for solving the One Max problem.
 *
 * The `OneMax` object provides an implementation of a genetic algorithm designed to solve the One Max problem, a
 * classic optimization problem in genetic algorithms. The goal of the One Max problem is to maximize the number of
 * `true` values in a binary string (or array of Boolean values). This problem is often used to test and demonstrate the
 * effectiveness of genetic algorithms.
 *
 * The `count` method defines the fitness function for the genetic algorithm, which counts the number of `true` values
 * in a given genotype.
 */
object OneMax {

    /** Counts the number of `true` values in the given genotype.
     *
     * The `count` method serves as the fitness function for the One Max problem. It evaluates a genotype (a sequence of
     * Boolean genes) and returns the number of `true` values it contains. The goal of the genetic algorithm is to
     * maximize this count, thereby solving the One Max problem.
     *
     * @param genotype The genotype to be evaluated, consisting of a sequence of Boolean genes.
     * @return The number of `true` values in the genotype.
     */
    def count(genotype: Genotype[Boolean, BooleanGene]): Double = {
        genotype.flatten().count(_ == true)
    }

    /** The main entry point for running the One Max genetic algorithm example.
     *
     * The `main` method sets up and executes a genetic algorithm designed to solve the One Max problem. It initializes
     * the genetic algorithm with a population of individuals represented by their genotype, each represented by a
     * sequence of Boolean chromosomes. Various genetic operators, such as selection, mutation, and crossover, are
     * applied to evolve the population over several generations. The algorithm aims to maximize the number of `true`
     * values in each genotype, ultimately finding a solution that contains only `true` values.
     *
     * The genetic algorithm is configured with the following components:
     * - **Population Size:** 100 individuals.
     * - **Chromosome Configuration:** Each chromosome has a size of 50 genes, with an initial `true` rate of 15%.
     * - **Parent Selector:** Roulette Wheel Selection.
     * - **Survivor Selector:** Tournament Selection.
     * - **Alterers:** Includes a Bit Flip Mutator and Uniform Crossover with a 60% chromosome rate.
     * - **Limits:** The algorithm runs for a maximum of 100 generations or until a fitness score of 50 is achieved.
     * - **Listeners:** Includes an `EvolutionSummary`
     */
    def main(args: Array[String]): Unit = {
        // Set up the random number generator utilized by the genetic algorithm
        given Random = new Random()

        // Set the equality threshold for floating-point comparisons
        given equalityThreshold: Double = 1e-6

        // Set the string representation mode to simple for concise output
        Domain.toStringMode = ToStringMode.SIMPLE

        val engine = GeneticAlgorithm.of(
                count,
                Genotype.of(
                    BooleanChromosome.builder()
                        .withSize(50)
                        .withTrueRate(0.15)
                        .withExecutor(
                            AkkaConcurrentConstructor[BooleanGene](
                                ActorSystem[SequenceActor.Command](SequenceActor(), "concurrent-system")
                            )
                        )
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
            .addListener(EvolutionPlotter(_))
            .build()

        engine.evolve()
        engine.listeners.foreach(_.display)
    }
}
