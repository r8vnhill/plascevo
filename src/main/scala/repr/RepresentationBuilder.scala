package cl.ravenhill.plascevo
package repr

import scala.util.Random

/** Represents a builder for creating instances of a specific [[Representation]] in an evolutionary algorithm.
 *
 * The `RepresentationBuilder` trait defines an interface for constructing instances of a representation, which
 * encapsulates the features of an individual in an evolutionary algorithm. This trait is useful in scenarios where
 * representations need to be dynamically generated or customized during the execution of the algorithm, often based
 * on random values or other runtime parameters.
 *
 * Implementing classes or traits must provide a concrete implementation of the [[build]] method, which is responsible
 * for creating and returning an instance of the representation type [[R]]. The `build` method is designed to work
 * within the context of a random number generator, allowing for stochastic construction of representations.
 *
 * @tparam T The type of value stored by the features within the representation.
 * @tparam F The type of feature contained within the representation, which must extend [[Feature]].
 * @tparam R The type of representation to be built, which must extend [[Representation]].
 *
 * @example
 * {{{
 * // Example implementation for a simple chromosome builder
 * class SimpleChromosomeBuilder extends RepresentationBuilder[Int, SimpleGene, SimpleChromosome] {
 *   override def build()(using random: Random): SimpleChromosome = {
 *     val genes = Seq.fill(10)(SimpleGene(random.nextInt(100)))
 *     SimpleChromosome(genes)
 *   }
 * }
 *
 * val chromosomeBuilder = new SimpleChromosomeBuilder()
 * given Random = new Random()
 * val chromosome = chromosomeBuilder.build()
 * // chromosome: SimpleChromosome = SimpleChromosome(Seq[SimpleGene](...))
 * }}}
 */
trait RepresentationBuilder[T, F <: Feature[T, F], R <: Representation[T, F]] {

    /** Builds and returns an instance of the representation.
     *
     * The `build` method constructs and returns an instance of the representation type [[R]]. The method operates
     * within the context of a given random number generator, allowing for stochastic or randomized creation of the
     * representation. Implementing classes must provide the specific logic for constructing the representation,
     * typically using the provided random number generator to initialize features or other elements of the
     * representation.
     *
     * @return An instance of the representation type `R`.
     */
    def build()(using random: Random): R
}
