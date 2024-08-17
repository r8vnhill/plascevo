package cl.ravenhill.plascevo
package genetics.genes

import repr.Feature

import scala.util.Random

/** A trait that represents a gene within a genetic algorithm.
 *
 * The `Gene` trait extends the `Feature` trait, encapsulating the behavior and characteristics of a gene, including
 * mutation and value generation. Genes are a fundamental part of the representation of individuals in genetic
 * algorithms.
 *
 * @tparam T The type of value stored by the gene.
 * @tparam G The type of the gene itself, which must extend `Gene`.
 */
trait Gene[T, G <: Gene[T, G]] extends Feature[T, G]:

    /** The generator function used to produce new values for the gene.
     *
     * This function takes the current value of the gene and a `Random` instance, and returns a new value. It is used
     * primarily during mutation to generate the gene's new value.
     */
    val generator: (T, Random) => T

    /** Mutates the gene by generating a new value.
     *
     * This method applies the generator function to the current value of the gene, using a random number generator from
     * the `Domain`. It then creates a duplicate of the gene with this new value.
     *
     * @return A new instance of the gene with the mutated value.
     */
    def mutate(): G = duplicateWithValue(generator(value, Domain.random))

    /** Flattens the gene into a sequence containing its value.
     *
     * This method returns a sequence containing only the current value of the gene, as genes are typically atomic
     * units within the representation.
     *
     * @return A sequence containing the value of the gene.
     */
    override def flatten(): Seq[T] = Seq(value)
