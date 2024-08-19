package cl.ravenhill.plascevo
package utils

sealed trait Box[T] {
    def value: Option[T]

    def fold[U](transform: T => U): Option[U] = value.map(transform)

    def map[U](transform: T => U): Box[U]

    def flatMap[U](transform: T => Box[U]): Box[U]

    def toMutable: MutableBox[T] = MutableBox(value)
}

case class ImmutableBox[T](value: Option[T]) extends Box[T] {
    override def map[U](transform: T => U): Box[U] = ImmutableBox(value.map(transform))

    override def flatMap[U](transform: T => Box[U]): Box[U] = value match {
        case Some(v) => transform(v)
        case None => ImmutableBox.empty[U]
    }
}

object ImmutableBox {
    def empty[U]: Box[U] = ImmutableBox(None)
}

case class MutableBox[T](var value: Option[T]) extends Box[T] {
    override def map[U](transform: T => U): MutableBox[U] = MutableBox(value.map(transform))

    override def flatMap[U](transform: T => Box[U]): MutableBox[U] = value match {
        case Some(v) => transform(v).toMutable
        case None => MutableBox.empty[U]
    }
}

object MutableBox {
    def empty[U]: MutableBox[U] = MutableBox(None)
}

object Box {
    def immutable[U](value: Option[U]): ImmutableBox[U] = ImmutableBox(value)

    def mutable[U](value: Option[U]): MutableBox[U] = MutableBox(value)
}
