package cl.ravenhill.plascevo
package matchers

import cl.ravenhill.composerr.exceptions.{CompositeException, ConstraintException}
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.matchers.{MatchResult, Matcher}

extension (exception: CompositeException)
    def shouldHaveInfringement[T <: ConstraintException](message: String)
        (using manifest: Manifest[T]): CompositeException = {
        val throwables = exception.throwables

        val matcher: Matcher[CompositeException] =
            if throwables.exists(_.isInstanceOf[T]) then
                Matcher { value =>
                    val filtered = throwables.collect { case ex: T => ex }
                    MatchResult(
                        filtered.exists(_.getMessage == message),
                        s"$value should have an infringement of type ${manifest.runtimeClass.getSimpleName} with message: $message. " +
                            s"Actual: $filtered",
                        s"$value should not have an infringement of type ${manifest.runtimeClass.getSimpleName} with message: $message. " +
                            s"Actual: $filtered"
                    )
                }
            else
                Matcher { value =>
                    MatchResult(
                        false,
                        s"$value should have an infringement of type ${manifest.runtimeClass.getSimpleName} with message: $message",
                        s"$value should not have an infringement of type ${manifest.runtimeClass.getSimpleName} with message: $message"
                    )
                }

        exception should matcher
        exception
    }
