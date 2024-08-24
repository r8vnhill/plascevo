/*
 * Copyright (c) 2024, Ignacio Slater M.
 * 2-Clause BSD License.
 */

package cl.ravenhill.plascevo
package assertions

import assertions.print.Printed

import cl.ravenhill.composerr.constrained
import cl.ravenhill.composerr.constraints.ints.BePositive
import munit.internal.console.StackTraces

/** Trait for collecting and managing errors within a system.
 *
 * The `ErrorCollector` trait provides a framework for capturing, managing, and reporting errors during execution. It
 * includes features such as error depth tracking, error context clues, and customizable error collection modes. This
 * trait is typically used in systems where error handling needs to be flexible, allowing for either immediate or
 * deferred error reporting based on the configured collection mode.
 */
trait ErrorCollector {

    /** Returns the current depth level of error collection.
     *
     * @return The current depth as an integer.
     */
    def depth: Int

    /** Sets the depth level of error collection.
     *
     * @param value The new depth level.
     */
    def depth_=(value: Int): Unit

    /** Returns the current subject related to the error context.
     *
     * @return An optional `Printed` value representing the subject.
     */
    def subject: Option[Printed]

    /** Sets the subject related to the error context.
     *
     * This method allows setting or updating the subject associated with the current error context.
     *
     * @param value An optional `Printed` value representing the new subject.
     */
    def subject_=(value: Option[Printed]): Unit

    /** Gets the current error collection mode.
     *
     * @return The current `ErrorCollectionMode`, which determines how errors are collected and reported.
     */
    def collectionMode: ErrorCollectionMode

    /** Sets the error collection mode.
     *
     * This method allows setting the mode that controls how errors are collected (either in a soft or hard manner).
     *
     * @param value The new `ErrorCollectionMode`.
     */
    def collectionMode_=(value: ErrorCollectionMode): Unit

    /** Returns the sequence of collected errors.
     *
     * This method provides access to the list of errors that have been collected so far.
     *
     * @return A sequence of `Throwable` objects representing the collected errors.
     */
    def errors: Seq[Throwable]

    /** Adds an error to the collection.
     *
     * This method allows pushing a new error into the collection of errors.
     *
     * @param error The `Throwable` representing the error to be collected.
     */
    def pushError(error: Throwable): Unit

    /** Clears all collected errors.
     *
     * This method clears the current collection of errors, resetting the state of the error collector.
     */
    def clear(): Unit

    /** Adds a clue to the current error context.
     *
     * A clue is a lambda function that provides additional context or information related to an error. This method
     * allows adding such clues to the current context.
     *
     * @param clue A lambda function that returns a `String` representing the clue.
     */
    def pushClue(clue: Clue): Unit

    /** Removes the most recently added clue from the error context.
     *
     * This method allows removing the last clue that was added to the current error context.
     */
    def popClue(): Unit

    /** Returns the sequence of clues in the current error context.
     *
     * This method provides access to the list of clues that have been added to the current error context.
     *
     * @return A sequence of `Clue` functions.
     */
    def clueContext: Seq[Clue]

    /** Collects or throws an error based on the current error collection mode.
     *
     * The `collectOrThrow` method processes an error by either collecting it or throwing it immediately, depending on
     * the configured error collection mode. The method first trims the stack trace of the error for better readability,
     * then checks the current error collection mode:
     *
     * - In `ErrorCollectionMode.Soft`, the error is added to the error collector's list of failures without
     *   interrupting the execution flow.
     * - In `ErrorCollectionMode.Hard`, the error is immediately thrown, halting the execution.
     *
     * This method is useful in systems where different error-handling strategies are required depending on the
     * operational context.
     *
     * @param error The `Throwable` representing the error to be processed.
     * @throws Throwable The error itself if the collection mode is `Hard`.
     */
    def collectOrThrow(error: Throwable): Unit = {
        StackTraces.trimStackTrace(error)
        collectionMode match {
            case ErrorCollectionMode.Soft => pushError(error)
            case ErrorCollectionMode.Hard => throw error
        }
    }
}

/** A type alias for a lambda function that provides additional context or information related to an error.
 *
 * The `Clue` type represents a function that, when called, returns a `String`. Clues are used in error contexts to give
 * more detailed information about the error, which can be useful for debugging or reporting.
 */
type Clue = () => String
