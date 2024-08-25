/*
 * Copyright (c) 2024, Ignacio Slater M.
 * 2-Clause BSD License.
 */

package cl.ravenhill.plascevo
package property.arbitrary

import property.RandomSource
import property.Utils.random

import cl.ravenhill.plascevo.property.arbitrary.shrinkers.Shrinker

type SampleFunction[A] = RandomSource => A

type EdgeCaseFunction[A] = RandomSource => Option[A]

class ArbitraryBuilder[A](
    private val sampleFunction: SampleFunction[A],
    private val classifier: Option[Classifier[A]],
    private val shrinker: Option[Shrinker[A]],
    private val edgeCaseFunction: Option[EdgeCaseFunction[A]],
) {
    def withClassifier(
        classifier: Classifier[A]
    ): ArbitraryBuilder[A] = new ArbitraryBuilder(sampleFunction, Some(classifier), shrinker, edgeCaseFunction)

    def withShrinker(
        shrinker: Shrinker[A]
    ): ArbitraryBuilder[A] = new ArbitraryBuilder(sampleFunction, classifier, Some(shrinker), edgeCaseFunction)

    def withEdgeCases(
        edgeCases: Seq[A]
    ): ArbitraryBuilder[A] = edgeCases match
        case Nil => ArbitraryBuilder(sampleFunction, classifier, shrinker, None)
        case _ => ArbitraryBuilder(sampleFunction, classifier, shrinker, Some(rs => Some(edgeCases.random(rs.random))))

    def build(): Arbitrary[A] = ???
}

object ArbitraryBuilder {
    def of[A](f: RandomSource => A): ArbitraryBuilder[A] = new ArbitraryBuilder[A](f, None, None, None)
}
