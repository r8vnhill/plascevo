package cl.ravenhill.plascebo

import org.scalacheck.*
import org.scalatest.matchers.should

/** An abstract base class for testing components within the Plascevo framework.
 *
 * This class extends `AnyFreeSpec` from ScalaTest, providing a flexible structure for writing unit tests. It also
 * mixes in `Matchers` for expressive assertions and `ScalaCheckPropertyChecks` for property-based testing.
 *
 * Extend this class to create custom tests for Plascevo components.
 */
abstract class AbstractPlascevoTest
    extends org.scalatest.freespec.AnyFreeSpec
        with should.Matchers
        with org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
        with org.scalatest.BeforeAndAfterEach {
}
