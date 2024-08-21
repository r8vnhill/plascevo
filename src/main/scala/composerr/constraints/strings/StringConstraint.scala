package cl.ravenhill.plascevo
package composerr.constraints.strings

import composerr.constraints.Constraint

import cl.ravenhill.composerr.exceptions.ConstraintException

trait StringConstraint extends Constraint[String] {
    override def generateException(description: String): ConstraintException = ConstraintException(description)
}
