package cl.ravenhill.plascevo
package utils

import java.util.Objects

/** A generic trait that encapsulates a value that may or may not be present.
 *
 * The `Box` trait provides a wrapper around an optional value, allowing for various operations such as mapping,
 * flat-mapping, and converting between immutable and mutable representations. It serves as a base trait for
 * different implementations, including `ImmutableBox` and `MutableBox`.
 *
 * @tparam T The type of the value contained within the `Box`.
 */
sealed trait Box[T] {

    /** Retrieves the value contained within the `Box`, if present.
     *
     * @return An `Option` containing the value if present, or `None` if the value is absent.
     */
    def value: Option[T]

    /** Applies a transformation function to the contained value if it exists.
     *
     * The `fold` method allows you to apply a function to the value within the `Box` and return the result wrapped
     * in an `Option`. If the `Box` is empty, the method returns `None`.
     *
     * @param transform The function to apply to the contained value.
     * @tparam U The type of the result after applying the transformation.
     * @return An `Option[U]` containing the transformed value, or `None` if the `Box` is empty.
     * @example
     * {{{
     * val box = ImmutableBox(Some(420))
     * val result = box.fold(_ * 2)
     * // result: Option[Int] = Some(840)
     * }}}
     */
    def fold[U](transform: T => U): Option[U] = value.map(transform)

    /** Maps the contained value to another `Box` using a transformation function.
     *
     * The `map` method applies a function to the value within the `Box`, returning a new `Box` containing the
     * transformed value. If the `Box` is empty, it returns an empty `Box`.
     *
     * @param transform The function to apply to the contained value.
     * @tparam U The type of the value in the resulting `Box`.
     * @return A new `Box[U]` containing the transformed value, or an empty `Box` if the original `Box` was empty.
     * @example
     * {{{
     * val box = ImmutableBox(Some(420))
     * val result = box.map(_ * 2)
     * // result: Box[Int] = ImmutableBox(Some(840))
     * }}}
     */
    def map[U](transform: T => U): Box[U]

    /** Flat-maps the contained value to another `Box` using a transformation function.
     *
     * The `flatMap` method applies a function that returns a new `Box` to the contained value, effectively flattening
     * nested `Box` instances. If the `Box` is empty, it returns an empty `Box`.
     *
     * @param transform The function to apply to the contained value, returning a new `Box`.
     * @tparam U The type of the value in the resulting `Box`.
     * @return A new `Box[U]` that is the result of applying the function to the contained value, or an empty `Box`
     *         if the original `Box` was empty.
     * @example
     * {{{
     * val box = ImmutableBox(Some(420))
     * val result = box.flatMap(value => ImmutableBox(Some(value * 2)))
     * // result: Box[Int] = ImmutableBox(Some(840))
     * }}}
     */
    def flatMap[U](transform: T => Box[U]): Box[U]

    /** Converts the `Box` into a mutable representation.
     *
     * The `toMutable` method converts an immutable `Box` into a `MutableBox`, allowing the contained value to be
     * changed after creation. If the `Box` is already mutable, this method simply returns it.
     *
     * @return A `MutableBox[T]` containing the same value as the original `Box`.
     */
    def toMutable: MutableBox[T] = MutableBox(value)
}

/** Represents an immutable box that contains a value of type `T`.
 *
 * The `ImmutableBox` class is a concrete implementation of the `Box` trait, designed to hold a value that cannot be
 * modified once the box is created. It provides methods for mapping and flat-mapping over the contained value.
 *
 * @param value The value contained within the box, wrapped in an `Option`.
 * @tparam T The type of the value contained within the box.
 */
final case class ImmutableBox[T](value: Option[T]) extends Box[T] {

    /** Maps the contained value to a new `Box` using a transformation function.
     *
     * @param transform The function to apply to the contained value.
     * @tparam U The type of the value in the resulting `Box`.
     * @return A new `ImmutableBox[U]` containing the transformed value, or an empty `Box` if the original `Box` was
     *         empty.
     */
    override def map[U](transform: T => U): Box[U] = ImmutableBox(value.map(transform))

    /** Flat-maps the contained value to a new `Box` using a transformation function.
     *
     * @param transform The function to apply to the contained value, returning a new `Box`.
     * @tparam U The type of the value in the resulting `Box`.
     * @return A new `Box[U]` that is the result of applying the function to the contained value, or an empty `Box` if
     *         the original `Box` was empty.
     */
    override def flatMap[U](transform: T => Box[U]): Box[U] = value match {
        case Some(v) => transform(v)
        case None => ImmutableBox.empty[U]
    }

    /** Compares this `ImmutableBox` to another object for equality.
     *
     * The `equals` method checks whether the `that` object is an instance of `ImmutableBox` and, if so, whether it
     * contains an equivalent value. Equality is determined based on both the type of the box and the contained value.
     *
     * @param that The object to compare with this `ImmutableBox`.
     * @return `true` if the `that` object is an `ImmutableBox` with the same value, `false` otherwise.
     */
    override def equals(that: Any): Boolean = that match {
        case that: ImmutableBox[?] => that.canEqual(this) && this.value == that.value
        case _ => false
    }

    /** Computes the hash code for this `ImmutableBox`.
     *
     * The `hashCode` method generates a hash code based on the class of the `ImmutableBox` and its contained value.
     * This method ensures that equal `ImmutableBox` instances produce the same hash code, supporting correct behavior
     * in hash-based collections such as `HashSet` or `HashMap`.
     *
     * @return The hash code for this `ImmutableBox`.
     */
    override def hashCode: Int = Objects.hash(classOf[ImmutableBox[?]], value)
}

/** Companion object for `ImmutableBox`, providing utility methods. */
object ImmutableBox {

    /** Creates an empty `ImmutableBox`.
     *
     * @tparam U The type of the value that the `ImmutableBox` would contain.
     * @return An `ImmutableBox` with no value (i.e., containing `None`).
     */
    def empty[U]: Box[U] = ImmutableBox(None)
}

/** Represents a mutable box that contains a value of type `T`.
 *
 * The `MutableBox` class is a concrete implementation of the `Box` trait, designed to hold a value that can be
 * modified after the box is created. It provides methods for mapping and flat-mapping over the contained value,
 * as well as mutating the value directly.
 *
 * @param value The mutable value contained within the box, wrapped in an `Option`.
 * @tparam T The type of the value contained within the box.
 */
final case class MutableBox[T](var value: Option[T]) extends Box[T] {

    /** Maps the contained value to a new `MutableBox` using a transformation function.
     *
     * @param transform The function to apply to the contained value.
     * @tparam U The type of the value in the resulting `MutableBox`.
     * @return A new `MutableBox[U]` containing the transformed value, or an empty `MutableBox` if the original `Box`
     *         was empty.
     */
    override def map[U](transform: T => U): MutableBox[U] = MutableBox(value.map(transform))

    /** Flat-maps the contained value to a new `MutableBox` using a transformation function.
     *
     * @param transform The function to apply to the contained value, returning a new `Box`.
     * @tparam U The type of the value in the resulting `Box`.
     * @return A new `MutableBox[U]` that is the result of applying the function to the contained value, or an empty
     *         `MutableBox` if the original `Box` was empty.
     */
    override def flatMap[U](transform: T => Box[U]): MutableBox[U] = value match {
        case Some(v) => transform(v).toMutable
        case None => MutableBox.empty[U]
    }
    
    /** Converts the `MutableBox` into an immutable representation.
     *
     * The `toImmutable` method converts a mutable `MutableBox` into an `ImmutableBox`, allowing the contained value to
     * be used in an immutable context. If the `Box` is already immutable, this method simply returns it.
     *
     * @return An `ImmutableBox[T]` containing the same value as the original `MutableBox`.
     */
    def toImmutable: ImmutableBox[T] = ImmutableBox(value)

    /**
     * Compares this `MutableBox` to another object for equality.
     * 
     * The `equals` method checks whether the `that` object is an instance of `MutableBox` and, if so, whether it
     * contains an equivalent value. Equality is determined based on both the type of the box and the contained value.
     * 
     * @param obj The object to compare with this `MutableBox`.
     * @return `true` if the `obj` object is a `MutableBox` with the same value, `false` otherwise.
     */
    override def equals(obj: Any): Boolean = obj match {
        case that: MutableBox[?] => that.canEqual(this) && this.value == that.value
        case _ => false
    }
    
    /**
     * Computes the hash code for this `MutableBox`.
     * 
     * The `hashCode` method generates a hash code based on the class of the `MutableBox` and its contained value. This
     * method ensures that equal `MutableBox` instances produce the same hash code, supporting correct behavior in
     * hash-based collections such as `HashSet` or `HashMap`.
     * 
     * @return The hash code for this `MutableBox`.
     */
    override def hashCode: Int = Objects.hash(classOf[MutableBox[?]], value)
}

/** Companion object for `MutableBox`, providing utility methods. */
object MutableBox {

    /** Creates an empty `MutableBox`.
     *
     * @tparam U The type of the value that the `MutableBox` would contain.
     * @return A `MutableBox` with no value (i.e., containing `None`).
     */
    def empty[U]: MutableBox[U] = MutableBox(None)
}

/** Provides factory methods for creating instances of `ImmutableBox` and `MutableBox`. */
object Box {

    /** Creates an `ImmutableBox` containing the specified value.
     *
     * @param value The value to be contained in the box, wrapped in an `Option`.
     * @tparam U The type of the value.
     * @return An `ImmutableBox` containing the specified value.
     */
    def immutable[U](value: Option[U]): ImmutableBox[U] = ImmutableBox(value)

    /** Creates a `MutableBox` containing the specified value.
     *
     * @param value The value to be contained in the box, wrapped in an `Option`.
     * @tparam U The type of the value.
     * @return A `MutableBox` containing the specified value.
     */
    def mutable[U](value: Option[U]): MutableBox[U] = MutableBox(value)
}
