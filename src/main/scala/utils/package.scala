package cl.ravenhill.plascevo

package object utils {

    extension (n: Double) {

        infix def ===(other: Double)(using equalityThreshold: Double): Boolean = Math.abs(n - other) < equalityThreshold

        infix def !==(other: Double)
            (using equalityThreshold: Double): Boolean = Math.abs(n - other) >= equalityThreshold
    }

    extension (n: Int) {

        /** Rounds the integer up to the nearest multiple of a specified value.
         *
         * The `roundUpToMultipleOf` method takes an integer `i` and rounds the current integer `n` up to the nearest
         * multiple of `i`. If `i` is 0, the method simply returns the original integer `n` since rounding up to a
         * multiple of zero is undefined.
         *
         * @param i The integer to which the current integer `n` should be rounded up.
         * @return The smallest integer that is a multiple of `i` and greater than or equal to `n`. If `i` is 0, returns
         *         `n`.
         */
        infix def roundUpToMultipleOf(i: Int): Int = i match {
            case 0 => n
            case _ =>
                val remainder = n % i
                if remainder == 0 then n else n + i - remainder
        }
    }

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

    extension[T] (iterable: Iterable[T]) {
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

    private def checkCountOverflow(count: Int): Int = {
        if (count < 0) throw ArithmeticException("Count overflow detected")
        count
    }

    extension (seq: Seq[Double]) {

        /** Subtracts a specified value from each element in the sequence.
         *
         * The `sub` method takes a `double` value and returns a new sequence where each element is the result of
         * subtracting the specified value from the corresponding element in the original sequence.
         *
         * @param double The value to subtract from each element in the iterable.
         * @return A new `Seq[Double]` where each element has been reduced by the specified value.
         */
        infix def sub(double: Double): Seq[Double] = seq.map(_ - double)

        /** Computes the incremental sums of a sequence of doubles.
         *
         * The `incremental` method returns a sequence where each element is the cumulative sum of the elements in the
         * original sequence up to that point. This is achieved by using the `scanLeft` operation, which iteratively
         * applies a summation function across the sequence, starting with an initial value of `0.0`.
         *
         * @return A sequence of `Double` values representing the cumulative sums of the original sequence.
         *         The first element corresponds to the sum of the first element of the original sequence, the second
         *         element to the sum of the first two elements, and so on.
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
