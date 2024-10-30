package com.furkhan.examples.DFs

import org.apache.spark.sql.functions._
import org.apache.spark.sql._
import org.apache.spark.sql.types.{
  FloatType,
  IntegerType,
  StringType,
  StructType
}
import org.apache.log4j._

object FurkhanMinTempDataSet {
    case class Temperature (
                           stationID: String,
                           date: Int,
                           measure_type: String,
                           temperature: Float
                           )

    def main(args: Array[String]): Unit = {
      Logger.getLogger("org").setLevel(Level.ERROR)

      val spark = SparkSession
        .builder
        .appName("MinTempDataSetScala")
        .master("local[*]")
        .getOrCreate()

      var tempSchema = new StructType()
        .add("stationID", StringType, nullable=true)
        .add("date", IntegerType, nullable=true)
        .add("measure_type", StringType, nullable=true)
        .add("temperature", FloatType, nullable=true)

      import spark.implicits._
      var inputCSV = spark.read
        .schema(tempSchema)
        .csv("data/1800.csv")
        .as[Temperature]

      inputCSV.printSchema()
      // Filter the TMIN
      val minTemps = inputCSV.filter($"measure_type" === "TMIN")

      // Project only stationID and temp.
      val stationTemps = minTemps.select("stationID", "temperature")

      // Find minimum temp for each station
      val result = stationTemps.groupBy("stationID").agg(
        min("temperature").alias("minimum")
      )

      val results = result.collect()

      for(result <- results) {
        val temp = result(1).asInstanceOf[Float]
        val formattedTemp = f"${temp}%.2f F"
        println(s"${result(0)}'s minimum temperature is: ${formattedTemp}")
      }

      spark.stop()
    }
}