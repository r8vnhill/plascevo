package cl.ravenhill.plascevo
package utils

import org.scalacheck.{Arbitrary, Gen}

class BoxTest extends AbstractPlascevoTest {
    "A Box instance" - {
        "should have a value that is set according to the constructor" in {
            forAll(boxGen(Gen.option(Arbitrary.arbAnyVal.arbitrary))) { box =>
                box.value shouldEqual box.value
            }
        }

        "when folding" - {
            "can apply a fold operation to extract the value" in {
                forAll(boxGen(Gen.option(Arbitrary.arbAnyVal.arbitrary))) { box =>
                    box.fold(identity) shouldEqual box.value
                }
            }

            "to a constant should always return that constant wrapped in an Option" in {
                forAll(
                    boxGen(Gen.option(Arbitrary.arbAnyVal.arbitrary)),
                    Arbitrary.arbAnyVal.arbitrary
                ) { (box, constant) =>
                    val result = box.fold(_ => constant)
                    result shouldEqual box.value.map(_ => constant)
                }
            }

            "two functions should be equivalent to applying their composition" in {
                forAll(
                    Gen.option(Gen.chooseNum(-1000, 1000)),
                    Gen.chooseNum(1, 10),
                    Gen.chooseNum(1, 10)
                ) { (optInt, add, mul) =>
                    val f: Int => Int = _ + add
                    val g: Int => Int = _ * mul
                    val box = MutableBox(optInt)

                    // Compose f and g using andThen and compare with applying fold separately
                    box.fold(f andThen g) shouldEqual box.fold(f).flatMap(result => Some(g(result)))
                }
            }
        }

        "can be converted to a mutable box" in {
            forAll(boxGen(Gen.option(Arbitrary.arbAnyVal.arbitrary))) { box =>
                val mutableBox = box.toMutable
                mutableBox shouldEqual MutableBox(box.value)
                mutableBox shouldNot be(ImmutableBox(box.value))
            }
        }
    }

    "An immutable box" - {
        "when mapping" - {
            "should apply identity function and return the same Box" in {
                forAll(immutableBoxGen(Gen.option(Arbitrary.arbAnyVal.arbitrary))) { box =>
                    box.map(identity) shouldEqual box
                }
            }

            "should map to a constant and return a Box containing the constant" in {
                forAll(
                    immutableBoxGen(Gen.option(Arbitrary.arbAnyVal.arbitrary)),
                    Arbitrary.arbInt.arbitrary
                ) { (box, constant) =>
                    val result = box.map(_ => constant)
                    result shouldEqual ImmutableBox(box.value.map(_ => constant))
                }
            }

            "should correctly apply composition of two functions" in {
                forAll(
                    Gen.option(Gen.chooseNum(-1000, 1000)),
                    Gen.chooseNum(1, 10),
                    Gen.chooseNum(1, 10)
                ) { (optInt, add, mul) =>
                    val f: Int => Int = _ + add
                    val g: Int => Int = _ * mul
                    val box = ImmutableBox(optInt)

                    box.map(f andThen g) shouldEqual box.map(f).map(g)
                }
            }

            "should return an empty Box when mapping an empty Box" in {
                val box = ImmutableBox(None)
                box.map(_.toString) shouldEqual ImmutableBox(None)
            }
        }

        "when flatMapping" - {
            "should apply the transformation function when box contains a value" in {
                forAll(Gen.option(Arbitrary.arbInt.arbitrary), Gen.chooseNum(1, 100)) { (optInt, add) =>
                    val f: Int => Box[Int] = v => ImmutableBox(Some(v + add))
                    val box = ImmutableBox(optInt)

                    // Check if flatMap correctly applies the function
                    val expected = optInt match {
                        case Some(v) => ImmutableBox(Some(v + add))
                        case None => ImmutableBox.empty[Int]
                    }

                    box.flatMap(f) shouldEqual expected
                }
            }

            "should return an empty box when the original box is empty" in {
                val box = ImmutableBox.empty[Int]
                val f: Int => Box[Int] = v => ImmutableBox(Some(v * 2))

                box.flatMap(f) shouldEqual ImmutableBox.empty[Int]
            }

            "should return an empty box when the transformation function returns an empty box" in {
                forAll(Arbitrary.arbInt.arbitrary) { value =>
                    val box = ImmutableBox(Some(value))
                    val f: Int => Box[Int] = _ => ImmutableBox.empty[Int]

                    box.flatMap(f) shouldEqual ImmutableBox.empty[Int]
                }
            }

            "should return a non-empty box when the transformation function returns a non-empty box" in {
                forAll(Arbitrary.arbInt.arbitrary, Gen.chooseNum(1, 100)) { (value, add) =>
                    val box = ImmutableBox(Some(value))
                    val f: Int => Box[Int] = v => ImmutableBox(Some(v + add))

                    box.flatMap(f) shouldEqual ImmutableBox(Some(value + add))
                }
            }

            "should behave like map when flatMapping with identity function" in {
                forAll(Arbitrary.arbInt.arbitrary) { value =>
                    val box = ImmutableBox(Some(value))
                    val identityFlatMap: Int => Box[Int] = v => ImmutableBox(Some(v))

                    box.flatMap(identityFlatMap) shouldEqual box.map(identity)
                }
            }
        }
    }
    
    "A MutableBox" - {
        "when mapping" - {
            "should apply identity function and return the same Box" in {
                forAll(mutableBoxGen(Gen.option(Arbitrary.arbAnyVal.arbitrary))) { box =>
                    box.map(identity) shouldEqual box
                }
            }

            "should map to a constant and return a Box containing the constant" in {
                forAll(
                    mutableBoxGen(Gen.option(Arbitrary.arbAnyVal.arbitrary)),
                    Arbitrary.arbInt.arbitrary
                ) { (box, constant) =>
                    val result = box.map(_ => constant)
                    result shouldEqual MutableBox(box.value.map(_ => constant))
                }
            }

            "should correctly apply composition of two functions" in {
                forAll(
                    Gen.option(Gen.chooseNum(-1000, 1000)),
                    Gen.chooseNum(1, 10),
                    Gen.chooseNum(1, 10)
                ) { (optInt, add, mul) =>
                    val f: Int => Int = _ + add
                    val g: Int => Int = _ * mul
                    val box = MutableBox(optInt)

                    box.map(f andThen g) shouldEqual box.map(f).map(g)
                }
            }

            "should return an empty Box when mapping an empty Box" in {
                val box = MutableBox(None)
                box.map(_.toString) shouldEqual MutableBox(None)
            }
        }

        "when flatMapping" - {
            "should apply the transformation function when box contains a value" in {
                forAll(Gen.option(Arbitrary.arbInt.arbitrary), Gen.chooseNum(1, 100)) { (optInt, add) =>
                    val f: Int => Box[Int] = v => MutableBox(Some(v + add))
                    val box = MutableBox(optInt)

                    // Check if flatMap correctly applies the function
                    val expected = optInt match {
                        case Some(v) => MutableBox(Some(v + add))
                        case None => MutableBox.empty[Int]
                    }

                    box.flatMap(f) shouldEqual expected
                }
            }

            "should return an empty box when the original box is empty" in {
                val box = MutableBox.empty[Int]
                val f: Int => Box[Int] = v => MutableBox(Some(v * 2))

                box.flatMap(f) shouldEqual MutableBox.empty[Int]
            }

            "should return an empty box when the transformation function returns an empty box" in {
                forAll(Arbitrary.arbInt.arbitrary) { value =>
                    val box = MutableBox(Some(value))
                    val f: Int => Box[Int] = _ => MutableBox.empty[Int]

                    box.flatMap(f) shouldEqual MutableBox.empty[Int]
                }
            }
        }
    }
}

/**
 * Generates a `Gen[Box[T]]` from a generator of `Option[T]`.
 *
 * This method takes a generator that produces `Option[T]` values and returns a new generator that produces
 * instances of `Box[T]`. The generated `Box[T]` can be either a `MutableBox` or an `ImmutableBox`, chosen randomly.
 *
 * @param gen The original `Gen[Option[T]]` from which `Option` values are generated.
 * @tparam T The type of the value contained within the `Box`.
 * @return A `Gen[Box[T]]` that produces instances of `Box[T]`, which can be either mutable or immutable.
 */
private def boxGen[T](
    gen: Gen[Option[T]]
): Gen[Box[T]] = gen.flatMap(o => Gen.oneOf(MutableBox(o), ImmutableBox(o)))

/**
 * Generates an `ImmutableBox[T]` from a given generator of `Option[T]`.
 *
 * This method creates a generator that produces instances of `ImmutableBox[T]` by wrapping the values generated by
 * the provided `Option[T]` generator. The resulting generator can be used in property-based testing to generate
 * various instances of `ImmutableBox` with different values.
 *
 * @param gen The generator that produces `Option[T]` values to be wrapped in an `ImmutableBox`.
 * @tparam T The type of the value contained within the `ImmutableBox`.
 * @return A generator that produces `ImmutableBox[T]` instances containing the values generated by the provided generator.
 */
private def immutableBoxGen[T](
    gen: Gen[Option[T]]
): Gen[ImmutableBox[T]] = gen.map(ImmutableBox(_))

/**
 * Generates a `MutableBox[T]` from a given generator of `Option[T]`.
 *
 * This method creates a generator that produces instances of `MutableBox[T]` by wrapping the values generated by
 * the provided `Option[T]` generator. The resulting generator can be used in property-based testing to generate
 * various instances of `MutableBox` with different values.
 *
 * @param gen The generator that produces `Option[T]` values to be wrapped in a `MutableBox`.
 * @tparam T The type of the value contained within the `MutableBox`.
 * @return A generator that produces `MutableBox[T]` instances containing the values generated by the provided generator.
 */
private def mutableBoxGen[T](
    gen: Gen[Option[T]]
): Gen[MutableBox[T]] = gen.map(MutableBox(_))  
