package mixins

import cl.ravenhill.plascevo.AbstractPlascevoTest
import cl.ravenhill.plascevo.mixins.FlatMappable
import org.scalacheck.Gen

class FlatMappableTest extends AbstractPlascevoTest:

    "A FlatMappable object" - {
        "should apply the flat map operation to its elements" in {
            forAll(arbFlatMappableAndFlattened) { case (flatMappable, flattened) =>
                val flatMapped = flatMappable.flatMap {
                    it => List(it, it * 10)
                }
                val expected = flattened.flatMap {
                    it => List(it, it * 10)
                }
                flatMapped shouldBe expected
            }
        }
    }

    /** Generates a pair consisting of a `FlatMappable` instance and its flattened `List` of elements.
     *
     * The `arbFlatMappableAndFlattened` method produces a generator (`Gen`) that creates a tuple containing an instance
     * of `FlatMappable[Int]` and a corresponding flattened `List[Int]`. The `FlatMappable` instance is constructed such
     * that its `flatten` method returns the flattened list generated alongside it.
     *
     * <h2>Usage:</h2>
     * Use this generator in property-based tests to verify the behavior of `FlatMappable` implementations, ensuring
     * that the `flatten` method behaves as expected.
     *
     * <h3>Example:</h3>
     * @example
     * {{{
     * val gen = arbFlatMappableAndFlattened
     * forAll(gen) { case (flatMappable, expectedFlattened) =>
     *   flatMappable.flatten() shouldBe expectedFlattened
     * }
     * }}}
     *
     * @return A generator that produces a tuple containing a `FlatMappable[Int]` instance and a corresponding flattened
     *         `List[Int]`.
     */
    def arbFlatMappableAndFlattened: Gen[(FlatMappable[Int], List[Int])] = for {
        size <- Gen.choose(0, 10)
        elementsAndFlattened <- Gen.listOfN(size, Gen.listOf(Gen.chooseNum(Int.MinValue, Int.MaxValue)))
    } yield {
        val flattened = elementsAndFlattened.flatten
        val flatMappable = new FlatMappable[Int] {
            override def flatten(): List[Int] = flattened
        }
        (flatMappable, flattened)
    }
