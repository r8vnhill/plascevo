package cl.ravenhill.plascevo
package listeners.summary

import evolution.states.EvolutionState
import listeners.{EvolutionListener, ListenerConfiguration}
import repr.{Feature, Representation}
import utils.{average, maxOfOption, minOfOption}

import scala.concurrent.duration.Deadline

class EvolutionSummary[T, F <: Feature[T, F], R <: Representation[T, F], S <: EvolutionState[T, F, R, S]](
    configuration: ListenerConfiguration[T, F, R]
) extends EvolutionListener[T, F, R, S](configuration)
  with GenerationSummary[T, F, R, S](configuration)
  with InitializationSummary[T, F, R, S](configuration)
  with EvaluationSummary[T, F, R, S](configuration)
  with ParentSelectionSummary[T, F, R, S](configuration)
  with SurvivorSelectionSummary[T, F, R, S](configuration)
  with AlterationSummary[T, F, R, S](configuration) {

    private val evolution = configuration.evolution
    private val precision = configuration.precision
    
    override def onEvolutionStart(state: S): Unit = {
        evolution.startTime = Some(Deadline.now)
    }
    
    override def onEvolutionEnd(state: S): Unit = {
        evolution.duration = precision(Deadline.now - evolution.startTime.getOrElse(Deadline.now))
    }
    
    override def toString: String = {
        val generations = evolution.generations
        s"""
           |------------ Evolution Summary ---------------
           |--> Initialization time: ${evolution.initialization.duration} ms
           |------------- Evaluation Times ----------------
           |--> Average: ${generations.map(_.evaluation.duration).average} ms
           |--> Max: ${generations.maxOfOption(_.evaluation.duration).get} ms
           |--> Min: ${generations.minOfOption(_.evaluation.duration).get} ms
           |-------------- Selection Times ----------------
           |--> Offspring Selection
           |   |--> Average: ${generations.map(_.parentSelection.duration).average} ms
           |   |--> Max: ${generations.maxOfOption(_.parentSelection.duration).get} ms
           |   |--> Min: ${generations.minOfOption(_.parentSelection.duration).get} ms
           |--> Survivor Selection
           |   |--> Average: ${generations.map(_.survivorSelection.duration).average} ms
           |   |--> Max: ${generations.maxOfOption(_.survivorSelection.duration).get} ms
           |   |--> Min: ${generations.minOfOption(_.survivorSelection.duration).get} ms
           |--------------- Alteration Times --------------
           |--> Average: ${generations.map(_.alteration.duration).average} ms
           |--> Max: ${generations.maxOfOption(_.alteration.duration).get} ms
           |--> Min: ${generations.minOfOption(_.alteration.duration).get} ms
           |-------------- Evolution Results --------------
           |--> Total time: ${evolution.duration} ms
           |--> Average generation time: ${generations.map(_.duration).average} ms
           |--> Max generation time: ${generations.maxOfOption(_.duration).get} ms
           |--> Min generation time: ${generations.minOfOption(_.duration).get} ms
           |--> Generation: ${evolution.generations.last.generation}
           |--> Steady generations: ${evolution.generations.last.steady}
           |--> Fittest: ${fittest.representation}
           |--> Best fitness: ${fittest.fitness}
            """.stripMargin
    }
}
