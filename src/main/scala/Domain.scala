package cl.ravenhill.plascevo


/** Contains global configuration settings for the evolutionary algorithm's domain.
 *
 * The `Domain` object serves as a central place to define and manage global settings that influence the behavior and
 * output of the evolutionary algorithm. One of the key settings is the [[toStringMode]], which determines how objects,
 * such as individuals and representations, are converted to string representations.
 */
object Domain {

    /** Specifies the mode for converting objects to their string representation.
     *
     * The `toStringMode` variable allows you to choose between different formats for the string representation of
     * objects within the domain. The available modes are defined by the [[ToStringMode]] enumeration. The default
     * mode is [[ToStringMode.DEFAULT]], but this can be changed to other modes such as [[ToStringMode.SIMPLE]] based on
     * the requirements of the specific context or output preference.
     *
     * @example
     * {{{
     * // Set the string representation mode to SIMPLE
     * Domain.toStringMode = ToStringMode.SIMPLE
     *
     * // Objects will now be represented in a simplified string format:
     * val individual: Individual[Int, SimpleGene, Genotype[Int, SimpleGene]] = ...
     * println(individual)
     * // Output might be something like: "[...] -> 42.0"
     * }}}
     */
    var toStringMode: ToStringMode = ToStringMode.DEFAULT
}

/** Enum representing different modes for string conversion.
 *
 * This is used to control how domain objects are represented as strings.
 */
enum ToStringMode {
    /** Default string conversion mode, providing detailed output. */
    case DEFAULT

    /** Simple string conversion mode, providing a more concise output. */
    case SIMPLE
}
