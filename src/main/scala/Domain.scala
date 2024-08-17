package cl.ravenhill.plascevo

/** Defines domain-level configuration and utilities for the evolutionary algorithm framework.
 *
 * This object manages the equality threshold used in comparisons, provides a random number generator, and controls the
 * string representation mode.
 */
object Domain:

    /** The default threshold for equality comparisons.
     *
     * This value is used when comparing floating-point numbers to determine if they are equal within a certain
     * tolerance.
     */
    val DEFAULT_EQUALITY_THRESHOLD: Double = 0.0001

    private var _equalityThreshold: Double = DEFAULT_EQUALITY_THRESHOLD

    /** Retrieves the current equality threshold.
     *
     * @return The threshold used for determining equality in floating-point comparisons.
     */
    def equalityThreshold: Double = _equalityThreshold

    /** Sets the equality threshold for floating-point comparisons.
     *
     * @param value The new threshold value. It must be greater than or equal to zero and not NaN.
     * @throws IllegalArgumentException if the value is negative or NaN.
     */
    def equalityThreshold_=(value: Double): Unit =
        require(value > 0.0 || value == 0.0, s"The equality threshold ($value) must be greater than or equal to zero")
        require(!value.isNaN, "The equality threshold must not be NaN")
        _equalityThreshold = value

    /** Random number generator used throughout the framework.
     *
     * This can be set to control the randomness within the domain, useful for reproducible results in testing or
     * experimentation.
     */
    var random: scala.util.Random = scala.util.Random

    /** Mode controlling the string representation of various domain components.
     *
     * This can be set to change how objects within the domain are converted to strings, either in a default or
     * simplified format.
     */
    var toStringMode: ToStringMode = ToStringMode.DEFAULT

/** Enum representing different modes for string conversion.
 *
 * This is used to control how domain objects are represented as strings.
 */
enum ToStringMode:
    /** Default string conversion mode, providing detailed output. */
    case DEFAULT

    /** Simple string conversion mode, providing a more concise output. */
    case SIMPLE

