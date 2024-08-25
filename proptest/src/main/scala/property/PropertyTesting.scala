/*
 * Copyright (c) 2024, Ignacio Slater M.
 * 2-Clause BSD License.
 */

package cl.ravenhill.plascevo
package property

import property.SysPropEnv.sysprop

object PropertyTesting {
    var defaultSeed: Option[Long] = sysprop("kotest.proptest.default.seed", None) { s => Some(s.toLong) }

    var defaultMinSuccess: Int = ???

    var defaultMaxFailure: Int = ???

    var defaultShrinkingMode: ShrinkingMode = ???

    var defaultListeners: List[PropertyTestListener] = ???
    
    var defaultEdgeCasesGenerationProbability: Double = ???
    
    var defaultOutputClassifications: Boolean = ???
    
    
}
