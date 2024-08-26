/*
 * Copyright (c) 2024, Ignacio Slater M.
 * 2-Clause BSD License.
 */

package munit.checkall
package arbitrary

/**
 * A trait for classifying values in property-based testing.
 *
 * The `Classifier` trait provides a mechanism for categorizing values during property-based testing. 
 * Classification is used to group test cases into categories based on specific characteristics of the values being
 * tested. This helps in understanding the distribution of test cases and ensuring that all relevant categories are
 * covered during testing.
 *
 * @tparam A The type of value that this classifier can categorize.
 */
trait Classifier[A] {

    /**
     * Classifies a given value into an optional category.
     *
     * The `apply` method takes a value of type `A` and returns an `Option[String]` representing the category to
     * which the value belongs. If the value does not belong to any specific category, the method returns `None`.
     * Classification is useful in property-based testing to ensure that test cases are properly grouped and that the
     * distribution of test cases across different categories is balanced.
     *
     * @param value The value to classify.
     * @return An `Option[String]` containing the category name, or `None` if the value does not fit into any category.
     */
    def apply(value: A): Option[String]
}
