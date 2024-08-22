package cl.ravenhill.plascevo
package genetics.genes.numeric

import matchers.containConstraintMessage

import cl.ravenhill.composerr.exceptions.{CompositeException, IterableConstraintException}
import org.scalacheck.{Arbitrary, Gen}

import scala.util.Random

class DoubleGeneTest extends AbstractPlascevoTest {
    "A DoubleGene" - {
        "can be created with a value and a range" in {
            forAll(
                nonNaNDoubleGen(),
                homogeneousOrderedPairGen(Arbitrary.arbDouble.arbitrary, strict = true)
            ) { (value, range) =>
                val gene = DoubleGene(value, range) { _ => true }
                gene.value shouldEqual value
                gene.range shouldEqual range
            }
        }

        "can generate a value" in {
            forAll(doubleGeneGen()(), rngPairGen()) { (gene, rng) =>
                given Random = rng._1

                val actual = gene.generate
                actual should (be >= gene.range._1 and be <= gene.range._2)
                val expected = rng._2.between(gene.range._1, gene.range._2)
                actual shouldEqual expected
            }
        }

        "when averaging" - {
            "throws an exception if the sequence of genes is empty" in {
                forAll(doubleGeneGen()()) { gene =>
                    val ex = intercept[CompositeException] {
                        gene.average(Seq.empty)
                    }
                    ex should containConstraintMessage[IterableConstraintException](
                        "Cannot calculate the average of an empty sequence of genes."
                    )
                }
            }

            "calculates the average value of a sequence of IntGene instances" in {
                forAll(
                    doubleGeneGen(Gen.choose(-100, 100))(),
                    Gen.listOfN(10, doubleGeneGen(Gen.choose(-100, 100))())
                ) { (gene, genes) =>
                    val expectedValue = (genes.map(_.value) :+ gene.value).sum / (genes.size + 1)
                    // +/- 1 to avoid rounding errors
                    gene.average(genes).value shouldBe (expectedValue +- 1)
                }
            }
        }
    }
}

def doubleGeneGen(
    valueGen: Gen[Double] = Gen.choose(-100.0, 100.0), // Narrowing the range
    rangeGen: Gen[(Double, Double)] = homogeneousOrderedPairGen(Gen.choose(-100, 100), strict = true)
)(predicate: Double => Boolean = { _ => true }): Gen[DoubleGene] = for {
    value <- valueGen
    range <- rangeGen
} yield DoubleGene(value, range)(predicate)
