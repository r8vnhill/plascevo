package cl.ravenhill.plascevo
package operators.alteration.mutation

import genetics.chromosomes.{Chromosome, chromosomeGen}
import genetics.genes.{SimpleGene, simpleGeneGen}

import org.scalacheck.Gen

import scala.util.Random

class MutatorTest extends AbstractPlascevoTest {
    "A Mutator" - {
        "when mutating a chromosome" - {
            "should add 1 to each gene's value" in {
                forAll(simpleMutatorGen(chromosomeRate = Gen.const(0.0)), chromosomeGen(simpleGeneGen()), randomGen()) {
                    (mutator, chromosome, random) =>
                        val mutated = mutator.mutateChromosome(chromosome)(using random)
                        mutated.genes should have size chromosome.genes.size
                        mutated.genes.zip(chromosome.genes).foreach { case (mutatedGene, originalGene) =>
                            mutatedGene.value shouldBe originalGene.value + 1
                        }
                }
            }
        }
        
        ""
    }
}

/** Generates a simple mutator for `SimpleGene` instances with customizable mutation rates.
 *
 * The `simpleMutatorGen` function returns a generator (`Gen[Mutator[Int, SimpleGene]]`) that produces instances of a
 * `Mutator` for `SimpleGene` types. The mutator is configured with specified or default mutation rates for individuals
 * and chromosomes, enabling controlled mutations within an evolutionary algorithm.
 *
 * @param individualRate  A generator that produces the probability of mutating an individual within the population.
 *                        Defaults to `probabilityGen`, which generates values between 0.0 and 1.0.
 * @param chromosomeRate  A generator that produces the probability of mutating a chromosome within an individual.
 *                        Defaults to `probabilityGen`, which generates values between 0.0 and 1.0.
 * @return A generator that produces a `Mutator[Int, SimpleGene]` with the specified mutation rates.
 */
def simpleMutatorGen(
    individualRate: Gen[Double] = probabilityGen,
    chromosomeRate: Gen[Double] = probabilityGen
): Gen[Mutator[Int, SimpleGene]] = for {
    ir <- individualRate
    cr <- chromosomeRate
} yield new Mutator[Int, SimpleGene] {
    override val individualRate: Double = ir
    override val chromosomeRate: Double = cr

    /** Mutates a chromosome by incrementing the value of each gene by 1.
     *
     * @param chromosome The chromosome to mutate.
     * @param random     The random number generator used to decide whether to apply mutations.
     * @return A new chromosome where each gene's value is incremented by 1.
     */
    override def mutateChromosome(chromosome: Chromosome[Int, SimpleGene])
        (using random: Random): Chromosome[Int, SimpleGene] =
        chromosome.map(gene => gene.duplicateWithValue(gene.value + 1))
}
