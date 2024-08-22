package cl.ravenhill.plascevo
package evolution.executors.construction

import NumericUtilities.{negativeIntGen, nonNegativeIntGen}
import exceptions.InvalidSizeException
import matchers.containConstraintMessage

import cl.ravenhill.composerr.exceptions.CompositeException

class SequentialConstructorTest extends AbstractPlascevoTest {
    "A SequentialConstructor" - {
        "when applying" - {
            "should throw an exception if the size is negative" in {
                forAll(negativeIntGen()) { size =>
                    val constructor = new SequentialConstructor[Int]()
                    val init = (index: Int) => index
                    val ex = intercept[CompositeException] {
                        constructor(size, init)
                    }
                    ex should containConstraintMessage[InvalidSizeException](
                        "Cannot create a sequence with a negative size."
                    )
                }
            }

            "should return a sequence of elements of the specified size" in {
                forAll(nonNegativeIntGen(100)) { size =>
                    val constructor = new SequentialConstructor[Int]()
                    val init = (index: Int) => index
                    val result = constructor(size, init)
                    result should have size size
                }
            }
        }
    }
}
