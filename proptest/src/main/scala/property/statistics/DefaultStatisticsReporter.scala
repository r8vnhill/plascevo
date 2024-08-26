/*
 * Copyright (c) 2024, Ignacio Slater M.
 * 2-Clause BSD License.
 */

package cl.ravenhill.plascevo
package property.statistics

import property.commons.{TestNameContextActor, TestNameContextElement}
import property.context.PropertyContext

import akka.actor.Scheduler
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.AskPattern.Askable
import akka.util.Timeout

import scala.concurrent.duration.DurationInt
import scala.concurrent.{ExecutionContext, Future}

object DefaultStatisticsReporter extends StatisticsReporter {
    /**
     * Outputs the test statistics, both unlabelled and labelled, to the console.
     *
     * This method handles the output of test statistics in two parts:
     * 1. Unlabelled statistics: These are statistics that have no associated label. If such statistics exist and are
     * non-empty, they are printed first.
     * 2. Labelled statistics: These are statistics that have associated labels. The method iterates through each label
     * and prints the corresponding statistics.
     *
     * @param statistics The `TestStatistics` object containing the details of the test results, including the number of
     *                   iterations, argument count, labels, and statistics. The method uses this information to
     *                   generate and print the output.
     * @param context    The `PropertyContext` object containing the context of the property-based test. This context
     *                   includes the test name, seed, and other relevant information.
     */
    override def output(statistics: TestStatistics)(using context: PropertyContext): Unit = {
        outputUnlabelledStats(statistics)
        outputLabelledStats(statistics)
    }

    /**
     * Outputs statistics that are unlabelled.
     *
     * @param statistics The statistics data to be output.
     */
    private def outputUnlabelledStats(statistics: TestStatistics)(using context: PropertyContext): Unit = {
        statistics.statistics.get(None) match {
            case Some(unlabelled) if unlabelled.nonEmpty =>
                val header = createHeader(statistics.iterations, statistics.numArgs, Option(null))
                printHeaderAndStats(header, unlabelled, statistics.iterations)
            case _ => // Do nothing if there are no unlabelled statistics
        }
    }

    /**
     * Outputs statistics that are labelled.
     *
     * @param statistics The statistics data to be output.
     */
    private def outputLabelledStats(statistics: TestStatistics)(using context: PropertyContext): Unit = {
        statistics.statistics.foreach {
            case (Some(label), stats) =>
                val header = createHeader(statistics.iterations, statistics.numArgs, Some(label))
                printHeaderAndStats(header, stats, statistics.iterations)
            case _ => // Do nothing if the label is None or if there are no stats
        }
    }

    /**
     * Prints a header and associated statistics.
     *
     * @param header     The header to be printed.
     * @param stats      The statistics to be printed.
     * @param iterations The total number of iterations.
     */
    private def printHeaderAndStats(header: String, stats: Map[Option[Any], Int], iterations: Int): Unit = {
        println()
        println(header)
        println()
        printStats(stats, iterations)
        println()
    }

    private def createHeader(iterations: Int, numArgs: Int, label: Option[Label])
        (using context: PropertyContext): String = {
        val testName = context.testName
        ???
    }

    private def printStats(stats: Map[Option[Any], Int], iterations: Int): Unit = ???
}
