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

        "can retrieve a gene by index" in {
            forAll(simpleChromosomeAndGeneAtIndexGen(simpleGeneGen(), Gen.choose(0, 100))) { tuple =>
                val (chromosome, gene, index) = tuple
                chromosome(index) should be(gene)
            }
        }
    }
}

/**
 * Generates a tuple consisting of a `SimpleChromosome`, a `SimpleGene` from that chromosome at a specific index,
 * and the index of that gene within the chromosome.
 *
 * The `simpleChromosomeAndGeneAtIndexGen` function creates a `SimpleChromosome` with a sequence of `SimpleGene`
 * instances. It ensures that one of the genes in the chromosome is specifically identified and returned as part of the
 * result, along with its index. This generator can be useful for testing scenarios where you need to verify the
 * behavior of operations on a specific gene within a chromosome.
 *
 * @param geneGen A generator for `SimpleGene` instances, used to populate the chromosome.
 * @param sizeGen A generator for the size of the chromosome, determining how many genes it will contain.
 * @return A generator that produces a tuple containing:
 *         - A `SimpleChromosome` populated with `SimpleGene` instances.
 *         - The specific `SimpleGene` located at the generated index within the chromosome.
 *         - The index of the `SimpleGene` within the chromosome.
 */
private def simpleChromosomeAndGeneAtIndexGen(
    geneGen: Gen[SimpleGene],
    sizeGen: Gen[Int]
): Gen[(SimpleChromosome, SimpleGene, Int)] = for {
    size <- sizeGen
    index <- Gen.choose(0, size - 1)
    genesBefore <- Gen.listOfN(index, geneGen)
    geneAtIndex <- geneGen
    genesAfter <- Gen.listOfN(size - index - 1, geneGen)
} yield {
    val genes = genesBefore ++ Seq(geneAtIndex) ++ genesAfter
    val chromosome = SimpleChromosome(genes)
    (chromosome, geneAtIndex, index)
}
