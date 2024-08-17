package cl.ravenhill.plascevo
package evolution.states

import repr.{Feature, Representation}
import mixins.{FlatMappable, Foldable}

trait EvolutionState[T, F <: Feature[T, F], R <: Representation[T, F]] extends FlatMappable[T] with Foldable[T]:
    def size = ???