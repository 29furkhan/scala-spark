package com.furkhan.examples

import org.apache.log4j._
import org.apache.spark._

import scala.math.min

/*
Input: Weather data from weather stations.
Find the minimum temperature for each weather station.
IDE001005,18000101,TMAX,-75,,,E,
IDE001006,18000102,TMIN,-75,,,E,
IDE001007,18000103,TMAX,-148,,,E,
*/

object FurkhanMinTemperatures {
  
  def parser(row:String): (String, String, Float) = {
    val fields = row.split(",")
    val stationID = fields(0)
    val entryType = fields(2)
    val temperature = fields(3).toFloat

    (stationID, entryType, temperature) // return value
  }

  def main(args: Array[String]) {
    Logger.getLogger("org").setLevel(Level.ERROR)
    
    val sc = new SparkContext("local[*]", "MinTemperatures")
    val rows = sc.textFile("data/1800.csv")
    val parsedLines = rows.map(parser)
    // We got the data parsed into (stationID, type, temp)
    
    val minTemps = parsedLines.filter(x => x._2 == "TMIN")
    // Filter out all but TMIN entries -> TMIN is a minimum constant entry
    
    val stationTemps = minTemps.map(input => (input._1, input._3))
    // Convert to (stationID, temperature)
    
    // Find the minimum temperature for each stationID
    val minTempsByStation = stationTemps.reduceByKey( (x,y) => min(x,y))
    
    // Collect, format, and print the results
    val results = minTempsByStation.collect()

    results.map(println)
    for(station <- results) {
      println(s"${station._1}'s minimum temperature is ${station._2}")
    }
    // Traverse the results to print each stations min temp formatted.
  }
}

/*
Output:
EZE00100082's minimum temperature is -135.0
ITE00100554's minimum temperature is -148.0
*/