package cl.ravenhill.plascevo
package genetics.chromosomes

import NumericUtilities.{negativeIntGen, nonNegativeIntGen}
import evolution.executors.construction.{AkkaConcurrentConstructor, SequenceActor, SequentialConstructor}
import exceptions.InvalidSizeException
import genetics.genes.SimpleGene
import matchers.containConstraintMessage

import akka.actor.typed.ActorSystem
import cl.ravenhill.composerr.exceptions.CompositeException

import scala.util.Random

class ChromosomeBuilderTest extends AbstractPlascevoTest {
    "A ChromosomeBuilder" - {
        "should have a default executor" in {
            val builder = SimpleChromosomeBuilder()
            builder.executor shouldBe a[SequentialConstructor[?]]
        }

        "can set a custom executor" in {
            val builder = new ChromosomeBuilder[Int, SimpleGene] {
                override def build()(
                    using random: Random
                ): Chromosome[Int, SimpleGene] = {
                    SimpleChromosome(Seq.empty[SimpleGene])
                }
            }
            val executor = new AkkaConcurrentConstructor[SimpleGene](
                ActorSystem[SequenceActor.Command](SequenceActor(), "concurrent-system")
            )
            builder.withExecutor(executor)
            builder.executor shouldBe a[AkkaConcurrentConstructor[?]]
        }

        "has a size property that" - {
            "defaults to None" in {
                val builder = SimpleChromosomeBuilder()
                builder.size shouldBe None
            }

            "throws an exception when set to a negative value" in {
                forAll(negativeIntGen()) { size =>
                    val builder = SimpleChromosomeBuilder()
                    val ex = intercept[CompositeException] {
                        builder.withSize(size)
                    }
                    ex should containConstraintMessage[InvalidSizeException](
                        "Chromosome size must be a non-negative integer."
                    )
                }
            }

            "ca be set to a non negative value" in {
                forAll(nonNegativeIntGen()) { size =>
                    val builder = SimpleChromosomeBuilder()
                    builder.withSize(size)
                    builder.size shouldBe Some(size)
                }
            }
        }
    }
}

/**
 * A builder class for creating instances of `SimpleChromosome`.
 *
 * The `SimpleChromosomeBuilder` class extends the `ChromosomeBuilder` trait, providing functionality to build
 * chromosomes of type `SimpleGene` with integer values. This builder requires that the chromosome size be explicitly
 * set before the `build` method is invoked. If the size is not set, a `NoSuchElementException` will be thrown.
 *
 * @throws NoSuchElementException if the size is not set before calling `build`.
 */
private class SimpleChromosomeBuilder extends ChromosomeBuilder[Int, SimpleGene] {

    /**
     * Builds a `SimpleChromosome` with the specified size.
     *
     * The `build` method generates a sequence of `SimpleGene` instances, each initialized with a random integer value.
     * The size of the chromosome is determined by the `size` property, which must be set prior to calling this method.
     * If the `size` is not set, a `NoSuchElementException` is thrown.
     */
    override def build()(
        using random: Random
    ): Chromosome[Int, SimpleGene] = {
        size match {
            case Some(s) => SimpleChromosome(Seq.fill(s)(SimpleGene(random.nextInt())))
            case None => throw new NoSuchElementException("Size must be set.")
        }
    }
}
