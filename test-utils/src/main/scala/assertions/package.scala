/*
 * Copyright (c) 2024, Ignacio Slater M.
 * 2-Clause BSD License.
 */

package cl.ravenhill.plascevo

package object assertions {
    /**
     * Converts the current error context clues into a formatted string.
     *
     * The `clueContextAsString` method retrieves the list of context clues from the `errorCollector`. If there are no
     * clues, it returns an empty string. If there are clues, it applies each clue function (which generates a string)
     * and concatenates the results into a single string, with each clue on a new line. An additional newline character
     * is appended at the end of the string for formatting purposes.
     *
     * @return A string representation of the context clues, each on a new line, or an empty string if no clues are
     *         present.
     */
    def clueContextAsString: String = errorCollector.clueContext match {
        case Nil => ""
        case clues => clues.map(_()).mkString("\n") + "\n"
    }
}
