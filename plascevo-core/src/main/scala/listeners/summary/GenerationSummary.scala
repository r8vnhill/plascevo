package cl.ravenhill.plascevo
package listeners.summary

import evolution.states.EvolutionState
import listeners.{EvolutionListener, ListenerConfiguration}
import listeners.mixins.GenerationListener
import listeners.records.{EvolutionRecord, GenerationRecord, IndividualRecord}
import ranking.IndividualRanker
import repr.{Feature, Representation}

import scala.concurrent.duration.{Deadline, Duration}
import scala.util.chaining.scalaUtilChainingOps

trait GenerationSummary[T, F <: Feature[T, F], R <: Representation[T, F], S <: EvolutionState[T, F, R, S]](
    configuration: ListenerConfiguration[T, F, R]
) extends GenerationListener[T, F, R, S] {
    private val evolution: EvolutionRecord[T, F, R] = configuration.evolution
    private val ranker: IndividualRanker[T, F, R] = configuration.ranker
    private val precision: Duration => Long = configuration.precision
    private val currentGeneration = configuration.currentGeneration

    override def onGenerationStart(state: S): Unit = {
        currentGeneration.value = Some(
            GenerationRecord(evolution.generations.size + 1).tap { record =>
                record.startTime = Some(Deadline.now)
                record.population.parents = Seq.tabulate(state.population.size) { index =>
                    IndividualRecord(state.population(index).representation, state.population(index).fitness)
                }
            }
        )
        evolution.generations += currentGeneration.value.get
    }

    override def onGenerationEnd(state: S): Unit = {
        currentGeneration.map { record =>
            record.duration = precision(Deadline.now - record.startTime.getOrElse(Deadline.now))
            record.population.offspring = Seq.tabulate(state.population.size) { index =>
                IndividualRecord(state.population(index).representation, state.population(index).fitness)
            }
            record.steady = EvolutionListener.computeSteadyGenerations(ranker, evolution)
        }
    }
}
