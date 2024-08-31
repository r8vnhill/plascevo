package cl.ravenhill.plascevo
package evolution.engines

import evolution.config.*
import evolution.states.GeneticEvolutionState
import genetics.genes.Gene
import genetics.{Genotype, GenotypeBuilder}
import listeners.EvolutionListener

import cl.ravenhill.composerr.Constrained
import cl.ravenhill.composerr.Constrained.constrained

import scala.math.*
import scala.util.Random
import scala.util.chaining.*

case class GeneticAlgorithm[
    T,
    G <: Gene[T, G],
    L <: EvolutionListener[T, G, Genotype[T, G], GeneticEvolutionState[T, G]]
](
    populationConfiguration: GeneticPopulationConfiguration[T, G],
    selectionConfiguration: SelectionConfiguration[T, G, Genotype[T, G]],
    alterationConfiguration: AlterationConfiguration[T, G, Genotype[T, G]],
    evolutionConfiguration: EvolutionConfiguration[T, G, Genotype[T, G], GeneticEvolutionState[T, G], L]
)(using random: Random, equalityThreshold: Double) extends AbstractGeneBasedEvolutionaryAlgorithm(
    populationConfiguration,
    selectionConfiguration,
    alterationConfiguration,
    evolutionConfiguration
) {
    override protected def iterateGeneration(
        state: GeneticEvolutionState[T, G]
    ): GeneticEvolutionState[T, G] = {
        val interceptedStart = interceptor.before(state)
        val initializedState = initialize(interceptedStart)
        val evaluatedState = evaluate(initializedState)
        val parents = selectParents(evaluatedState)
        val survivors = selectSurvivors(evaluatedState)
        val offspring = alter(parents)
        val nextPopulation = survivors.population ++ offspring.population
        val nextGeneration = evaluate(evaluatedState.copy(population = nextPopulation))
        interceptor.after(nextGeneration).copy(generation = nextGeneration.generation + 1)
    }

    def initialize(state: GeneticEvolutionState[T, G]): GeneticEvolutionState[T, G] = if (state.isEmpty) {
        // Notify listeners that initialization is starting
        allListeners.foreach(_.onInitializationStart(state))
        // Generate the initial population if the state is empty
        val individuals = Iterator.continually(genotypeBuilder.build())
            .take(populationSize)
            .map(Individual(_))
            .toList
        state.copy(population = individuals).tap { s =>
            // Notify listeners that initialization is ending
            allListeners.foreach(_.onInitializationEnd(s))
        }
    } else {
        // If the state is not empty, return it as is
        state
    }

    def evaluate(state: GeneticEvolutionState[T, G]): GeneticEvolutionState[T, G] = {
        require(
            state.population.size == populationSize,
            "Population size must be equal to the population size of the state"
        )
        allListeners.foreach(_.onEvaluationStart(state))
        val evaluated = evaluator.apply(state).tap { state =>
            require(
                state.size == populationSize,
                "Evaluator must return the same number of individuals as the population size"
            )
            require(state.population.forall(_.isEvaluated), "All individuals must be evaluated")
        }
        allListeners.foreach(_.onEvaluationEnd(evaluated))
        evaluated
    }

    def selectParents(state: GeneticEvolutionState[T, G]): GeneticEvolutionState[T, G] = {
        allListeners.foreach(_.onParentSelectionStart(state))
        val selected = parentSelector(state, floor((1 - survivalRate) * populationSize).toInt, { individuals =>
            state.copy(population = individuals)
        })
        allListeners.foreach(_.onParentSelectionEnd(selected))
        selected
    }

    def selectSurvivors(state: GeneticEvolutionState[T, G]): GeneticEvolutionState[T, G] = {
        allListeners.foreach(_.onSurvivorSelectionStart(state))
        val selected = survivorSelector(state, ceil(survivalRate * populationSize).toInt, { individuals =>
            state.copy(population = individuals)
        })
        allListeners.foreach(_.onSurvivorSelectionEnd(selected))
        selected
    }

    def alter(state: GeneticEvolutionState[T, G]): GeneticEvolutionState[T, G] = {
        allListeners.foreach(_.onAlterationStart(state))
        val altered = alterers.foldLeft(state) { (acc, alterer) =>
            alterer(acc, state.population.size, { individuals =>
                acc.copy(population = individuals)
            })
        }
        allListeners.foreach(_.onAlterationEnd(altered))
        altered
    }
}

object GeneticAlgorithm {
    def of[T, G <: Gene[T, G]](
        fitnessFunction: Genotype[T, G] => Double,
        genotypeBuilder: GenotypeBuilder[T, G]
    ): GeneticAlgorithmBuilder[T, G] = GeneticAlgorithmBuilder(
        fitnessFunction,
        genotypeBuilder
    )
}
