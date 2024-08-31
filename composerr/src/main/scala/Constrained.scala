package cl.ravenhill.composerr

import exceptions.CompositeException
import scala.util.chaining.scalaUtilChainingOps

object Constrained {

    /**
     * Executes a block of code that collects and evaluates multiple constraints, returning a composite exception if any
     * constraints fail.
     *
     * The `constrained` inline method provides a mechanism to execute a block of code that collects and evaluates
     * multiple constraints within a `ComposerrScope`. The method returns a `CompositeException` if any of the
     * constraints fail. If no constraints fail, the method returns `Unit`.
     *
     * @param builder A block of code that defines the constraints to be evaluated within the `ComposerrScope`. The
     *                block is provided as an inline lambda and executed within the context of the scope.
     * @return A `CompositeException` containing all the errors if any constraints fail, or `Unit` if all constraints
     *         pass without errors.
     */
    inline def constrained(builder: ComposerrScope ?=> Unit): Either[CompositeException, Unit] = {
        val scope = new ComposerrScope().tap { scope =>
            builder(using scope)
        }
        scope.failures match {
            case Nil => Right(())
            case errors => Left(CompositeException(errors))
        }
    }

    extension [T](value: T) {
        def constrainedTo(builder: ComposerrScope ?=> Unit): Either[CompositeException, T] = {
            val scope = new ComposerrScope().tap { scope =>
                builder(using scope)
            }
            scope.failures match {
                case Nil => Right(value)
                case errors => Left(CompositeException(errors))
            }
        }
    }
}
