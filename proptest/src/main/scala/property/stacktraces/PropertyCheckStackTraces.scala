/*
 * Copyright (c) 2024, Ignacio Slater M.
 * 2-Clause BSD License.
 */

package cl.ravenhill.plascevo
package property.stacktraces

trait PropertyCheckStackTraces {
    def root(cause: Throwable): Throwable

    def throwableLocation(throwable: Throwable, n: Int): Option[Seq[String]]
}

private[property] object PropertyCheckStackTraces {
    val stackTraces: PropertyCheckStackTraces = new PropertyCheckStackTraces {
        override def root(cause: Throwable): Throwable = ???

        override def throwableLocation(throwable: Throwable, n: Int): Option[Seq[String]] = ???
    }
}
