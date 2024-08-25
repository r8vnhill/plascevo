/*
 * Copyright (c) 2024, Ignacio Slater M.
 * 2-Clause BSD License.
 */

package cl.ravenhill.plascevo
package property.statistics

/**
 * Enum representing the various modes for reporting statistics during property-based testing.
 *
 * The `StatisticsReporterMode` enum defines the modes that control how and when statistics are reported in the
 * property-based testing process. Depending on the selected mode, statistics can be reported after every test, only
 * after failed tests, only after successful tests, or not at all.
 */
enum StatisticsReporterMode {

    /** Always report statistics, regardless of the test result.
     *
     * When the mode is set to `On`, statistics are reported after every test, whether the test passes or fails.
     */
    case On

    /** Only report statistics when a test fails.
     *
     * When the mode is set to `Failed`, statistics are reported only after a test fails. If the test passes, no
     * statistics are reported.
     */
    case Failed

    /** Only report statistics when a test passes.
     *
     * When the mode is set to `Success`, statistics are reported only after a test passes. If the test fails, no
     * statistics are reported.
     */
    case Success

    /** Do not report statistics.
     *
     * When the mode is set to `Off`, no statistics are reported, regardless of the test result.
     */
    case Off
}
