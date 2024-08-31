/*
 * Copyright (c) 2024, Ignacio Slater M.
 * 2-Clause BSD License.
 */

package cl.ravenhill.munit.assertions

/**
 * An object that provides utilities for formatting error messages in a style consistent with IntelliJ IDEA.
 *
 * The `IntelliJFormatter` object offers methods to format error messages in a way that is easily readable and familiar
 * to users of IntelliJ IDEA. The formatted messages display both the expected and actual values, with an optional
 * inclusion of type information.
 */
object IntelliJFormatter {

    /**
     * Formats an error message showing the expected and actual values.
     *
     * The `intellijFormatError` method creates a formatted error message that compares the expected and actual values.
     * The format follows the IntelliJ IDEA convention, making it easy to identify discrepancies between the two values.
     *
     * @param expected The expected value wrapped in an `Expected` instance.
     * @param actual   The actual value wrapped in an `Actual` instance.
     * @return A `String` representing the formatted error message, indicating the difference between the expected and
     *         actual values.
     * @example
     * {{{
     * val expected = Expected(Printed("foo"))
     * val actual = Actual(Printed("bar"))
     * val errorMessage = IntelliJFormatter.intellijFormatError(expected, actual)
     * // errorMessage: String = "expected:<foo> but was:<bar>"
     * }}}
     */
    def intelliJFormatError(
        expected: Expected,
        actual: Actual
    ): String = s"expected:<${expected.value.value}> but was:<${actual.value.value}>"

    /**
     * Formats an error message showing the expected and actual values along with their type information.
     *
     * The `intelliJFormatErrorWithType` method creates a formatted error message that not only compares the expected 
     * and actual values but also includes their associated type information. This is particularly useful when the type 
     * of the values plays a significant role in understanding the error.
     *
     * @param expected The expected value with its type information, wrapped in an `ExpectedWithType` instance.
     * @param actual   The actual value with its type information, wrapped in an `ActualWithType` instance.
     * @return A `String` representing the formatted error message, including both the values and their types.
     * @example
     * {{{
     * val expected = ExpectedWithType(PrintedWithType("foo", "String"))
     * val actual = ActualWithType(PrintedWithType("bar", "String"))
     * val errorMessage = IntelliJFormatter.intelliJFormatErrorWithType(expected, actual)
     * // errorMessage: String = "expected:String<foo> but was:String<bar>"
     * }}}
     */
    def intelliJFormatErrorWithType(
        expected: ExpectedWithType,
        actual: ActualWithType
    ): String = s"expected:${expected.value.typeInfo}<${expected.value.value}> " +
        s"but was:${actual.value.typeInfo}<${actual.value.value}>"
}
