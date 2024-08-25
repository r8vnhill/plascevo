/*
 * Copyright (c) 2024, Ignacio Slater M.
 * 2-Clause BSD License.
 */

package cl.ravenhill.plascevo
package property

import property.SysPropEnv.sysprop

object PropertyTesting {

    var defaultSeed: Option[Long] = sysprop("plascevo.proptest.default.seed", None) { s => Some(s.toLong) }

    var defaultMinSuccess: Int = sysprop("plascevo.proptest.default.min-success", Int.MaxValue) {
        _.toInt
    }

    var defaultMaxFailure: Int = sysprop("plascevo.proptest.default.max-failure", 0) {
        _.toInt
    }

    var defaultShrinkingMode: ShrinkingMode = BoundedShrinking(1000)

    var defaultListeners: Seq[PropertyTestListener] = Seq.empty

    var defaultEdgeCasesGenerationProbability: Double =
        sysprop("plascevo.proptest.arb.edgecases-generation-probability", 0.02) {
            _.toDouble
        }


    var defaultOutputClassifications: Boolean = sysprop("plascevo.proptest.arb.output.classifications", false) {
        _.toBoolean
    }

    var failOnSeed: Boolean = sysprop("plascevo.proptest.seed.fail-if-set", false) {
        _.toBoolean
    }
    
    var defaultIterations: Int = sysprop("plascevo.proptest.default.iteration.count", 1000) {
        _.toInt
    }
}
