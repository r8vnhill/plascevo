/*
 * Copyright (c) 2024, Ignacio Slater M.
 * 2-Clause BSD License.
 */

package cl.ravenhill.munit.collectors

/** Represents the mode for collecting errors in a system.
 *
 * The `ErrorCollectionMode` enumeration defines two modes of operation for handling errors within a system. Depending
 * on the mode selected, the system can either continue execution while collecting errors (soft mode) or stop execution
 * immediately when an error occurs (hard mode). This enumeration is useful in contexts where the error-handling
 * strategy needs to be configurable.
 */
enum ErrorCollectionMode {

    /** Soft error collection mode.
     *
     * In `Soft` mode, the system continues execution even if errors occur, allowing multiple errors to be collected
     * and reported. This mode is useful in scenarios where all errors need to be gathered before taking action, such as
     * in batch processing or form validation.
     */
    case Soft

    /** Hard error collection mode.
     *
     * In `Hard` mode, the system stops execution immediately upon encountering an error. This mode is suitable for 
     * critical operations where continuing after an error could lead to inconsistent states or further issues, such as
     * in transaction processing or critical system operations.
     */
    case Hard
}
