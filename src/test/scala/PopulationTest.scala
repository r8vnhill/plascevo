package cl.ravenhill.plascevo

import repr.{Feature, Representation, simpleFeatureGen, simpleRepresentationGen}

import org.scalacheck.{Arbitrary, Gen}

import scala.collection.mutable.ListBuffer

class PopulationTest extends AbstractPlascevoTest:
    "A Population of Individuals" - {
        "should have a fitness property that is equal to the list of fitness values of the individuals" in {
            forAll(
                populationAndFitnessGen(
                    individualGen(
                        simpleRepresentationGen(simpleFeatureGen(Arbitrary.arbInt.arbitrary)),
                        nonNaNDoubleGen()
                    )
                )
            ) { (population, fitness) =>
                val populationFitness = population.fitness
                populationFitness shouldBe fitness
            }
        }
    }

private def populationAndFitnessGen[T, F <: Feature[T, F], R <: Representation[T, F]](
    individualGen: Gen[Individual[T, F, R]],
    sizeGen: Gen[Int] = Gen.chooseNum(0, 100)
) = sizeGen.map(size =>
    val fitness = ListBuffer.empty[Double]
    val population = ListBuffer.empty[Individual[T, F, R]]
    for _ <- 0 until size do
        val individual = individualGen.sample.get
        population += individual
        fitness += individual.fitness
    (population.toList, fitness.toList)
)