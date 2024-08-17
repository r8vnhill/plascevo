package cl.ravenhill.plascevo

import org.scalacheck.Gen

/** A package object for the `repr` package, containing utility classes and functions related to features in an
 * evolutionary algorithm.
 *
 * This package includes the `SimpleFeature` class, a basic implementation of the `Feature` trait, and a generator
 * method for creating instances of `SimpleFeature`.
 */
package object repr:

    /** A simple implementation of the `Feature` trait.
     *
     * The `SimpleFeature` class represents a basic feature in an evolutionary algorithm, storing a value of type `T`.
     * It provides methods for duplicating the feature with a new value and for converting the feature to a string.
     *
     * @param value The value held by the feature.
     * @tparam T The type of the value held by the feature.
     */
    class SimpleFeature[T](override val value: T) extends Feature[T, SimpleFeature[T]]:
        override def toString: String = s"SimpleFeature($value)"

        override def duplicateWithValue(value: T): SimpleFeature[T] = SimpleFeature(value)

        override def foldRight[U](initial: U)(f: (T, U) => U): U = f(value, initial)
        
        override def foldLeft[U](initial: U)(f: (U, T) => U): U = f(initial, value)

        override def flatten(): List[T] = List(value)
        
    /** Generates an instance of `SimpleFeature` using the provided generator.
     *
     * The `arbSimpleFeature` method produces a generator (`Gen`) that creates instances of `SimpleFeature[T]`,  using a
     * provided generator for the value of type `T`.
     *
     * @param gen A generator for the value of type `T`.
     * @tparam T The type of the value held by the feature.
     * @return A generator that produces instances of `SimpleFeature[T]`.
     */
    def simpleFeatureGen[T](gen: Gen[T]): Gen[SimpleFeature[T]] = gen.map(SimpleFeature(_))

    /** Generates an instance of `Representation` based on the provided feature generator and validity status.
     *
     * The `arbSimpleRepresentation` method produces a generator (`Gen`) that creates instances of
     * `Representation[T, F]`, where each instance is either valid or invalid based on the supplied
     * `isValidRepresentation` generator. The `AbstractBaseRepresentation` class is used as the base implementation,
     * which can be customized to either inherit the default behavior or override the `verify` method for invalid
     * representations.
     *
     * @param feature               A generator for features of type `F`, which must extend `Feature[T, F]`.
     * @param isValidRepresentation A generator for the validity status, defaulting to a random choice between `VALID`
     *                              and `INVALID`.
     * @tparam T The type of value held by the features.
     * @tparam F The type of the feature, which must extend `Feature[T, F]`.
     * @return A generator that produces instances of `Representation[T, F]` with the appropriate validity.
     */
    def simpleRepresentationGen[T, F <: Feature[T, F]](
        feature: Gen[F],
        isValidRepresentation: Gen[IsValidRepresentation] = Gen.oneOf(IsValidRepresentation.values)
    ): Gen[Representation[T, F]] = for {
        features <- Gen.listOf(feature)
        validity <- isValidRepresentation
    } yield {
        validity match {
            case IsValidRepresentation.VALID => new AbstractBaseRepresentation[T, F](features) {
                // No additional methods needed; inherits from BaseRepresentation.
            }
            case IsValidRepresentation.INVALID => new AbstractBaseRepresentation[T, F](features) {
                override def verify(): Boolean = false
            }
        }
    }

    /** Enumeration representing the validity status of a `Representation`. */
    enum IsValidRepresentation:
        case VALID, INVALID

    /** A base abstract class for implementing `Representation` with a list of features.
     *
     * The `AbstractBaseRepresentation` class provides a foundation for creating representations that manage a list of
     * features. It includes implementations of methods for accessing features, folding over their values, and verifying
     * the representation.
     *
     * @param features A list of features that this representation manages.
     * @tparam T The type of value held by the features.
     * @tparam F The type of the feature, which must extend `Feature[T, F]`.
     */
    private abstract class AbstractBaseRepresentation[T, F <: Feature[T, F]](features: List[F])
        extends Representation[T, F]:
        override val size: Int = features.size
        
        override def foldLeft[U](initial: U)(f: (U, T) => U): U =
            features.foldLeft(initial)((acc, feature) => f(acc, feature.value))

        override def foldRight[U](initial: U)(f: (T, U) => U): U =
            features.foldRight(initial)((feature, acc) => f(feature.value, acc))

        override def flatten(): List[T] = features.flatMap(_.flatten())
        
        override def verify(): Boolean = true
