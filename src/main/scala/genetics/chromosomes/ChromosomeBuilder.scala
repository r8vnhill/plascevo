package cl.ravenhill.plascevo
package genetics.chromosomes

import genetics.genes.Gene
import cl.ravenhill.plascevo.evolution.executors.construction.{ConstructorExecutor, SequentialConstructor}

/** A trait for building `Chromosome` instances in a genetic algorithm.
 *
 * The `ChromosomeBuilder` trait provides a flexible interface for constructing `Chromosome` instances. It allows users
 * to specify a custom `ConstructorExecutor` and size for the chromosome before building the final `Chromosome`
 * instance.
 *
 * @tparam T The type of value stored by the genes in the chromosome.
 * @tparam G The type of gene that the chromosome holds, which must extend [[Gene]].
 */
trait ChromosomeBuilder[T, G <: Gene[T, G]] {

    /** A private variable holding the `ConstructorExecutor` used to create genes in the chromosome.
     *
     * The `_executor` is initialized with a default `SequentialConstructor`, which constructs the genes sequentially.
     * It can be customized using the `withExecutor` method.
     */
    protected var _executor: ConstructorExecutor[G] = SequentialConstructor[G]()

    /** Sets a custom `ConstructorExecutor` for constructing genes in the chromosome.
     *
     * This method allows the user to specify a custom `ConstructorExecutor` to be used when constructing the genes in
     * the chromosome. The method returns the builder instance to enable method chaining.
     *
     * @param executor The `ConstructorExecutor` to be used for constructing genes.
     * @return The current instance of the `ChromosomeBuilder` for method chaining.
     */
    def withExecutor(executor: ConstructorExecutor[G]): this.type = {
        _executor = executor
        this
    }

    /** A private variable holding the optional size of the chromosome.
     *
     * The `_size` variable can be set using the `withSize` method to specify the number of genes in the chromosome. If
     * not set, the size will be determined by the implementation of the `build` method.
     */
    protected var _size: Option[Int] = None

    /** Sets the size of the chromosome.
     *
     * This method allows the user to specify the size of the chromosome, i.e., the number of genes it contains. The
     * method returns the builder instance to enable method chaining.
     *
     * @param size The desired size of the chromosome.
     * @return The current instance of the `ChromosomeBuilder` for method chaining.
     */
    def withSize(size: Int): this.type = {
        _size = Some(size)
        this
    }

    /** Builds and returns a `Chromosome` instance based on the current configuration.
     *
     * The `build` method constructs a `Chromosome` using the specified `ConstructorExecutor` and size. It must be
     * implemented by concrete classes that extend this trait.
     *
     * @return A new `Chromosome[T, G]` instance.
     */
    def build(): Chromosome[T, G]
}
