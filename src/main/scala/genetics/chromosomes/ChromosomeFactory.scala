package cl.ravenhill.plascevo
package genetics.chromosomes

import evolution.executors.{ConstructorExecutor, SequentialConstructor}
import genetics.genes.Gene

/** A trait that represents a factory for creating chromosomes in a genetic algorithm.
 *
 * The `ChromosomeFactory` trait defines an interface for creating instances of `Chromosome[T, G]`. It provides
 * functionality to manage a `ConstructorExecutor` that is used to construct the genes within the chromosome. The
 * executor can be customized or left as `None`, depending on the specific needs of the implementation.
 *
 * @tparam T The type of value stored by the genes within the chromosome.
 * @tparam G The type of gene that the chromosome holds, which must extend [[Gene]].
 */
trait ChromosomeFactory[T, G <: Gene[T, G]] {

    /** The executor used to construct the genes within the chromosome.
     *
     * The `ConstructorExecutor` is responsible for generating the genes that make up the chromosome. It can be
     * customized to use different strategies for constructing the genes, such as parallel or sequential execution.
     */
    var executor: ConstructorExecutor[G] = SequentialConstructor[G]()

    /** The size of the chromosome.
     *
     * The size of the chromosome is defined as the number of genes it contains. This value can be set explicitly or
     * left as `None` to be determined by the implementation.
     */
    var size: Option[Int] = None
    
    /** Creates a new instance of `Chromosome[T, G]`.
     *
     * This method is responsible for generating a new chromosome, typically using the `ConstructorExecutor` to
     * construct the genes that make up the chromosome. The specifics of how the chromosome is created are defined by
     * the implementing class.
     *
     * @return A new instance of `Chromosome[T, G]`.
     */
    def make(): Chromosome[T, G]
}
