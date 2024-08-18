package cl.ravenhill.plascevo
package examples

import evolution.engines.GeneticAlgorithmBuilder
import genetics.Genotype
import genetics.chromosomes.BooleanChromosome
import genetics.genes.BooleanGene

object OneMax {
    def count(genotype: Genotype[Boolean, BooleanGene]): Double = {
        genotype.flatten().count(_ == true)
    }

    def main(args: Array[String]): Unit = {
        val engine = GeneticAlgorithmBuilder(
            count,
            Genotype.of(
                BooleanChromosome.builder()
                    .withSize(50)
                    .withTrueRate(0.15)
            )
        )
            .withPopulationSize(100)
//            .withParentSelector()
            .build()
    }
}
