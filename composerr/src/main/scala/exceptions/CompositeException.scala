package cl.ravenhill.composerr
package exceptions

class CompositeException(val throwables: List[Throwable]) extends Exception(
    if (throwables.size == 1)
        s"An exception occurred -- [${throwables.head.getClass.getSimpleName}] ${throwables.head.getMessage}"
    else
        "Multiple exceptions occurred -- " +
            throwables.map(t => s"{ [${t.getClass.getSimpleName}] ${t.getMessage} }").mkString(",\n")
) {
    require(throwables.nonEmpty, "The list of throwables cannot be empty")
}
