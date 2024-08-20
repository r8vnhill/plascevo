package cl.ravenhill.plascevo
package listeners.plotter

import evolution.states.EvolutionState
import listeners.records.GenerationRecord
import listeners.{EvolutionListener, ListenerConfiguration}
import repr.{Feature, Representation}

import org.jfree.chart.plot.{PlotOrientation, XYPlot}
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer
import org.jfree.chart.*
import org.jfree.chart.labels.XYToolTipGenerator
import org.jfree.data.xy.{XYDataset, XYSeries, XYSeriesCollection}

import java.awt.BasicStroke
import javax.swing.{JFrame, WindowConstants}

class EvolutionPlotter[T, F <: Feature[T, F], R <: Representation[T, F], S <: EvolutionState[T, F, R, S]](
    configuration: ListenerConfiguration[T, F, R]
) extends EvolutionListener[T, F, R, S](configuration) with GenerationPlotterListener[T, F, R, S](configuration) {

    private val evolution = configuration.evolution

    private val ranker = configuration.ranker

    override def display: Unit = {
        val generations = evolution.generations
        val (bestFitness, worstFitness, averageFitness) = computeFitnessTriplet(generations.toList)
        val chart = createFitnessChart(bestFitness, worstFitness, averageFitness)
        showChart(chart)
    }

    protected def computeFitnessTriplet(
        generations: List[GenerationRecord[T, F, R]]
    ): (List[Double], List[Double], List[Double]) = {
        val sorted = generations.map { generation =>
            ranker.sort(generation.population.offspring.map(_.toIndividual()))
        }
        val bestFitness = sorted.map(_.head.fitness)
        val worstFitness = sorted.map(_.last.fitness)
        val averageFitness = sorted.map(population => population.map(_.fitness).sum / population.size)
        (bestFitness, worstFitness, averageFitness)
    }

    protected def createFitnessChart(best: List[Double], worst: List[Double], average: List[Double]): JFreeChart = {
        val dataset = new XYSeriesCollection()

        val bestSeries = new XYSeries("Best Fitness")
        best.indices.foreach { i =>
            bestSeries.add(i, best(i))
        }
        dataset.addSeries(bestSeries)

        val worstSeries = new XYSeries("Worst Fitness")
        worst.indices.foreach { i =>
            worstSeries.add(i, worst(i))
        }
        dataset.addSeries(worstSeries)

        val averageSeries = new XYSeries("Average Fitness")
        average.indices.foreach { i =>
            averageSeries.add(i, average(i))
        }
        dataset.addSeries(averageSeries)

        val chart = ChartFactory.createXYLineChart(
            "Fitness Over Generations",
            "Generations",
            "Fitness",
            dataset,
            PlotOrientation.VERTICAL,
            true,
            true,
            false
        )

        val plot: XYPlot = chart.getXYPlot

        val renderer = new XYLineAndShapeRenderer()

        // Increase line thickness
        val thickStroke = new BasicStroke(2.0f)
        renderer.setSeriesStroke(0, thickStroke)
        renderer.setSeriesStroke(1, thickStroke)
        renderer.setSeriesStroke(2, thickStroke)

        // Enable tooltips
        renderer.setDefaultToolTipGenerator((dataset: XYDataset, series: Int, item: Int) => {
            val x = dataset.getX(series, item)
            val y = dataset.getY(series, item)
            s"Generation: $x, Fitness: $y"
        })

        // Apply the customized renderer to the plot
        plot.setRenderer(renderer)

        chart
    }

    private def showChart(chart: JFreeChart): Unit = {
        val chartPanel = new ChartPanel(chart)

        // Enable zooming and panning
        chartPanel.setMouseWheelEnabled(true)
        chartPanel.setDomainZoomable(true)
        chartPanel.setRangeZoomable(true)

        // Set a preferred size for the chart panel
        chartPanel.setPreferredSize(new java.awt.Dimension(800, 600))

        // Add a chart mouse listener to handle mouse clicks and movements
        chartPanel.addChartMouseListener(new ChartMouseListener {
            override def chartMouseClicked(event: ChartMouseEvent): Unit = {
                val entity = event.getEntity
                if (entity != null) {
                    println(s"Clicked on: ${entity.getToolTipText}")
                }
            }

            override def chartMouseMoved(event: ChartMouseEvent): Unit = {
                // You can add custom behavior for mouse movements if needed
            }
        })

        // Create and display the JFrame
        val frame = new JFrame("Interactive Fitness Chart")
        frame.setContentPane(chartPanel)
        frame.pack()
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE)
        frame.setVisible(true)
    }
}
