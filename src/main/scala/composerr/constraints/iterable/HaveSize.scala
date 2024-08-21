package cl.ravenhill.plascevo
package composerr.constraints.iterable

case class HaveSize[T](predicate: Int => Boolean) extends IterableConstraint[T] {

    def this(size: Int) = this(_ == size)

    override val validator: Iterable[T] => Boolean = _.size match {
        case size if predicate(size) => true
        case _ => false
    }
}
