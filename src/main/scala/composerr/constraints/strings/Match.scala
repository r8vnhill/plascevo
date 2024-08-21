package cl.ravenhill.plascevo
package composerr.constraints.strings

import scala.util.matching.Regex

class Match(regex: Regex) extends StringConstraint {
  /**
   * A function that checks if a value of type `T` satisfies the constraint.
   *
   * This function should return `true` if the value meets the constraint's requirements, and `false` otherwise.
   */
  override val validator: String => Boolean = regex.matches
}
