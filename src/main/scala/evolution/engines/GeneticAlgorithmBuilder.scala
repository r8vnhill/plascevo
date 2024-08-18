package cl.ravenhill.plascevo
package evolution.engines

import evolution.EvolutionInterceptor
import evolution.config.*
import evolution.executors.evaluation.EvaluationExecutorFactory
import evolution.states.GeneticEvolutionState
import genetics.genes.Gene
import genetics.{Genotype, GenotypeBuilder}
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

/** A builder class for constructing instances of a genetic algorithm.
 *
 * The `GeneticAlgorithmBuilder` class provides a flexible and fluent interface for configuring and creating instances
 * of a genetic algorithm. It allows for customization of various aspects of the algorithm, such as population size,
 * survival rate, selection strategies, and more. The builder pattern used in this class enables method chaining, making
 * it easy to set up a genetic algorithm with specific configurations.
 *
 * @param fitnessFunction  The function used to evaluate the fitness of a genotype.
 * @param genotypeFactory  The builder used to create instances of `Genotype[T, G]`.
 * @tparam T The type of value stored by the genes in the genotype.
 * @tparam G The type of gene that the genotype holds, which must extend [[Gene]].
 */
class GeneticAlgorithmBuilder[T, G <: Gene[T, G]](
    val fitnessFunction: Genotype[T, G] => Double,
    val genotypeFactory: GenotypeBuilder[T, G],
) {

    /** The size of the population in the genetic algorithm. */
    private var _populationSize: Int = GeneticAlgorithmBuilder.defaultPopulationSize

    /** Sets the population size for the genetic algorithm.
     *
     * @param size The desired population size. Must be greater than 0.
     * @return The current instance of the `GeneticAlgorithmBuilder` for method chaining.
     * @throws IllegalArgumentException if `size` is less than or equal to 0.
     */
    def withPopulationSize(size: Int): GeneticAlgorithmBuilder[T, G] = {
        require(size > 0, "Population size must be greater than 0")
        _populationSize = size
        this
    }

    /** The survival rate in the genetic algorithm. */
    private var _survivalRate: Double = GeneticAlgorithmBuilder.defaultSurvivalRate

    /** Sets the survival rate for the genetic algorithm.
     *
     * @param rate The desired survival rate. Must be between 0.0 and 1.0.
     * @return The current instance of the `GeneticAlgorithmBuilder` for method chaining.
     * @throws IllegalArgumentException if `rate` is not between 0.0 and 1.0.
     */
    def withSurvivalRate(rate: Double): GeneticAlgorithmBuilder[T, G] = {
        require(rate >= 0.0 && rate <= 1.0, "Survival rate must be between 0.0 and 1.0")
        _survivalRate = rate
        this
    }

    /** The ranker used to evaluate and compare individuals in the genetic algorithm. */
    private var _ranker: IndividualRanker[T, G, Genotype[T, G]] = GeneticAlgorithmBuilder.defaultRanker

    /** Sets the ranker for the genetic algorithm.
     *
     * @param ranker The `IndividualRanker` to be used for evaluating and comparing individuals.
     * @return The current instance of the `GeneticAlgorithmBuilder` for method chaining.
     */
    def withRanker(ranker: IndividualRanker[T, G, Genotype[T, G]]): GeneticAlgorithmBuilder[T, G] = {
        _ranker = ranker
        this
    }

    /** The selector used to choose parents from the population for reproduction. */
    private var _parentSelector: Selector[T, G, Genotype[T, G]] = GeneticAlgorithmBuilder.defaultParentSelector

    /** Sets the parent selector for the genetic algorithm.
     *
     * @param selector The `Selector` to be used for choosing parents.
     * @return The current instance of the `GeneticAlgorithmBuilder` for method chaining.
     */
    def withParentSelector(selector: Selector[T, G, Genotype[T, G]]): GeneticAlgorithmBuilder[T, G] = {
        _parentSelector = selector
        this
    }

    /** The selector used to choose offspring for inclusion in the next generation. */
    private var _offspringSelector: Selector[T, G, Genotype[T, G]] = GeneticAlgorithmBuilder.defaultOffspringSelector

    /** Sets the offspring selector for the genetic algorithm.
     *
     * @param selector The `Selector` to be used for choosing offspring.
     * @return The current instance of the `GeneticAlgorithmBuilder` for method chaining.
     */
    def withOffspringSelector(selector: Selector[T, G, Genotype[T, G]]): GeneticAlgorithmBuilder[T, G] = {
        _offspringSelector = selector
        this
    }

    /** A list of listener factories that produce listeners for the genetic algorithm. */
    private val _listeners: ListBuffer[ListenerFactory[T, G]] = ListBuffer(GeneticAlgorithmBuilder.defaultListeners *)

    /** Adds a listener to the genetic algorithm.
     *
     * @param listener The `ListenerFactory` to be added to the algorithm.
     * @return The current instance of the `GeneticAlgorithmBuilder` for method chaining.
     */
    def addListener(listener: ListenerFactory[T, G]): GeneticAlgorithmBuilder[T, G] = {
        _listeners += listener
        this
    }

    /** A list of limit factories that produce limits for the genetic algorithm. */
    private val _limits: ListBuffer[LimitFactory[T, G]] = ListBuffer(GeneticAlgorithmBuilder.defaultLimits *)

    /** Adds a limit to the genetic algorithm.
     *
     * @param limit The `LimitFactory` to be added to the algorithm.
     * @return The current instance of the `GeneticAlgorithmBuilder` for method chaining.
     */
    def addLimit(limit: LimitFactory[T, G]): GeneticAlgorithmBuilder[T, G] = {
        _limits += limit
        this
    }

    /** A list of alterers that define the alterations to be applied to the population. */
    private val _alterers: ListBuffer[Alterer[T, G, Genotype[T, G]]] =
        ListBuffer(GeneticAlgorithmBuilder.defaultAlterers *)

    /** Adds an alterer to the genetic algorithm.
     *
     * @param alterer The `Alterer` to be added to the algorithm.
     * @return The current instance of the `GeneticAlgorithmBuilder` for method chaining.
     */
    def addAlterer(alterer: Alterer[T, G, Genotype[T, G]]): GeneticAlgorithmBuilder[T, G] = {
        _alterers += alterer
        this
    }

    /** The evaluator used to evaluate the fitness of individuals in the genetic algorithm. */
    private var _evaluator = GeneticAlgorithmBuilder.defaultEvaluator[T, G]

    /** Sets the evaluator for the genetic algorithm.
     *
     * @param evaluator The `EvaluationExecutorFactory` to be used for evaluating fitness.
     * @return The current instance of the `GeneticAlgorithmBuilder` for method chaining.
     */
    def withEvaluator(
        evaluator: EvaluationExecutorFactory[T, G, Genotype[T, G], GeneticEvolutionState[T, G]]
    ): GeneticAlgorithmBuilder[T, G] = {
        _evaluator = evaluator
        this
    }

    /** The interceptor used to modify the evolutionary state before and after steps in the genetic algorithm. */
    private var _interceptor = GeneticAlgorithmBuilder.defaultInterceptor[T, G]

    /** Sets the interceptor for the genetic algorithm.
     *
     * @param interceptor The `EvolutionInterceptor` to be used in the algorithm.
     * @return The current instance of the `GeneticAlgorithmBuilder` for method chaining.
     */
    def withInterceptor(
        interceptor: EvolutionInterceptor[T, G, Genotype[T, G], GeneticEvolutionState[T, G]]
    ): GeneticAlgorithmBuilder[T, G] = {
        _interceptor = interceptor
        this
    }

    /** Creates and returns an instance of a genetic algorithm based on the current configuration.
     *
     * **Notes**: The `build` method was refactored to call smaller, focused methods for each part of the genetic
     * algorithm's configuration. This reduces the complexity of the method and helps prevent Scala compiler "pickling"
     * errors related to complex type serialization.
     *
     * @return A `GeneticAlgorithm` instance configured with the current settings.
     */
    def build() = GeneticAlgorithm(
        populationConfiguration = makePopulationConfig(),
        selectionConfiguration = makeSelectionConfig(),
        alterationConfiguration = AlterationConfiguration(_alterers.toSeq),
        evolutionConfiguration = makeEvolutionConfig()
    )
    
    /** Creates a population configuration based on the current settings.
     *
     * **Notes**: This method was introduced to reduce the complexity of the `build` method, which previously contained
     * deeply nested method chains that contributed to "pickling" errors in the Scala compiler.
     *
     * @return A `PopulationConfig` instance representing the population configuration.
     */
    private def makePopulationConfig(): PopulationConfig[T, G] =
        GeneticPopulationConfiguration(genotypeFactory, _populationSize)

    /** Creates a selection configuration based on the current settings.
     *
     * **Notes**: This method was introduced to simplify the `build` method, helping to avoid issues with the Scala
     * compiler by breaking down complex configurations into smaller, more manageable methods.
     *
     * @return A `SelectionConfig` instance representing the selection configuration.
     */
    private def makeSelectionConfig(): SelectionConfig[T, G] =
        SelectionConfiguration(_survivalRate, _parentSelector, _offspringSelector)

    /** Creates an evolution configuration based on the current settings.
     *
     * **Notes**: This method was introduced to manage the complexity of the `build` method by encapsulating the logic
     * for setting up the evolution configuration in a separate method. This helps to mitigate issues related to the
     * Scala compiler's handling of complex types.
     *
     * @return An `EvolutionConfiguration` instance representing the evolution configuration.
     */
    private def makeEvolutionConfig(): EvolutionConfiguration[T, G, Genotype[T, G], GeneticEvolutionState[T, G]] =
        EvolutionConfiguration(
            limits = _limits.map(_.apply(ListenerConfiguration(_ranker))).toSeq,
            listeners = _listeners.map(_.apply(ListenerConfiguration(_ranker))).toSeq,
            ranker = _ranker,
            evaluator = _evaluator.creator(fitnessFunction),
            interceptor = _interceptor,
            initialState = GeneticEvolutionState.empty[T, G](_ranker)
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
object GeneticAlgorithmBuilder {

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
