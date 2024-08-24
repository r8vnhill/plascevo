/*
 * Copyright (c) 2024, Ignacio Slater M.
 * 2-Clause BSD License.
 */

package cl.ravenhill.plascevo

import matchers.ShouldMatchers

/** Abstract base class for unit tests in the Plascevo framework using MUnit and ShouldMatchers.
 *
 * The `AbstractMunitPlascevoTest` class serves as an abstract base class for writing unit tests within the Plascevo
 * framework. It extends `munit.FunSuite`, which provides the structure and functionality for defining test suites
 * using the MUnit testing framework. Additionally, it mixes in the `ShouldMatchers` trait, allowing for more expressive
 * assertions and matchers in the tests.
 *
 * This class is intended to be extended by other test classes within the Plascevo framework, providing a consistent
 * foundation and utility methods for writing tests.
 */
abstract class AbstractMunitPlascevoTest extends munit.FunSuite with ShouldMatchers
