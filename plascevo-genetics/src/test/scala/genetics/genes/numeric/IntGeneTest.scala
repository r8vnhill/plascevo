package cl.ravenhill.plascevo
package genetics.genes.numeric

import cl.ravenhill.composerr.exceptions.{CompositeException, IterableConstraintException}
import cl.ravenhill.plascevo.matchers.containConstraintMessage
import org.scalacheck.{Arbitrary, Gen}

import scala.util.Random

class IntGeneTest extends AbstractPlascevoTest {
    "An IntGene" - {
        "can be created with a value and a range" in {
            forAll(
                Arbitrary.arbInt.arbitrary,
                homogeneousOrderedPairGen(Arbitrary.arbInt.arbitrary)
            ) { (value, range) =>
                val gene = IntGene(value, range) { _ => true }
                gene.value shouldEqual value
                gene.range shouldEqual range
            }
        }

        "can generate a value" in {
            forAll(intGeneGen()(), rngPairGen()) { (gene, rng) =>
                given Random = rng._1

                val actual = gene.generate
                actual should (be >= gene.range._1 and be <= gene.range._2)
                val expected = rng._2.between(gene.range._1, gene.range._2)
                actual shouldEqual expected
            }
        }

        "when averaging" - {
            "throws an exception if the sequence of genes is empty" in {
                forAll(intGeneGen()()) { gene =>
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
                    intGeneGen(Gen.choose(-100, 100))(),
                    Gen.listOfN(10, intGeneGen(Gen.choose(-100, 100))())
                ) { (gene, genes) =>
                    val expectedValue = (genes.map(_.value) :+ gene.value).sum.toDouble / (genes.size + 1)
                    // +/- 1 to avoid rounding errors
                    gene.average(genes).value shouldBe expectedValue.round.toInt +- 1
                }
            }
        }
    }
}

def intGeneGen(
    valueGen: Gen[Int] = Gen.choose(-100, 100), // Narrowing the range
    rangeGen: Gen[(Int, Int)] = homogeneousOrderedPairGen(Gen.choose(-100, 100), strict = true)
)(predicate: Int => Boolean = { _ => true }): Gen[IntGene] = for {
    value <- valueGen
    range <- rangeGen
} yield IntGene(value, range)(predicate)
