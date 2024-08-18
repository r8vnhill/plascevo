package cl.ravenhill.plascevo
package evolution.engines

import evolution.config.EvolutionConfiguration
import evolution.states.SimpleState
import limits.Limit
import listeners.EvolutionListener
import repr.{Representation, SimpleFeature}

class EvolverTest extends AbstractPlascevoTest {
    "An evolver" - {
//        "can evolve a population" in {
//            val configuration = new EvolutionConfiguration(
//                listeners = List(new SimpleListener),
//                limits = List(new SimpleLimit),
//                initialState = SimpleState(
//                    population = List.empty,
//                    ranker = null,
//                    generation = 0
//                )
//            )
//            configuration.listeners.head.asInstanceOf[SimpleListener].evolutionStarted shouldBe false
//            configuration.listeners.head.asInstanceOf[SimpleListener].evolutionEnded shouldBe false
//            configuration.limits.head(configuration.initialState) shouldBe false
//            val evolver = new SimpleEvolver(configuration)
//            val finalState = evolver.evolve()
//            finalState.population should have size 0
//            configuration.listeners.head.asInstanceOf[SimpleListener].evolutionStarted shouldBe true
//            configuration.listeners.head.asInstanceOf[SimpleListener].evolutionEnded shouldBe true
//            configuration.limits.head(finalState) shouldBe true
//        }
    }
}

private class SimpleEvolver(
    configuration: EvolutionConfiguration[
        Int,
        SimpleFeature[Int],
        Representation[Int, SimpleFeature[Int]],
        SimpleState[Int, SimpleFeature[Int], Representation[Int, SimpleFeature[Int]]]
    ]
) extends Evolver[
    Int,
    SimpleFeature[Int],
    Representation[Int, SimpleFeature[Int]],
    SimpleState[Int, SimpleFeature[Int], Representation[Int, SimpleFeature[Int]]]
](configuration) {
    override protected def iterateGeneration(
        state: SimpleState[Int, SimpleFeature[Int], Representation[Int, SimpleFeature[Int]]]
    ): SimpleState[Int, SimpleFeature[Int], Representation[Int, SimpleFeature[Int]]] = state
}

private class SimpleListener
    extends EvolutionListener[
        Int,
        SimpleFeature[Int],
        Representation[Int, SimpleFeature[Int]],
        SimpleState[Int, SimpleFeature[Int], Representation[Int, SimpleFeature[Int]]]
    ] {
    var evolutionStarted = false
    var evolutionEnded = false

    override def onEvolutionStart(
        state: SimpleState[Int, SimpleFeature[Int], Representation[Int, SimpleFeature[Int]]]
    ): Unit = evolutionStarted = true

    override def onEvolutionEnd(
        state: SimpleState[Int, SimpleFeature[Int], Representation[Int, SimpleFeature[Int]]]
    ): Unit = evolutionEnded = true
}

private class GenerationCounterListener
    extends EvolutionListener[
        Int,
        SimpleFeature[Int],
        Representation[Int, SimpleFeature[Int]],
        SimpleState[Int, SimpleFeature[Int], Representation[Int, SimpleFeature[Int]]]
    ] {
    var generationStarted = 0
    var generationEnded = 0

    override def onGenerationStart(
        state: SimpleState[Int, SimpleFeature[Int], Representation[Int, SimpleFeature[Int]]]
    ): Unit = generationStarted += 1

    override def onGenerationEnd(
        state: SimpleState[Int, SimpleFeature[Int], Representation[Int, SimpleFeature[Int]]]
    ): Unit = generationEnded += 1
}

private class SimpleLimit extends Limit[
    Int,
    SimpleFeature[Int],
    Representation[Int, SimpleFeature[Int]],
    SimpleState[Int, SimpleFeature[Int], Representation[Int, SimpleFeature[Int]]],
    GenerationCounterListener
](
    listener = new GenerationCounterListener,
    predicate = (listener, state) => listener.generationStarted >= 10
)
