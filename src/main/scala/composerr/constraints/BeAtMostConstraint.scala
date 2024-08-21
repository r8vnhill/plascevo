package cl.ravenhill.plascevo
package composerr.constraints

/**
 * A constraint that validates whether a value is at most a specified maximum.
 *
 * The `BeAtMostConstraint` trait extends the `Constraint` trait and checks if a value of type `T` is less than or equal
 * to a specified maximum value. The comparison is performed using an implicit `Ordering[T]`, making this trait
 * applicable to any type `T` that has an ordering defined.
 *
 * @tparam T The type of value that this constraint applies to.
 *
 * @param max The maximum allowable value of type `T`.
 * @param ord An implicit `Ordering[T]` that defines how values of type `T` are compared.
 *            
 * @example
 * {{{
 * case class CustomNumber(n: Int)
 * 
 * given Ordering[CustomNumber] = Ordering.by(_.n)
 * val beAtMostZero = BeAtMostConstraint(CustomNumber(0)) {}
 * constrained {
 *     "Number must be at most zero" in { CustomNumber(-5) must beAtMostZero }
 * }
 * }}}
 */
trait BeAtMostConstraint[T](val max: T)(using ord: Ordering[T]) extends Constraint[T] {

    /**
     * Validates whether the given value is at most the specified maximum value.
     *
     * This method checks if the input value is less than or equal to the `max` value using the provided `Ordering[T]`.
     * It returns `true` if the value satisfies the constraint, and `false` otherwise.
     * 
     * @return `true` if the value is less than or equal to `max`, `false` otherwise.
     */
    override val validator: T => Boolean = ord.lteq(_, max)
}
