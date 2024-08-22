package genetics.chromosomes

import cl.ravenhill.composerr.constrained
import cl.ravenhill.composerr.constraints.iterable.HaveSize
import cl.ravenhill.plascevo.exceptions.InvalidSizeException

object ChromosomeUtils {
    private[chromosomes] def enforceConstraints[T](ranges: Seq[(T, T)], filters: Seq[T => T], size: Int): Unit = {
        constrained {
            if (ranges.size > 1) {
                "Chromosome with multiple ranges must have the same number of genes as ranges." ~ (
                    ranges must HaveSize { _ == size },
                    InvalidSizeException(_)
                )
            }
            if (filters.size > 1) {
                "Chromosome with multiple filters must have the same number of genes as filters." ~ (
                    filters must HaveSize { _ == size },
                    InvalidSizeException(_)
                )
            }
        }
    }

    private[chromosomes] def adjustRangesAndFilters[T](
        size: Int,
        ranges: Seq[(T, T)],
        defaultRange: (T, T),
        filters: Seq[T => T],
        defaultFilter: T => T
    ) = {
        ranges.size match {
            case 0 => Seq.fill(size)(defaultRange)
            case 1 => Seq.fill(size)(ranges.head)
            case _ => ranges
        }
        filters.size match {
            case 0 => Seq.fill(size)(defaultFilter)
            case 1 => Seq.fill(size)(filters.head)
            case _ => filters
        }
    }
}
