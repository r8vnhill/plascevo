package cl.ravenhill.plascevo
package listeners.summary

import evolution.states.EvolutionState
import listeners.{EvolutionListener, ListenerConfiguration}
import repr.{Feature, Representation}
import utils.{average, maxOfOption, minOfOption}

class EvolutionSummary[T, F <: Feature[T, F], R <: Representation[T, F], S <: EvolutionState[T, F, R, S]](
    configuration: ListenerConfiguration[T, F, R]
) extends EvolutionListener[T, F, R, S](configuration) with  GenerationSummary[T, F, R, S](configuration) {
    private val precision = configuration.precision
    private val evolution = configuration.evolution

    override def toString: String = {
        val generations = evolution.generations
        s"""
            ------------ Evolution Summary ---------------
           |--> Initialization time: ${evolution.initialization.duration} ms
            ------------- Evaluation Times ----------------
           |--> Average: ${
            generations.map {
                _.evaluation.duration
            }.average
        } ms
           |--> Max: ${
            generations.maxOfOption {
                _.evaluation.duration
            }
        } ms
           |--> Min: ${
            generations.minOfOption {
                _.evaluation.duration
            }
        } ms
            -------------- Selection Times ----------------
           |   |--> Offspring Selection
           |   |   |--> Average: ${
            generations.map {
                _.parentSelection.duration
            }.average
        } ms
           |   |   |--> Max: ${
            generations.maxOfOption {
                _.parentSelection.duration
            }
        } ms
           |   |   |--> Min: ${
            generations.minOfOption {
                _.parentSelection.duration
            }
        } ms
           |   |--> Survivor Selection
           |   |   |--> Average: ${
            generations.map {
                _.survivorSelection.duration
            }.average
        } ms
           |   |   |--> Max: ${
            generations.maxOfOption {
                _.survivorSelection.duration
            }
        } ms
           |   |   |--> Min: ${
            generations.minOfOption {
                _.survivorSelection.duration
            }
        } ms
            --------------- Alteration Times --------------
           |--> Average: ${
            generations.map {
                _.alteration.duration
            }.average
        } ms
           |--> Max: ${
            generations.maxOfOption {
                _.alteration.duration
            }
        } ms
           |--> Min: ${
            generations.minOfOption {
                _.alteration.duration
            }
        } ms
            -------------- Evolution Results --------------
           |--> Total time: ${evolution.duration} ms
           |--> Average generation time: ${
            generations.map {
                it.duration
            }.average()
        } ms
           |--> Max generation time: ${
            generations.maxOfOrNull {
                it.duration
            }
        } ms
           |--> Min generation time: ${
            generations.minOfOrNull {
                it.duration
            }
        } ms
           |--> Generation: ${evolution.generations.last().generation}
           |--> Steady generations: ${evolution.generations.last().steady}
           |--> Fittest: ${fittest(ranker, evolution).genotype}
           |--> Best fitness: ${fittest(ranker, evolution).fitness}
            """.stripMargin
    }
}
