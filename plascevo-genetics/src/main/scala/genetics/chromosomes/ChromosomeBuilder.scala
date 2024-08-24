/*
 * Copyright (c) 2024, Ignacio Slater M.
 * 2-Clause BSD License.
 */

package cl.ravenhill.plascevo
package genetics.chromosomes

import evolution.executors.construction.{ConstructorExecutor, SequentialConstructor}
import exceptions.InvalidSizeException
import genetics.genes.Gene

import cl.ravenhill.composerr.constrained
import cl.ravenhill.composerr.constraints.ints.BeNegative

import scala.util.Random

/** A builder trait for constructing instances of a specific `Chromosome` in an evolutionary algorithm.
 *
 * The `ChromosomeBuilder` trait provides a flexible and customizable interface for creating chromosomes, which are
 * collections of genes that represent potential solutions in an evolutionary algorithm. This trait allows users to
 * specify an executor for gene construction and the size of the chromosome, facilitating the creation of chromosomes
 * with varying structures and configurations.
 *
 * @tparam T The type of value stored by the genes within the chromosome.
 * @tparam G The type of gene contained within the chromosome, which must extend [[Gene]].
 * @example
 * {{{
 * // Example implementation of a simple chromosome builder
 * class MyChromosomeBuilder extends ChromosomeBuilder[Int, MyGene] {
 *   override def build()(using random: Random): Chromosome[Int, MyGene] = {
 *     val genes = _executor(_size.getOrElse(10), _ => MyGene(random.nextInt(100)))
 *     MyChromosome(genes)
 *   }
 * }
 *
 * val builder = new MyChromosomeBuilder()
 * builder.withSize(20).withExecutor(SequentialConstructor[MyGene]())
 * val chromosome = builder.build()
 * // chromosome: MyChromosome = MyChromosome(Seq[MyGene](...))
 * }}}
 */
trait ChromosomeBuilder[T, G <: Gene[T, G]] {

    /** The executor responsible for constructing genes within the chromosome.
     *
     * The `_executor` field holds an instance of `ConstructorExecutor` that is used to generate the genes for the
     * chromosome. By default, it is initialized with a `SequentialConstructor`, but it can be customized by using the
     * `withExecutor` method.
     */
    protected var _executor: ConstructorExecutor[G] = SequentialConstructor[G]()

    /** Returns the current executor used for gene construction.
     *
     * @return The `ConstructorExecutor` instance used for generating genes.
     */
    def executor: ConstructorExecutor[G] = _executor

    /** Sets the executor for gene construction and returns the builder.
     *
     * The `withExecutor` method allows for the customization of the gene construction process by specifying a different
     * `ConstructorExecutor`. This method returns the builder itself, enabling method chaining.
     *
     * @param executor The `ConstructorExecutor` to be used for generating genes.
     * @return The builder instance with the updated executor.
     */
    final def withExecutor(executor: ConstructorExecutor[G]): this.type = {
        _executor = executor
        this
    }

    /** The size of the chromosome, representing the number of genes it contains.
     *
     * The `_size` field holds an optional integer that specifies the number of genes in the chromosome. If not
     * provided, the size is determined by the builder implementation.
     */
    protected var _size: Option[Int] = None

    /** Returns the current size of the chromosome.
     *
     * @return An `Option[Int]` representing the size of the chromosome, if defined.
     */
    def size: Option[Int] = _size

    /** Sets the size of the chromosome and returns the builder.
     *
     * The `withSize` method allows for the customization of the chromosome size by specifying the number of genes it
     * should contain. This method returns the builder itself, enabling method chaining.
     *
     * @param size The number of genes to be included in the chromosome.
     * @return The builder instance with the updated size.
     */
    def withSize(size: Int): this.type = {
        constrained {
            "Chromosome size must be a non-negative integer." ~ (size mustNot BeNegative, InvalidSizeException(_))
        }
        _size = Some(size)
        this
    }

    /** Builds and returns an instance of the chromosome.
     *
     * The `build` method constructs a chromosome using the specified executor and size. The method operates within
     * the context of a random number generator, allowing for stochastic or randomized creation of the chromosome.
     * Implementing classes must provide the specific logic for constructing the chromosome.
     *
     * @param random An implicit random number generator used during the construction process.
     * @return An instance of the `Chromosome` type `R`.
     */
    def build()(using random: Random): Chromosome[T, G]
}
