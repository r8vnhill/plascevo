package cl.ravenhill.plascevo
package matchers

import cl.ravenhill.composerr.exceptions.{CompositeException, ConstraintException}
import org.scalatest.matchers.{MatchResult, Matcher}

import scala.reflect.ClassTag

def containConstraintMessage[T <: ConstraintException](expectedMessage: String)
    (using ct: ClassTag[T]): Matcher[CompositeException] = (ex: CompositeException) => {
    val matchingMessages = ex.throwables.collect {
        case e if ct.runtimeClass.isInstance(e) => e.getMessage
    }

    MatchResult(
        matchingMessages.contains(expectedMessage),
        s"None of the ${matchingMessages.size} matching exceptions contained the message: '$expectedMessage'. Found: $matchingMessages",
        s"At least one exception contained the message: '$expectedMessage'"
    )
}
