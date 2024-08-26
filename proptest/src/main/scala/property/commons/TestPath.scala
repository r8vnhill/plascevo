/*
 * Copyright (c) 2024, Ignacio Slater M.
 * 2-Clause BSD License.
 */

package cl.ravenhill.plascevo
package property.commons

import java.nio.file.{Files, Path, Paths}

/**
 * Represents the path to a test's seed file or related resources.
 *
 * The `TestPath` case class encapsulates the path value as a `String`. This path is used to locate seed files and other
 * resources associated with a specific test in the property-based testing framework. The `value` represents the file
 * path or identifier for the test's resources.
 *
 * This class also provides utilities to handle the seed file path within the `.plascevo/seeds` directory,
 * ensuring that directories are created as needed.
 *
 * @param value A `String` representing the path to the test's resources.
 */
case class TestPath(value: String) {

    /**
     * Lazily initializes the directory for storing seed files.
     *
     * The `seedDirectory` is determined based on the `XDG_CACHE_HOME` environment variable, or defaults to the user's
     * home directory if not set. Within this directory, a `.plascevo/seeds` folder is created (if it doesn't already
     * exist) to store seed files related to the property-based tests.
     *
     * @return A `Path` representing the directory for seed files.
     */
    private[property] lazy val seedDirectory: Path = {
        val baseDir = Option(System.getenv("XDG_CACHE_HOME")).filter(_.nonEmpty)
            .getOrElse(System.getProperty("user.home"))

        val plascevoConfigDir = Paths.get(baseDir).resolve(".plascevo")

        val seedsDir = plascevoConfigDir.resolve("seeds")
        Files.createDirectories(seedsDir)
        seedsDir
    }

    /**
     * Resolves the full path to the seed file for this test.
     *
     * The `seedPath` method returns the full path to the seed file associated with this test path, located within the
     * `.plascevo/seeds` directory. The seed file name is derived by sanitizing the `value` to replace characters
     * that may not be valid in a file name.
     *
     * @return A `Path` representing the full path to the seed file.
     */
    def seedPath: Path = seedDirectory.resolve(seedFileName)

    /**
     * Generates a sanitized seed file name from the test path value.
     *
     * The `seedFileName` method replaces any occurrence of the characters `/ \ < > : ( )` in the `value` field with
     * an underscore `_`, creating a valid file name for storing the seed.
     *
     * @return A `String` representing the sanitized seed file name.
     */
    private def seedFileName: String = value.replaceAll("""[/\\<>:()]""", "_")
}
