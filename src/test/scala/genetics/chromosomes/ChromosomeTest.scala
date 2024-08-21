package cl.ravenhill.plascevo
package genetics.chromosomes
import genetics.genes.{Gene, SimpleGene, simpleGeneGen}
import matchers.beValid

import org.scalacheck.{Arbitrary, Gen}

import scala.collection.mutable.ListBuffer

class ChromosomeTest extends AbstractPlascevoTest {
    "A Chromosome" - {
        "should have a size property that" - {
            "returns the number of genes in the chromosome" in {
                forAll(chromosomeAndSizeGen(simpleGeneGen())) { (chromosome, size) =>
                    chromosome should have size size
                }
            }
        }

        "when testing for emptiness" - {
            "should return true if the chromosome is empty" in {
                val emptyChromosome = SimpleChromosome(Seq.empty)
                emptyChromosome should be(empty)
            }

            "should return false if the chromosome is not empty" in {
                forAll(chromosomeGen(simpleGeneGen(), 1)) { chromosome =>
                    chromosome should not be empty
                }
            }
        }

        "can be flattened" in {
            forAll(chromosomeGen(simpleGeneGen())) { chromosome =>
                val flattened = chromosome.flatten()
                flattened should be(chromosome.genes.flatMap(_.flatten()))
            }
        }

        "when verifying" - {
            "should return true if all genes are valid" in {
                forAll(chromosomeGen(simpleGeneGen())) { chromosome =>
                    chromosome should beValid
                }
            }

            "should return false if any gene is invalid" in {
                forAll(chromosomeWithInvalidGenes()) { chromosome =>
                    chromosome should not(beValid)
                }
            }
        }

        "can  be folded to the left" in {
            forAll(chromosomeGen(simpleGeneGen())) { chromosome =>
                val folded = chromosome.foldLeft(0)(_ + _)
                val expected = chromosome.genes.map(_.value).sum
                folded should be(expected)
            }
        }

        "can be folded to the right" in {
            forAll(chromosomeGen(simpleGeneGen())) { chromosome =>
                val folded = chromosome.foldRight(0)(_ + _)
                val expected = chromosome.genes.map(_.value).sum
                folded should be(expected)
            }
        }
    }
}

private def simpleChromosomeAndGeneAtIndexGen(geneGen: Gen
