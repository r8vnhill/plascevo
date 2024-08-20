# Documentation Guidelines

### 1. Markdown Formatting for Code Snippets

When writing code snippets in documentation, **ALWAYS** use proper Markdown syntax. Enclose all code snippets with:

```scala
{{{
// ...
}}}
```

This ensures that the code is readable and properly formatted.

### 2. Preferred Use of Reference Links

**PREFER** using reference links (e.g., [[String]]) over monospace formatting (e.g., `String`) when referencing types or
other significant elements in the documentation.

### 3. Scaladoc format

When writing Scaladoc comments, follow this format:

```scala
/** Short description.
  *
  * Detailed description.
  */
```
Note that the summary should start in the same line as the opening `/**` and end with a period. The detailed description should start in the next line and be separated by a blank line.
The first * in the detailed description should be aligned with the second * in the summary.

### 4. Usage Examples

When providing usage examples in docstring comments, follow this syntax:

````scala
/**
 * Documentation.
 *
 * <h2>Usage:</h2>
 * 
 * Usage details and scenarios.
 * 
 * @tags
 * @example
 * {{{
 * // example 1 code
 * }}}
 * @example
 * {{{
 * // example 2 code
 * }}}
 */
def foo(params) = elements.forEach(action)
````
