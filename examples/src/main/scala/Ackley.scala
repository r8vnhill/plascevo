package cl.ravenhill.plascevo

/*
 * Copyright (c) 2024, Ignacio Slater M.
 * 2-Clause BSD License.
 */

import evolution.engines.GeneticAlgorithm
import genetics.Genotype
import genetics.genes.numeric.DoubleGene


object Ackley {
    // Constants used in the Ackley function calculation
    private val a = 20.0 // Constant 'A' in the Ackley function
    private val b = 0.2 // Constant 'B' in the Ackley function
    private val c = 2 * math.Pi // Constant 'C' in the Ackley function, representing 2 times Pi
    private val d = 0.5 // Factor used in the Ackley function's square root and exponential components
    private val e = math.exp(1.0) // Euler's number, used in the Ackley function

    private def ackley(genotype: Genotype[Double, DoubleGene]) = genotype.flatten() match {
        case Seq(x, y) =>
            val sum1 = x * x + y * y
            val sum2 = math.cos(c * x) + math.cos(c * y)
            val term1 = -a * math.exp(-b * math.sqrt(sum1))
            val term2 = -math.exp(sum2)
            term1 + term2 + a + e
        case _ => throw new IllegalArgumentException("Genotype must contain exactly 2 genes")
    }

    def main(args: Array[String]): Unit = {
        val engine = GeneticAlgorithm.of(
            ackley,
            Genotype.of(
                ??? // DoubleChromosome(???)
            )
        )
    }
}
