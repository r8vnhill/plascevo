/*
 * Copyright (c) 2024, Ignacio Slater M.
 * 2-Clause BSD License.
 */

package cl.ravenhill.plascevo
package property

object SysPropEnv {
    
    /**
     * Retrieves a system property value, converts it using a provided converter function, and returns it. If the
     * system property is not set, returns a default value.
     *
     * The `sysprop` method looks up a system property by its key. If the property is found, it converts the property
     * value to the desired type using the provided converter function. If the property is not found, it returns a
     * default value.
     *
     * @param key The key of the system property to look up.
     * @param default The default value to return if the system property is not set.
     * @param converter A function that converts the system property value (if found) from a String to the desired type
     *                  `T`.
     * @tparam T The type of the value to return. This is the type that the converter function returns.
     * @return The converted value of the system property if found, otherwise the default value.
     */
    def sysprop[T](key: String, default: T)(converter: String => T): T = sysprop(key).map(converter).getOrElse(default)

    /**
     * Retrieves the value of a system property as an `Option[String]`.
     *
     * The `sysprop` method attempts to retrieve the value of the system property identified by the given key. If the
     * system property is set, it returns the value wrapped in a `Some`. If the property is not set, it returns `None`.
     *
     * @param key The key of the system property to retrieve.
     * @return An `Option[String]` containing the system property value if it is set, or `None` if it is not.
     */
    def sysprop(key: String): Option[String] = Option(System.getProperty(key))
}
