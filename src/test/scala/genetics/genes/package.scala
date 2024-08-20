package cl.ravenhill.plascevo
package genetics

import org.scalacheck.{Arbitrary, Gen}

import scala.util.Random

package object genes:
    /** Represents a simple gene in an evolutionary algorithm.
     *
     * The `SimpleGene` case class models a gene with an integer value and a validity flag. It provides mechanisms to
     * generate new gene values and duplicate the gene with a new value. Additionally, it offers a `verify` method to 
     * check the validity of the gene.
     *
     * @param value   The integer value held by the gene.
     * @param isValid A boolean flag indicating whether the gene is valid. Defaults to `true`.
     */
    case class SimpleGene(value: Int, isValid: Boolean = true) extends Gene[Int, SimpleGene]:

        /** A generator function that creates a new gene value.
         *
         * This function generates a new integer value for the gene by incrementing the current value by 1.
         */
        override val generator: (Int, Random) => Int = (v, _) => v + 1

        /** Creates a duplicate of this gene with a new value.
         *
         * @param value The new value for the duplicated gene.
         * @return A new `SimpleGene` instance with the specified value.
         */
        override def duplicateWithValue(value: Int): SimpleGene = copy(value = value)

        /** Verifies the validity of this gene.
         *
         * This method checks the validity flag of the gene. If the gene is marked as invalid (`isValid` is `false`), 
         * it returns `false`. Otherwise, it delegates to the superclass `verify` method.
         *
         * @return `true` if the gene is valid, `false` otherwise.
         */
        override def verify(): Boolean =
            if !isValid then false
            else super.verify()
            

    /** Generates a `SimpleGene` with a specified validity.
     *
     * The `simpleGeneGen` function produces a generator (`Gen`) that creates instances of `SimpleGene`. The generated
     * gene's value is a random integer, and its validity is determined by the `isValid` generator.
     *
     * @param isValid A generator that produces boolean values indicating the validity of the gene.
     * @return A generator that produces `SimpleGene` instances with random values and specified validity.
     */
    def simpleGeneGen(
        isValid: Gen[Boolean] = Gen.const(true)
    ): Gen[SimpleGene] =
        for
            value <- Arbitrary.arbInt.arbitrary
            valid <- isValid
        yield SimpleGene(value, valid)
