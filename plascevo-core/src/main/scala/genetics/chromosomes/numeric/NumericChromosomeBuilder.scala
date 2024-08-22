package cl.ravenhill.plascevo
package genetics.chromosomes.numeric

import genetics.chromosomes.{Chromosome, ChromosomeBuilder}
import genetics.genes.numeric.NumericGene

import scala.util.Random

trait NumericChromosomeBuilder[T, G <: NumericGene[T, G]] extends ChromosomeBuilder[T, G] {
    def range: (T, T)

    override final def build()(
        using random: Random
    ): Chromosome[T, G] = {
        ???
    }
}
