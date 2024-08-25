/*
 * Copyright (c) 2024, Ignacio Slater M.
 * 2-Clause BSD License.
 */

package cl.ravenhill.plascevo
package property.arbitrary.numbers

import property.arbitrary.Classifier

/**
 * A classifier for integers that categorizes values based on their properties.
 *
 * The `IntClassifier` class provides a mechanism for classifying integer values into specific categories based on 
 * their characteristics. The classification considers whether the value is zero, the minimum or maximum value, 
 * positive or negative, and whether it is even or odd. This classifier is useful in property-based testing to ensure 
 * that different categories of integers are properly represented and tested.
 *
 * @param min The minimum value that the classifier recognizes as "MIN".
 * @param max The maximum value that the classifier recognizes as "MAX".
 */
class IntClassifier(private val min: Int, private val max: Int) extends Classifier[Int] {

    /**
     * Constructs an `IntClassifier` using a `Range` to define the minimum and maximum values.
     *
     * This auxiliary constructor allows you to create an `IntClassifier` by specifying a `Range` object. The start of
     * the range is used as the minimum value (`min`), and the end of the range is used as the maximum value (`max`).
     * This constructor provides a convenient way to initialize the classifier with a range of values.
     *
     * @param range A `Range` object that defines the minimum (`range.start`) and maximum (`range.end`) values for the
     *              classifier.
     */
    def this(range: Range) = this(range.start, range.end)

    /**
     * Classifies an integer value into one of several categories.
     *
     * @param value The integer value to classify.
     * @return An `Option[String]` containing the category name, or `None` if the value does not fit into any category.
     */
    override def apply(value: Int): Option[String] = value match
        case 0 => Some("ZERO")
        case `min` => Some("MIN")
        case `max` => Some("MAX")
        case _ if value > 0 && value % 2 == 0 => Some("POSITIVE_EVEN")
        case _ if value > 0 => Some("POSITIVE_ODD")
        case _ if value < 0 && value % 2 == 0 => Some("NEGATIVE_EVEN")
        case _ => Some("NEGATIVE_ODD")
        case _ => None
}
