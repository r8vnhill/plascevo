/*
 * Copyright (c) 2024, Ignacio Slater M.
 * 2-Clause BSD License.
 */

package cl.ravenhill.plascevo
package assertions

import assertions.print.Printed

import cl.ravenhill.composerr.Constrained.{constrained, constrainedTo}
import cl.ravenhill.composerr.constraints.ints.BePositive
import cl.ravenhill.composerr.constraints.iterable.BeEmpty

import scala.collection.mutable.ListBuffer

/** A basic implementation of the `ErrorCollector` trait for managing and reporting errors.
 *
 * The `BasicErrorCollector` class provides a straightforward mechanism for collecting and handling errors, including
 * the ability to track error context clues and manage the depth of error collection. It supports both "soft" and
 * "hard" error collection modes, allowing flexibility in how errors are accumulated and reported.
 *
 * This class is designed to be extended by other classes that need a basic error collection mechanism with additional
 * customization.
 */
open class BasicErrorCollector extends ErrorCollector {

    /** A buffer that stores the list of collected failures.
     *
     * The `failures` buffer holds the errors that have been collected during the operation of the system.
     */
    protected val failures: ListBuffer[Throwable] = ListBuffer.empty

    /** The mode of error collection, either "soft" or "hard".
     *
     * The `mode` variable determines how errors are collected. In "hard" mode, errors may be thrown immediately,
     * whereas in "soft" mode, they may be accumulated for later processing.
     */
    protected final var mode: ErrorCollectionMode = ErrorCollectionMode.Hard

    /** A buffer that stores context clues for errors.
     *
     * The `clues` buffer holds a list of clues that provide additional context for the errors being collected. These
     * clues are used to enhance the information available when reporting or debugging errors.
     */
    protected val clues: ListBuffer[Clue] = ListBuffer.empty

    /** The subject related to the current error context.
     *
     * The `_subject` variable holds an optional value representing the subject or context of the current error. This
     * can be used to provide additional information when reporting errors.
     */
    private var _subject: Option[Printed] = None

    /** The current depth level of error collection.
     *
     * The `_depth` variable tracks how deeply nested the error collection process is. This can be useful in systems
     * where nested operations require different levels of error handling.
     */
    private var _depth: Int = 0

    /** Returns the current depth level of error collection.
     *
     * @return The current depth as an integer.
     */
    override def depth: Int = _depth

    /** Sets the depth level of error collection.
     *
     * Ensures that the depth level is a positive integer before setting it.
     *
     * @param value The new depth level.
     * @throws CompositeException Containing all constraints that failed.
     * @throws IntConstraintException If the depth is not a positive integer; wrapped in a `CompositeException`.
     */
    override def depth_=(value: Int): Unit = {
        constrained {
            "Depth must be a positive integer" | {
                value must BePositive
            }
        }
        _depth = value
    }

    /** Returns the subject related to the current error context.
     *
     * @return An optional `Printed` value representing the subject.
     */
    override def subject: Option[Printed] = _subject

    /** Sets the subject related to the current error context.
     * 
     * @param value An optional `Printed` value representing the new subject.
     */
    override def subject_=(value: Option[Printed]): Unit = _subject = value

    /** Gets the current error collection mode.
     *
     * @return The current `ErrorCollectionMode`, which determines how errors are collected and reported.
     */
    override def collectionMode: ErrorCollectionMode = mode

    /** Sets the error collection mode.
     * 
     * @param value The new `ErrorCollectionMode`.
     */
    override def collectionMode_=(value: ErrorCollectionMode): Unit = mode = value

    /** Adds a clue to the current error context.
     *
     * A clue is a lambda function that provides additional context or information related to an error. This method
     * allows adding such clues to the current context.
     *
     * @param clue A lambda function that returns a `String` representing the clue.
     */
    override def pushClue(clue: Clue): Unit = clues += clue

    /** Removes the most recently added clue from the error context.
     *
     * This method ensures that there is at least one clue in the context before attempting to remove it, preventing
     * errors due to empty clues.
     *
     * @throws CompositeException Containing all constraints that failed.
     * @throws IterableConstraintException If the collection is empty; wrapped in a `CompositeException`.
     */
    override def popClue(): Unit = clues.constrainedTo {
        "Cannot pop a clue when there are no clues" | {
            clues mustNot BeEmpty()
        }
    }.remove(0)

    /** Returns the sequence of clues in the current error context.
     * 
     * @return A sequence of `Clue` functions.
     */
    override def clueContext: Seq[Clue] = clues.reverse.toSeq

    /** Adds an error to the collection.
     * 
     * @param error The `Throwable` representing the error to be collected.
     */
    override def pushError(error: Throwable): Unit = failures += error

    /** Returns the sequence of collected errors.
     * 
     * @return A sequence of `Throwable` objects representing the collected errors.
     */
    override def errors: Seq[Throwable] = failures.toSeq

    /** Clears all collected errors. */
    override def clear(): Unit = failures.clear()
}
