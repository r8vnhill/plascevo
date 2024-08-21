package cl.ravenhill.composerr
package exceptions

/**
 * A specialized exception that aggregates multiple exceptions (throwables) into a single composite exception.
 *
 * The `CompositeException` class is useful when multiple errors need to be reported together. If only one throwable is
 * provided, the message will indicate that a single exception occurred. If multiple throwables are provided, the
 * message will list each exception with its corresponding message.
 *
 * @constructor Creates a new `CompositeException` with a list of throwables.
 *
 * @param throwables The list of `Throwable` instances that caused this exception. The list cannot be empty.
 *
 * @throws IllegalArgumentException if the list of throwables is empty.
 *
 * @example
 * {{{
 * val exceptions = List(
 *   new IllegalArgumentException("Invalid argument"),
 *   new NullPointerException("Null value")
 * )
 *
 * val compositeException = CompositeException(exceptions)
 * throw compositeException
 * }}}
 */
class CompositeException(val throwables: List[Throwable]) extends Exception(
  if (throwables.size == 1)
    s"An exception occurred -- [${throwables.head.getClass.getSimpleName}] ${throwables.head.getMessage}"
  else
    "Multiple exceptions occurred -- " +
      throwables.map(t => s"{ [${t.getClass.getSimpleName}] ${t.getMessage} }").mkString(",\n")
) {
  require(throwables.nonEmpty, "The list of throwables cannot be empty")
}
