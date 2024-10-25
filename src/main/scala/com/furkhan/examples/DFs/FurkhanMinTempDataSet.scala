package com.furkhan.examples.DFs;

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

      val tempSchema = new StructType()
      tempSchema
        .add("stationID", StringType, nullable=true)
        .add("date", IntegerType, nullable=true)
        .add("measure-type", StringType, nullable=true)
        .add("temperature", FloatType, nullable=true)

      import spark.implicits._
      var inputCSV = spark.read
        .schema(tempSchema)
        .csv("data/1800.csv")
        .as[Temperature]

      // Filter the TMIN
      val minTemps = inputCSV.filter($"measure_type" === "TMIN")

      // Project only stationID and temp.
      val stationTemps = minTemps.select("stationID", "temperature")

      // Find minimum temp for each station
      stationTemps.groupBy("stationID").agg(
        min("temperature").alias("minimum")
      ).show()

    }
}