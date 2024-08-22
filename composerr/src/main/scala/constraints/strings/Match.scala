package cl.ravenhill.composerr
package constraints.strings

import scala.util.matching.Regex

class Match(regex: Regex) extends StringConstraint {
  
  override val validator: String => Boolean = regex.matches
}
