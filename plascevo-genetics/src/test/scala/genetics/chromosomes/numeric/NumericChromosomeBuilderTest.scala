/*
 * Copyright (c) 2024, Ignacio Slater M.
 * 2-Clause BSD License.
 */

package cl.ravenhill.plascevo
package genetics.chromosomes.numeric

import genetics.chromosomes.{Chromosome, SimpleNumericChromosome}
import genetics.genes.numeric.SimpleNumericGene

import org.scalacheck.Gen

import scala.util.Random

class NumericChromosomeBuilderTest extends AbstractPlascevoTest {

    "A NumericChromosomeBuilder" - {
        "should be able to create a chromosome with the specified ranges and filters" in {
            forAll(
                randomGen(),
                rangesAndFiltersGen[Int](
                    Gen.chooseNum(1, 10),
                    homogeneousOrderedPairGen(Gen.chooseNum(-100, 100), strict = true),
                    (min, max) => Gen.oneOf(
                        (value: Int) => value % 2 == 0,
                        (value: Int) => value % 2 != 0
                    )
                )
            ) { case (random, (ranges, filters)) =>
                given Random = random

                val builder = new SimpleNumericChromosomeBuilder()
                    .withSize(math.max(ranges.size, filters.size))
                ranges.foreach(range => builder.addRange(range))
                filters.foreach(filter => builder.addFilter(filter))
                val chromosome = builder.build()
                chromosome.genes.zip(ranges).foreach { case (gene, (min, max)) =>
                    gene.value should (be >= min and be <= max)
                }
            }
        }
    }
}

def rangesAndFiltersGen[T](
    sizeGen: Gen[Int],
    rangeGen: Gen[(T, T)],
    filterGen: (T, T) => Gen[T => Boolean]
): Gen[(Seq[(T, T)], Seq[T => Boolean])] = for {
    size <- sizeGen
    defaultRange <- rangeGen
    ranges <- Gen.frequency(
        (1, Gen.const(Seq.empty)),
        (1, (1 to size).foldLeft(Gen.const(Seq.empty[(T, T)])) { case (gen, _) =>
            for {
                ranges <- gen
                range <- rangeGen
            } yield ranges :+ range
        }),
        (8, (0 until size).foldLeft(Gen.const(Seq.empty[(T, T)])) { case (gen, i) =>
            for {
                ranges <- gen
                range <- rangeGen
            } yield ranges :+ range
        })
    )
    filters <- {
        ranges match
            case Nil | Seq(_) => Gen.frequency(
                (1, Gen.const(Seq.empty)),
                (1, filterGen(defaultRange._1, defaultRange._2).map(Seq(_))),
                (8, Gen.listOfN(size, filterGen(defaultRange._1, defaultRange._2)))
            )
            case _ => Gen.listOfN(size, filterGen(defaultRange._1, defaultRange._2)) 
    }
} yield (ranges, filters)

private class SimpleNumericChromosomeBuilder extends NumericChromosomeBuilder[Int, SimpleNumericGene] {

    override def defaultRange: (Int, Int) = (0, 100)

    /** Creates a `SimpleNumericChromosome` with genes generated based on specified ranges and filters.
     *
     * This method generates a sequence of `SimpleNumericGene` instances by iterating over the provided `ranges` and
     * applying corresponding filters to ensure that each gene's value satisfies the specified constraints. If no
     * filters or a single filter is provided, it will be replicated across all genes.
     *
     * @param ranges  A sequence of `(Int, Int)` tuples representing the minimum and maximum bounds for each gene's
     *                value. Each tuple specifies the range within which the corresponding gene's value should fall.
     * @param filters A sequence of functions, where each function takes an `Int` value and returns a `Boolean`
     *                indicating whether the value satisfies the filter's condition. The filters are applied to ensure
     *                that each gene's value adheres to the specified constraints.
     * @param random  An implicit `Random` instance used to generate random values within the specified ranges. This
     *                allows the method to generate random gene values that conform to the provided filters and ranges.
     * @return A `SimpleNumericChromosome` instance containing the generated genes. Each gene's value will be within its
     *         specified range and will satisfy its corresponding filter condition.
     */
    override def createChromosome(
        ranges: Seq[(Int, Int)],
        filters: Seq[Int => Boolean]
    )(
        using random: Random
    ): Chromosome[Int, SimpleNumericGene] = {
        val resolvedFilters = resolveFilters(filters, ranges.size)
        val genes = ranges.zip(resolvedFilters).map { case ((min, max), filter) =>
            val value = generateValidValue(min, max, filter)
            SimpleNumericGene(value)(_ => true)
        }
        SimpleNumericChromosome(genes)
    }


    /** Resolves the filters based on the provided sequence and its size.
     *
     * @param filters The sequence of filters.
     * @param size    The size of the sequence.
     * @return A sequence of filters resolved to match the size.
     */
    private def resolveFilters(
        filters: Seq[Int => Boolean],
        size: Int
    ): Seq[Int => Boolean] = filters match {
        case Nil => Seq.fill(size)(defaultFilter)
        case single :: Nil => Seq.fill(size)(single)
        case _ => filters
    }

    /** Generates a valid value within the given range that satisfies the filter.
     *
     * @param min    The minimum value of the range.
     * @param max    The maximum value of the range.
     * @param filter The filter function to validate the value.
     * @return A valid integer value within the range that satisfies the filter.
     */
    private def generateValidValue(
        min: Int,
        max: Int,
        filter: Int => Boolean
    ): Int = {
        var value = min
        while (!filter(value)) {
            value = if (value < max) value + 1 else min
        }
        value
    }
}
