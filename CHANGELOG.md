# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/) and this project adheres to 
[Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [2.0.0] - 2024-08-05

## Added
- **New constrainedTo() function:** Introduced a new function to apply constraints to the result of an operation, returning the result if it satisfies the constraints or throwing an exception otherwise.
- **`BeNull` Constraint:** Added a new constraint to validate if a value is `null`.

## Changed
- **Migration to KMP:** The library has been migrated to Kotlin Multiplatform (KMP) to support cross-platform development.
- **Updated Kotlin Version:** Upgraded the Kotlin version to `2.0.0` to align with the latest language features and improvements.

## [1.5.0] - 2024-01-17

## Added
- **Custom Exception Specification for Constraints** is now stable and no longer experimental. 

## Changed
- `CompositeException` now displays the source exception's class name in the message for better readability.

## [1.4.0] - 2023-12-21

### Added

- [EXPERIMENTAL] **`BeFinite` Constraint:**
  - Introduced the `BeFinite` object in the `cl.ravenhill.jakt.constraints.doubles` package.
  - This new constraint implements the `DoubleConstraint` interface and is designed to check if a given `Double` value is finite.
  - The primary purpose is to enhance the validation capabilities for `Double` values, ensuring they are neither infinite nor `NaN`.
- [EXPERIMENTAL] **`BeInfinite` Double Constraint:**
  - Introduced the `BeInfinite` data object in the `constraints.doubles` package.
  - This constraint is designed to validate whether a `Double` value is infinite (either positive or negative infinity).
  - It serves as a crucial tool for scenarios involving mathematical computations or simulations where the concept of infinity is relevant, such as in cases of overflow.
- [EXPERIMENTAL] **`BeNaN` Double Constraint:**
  - Introduced the `BeNaN` data object within the library, specifically for validating if a `Double` value is NaN (Not a Number).
  - This new feature significantly enhances the library's capabilities by enabling the detection of NaN values in numerical computations, which is crucial for accurate and reliable data validation in mathematical and scientific applications.
- [EXPERIMENTAL] [#1](https://github.com/r8vnhill/strait-jakt/issues/1) **Custom Exception Specification for Constraints**

## [1.3.1] - 2023-12-20

### Added
- **`BePositive` Constraint:**
  - [EXPERIMENTAL] Introduced the `BePositive` constraint for validating positive double values. This new constraint enhances the library's capabilities in handling numerical validations.

- **ExperimentalJakt Annotation:**
  - Introduced the `ExperimentalJakt` annotation as a marker for experimental features within the Jakt project. This annotation helps in identifying features that are still in the experimental phase and may be subject to change.

### Changed
- **Kotlin Version Update:**
  - Downgraded the Kotlin version to `1.9.21` to resolve compatibility issues.

## [1.3.0] - 2023-12-20

### Changed
- **Updated Dependencies:**
  - **Kotest:** Upgraded the Kotest framework for assertions, properties, and junit5 runner to their latest versions. Adjustments made in configuration files to align with the new versions.
  - **Dokka Plugin:** Updated to its newer version. Configuration files modified to accommodate changes in the plugin.
  - **Detekt Plugin:** Upgraded to the latest version. Necessary adjustments made in the respective configuration files.

## [1.2.0] - 2023-12-04

### Added
- `shortCircuit` flag to stop evaluating constraints after the first failure.
- New String Validation Constraints:
  - `BeAlphanumeric`: Validates if a string contains only alphanumeric characters.
  - `BeEmpty`: Checks if a string is empty.
  - `Contain`: Ensures a string matches a specified regular expression.
- Test Suites for String Constraints:
  - `BeAlphanumericTest` for validating alphanumeric strings.
  - `BeEmptyTest` for validating empty strings.
  - `ContainTest` for validating strings against regular expressions.
- `BeMonotonicallyIncreasing` Constraint: Validates that a collection of elements is monotonically increasing, useful for sorted lists and sequences.    
- Special Characters Generator in `Strings` (datatypes): Facilitates creating test datasets with non-alphanumeric characters.
- `BeMonotonicallyDecreasing` Constraint:
  - A new constraint designed to validate if a collection of elements is monotonically decreasing.
  - Useful in scenarios such as verifying sorted arrays in descending order.
- `BeMonotonicallyDecreasingTest`:
  - A test suite developed to ensure the correctness of the `BeMonotonicallyDecreasing` constraint under various conditions.
- `BeAtLeast` Constraint:
  - A new constraint for checking if a numerical value (both Double and Int) is at least a specified minimum.
  - Accompanied by test suites to ensure functionality for both Double and Int types.
- `BeAtMost` Constraint:
  - A new constraint for validating whether a value is less than or equal to a specified maximum.
  - Useful for enforcing upper limits in various calculations, settings, or measurements.
- `BeNegativeConstraint`
  - A new constraint for checking if a comparable value is negative.
  - Useful for ensuring that a value is less than "zero".
- `BePositiveConstraint`
  - A new constraint for checking if a comparable value is positive.
  - Useful for ensuring that a value is greater than "zero".

### Removed
- Obsolete Pair Constraints:
  - `BeFinite.kt`
  - `BeStrictlyOrdered.kt`
  - `PairConstraint.kt`
  - Corresponding tests for these constraints.

### Changed
- Project Settings Files: Adjustments made for improved project configuration.
- `CollectionConstraint` Refactoring:
  - Enhanced to support generic collections.
  - Replaced wildcards with generic types for increased type safety and to avoid unexpected behaviors.
- Refactored `BeInRange` Constraint:
  - Updated for better extensibility and ease of future enhancements.


### Improved
- Documentation:
  - `BeAlphanumeric` and `BeEmpty` classes now have enhanced documentation for better readability and comprehension.
- `JaktTest` Enhancements:
  - Added assertions to verify the `BeEqualTo` constraint behavior.
  - Included comprehensive tests for `CompositeException` handling.
  - Assured correct application of constraints and appropriate exception handling.
- Testing Structure:
  - Refactored existing tests by introducing a generic function for constraint validation.
  - Updated test cases to align with the new testing approach.
  - This improvement focuses on enhancing code reusability and readability in tests.

## [1.1.0] - 2023-11-08

### Added
- Predicate support to the `HaveSize` constraint for more flexible collection size checks.
- A `CHANGELOG` file to the project for documenting changes per industry standards.

### Improved
- The `CompositeException` to ensure that it is only thrown when there is at least one exception present.

### Refactored
- The `Ranges` utility class to simplify its implementation.

### Removed
- Deprecated functions from the `Ranges` utility class.

### Changed
- Moved the `Ranges` utility class to an appropriate package to better reflect its usage within the project.
