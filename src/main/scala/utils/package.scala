package cl.ravenhill.plascevo

package object utils {
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
}
