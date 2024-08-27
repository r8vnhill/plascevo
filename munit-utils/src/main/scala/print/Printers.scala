/*
 * Copyright (c) 2024, Ignacio Slater M.
 * 2-Clause BSD License.
 */

package cl.ravenhill.munit
package print

import scala.collection.mutable

/**
 * A registry for managing custom printers (`Print`) for specific classes.
 *
 * The `Printers` object provides a mechanism to register, retrieve, and remove custom printers for various classes.
 * These printers are used to define how instances of a particular class should be printed or displayed. This registry
 * is useful in scenarios where you need custom printing logic for specific types in a property-based testing or
 * logging framework.
 */
object Printers {

    /**
     * A mutable map that stores printers (`Print`) associated with specific classes.
     *
     * The map's keys are `Class` objects, and the values are `Print` instances that define how objects of that class
     * should be printed.
     */
    private val shows = mutable.Map[Class[?], Print[?]](
        classOf[Integer] -> IntPrint
    )

    /**
     * Registers a custom printer for a specific class.
     *
     * The `add` method associates a custom printer (`Print`) with a specific class. This printer will be used whenever
     * an instance of the class needs to be printed.
     *
     * @param clazz   The `Class` object representing the type for which the custom printer is being registered.
     * @param printer The `Print[T]` instance that defines how instances of the class should be printed.
     * @tparam T The type of the class for which the printer is being registered.
     */
    def add[T](clazz: Class[? <: T], printer: Print[T]): Unit =
        shows(clazz) = printer

    /**
     * Removes the custom printer associated with a specific class.
     *
     * The `remove` method deletes the entry in the registry for the specified class, effectively removing the custom
     * printing logic for that class.
     *
     * @param clazz The `Class` object representing the type whose printer should be removed.
     */
    def remove(clazz: Class[?]): Unit = shows.remove(clazz)

    /**
     * Retrieves a copy of the current registry of printers.
     *
     * The `all` method returns a copy of the current state of the printer registry as an immutable map. This allows
     * clients to view the registered printers without modifying the registry.
     *
     * @return A `Map` of class-printer pairs, representing the current state of the registry.
     */
    def all: Map[Class[?], Print[?]] = shows.toMap
}
