package cl.ravenhill.plascevo
package genetics.chromosomes

import genetics.genes.{Gene, SimpleGene, simpleGeneGen}
import matchers.beValid

import org.scalacheck.{Arbitrary, Gen}

import scala.collection.mutable.ListBuffer

class ChromosomeTest extends AbstractPlascevoTest:
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
                flattened should be(chromosome.map(_.value))
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
                val expected = chromosome.map(_.value).sum
                folded should be(expected)
            }
        }

        "can be folded to the right" in {
            forAll(chromosomeGen(simpleGeneGen())) { chromosome =>
                val folded = chromosome.foldRight(0)(_ + _)
                val expected = chromosome.map(_.value).sum
                folded should be(expected)
            }
        }
    }

    /** Generates a chromosome for an evolutionary algorithm.
     *
     * The `chromosomeGen` function produces a generator (`Gen`) that creates a `Chromosome` by sampling genes from the
     * provided `geneGen` generator. The number of genes in the chromosome is determined by a random size
     * between the specified minimum and maximum values.
     *
     * This function is a simplified version of `chromosomeAndSizeGen`, returning only the generated `Chromosome`
     * without its size.
     *
     * @param geneGen A generator for individual genes of type `G`.
     * @param minSize The minimum number of genes in the chromosome. Defaults to `0`.
     * @param maxSize The maximum number of genes in the chromosome. Defaults to `10`.
     * @tparam T The type of value held by the genes.
     * @tparam G The type of the gene, which must extend `Gene[T, G]`.
     * @return A generator that produces a `Chromosome[T, G]` with a random number of genes between `minSize` and `maxSize`.
     */
    private def chromosomeGen[T, G <: Gene[T, G]](
        geneGen: Gen[G],
        minSize: Int = 0,
        maxSize: Int = 10
    ): Gen[Chromosome[T, G]] = chromosomeAndSizeGen(geneGen, minSize, maxSize).map(_._1)


    /** Generates a chromosome and its size for an evolutionary algorithm.
     *
     * The `chromosomeAndSizeGen` function produces a generator (`Gen`) that creates a tuple consisting of a `Chromosome`
     * and its size. The chromosome is generated by sampling genes from the provided `geneGen` generator, with the number of
     * genes determined by a random size between the specified minimum and maximum values.
     *
     * @param geneGen A generator for individual genes of type `G`.
     * @param minSize The minimum number of genes in the chromosome. Defaults to `0`.
     * @param maxSize The maximum number of genes in the chromosome. Defaults to `10`.
     * @tparam T The type of value held by the genes.
     * @tparam G The type of the gene, which must extend `Gene[T, G]`.
     * @return A generator that produces a tuple containing a `Chromosome[T, G]` and its size.
     */
    private def chromosomeAndSizeGen[T, G <: Gene[T, G]](
        geneGen: Gen[G],
        minSize: Int = 0,
        maxSize: Int = 10
    ): Gen[(Chromosome[T, G], Int)] =
        Gen.chooseNum(minSize, maxSize).flatMap { size =>
            val genes = ListBuffer.empty[G]
            for _ <- 0 until size do
                genes += geneGen.sample.get
            (new Chromosome[T, G](genes.toSeq) {}, size)
        }

    /** Generates a chromosome with at least one invalid gene for testing purposes.
     *
     * The `chromosomeWithInvalidGenes` function creates a generator that produces a `SimpleChromosome` containing a
     * sequence of genes, with at least one gene guaranteed to be invalid. The number of genes in the chromosome is
     * determined by a random size between the specified minimum and maximum values.
     *
     * @param minSize The minimum number of genes in the chromosome. Defaults to `0`.
     * @param maxSize The maximum number of genes in the chromosome. Defaults to `10`.
     * @param isValid A generator that produces boolean values indicating the validity of each gene.
     *                The last gene in the chromosome is always invalid (`false`).
     * @return A generator that produces a tuple containing a `SimpleChromosome` with invalid genes and the size of the
     *         chromosome.
     */
    private def chromosomeWithInvalidGenes(
        minSize: Int = 0, maxSize: Int = 10, isValid: Gen[Boolean] = Arbitrary.arbBool.arbitrary
    ): Gen[SimpleChromosome] =
        Gen.chooseNum(minSize, maxSize).flatMap { size =>
            val genes = ListBuffer.empty[SimpleGene]
            for _ <- 0 until size do
                genes += simpleGeneGen(isValid).sample.get
            // Ensure the chromosome has at least one invalid gene.
            genes += simpleGeneGen(Gen.const(false)).sample.get
            SimpleChromosome(genes.toSeq)
        }

    /** Represents a simple chromosome for testing purposes. */
    case class SimpleChromosome(genes: Seq[SimpleGene]) extends Chromosome[Int, SimpleGene](genes)
