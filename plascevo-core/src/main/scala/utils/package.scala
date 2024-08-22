package cl.ravenhill.plascevo

package object utils {

    extension (b: Boolean) {

        /** Converts the boolean value to an integer.
         *
         * The `toInt` method returns 1 if the boolean value is `true`, and 0 if the boolean value is `false`.
         *
         * @return 1 if `b` is `true`, otherwise 0.
         */
        def toInt: Int = if b then 1 else 0
    }

    extension (iterable: Iterable[Long]) {

        /** Calculates the average of the elements in this `Iterable[Long]`.
         *
         * The `average` method computes the sum of all elements in the iterable and divides it by the number of
         * elements. It performs a check to prevent overflow in the count of elements. If the iterable is empty, the
         * method returns `Double.NaN`.
         *
         * @return The average of the elements as a `Double`, or `Double.NaN` if the iterable is empty.
         * @throws ArithmeticException if the count of elements overflows.
         */
        def average: Double = {
            var sum = 0.0
            var count = 0
            for (value <- iterable) {
                sum += value
                count += 1
                checkCountOverflow(count)
            }
            if (count == 0) then Double.NaN else sum / count
        }
    }

    extension [T](iterable: Iterable[T]) {

        /** Finds the maximum value in the collection based on a selector function, returning it as an `Option`.
         *
         * The `maxOfOption` method iterates through the collection, applies the `selector` function to each element,
         * and tracks the maximum value based on the provided ordering. If the collection is empty, it returns `None`.
         *
         * @param selector A function that extracts the value to be compared from each element in the collection.
         * @param ord      An implicit `Ordering` instance that defines how to compare the extracted values.
         * @tparam R The type of the value extracted by the selector, which must have an implicit `Ordering`.
         * @return An `Option` containing the maximum value based on the selector, or `None` if the collection is empty.
         * @example
         * {{{
         * val numbers = List(1, 2, 3, 4, 5)
         * val maxNumber = numbers.maxOfOption(identity)
         * // maxNumber: Option[Int] = Some(5)
         *
         * val people = List(("Alice", 30), ("Bob", 25), ("Charlie", 35))
         * val oldestPerson = people.maxOfOption(_._2)
         * // oldestPerson: Option[Int] = Some(35)
         * }}}
         */
        def maxOfOption[R](selector: T => R)(using ord: Ordering[R]): Option[R] = {
            val iterator = iterable.iterator
            if (!iterator.hasNext) return None
            var maxValue = selector(iterator.next())
            while (iterator.hasNext) {
                val value = selector(iterator.next())
                if (ord.gt(value, maxValue)) maxValue = value
            }
            Some(maxValue)
        }

        /** Finds the minimum value in the collection based on a selector function, returning it as an `Option`.
         *
         * The `minOfOption` method iterates through the collection, applies the `selector` function to each element,
         * and tracks the minimum value based on the provided ordering. If the collection is empty, it returns `None`.
         *
         * @param selector A function that extracts the value to be compared from each element in the collection.
         * @param ord      An implicit `Ordering` instance that defines how to compare the extracted values.
         * @tparam R The type of the value extracted by the selector, which must have an implicit `Ordering`.
         * @return An `Option` containing the minimum value based on the selector, or `None` if the collection is empty.
         * @example
         * {{{
         * val numbers = List(1, 2, 3, 4, 5)
         * val minNumber = numbers.minOfOption(identity)
         * // minNumber: Option[Int] = Some(1)
         *
         * val people = List(("Alice", 30), ("Bob", 25), ("Charlie", 35))
         * val youngestPerson = people.minOfOption(_._2)
         * // youngestPerson: Option[Int] = Some(25)
         * }}}
         */
        def minOfOption[R](selector: T => R)(using ord: Ordering[R]): Option[R] = {
            val iterator = iterable.iterator
            if (!iterator.hasNext) return None
            var minValue = selector(iterator.next())
            while (iterator.hasNext) {
                val value = selector(iterator.next())
                if (ord.lt(value, minValue)) minValue = value
            }
            Some(minValue)
        }
    }

    /** Checks for overflow in the count of elements during iteration.
     *
     * The `checkCountOverflow` method is a utility function designed to detect integer overflow when counting elements
     * during an iteration. If the count becomes negative, which indicates an overflow, the method throws an
     * `ArithmeticException`. This is crucial for ensuring the integrity of operations that rely on accurate element
     * counts.
     *
     * @param count The current count of elements.
     * @return The count if no overflow is detected.
     * @throws ArithmeticException if the count overflows and becomes negative.
     */
    private def checkCountOverflow(count: Int): Int = {
        if (count < 0) throw ArithmeticException("Count overflow detected")
        count
    }

    extension (seq: Seq[Double]) {

        /** Subtracts a given `Double` value from each element in the sequence.
         *
         * The `sub` method returns a new sequence where the specified `double` value is subtracted from each element of
         * the original sequence. This is useful in situations where you need to adjust all values in the sequence by a
         * fixed amount.
         *
         * @param double The `Double` value to subtract from each element in the sequence.
         * @return A new sequence where each element is the result of the subtraction.
         * @example
         * {{{
         * val numbers = Seq(5.0, 10.0, 15.0)
         * val result = numbers sub 2.5
         * // result: Seq[Double] = Seq(2.5, 7.5, 12.5)
         * }}}
         */
        infix def sub(double: Double): Seq[Double] = seq.map(_ - double)

        /** Computes the cumulative sum (incremental values) of the sequence.
         *
         * The `incremental` method calculates the cumulative sum of the elements in the sequence, returning a new
         * sequence where each element is the sum of all previous elements in the original sequence up to that point.
         * This is useful for generating running totals or for understanding the progression of values in the sequence.
         *
         * @return A sequence of cumulative sums.
         * @example
         * {{{
         * val numbers = Seq(1.0, 2.0, 3.0, 4.0)
         * val cumulativeSums = numbers.incremental
         * // cumulativeSums: Seq[Double] = Seq(1.0, 3.0, 6.0, 10.0)
         * }}}
         */
        def incremental: Seq[Double] = seq.scanLeft(0.0)(_ + _).tail
    }

    /** An enumeration representing the sorting state of a collection or data structure.
     *
     * The `Sorting` enum defines two possible states: `Sorted` and `Unsorted`. This can be used to indicate whether a
     * collection, such as a list or array, is or should be sorted or not. It is useful in algorithms or data structures
     * where the sorting state of the data needs to be tracked or checked like in the case of the roulette wheel
     * selection operator.
     */
    enum Sorting {

        /** Indicates that the collection or data structure is sorted. */
        case Sorted

        /** Indicates that the collection or data structure is not sorted. */
        case Unsorted
    }

    /** An enumeration representing the exclusivity of selection in a genetic algorithm.
     *
     * The `Exclusivity` enum defines two possible states for selection during the evolutionary process: `Exclusive` and
     * `NonExclusive`. These states determine whether the same individual can be selected multiple times to be parents
     * of the next generation.
     */
    enum Exclusivity {

        /** Indicates that a selection is exclusive, meaning that the same individual cannot be selected more than once
         * to be a parent of the next generation.
         */
        case Exclusive

        /** Indicates that a selection is non-exclusive, allowing the same individual to be selected multiple times to
         * be a parent of the next generation.
         */
        case NonExclusive
    }
}
