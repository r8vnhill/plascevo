/*
 * Copyright (c) 2024, Ignacio Slater M.
 * 2-Clause BSD License.
 */

package munit.checkall
package stacktraces

import scala.annotation.tailrec
import scala.util.{Failure, Success, Try}

/** Trait that provides utility methods for working with stack traces in property-based testing.
 *
 * The `PropertyCheckStackTraces` trait defines two methods that are useful for analyzing and processing exceptions
 * (throwables) during property-based testing. These methods allow for identifying the root cause of an exception and
 * extracting specific stack trace locations.
 */
trait PropertyCheckStackTraces {

    /** Recursively retrieves the root cause of a given throwable.
     *
     * The `root` method traces back through the chain of causes to find the original source of the exception. If an
     * exception does not have a cause, it returns the exception itself.
     *
     * @param cause The throwable for which to find the root cause.
     * @return The root cause of the given throwable.
     */
    def root(cause: Throwable): Throwable

    /** Retrieves a specific number of stack trace locations from a throwable.
     *
     * The `throwableLocation` method returns a sequence of stack trace elements, converted to strings, for a given
     * throwable. The method attempts to return up to `n` elements from the stack trace. The method handles any
     * potential exceptions during this process using the `Try` monad.
     *
     * @param throwable The throwable from which to extract the stack trace.
     * @param n         The number of stack trace elements to retrieve.
     * @return A `Try[Seq[String]]` containing up to `n` stack trace elements as strings, or a `Failure` if an error
     *         occurs.
     */
    def throwableLocation(throwable: Try[Throwable], n: Int): Try[Seq[String]]
}

/** Utility object for handling stack traces in property-based testing.
 *
 * The `PropertyCheckStackTraces` object provides an implementation of the `PropertyCheckStackTraces` trait. It includes
 * methods for finding the root cause of a throwable and extracting stack trace elements, with error handling through
 * the `Try` monad.
 */
private[checkall] object PropertyCheckStackTraces {

    /** An instance of the `PropertyCheckStackTraces` trait.
     *
     * This instance provides implementations for methods that assist in analyzing exceptions by finding their root
     * causes and extracting relevant parts of their stack traces.
     */
    val default: PropertyCheckStackTraces = new PropertyCheckStackTraces {

        /** Recursively retrieves the root cause of a throwable.
         *
         * The `root` method follows the chain of causes from the given throwable to find the original exception that
         * started the chain. If any error occurs while accessing the cause, the method returns the current throwable.
         *
         * @param t The throwable for which to find the root cause.
         * @return The root cause of the given throwable, or the throwable itself if no cause is found or an error
         *         occurs.
         */
        @tailrec
        override def root(t: Throwable): Throwable = {
            Try(Option(t.getCause)) match {
                case scala.util.Success(Some(cause)) => root(cause)
                case scala.util.Success(None) => t
                case scala.util.Failure(_) => t
            }
        }

        /** Extracts and formats a specific number of stack trace elements from a throwable.
         *
         * The `throwableLocation` method attempts to retrieve up to `n` elements from the stack trace of the given
         * throwable's cause. The method filters out any stack trace elements that belong to the `cl.ravenhill.plascevo`
         * package and then formats the remaining elements as strings. If any error occurs during this process, the
         * method returns an empty sequence.
         *
         * @param throwable The throwable from which to extract the stack trace elements.
         * @param n The number of stack trace elements to retrieve.
         * @return A `Try[Seq[String]]` containing up to `n` formatted stack trace elements as strings, or a `Failure`
         *         if an error occurs.
         */
        override def throwableLocation(
            throwable: Try[Throwable],
            n: Int
        ): Try[Seq[String]] = Try {
            throwable.toOption.flatMap { t =>
                Option(t.getCause).flatMap { cause =>
                    Option(cause.getStackTrace)
                        .map(_.toSeq)
                        .map { stackTrace =>
                            stackTrace
                                .dropWhile(_.getClassName.startsWith("cl.ravenhill.plascevo"))
                                .take(n)
                                .map(_.toString)
                        }
                }
            }.getOrElse(Seq.empty)
        }.recover {
            case _: Throwable => Seq.empty
        }
    }
}
