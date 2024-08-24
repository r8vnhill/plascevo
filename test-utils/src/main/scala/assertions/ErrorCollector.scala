/*
 * Copyright (c) 2024, Ignacio Slater M.
 * 2-Clause BSD License.
 */

package cl.ravenhill.plascevo
package assertions

import assertions.print.Printed

import cl.ravenhill.composerr.constrained
import cl.ravenhill.composerr.constraints.ints.BePositive

trait ErrorCollector {
    protected var _depth: Int

    final def depth: Int = _depth

    final def depth_=(value: Int): Unit = {
        constrained {
            "Depth must be a positive integer" | {
                value must BePositive
            }
        }
        _depth = value
    }

    protected var _subject: Option[Printed]

    final def subject: Option[Printed] = _subject

    final def subject_=(value: Option[Printed]): Unit = {
        _subject = value
    }
}
