package cl.ravenhill.plascevo
package genetics

import genetics.chromosomes.{Chromosome, ChromosomeBuilder}
import genetics.genes.Gene
import repr.RepresentationFactory

import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.util.Random

/** A factory class for creating genotypes in a genetic algorithm.
 *
 * The `GenotypeFactory` class extends the `RepresentationFactory` trait and is responsible for creating instances of
 * `Genotype[T, G]`. It maintains a collection of `ChromosomeFactory[T, G]` instances, which are used to create the
 * chromosomes that make up the genotype.
 *
 * @tparam T The type of value stored by the genes within the chromosomes.
 * @tparam G The type of gene that the chromosomes hold, which must extend [[Gene]].
 */
class GenotypeBuilder[T, G <: Gene[T, G]] extends RepresentationFactory[T, G, Genotype[T, G]] {

    /** A list buffer that stores the chromosome factories used to create chromosomes for the genotype.
     *
     * This variable holds a mutable list buffer of `ChromosomeFactory[T, G]` instances. Each factory in this list is
     * responsible for creating a single chromosome within the genotype. The order and number of factories in this list
     * determine the structure of the resulting genotype.
     */
    private val chromosomes = ListBuffer.empty[ChromosomeBuilder[T, G]]

    def addChromosome(chromosomeFactory: ChromosomeBuilder[T, G]): GenotypeBuilder[T, G] = {
        chromosomes += chromosomeFactory
        this
    }
    
    /** Creates a new instance of `Genotype[T, G]`.
     *
     * This method generates a genotype by invoking the `make` method of each `ChromosomeFactory` in the `chromosomes`
     * list buffer. The resulting sequence of chromosomes is then used to construct and return a new `Genotype[T, G]`.
     *
     * @return A new instance of `Genotype[T, G]` containing the chromosomes created by the chromosome factories.
     */
    override def build()(using random: Random): Genotype[T, G] = {
        val chromosomeSeq = chromosomes.map(_.build()).toSeq
        Genotype(chromosomeSeq)
    }

    override def toString: String = s"GenotypeBuilder(chromosomes = $chromosomes)"
}
