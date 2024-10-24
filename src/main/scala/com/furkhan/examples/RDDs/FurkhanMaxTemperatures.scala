package com.furkhan.examples.RDDs

import org.apache.log4j._
import org.apache.spark._

import scala.math.max

object FurkhanMaxTemperatures {

  def parser(row: String) : (String, String, Float) = {
    val columns = row.split(",")
    val stationID = columns(0)
    val entryType = columns(2)
    val temperature = columns(3).toFloat

    (stationID, entryType, temperature)
  }
    /** Our main function where the action happens */
  def main(args: Array[String]) {
    Logger.getLogger("org").setLevel(Level.ERROR)

    val sc = new SparkContext("local[*]", "MaxTemp")
    val linesRDD = sc.textFile("data/1800.csv")
    var tupleRDD = linesRDD.map(parser)

    // Fetch only the TMAX data.
    tupleRDD = tupleRDD.filter(input => input._2 == "TMAX")
    var modifiedRDD = tupleRDD.map(input => (input._1, input._3))

    // Find the maximum for specific temp
    val maxTemperatures = modifiedRDD.reduceByKey((x, y) => max(x, y))

    maxTemperatures.foreach(println)
    for(station <- maxTemperatures) {
      println(s"${station._1}'s max temperature is ${station._2}'")
    }
  }
}