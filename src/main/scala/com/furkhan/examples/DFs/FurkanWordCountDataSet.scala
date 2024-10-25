package com.furkhan.examples.DFs

import org.apache.log4j._
import org.apache.spark.sql._

object FurkanWordCountDataSet {

  case class Book(value: String)

  def main(args: Array[String]) {
    Logger.getLogger("org").setLevel(Level.ERROR)

    val spark = SparkSession
      .builder
      .appName("DataFramesDataSet")
      .master("local[*]")
      .getOrCreate()

    val path = "data/book.txt"
    import spark.implicits._

    var inputDS = spark.read.text(path).as[Book]

    println("Here is our inferred schema:")
    inputDS.printSchema()

    import org.apache.spark.sql.functions._

    println("Split the words by \\W+ and explode them into multiple rows.")
    var splitDS = inputDS.withColumn("tempSplitted", split(col("value"), "\\W+"))
    var explodedDS = splitDS.withColumn("explodedWords", explode(col("tempSplitted")))
    explodedDS = explodedDS.withColumn("explodedWords", lower(col("explodedWords")))

    println("Show the count of each word")
    explodedDS.groupBy("explodedWords").agg(
      count("explodedWords").alias("word_frequency")
    ).sort(col("word_frequency").desc).show(truncate=false)

    spark.stop()
  }
}