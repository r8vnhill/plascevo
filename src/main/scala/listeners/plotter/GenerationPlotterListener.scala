package cl.ravenhill.plascevo
package listeners.plotter

import evolution.states.EvolutionState
import listeners.ListenerConfiguration
import listeners.mixins.GenerationListener
import listeners.records.{GenerationRecord, IndividualRecord}
import repr.{Feature, Representation}

trait GenerationPlotterListener[T, F <: Feature[T, F], R <: Representation[T, F], S <: EvolutionState[T, F, R, S]](
    configuration: ListenerConfiguration[T, F, R]
) extends GenerationListener[T, F, R, S] {

    private val currentGeneration = configuration.currentGeneration
    private val evolution = configuration.evolution

    override def onGenerationStart(state: S): Unit = {
        currentGeneration.value = Some(
            GenerationRecord(state.generation)
        )
        evolution.generations += currentGeneration.value.get
    }

    override def onGenerationEnd(state: S): Unit = {
        currentGeneration.map { record =>
            record.population.offspring = List.tabulate(state.population.size) { index =>
                IndividualRecord.fromIndividual(state.population(index))
            }
        }
    }
}
