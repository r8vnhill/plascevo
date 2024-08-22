package cl.ravenhill.plascevo
package genetics

import genetics.chromosomes.{Chromosome, SimpleChromosome, chromosomeGen, chromosomeWithInvalidGenes}
import genetics.genes.{Gene, SimpleGene, simpleGeneGen}
import matchers.beValid

import org.scalacheck.{Arbitrary, Gen}

import scala.collection.mutable.ListBuffer

class GenotypeTest extends AbstractPlascevoTest {
    "A Genotype" - {
        "can be created with a list of chromosomes" in {
            forAll(Gen.listOf(chromosomeGen(simpleGeneGen()))) { chromosomes =>
                val genotype = Genotype(chromosomes)
                genotype.chromosomes should be(chromosomes)
            }
        }

        "can be flattened" in {
            forAll(
                genotypeAndFlattenedChromosomesGen(simpleGeneGen(), SimpleChromosome.apply)
            ) { case (genotype, flattenedGenes) =>
                val flattened = genotype.flatten()
                flattened should be(flattenedGenes.map(_.value))
            }
        }

        "should have a size property that" - {
            "returns the number of chromosomes in the genotype" in {
                forAll(
                    genotypeGen(simpleGeneGen())
                ) { genotype =>
                    genotype should have size (genotype.chromosomes.size)
                }
            }
        }

        "when verifying" - {
            "should return true if all chromosomes are valid" in {
                forAll(Gen.listOf(chromosomeGen(simpleGeneGen()))) { chromosomes =>
                    val genotype = Genotype(chromosomes)
                    genotype should beValid
                }
            }

            "should return false if any chromosome is invalid" in {
                forAll(genotypeWithInvalidChromosomesGen()) { genotype =>
                    genotype should not(beValid)
                }
            }
        }

        "can be folded to the left" in {
            forAll(
                genotypeAndFlattenedChromosomesGen(simpleGeneGen(), SimpleChromosome.apply)
            ) { case (genotype, flattenedGenes) =>
                val folded = genotype.foldLeft(0)(_ + _)
                val expected = flattenedGenes.map(_.value).sum
                folded should be(expected)
            }
        }

        "can be folded to the right" in {
            forAll(
                genotypeAndFlattenedChromosomesGen(simpleGeneGen(), SimpleChromosome.apply)
            ) { case (genotype, flattenedGenes) =>
                val folded = genotype.foldRight(0)(_ + _)
                val expected = flattenedGenes.map(_.value).sum
                folded should be(expected)
            }
        }
    }
}

/** Generates a genotype for an evolutionary algorithm.
 *
 * The `genotypeGen` function produces a generator (`Gen`) that creates a `Genotype` by generating a list of
 * chromosomes. Each chromosome is created using the provided `geneGen` generator.
 *
 * @param geneGen A generator for individual genes of type `G`.
 * @tparam T The type of value held by the genes.
 * @tparam G The type of the gene, which must extend `Gene[T, G]`.
 * @return A generator that produces a `Genotype[T, G]` containing a list of chromosomes generated from the provided
 *         `geneGen`.
 */
def genotypeGen[T, G <: Gene[T, G]](
    geneGen: Gen[SimpleGene],
): Gen[Genotype[Int, SimpleGene]] =
    Gen.listOf(chromosomeGen(geneGen)).map(chromosomes => Genotype(chromosomes))

/** Generates a genotype and its flattened chromosomes for testing purposes.
 *
 * The `genotypeAndFlattenedChromosomesGen` function produces a `Genotype` along with a sequence of its flattened genes.
 * This is useful for testing scenarios where both the structure of the genotype and the individual genes need to be
 * validated. The number of chromosomes and the number of genes in each chromosome are randomly determined within a
 * specified range.
 *
 * @param geneGen           A generator for individual genes of type `G`.
 * @param chromosomeBuilder A function that builds a `Chromosome` from a sequence of genes.
 * @tparam T The type of value held by the genes.
 * @tparam G The type of the gene, which must extend `Gene[T, G]`.
 * @return A tuple containing a `Genotype[T, G]` and a sequence of flattened genes from the genotype.
 */
private def genotypeAndFlattenedChromosomesGen[T, G <: Gene[T, G]](
    geneGen: Gen[G],
    chromosomeBuilder: Seq[G] => Chromosome[T, G]
): (Genotype[T, G], Seq[G]) = {
    val genotypeSize = Gen.chooseNum(0, 10).sample.get
    val chromosomes = ListBuffer.empty[Chromosome[T, G]]
    val flattenedGenes = ListBuffer.empty[G]

    for (_ <- 0 until genotypeSize) {
        val chromosomeSize = Gen.chooseNum(0, 10).sample.get
        val genes = ListBuffer.empty[G]

        for (_ <- 0 until chromosomeSize) {
            val gene = geneGen.sample.get
            genes += gene
            flattenedGenes += gene
        }

        chromosomes += chromosomeBuilder(genes.toSeq)
    }

    (Genotype(chromosomes.toSeq), flattenedGenes.toSeq)
}

/** Generates a genotype with at least one invalid chromosome for testing purposes.
 *
 * The `genotypeWithInvalidChromosomes` function produces a generator (`Gen`) that creates a `Genotype` containing list
 * of chromosomes, where at least one chromosome is guaranteed to be invalid. This is useful for testing scenarios where
 * the validation logic of evolutionary algorithms needs to be verified.
 *
 * The invalid chromosome is generated and prepended to the list of valid chromosomes.
 *
 * @return A generator that produces a `Genotype[Int, SimpleGene]` with at least one invalid chromosome.
 */
def genotypeWithInvalidChromosomesGen(): Gen[Genotype[Int, SimpleGene]] = {
    val chromosomes = Gen.listOf(chromosomeGen(simpleGeneGen(Arbitrary.arbBool.arbitrary)))
    val invalid = chromosomeWithInvalidGenes().sample.get
    chromosomes.map(chromosomes => Genotype(invalid :: chromosomes))
}
