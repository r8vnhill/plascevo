package cl.ravenhill.plascevo
package listeners.records

import scala.concurrent.duration.{Deadline, Duration}

/** An abstract class that represents a timed record, tracking the start time and duration of an event.
 *
 * The `AbstractTimedRecord` class provides a basic structure for tracking the timing of events or processes. It
 * includes an optional start time and a duration, which is measured in nanoseconds. Subclasses can extend this class to
 * implement specific timing-related functionality.
 */
trait TimedRecord:

    /** The start time of the event or process.
     *
     * This value is optional and may not be set. If set, it represents the point in time when the event or process
     * started.
     */
    var startTime: Option[Deadline] = None

    /** The duration of the event or process.
     *
     * This value represents the duration in nanoseconds. It is initialized to zero, indicating that no time has passed.
     */
    var duration: Long = Duration.Zero.toNanos
