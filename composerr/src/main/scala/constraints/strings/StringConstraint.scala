package cl.ravenhill.composerr
package constraints.strings

import constraints.Constraint
import exceptions.ConstraintException

trait StringConstraint extends Constraint[String] {
    override def generateException(description: String): ConstraintException = ConstraintException(description)
}
