package cl.ravenhill.plascevo
package genetics.genes.numeric

import matchers.beValid

import org.scalacheck.{Arbitrary, Gen}

import scala.util.Random

class NumericGeneTest extends AbstractPlascevoTest {
    given Random = new Random()

    "A NumericGene" - {
        "can be mutated" in {
            val positivePredicate: Int => Boolean = _ > 0
            val evenPredicate: Int => Boolean = _ % 2 == 0
            val lessThanHundredPredicate: Int => Boolean = _ < 100

            forAll(numericGeneGen()(positivePredicate)) { gene =>
                val mutatedGene = gene.mutate()
                mutatedGene.value should be > 0
                mutatedGene should beValid
            }

            forAll(numericGeneGen()(evenPredicate)) { gene =>
                val mutatedGene = gene.mutate()
                mutatedGene.value % 2 shouldEqual 0
                mutatedGene should beValid
            }

            forAll(numericGeneGen()(lessThanHundredPredicate)) { gene =>
                val mutatedGene = gene.mutate()
                mutatedGene.value should be < 100
                mutatedGene should beValid
            }
        }
    }
}

/**
 * Generates a `SimpleNumericGene` using a provided generator for the gene's value.
 *
 * The `numericGeneGen` function is a generator that creates instances of `SimpleNumericGene`. It accepts a generator
 * for the gene's value and an optional predicate that acts as a validation function for the gene's value. The predicate
 * defaults to a function that always returns `true`, meaning no validation is applied by default.
 *
 * @param gen       The generator used to produce the value for the gene.
 * @param predicate A predicate function that validates the generated value. Defaults to a function that always returns
 *                  `true`.
 * @return A generator for `SimpleNumericGene` instances.
 */
def numericGeneGen(gen: Gen[Int] = Gen.chooseNum(-1000, 1000))
    (predicate: Int => Boolean = { _ => true }): Gen[SimpleNumericGene] = for {
    value <- gen
} yield SimpleNumericGene(value)(predicate)

/**
 * A simple implementation of a numeric gene.
 *
 * The `SimpleNumericGene` class extends the `NumericGene` trait, providing a basic implementation for genes that store
 * integer values. Each gene is validated against a predicate function, which determines whether the gene's value is
 * considered valid.
 *
 * @param value     The integer value stored by the gene.
 * @param predicate A predicate function that validates the gene's value. If the predicate returns `true`, the gene is
 *                  considered valid; otherwise, it is invalid.
 */
class SimpleNumericGene(override val value: Int)(override val predicate: Int => Boolean)
    extends NumericGene[Int, SimpleNumericGene] {

    private var increased: Int = 0

    /**
     * Creates a duplicate of this gene with a new value.
     *
     * The `duplicateWithValue` method returns a new instance of `SimpleNumericGene` with the specified value,
     * retaining the original predicate function for validation.
     *
     * @param value The new value for the duplicated gene.
     * @return A new `SimpleNumericGene` instance with the specified value.
     */
    override def duplicateWithValue(value: Int): SimpleNumericGene = new SimpleNumericGene(value)(predicate)

    /**
     * Generates a random integer based on the current value, using the provided random number generator.
     *
     * @param random The random number generator to use for generating the integer.
     * @return The generated random integer.
     */
    override def generator(using random: Random): Int = {
        increased += 15
        value + increased
    }

    /**
     * Calculates the average value of a sequence of `SimpleNumericGene` instances.
     *
     * The `average` method takes a sequence of `SimpleNumericGene` instances, calculates the average of their values,
     * and returns a new `SimpleNumericGene` instance with the averaged value. The original predicate function is
     * retained for the new gene.
     *
     * @param genes A sequence of `SimpleNumericGene` instances to average.
     * @return A new `SimpleNumericGene` instance with the average value.
     */
    override def average(
        genes: Seq[SimpleNumericGene]
    ): SimpleNumericGene = {
        val sum = genes.map(_.value).sum
        val avg = sum / genes.size
        new SimpleNumericGene(avg)(predicate)
    }

    /**
     * Converts the gene's value to a `Double`.
     *
     * The `toDouble` method returns the gene's value as a `Double`.
     *
     * @return The value of the gene as a `Double`.
     */
    override def toDouble: Double = value.toDouble

    /**
     * Converts the gene's value to an `Int`.
     *
     * The `toInt` method returns the gene's value as an `Int`.
     *
     * @return The value of the gene as an `Int`.
     */
    override def toInt: Int = value
}
