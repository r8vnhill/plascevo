package cl.ravenhill.plascevo
package evolution.executors.construction

import NumericUtilities.{negativeIntGen, nonNegativeIntGen}
import exceptions.InvalidSizeException
import matchers.containConstraintMessage

import akka.actor.typed.ActorSystem
import cl.ravenhill.composerr.exceptions.CompositeException

import scala.util.Random

class AkkaConcurrentConstructorTest extends AbstractPlascevoTest {

    private val system = ActorSystem[SequenceActor.Command](SequenceActor(), "concurrent-system")

    "An AkkaConcurrentConstructor" - {

        "throw an exception if the size is negative" in {
            forAll(negativeIntGen()) { size =>
                val constructor = new AkkaConcurrentConstructor[Int](system)
                val init = (index: Int) => index
                val ex = intercept[CompositeException] {
                    constructor(size, init)
                }
                ex should containConstraintMessage[InvalidSizeException](
                    "Cannot create a sequence with a negative size."
                )
            }
        }

        "return a sequence of elements of the specified size" in {
            forAll(nonNegativeIntGen(100)) { size =>
                val constructor = new AkkaConcurrentConstructor[Int](system)
                val init = (index: Int) => index
                val result = constructor(size, init)
                result should have size size
            }
        }

        "concurrently generate elements in the sequence" in {
            val size = 100
            val constructor = new AkkaConcurrentConstructor[Int](system)
            val init = (index: Int) => Random.nextInt()

            val result = constructor(size, init)

            result should have size size
            result.toSet.size should be > 1 // Ensuring that different elements were generated
        }

        "handle large sequences concurrently" in {
            val size = 10000
            val constructor = new AkkaConcurrentConstructor[Int](system)
            val init = (index: Int) => index

            val result = constructor(size, init)

            result should have size size
            result shouldEqual (0 until size)
        }
    }
}
