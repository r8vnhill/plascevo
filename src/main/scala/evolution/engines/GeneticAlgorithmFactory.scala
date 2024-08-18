package cl.ravenhill.plascevo
package evolution.engines

import evolution.EvolutionInterceptor
import evolution.config.*
import evolution.executors.evaluation.EvaluationExecutorFactory
import evolution.states.GeneticEvolutionState
import genetics.genes.Gene
import genetics.{Genotype, GenotypeFactory}
import limits.Limit
import listeners.{EvolutionListener, ListenerConfiguration}
import operators.alteration.Alterer
import operators.selection.{Selector, TournamentSelector}
import ranking.{FitnessMaxRanker, IndividualRanker}

import scala.collection.mutable.ListBuffer

/** A type alias for a factory function that creates an `EvolutionListener` based on a listener configuration.
 *
 * The `ListenerFactory` type defines a function that takes a `ListenerConfiguration` and returns an
 * `EvolutionListener`. This factory pattern allows for the creation of listeners that can be customized based on the
 * specific configuration provided.
 */
private type ListenerFactory[T, G <: Gene[T, G]] =
    ListenerConfiguration[T, G, Genotype[T, G]] => EvolutionListener[T, G, Genotype[T, G], GeneticEvolutionState[T, G]]

/** A type alias for a factory function that creates a `Limit` based on a listener configuration.
 *
 * The `LimitFactory` type defines a function that takes a `ListenerConfiguration` and returns a `Limit`. This factory
 * pattern allows for the creation of limits that can be customized based on the specific configuration provided.
 */
private type LimitFactory[T, G <: Gene[T, G]] =
    ListenerConfiguration[T, G, Genotype[T, G]] =>
        Limit[
            T,
            G,
            Genotype[T, G],
            GeneticEvolutionState[T, G],
            EvolutionListener[T, G, Genotype[T, G], GeneticEvolutionState[T, G]]
        ]

/** Type alias for the genetic population configuration.
 *
 * `PopulationConfig` represents a shorthand for the `GeneticPopulationConfiguration` type, which is used to configure
 * the population in a genetic algorithm.
 */
type PopulationConfig[T, G <: Gene[T, G]] = GeneticPopulationConfiguration[T, G]

/** Type alias for the selection configuration in a genetic algorithm.
 *
 * `SelectionConfig` represents a shorthand for the `SelectionConfiguration` type, which is used to configure the
 * selection process in a genetic algorithm.
 */
type SelectionConfig[T, G <: Gene[T, G]] = SelectionConfiguration[T, G, Genotype[T, G]]

/** A factory class for creating instances of a genetic algorithm.
 *
 * The `GeneticAlgorithmFactory` class provides a flexible way to configure and create instances of a genetic algorithm.
 * It allows the user to set various parameters, such as population size, survival rate, and selection strategies.
 * Additionally, the factory manages listeners, limits, alterers, evaluators, and interceptors that define the behavior
 * of the genetic algorithm.
 *
 * **Potential Issue**: The use of complex generic types and method chains in the `make` method can lead to Scala
 * compiler "pickling" errors, which occur when the compiler struggles to serialize or save the type information.
 *
 * **Solution**: To mitigate these issues, type aliases (`PopulationConfig` and `SelectionConfig`) have been introduced
 * to simplify type expressions. Additionally, the complex method chains in the `make` method have been refactored into
 * smaller, more focused methods. This reduces the complexity of the type information the compiler needs to handle and
 * improves maintainability.
 *
 * @param fitnessFunction The function used to evaluate the fitness of a genotype.
 * @param genotypeFactory The factory used to create instances of `Genotype[T, G]`.
 * @tparam T The type of value stored by the gene.
 * @tparam G The type of gene that the genotypes hold, which must extend [[Gene]].
 */
class GeneticAlgorithmFactory[T, G <: Gene[T, G]](
    val fitnessFunction: Genotype[T, G] => Double,
    val genotypeFactory: GenotypeFactory[T, G],
) {

    /** The size of the population in the genetic algorithm. */
    private var _populationSize: Int = GeneticAlgorithmFactory.defaultPopulationSize

    /** Gets the current population size.
     *
     * @return The current population size.
     */
    def populationSize: Int = _populationSize

    /** Sets the population size.
     *
     * @param size The desired population size. Must be greater than 0.
     * @throws IllegalArgumentException if `size` is less than or equal to 0.
     */
    def populationSize_=(size: Int): Unit = {
        require(size > 0, "Population size must be greater than 0")
        _populationSize = size
    }

    /** The survival rate in the genetic algorithm. */
    private var _survivalRate: Double = GeneticAlgorithmFactory.defaultSurvivalRate

    /** Gets the current survival rate.
     *
     * @return The current survival rate.
     */
    def survivalRate: Double = _survivalRate

    /** Sets the survival rate.
     *
     * @param rate The desired survival rate. Must be between 0.0 and 1.0.
     * @throws IllegalArgumentException if `rate` is not between 0.0 and 1.0.
     */
    def survivalRate_=(rate: Double): Unit = {
        require(rate >= 0.0 && rate <= 1.0, "Survival rate must be between 0.0 and 1.0")
        _survivalRate = rate
    }

    /** The ranker used to evaluate and compare individuals in the genetic algorithm. */
    var ranker: IndividualRanker[T, G, Genotype[T, G]] = GeneticAlgorithmFactory.defaultRanker

    /** The selector used to choose parents from the population for reproduction. */
    var parentSelector: Selector[T, G, Genotype[T, G]] = GeneticAlgorithmFactory.defaultParentSelector

    /** The selector used to choose offspring for inclusion in the next generation. */
    var offspringSelector: Selector[T, G, Genotype[T, G]] = GeneticAlgorithmFactory.defaultOffspringSelector

    /** A list of listener factories that produce listeners for the genetic algorithm. */
    var listeners: ListBuffer[ListenerFactory[T, G]] = ListBuffer(GeneticAlgorithmFactory.defaultListeners *)

    /** A list of limit factories that produce limits for the genetic algorithm. */
    var limits: ListBuffer[LimitFactory[T, G]] = ListBuffer(GeneticAlgorithmFactory.defaultLimits *)

    /** A list of alterers that define the alterations to be applied to the population. */
    var alterers: ListBuffer[Alterer[T, G, Genotype[T, G]]] = ListBuffer(GeneticAlgorithmFactory.defaultAlterers *)

    /** The evaluator used to evaluate the fitness of individuals in the genetic algorithm. */
    var evaluator = GeneticAlgorithmFactory.defaultEvaluator[T, G]

    /** The interceptor used to modify the evolutionary state before and after steps in the genetic algorithm. */
    var interceptor = GeneticAlgorithmFactory.defaultInterceptor[T, G]

    /** Creates a population configuration based on the current settings.
     *
     * **Notes**: This method was introduced to reduce the complexity of the `make` method, which previously contained
     * deeply nested method chains that contributed to "pickling" errors in the Scala compiler.
     *
     * @return A `PopulationConfig` instance representing the population configuration.
     */
    private def makePopulationConfig(): PopulationConfig[T, G] =
        GeneticPopulationConfiguration(genotypeFactory, populationSize)

    /** Creates a selection configuration based on the current settings.
     *
     * **Notes**: This method was introduced to simplify the `make` method, helping to avoid issues with the Scala
     * compiler by breaking down complex configurations into smaller, more manageable methods.
     *
     * @return A `SelectionConfig` instance representing the selection configuration.
     */
    def makeSelectionConfig(): SelectionConfig[T, G] =
        SelectionConfiguration(survivalRate, parentSelector, offspringSelector)

    /** Creates and returns an instance of a genetic algorithm based on the current configuration.
     *
     * **Notes**: The `make` method was refactored to call smaller, focused methods for each part of the genetic
     * algorithm's configuration. This reduces the complexity of the method and helps prevent Scala compiler "pickling"
     * errors related to complex type serialization.
     *
     * @return A `GeneticAlgorithm` instance configured with the current settings.
     */
    def make() = GeneticAlgorithm(
        populationConfiguration = makePopulationConfig(),
        selectionConfiguration = makeSelectionConfig(),
        alterationConfiguration = AlterationConfiguration(alterers.toSeq),
        evolutionConfiguration = makeEvolutionConfig()
    )

    /** Creates an evolution configuration based on the current settings.
     *
     * **Notes**: This method was introduced to manage the complexity of the `make` method by encapsulating the logic
     * for setting up the evolution configuration in a separate method. This helps to mitigate issues related to the
     * Scala compiler's handling of complex types.
     *
     * @return An `EvolutionConfiguration` instance representing the evolution configuration.
     */
    private def makeEvolutionConfig(): EvolutionConfiguration[T, G, Genotype[T, G], GeneticEvolutionState[T, G]] =
        EvolutionConfiguration(
            limits = limits.map(_.apply(ListenerConfiguration(ranker))).toSeq,
            listeners = listeners.map(_.apply(ListenerConfiguration(ranker))).toSeq,
            ranker = ranker,
            evaluator = evaluator.creator(fitnessFunction),
            interceptor = interceptor,
            initialState = GeneticEvolutionState.empty[T, G](ranker)
        )
}

/** A factory object that provides default configurations and components for creating genetic algorithms.
 *
 * The `GeneticAlgorithmFactory` object serves as a centralized location for default values and components commonly used
 * in genetic algorithms. It provides default implementations for selectors, alterers, evaluators, limits, listeners,
 * and interceptors, making it easier to set up a genetic algorithm with sensible defaults.
 *
 * The defaults are designed to be flexible and can be overridden by users to customize the behavior of the genetic
 * algorithm.
 */
object GeneticAlgorithmFactory {

    /** The default population size for the genetic algorithm.
     *
     * This value represents the number of individuals in the population and is set to 100 by default.
     */
    val defaultPopulationSize: Int = 100

    /** The default survival rate for the genetic algorithm.
     *
     * This value represents the proportion of the population that survives to the next generation and is set to 0.5
     * (50%) by default.
     */
    val defaultSurvivalRate: Double = 0.5

    /** Provides a default ranker for individuals in a genetic algorithm.
     *
     * The `defaultRanker` function returns an instance of `IndividualRanker` that ranks individuals based on their
     * fitness. By default, it uses the `FitnessMaxRanker`, which ranks individuals such that those with higher fitness
     * are considered better. This ranker is typically used to evaluate and compare individuals within the population
     * during the evolutionary process.
     *
     * @tparam T The type of value stored by the gene.
     * @tparam G The type of gene that the genotypes hold, which must extend [[Gene]].
     * @return An `IndividualRanker` instance that ranks individuals by maximizing their fitness.
     */
    def defaultRanker[T, G <: Gene[T, G]]: IndividualRanker[T, G, Genotype[T, G]] = FitnessMaxRanker()

    /** Provides a default parent selector for the genetic algorithm.
     *
     * This method returns a `TournamentSelector`, which is used to select parents from the population for reproduction.
     * The default selector can be overridden to customize the selection strategy.
     *
     * @tparam T The type of value stored by the gene.
     * @tparam G The type of gene that the genotypes hold, which must extend [[Gene]].
     * @return A `Selector` instance for selecting parents.
     */
    def defaultParentSelector[T, G <: Gene[T, G]]: Selector[T, G, Genotype[T, G]] = TournamentSelector()

    /** Provides a default offspring selector for the genetic algorithm.
     *
     * This method returns a `TournamentSelector`, which is used to select offspring from the population for inclusion
     * in the next generation.The default selector can be overridden to customize the selection strategy.
     *
     * @tparam T The type of value stored by the gene.
     * @tparam G The type of gene that the genotypes hold, which must extend [[Gene]].
     * @return A `Selector` instance for selecting offspring.
     */
    def defaultOffspringSelector[T, G <: Gene[T, G]]: Selector[T, G, Genotype[T, G]] = TournamentSelector()

    /** Provides a default sequence of alterers for the genetic algorithm.
     *
     * This method returns an empty sequence by default, but can be overridden to include specific alterers such as
     * mutation or crossover operators.
     *
     * @tparam T The type of value stored by the gene.
     * @tparam G The type of gene that the genotypes hold, which must extend [[Gene]].
     * @return A sequence of `Alterer` instances to modify the population.
     */
    def defaultAlterers[T, G <: Gene[T, G]]: Seq[Alterer[T, G, Genotype[T, G]]] = Seq()

    /** Provides a default sequence of limit factories for the genetic algorithm.
     *
     * The `defaultLimits` function returns a sequence of `LimitFactory` instances that are used to create `Limit`
     * objects for the genetic algorithm. By default, this sequence is empty, meaning no limits are predefined. Users
     * can override this to include specific limits that define stopping conditions for the evolutionary process.
     *
     * @tparam T The type of value stored by the gene.
     * @tparam G The type of gene that the genotypes hold, which must extend [[Gene]].
     * @return A sequence of `LimitFactory` instances for creating `Limit` objects.
     */
    def defaultLimits[T, G <: Gene[T, G]]: Seq[LimitFactory[T, G]] = Seq()

    /** Provides a default sequence of listener factories for the genetic algorithm.
     *
     * The `defaultListeners` function returns a sequence of `ListenerFactory` instances that are used to create
     * `EvolutionListener` objects for the genetic algorithm. By default, this sequence is empty, meaning no listeners
     * are predefined. Users can override this to include specific listeners that react to events during the
     * evolutionary process, such as evolution start, evolution end, and generation start/end.
     *
     * @tparam T The type of value stored by the gene.
     * @tparam G The type of gene that the genotypes hold, which must extend [[Gene]].
     * @return A sequence of `ListenerFactory` instances for creating `EvolutionListener` objects.
     */
    def defaultListeners[T, G <: Gene[T, G]]: Seq[ListenerFactory[T, G]] = Seq()

    /** Provides a default evaluator for the genetic algorithm.
     *
     * This method returns a default `EvaluationExecutorFactory` instance, which can be used to create evaluators for
     * the population. The default factory can be overridden to customize the evaluation process.
     *
     * @tparam T The type of value stored by the gene.
     * @tparam G The type of gene that the genotypes hold, which must extend [[Gene]].
     * @return An `EvaluationExecutorFactory` instance for creating evaluators.
     */
    def defaultEvaluator[T, G <: Gene[T, G]] =
        EvaluationExecutorFactory[T, G, Genotype[T, G], GeneticEvolutionState[T, G]]()

    /** Provides a default evolution interceptor for the genetic algorithm.
     *
     * This method returns an identity `EvolutionInterceptor`, which does not modify the evolutionary state. Users can
     * override this to include specific behaviors before and after evolution steps.
     *
     * @tparam T The type of value stored by the gene.
     * @tparam G The type of gene that the genotypes hold, which must extend [[Gene]].
     * @return An `EvolutionInterceptor` instance for modifying the evolutionary state before and after steps.
     */
    def defaultInterceptor[T, G <: Gene[T, G]] =
        EvolutionInterceptor.identity[T, G, Genotype[T, G], GeneticEvolutionState[T, G]]
}
