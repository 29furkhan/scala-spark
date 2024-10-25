package com.furkhan.examples.DFs

import org.apache.log4j._
import org.apache.spark.sql._

object FurkhanDataFramesDataset {

  case class Person(id: Int, name: String, age: Int, friends: Int)

  def main(args: Array[String]) {
    Logger.getLogger("org").setLevel(Level.ERROR)

    val spark = SparkSession
      .builder
      .appName("DataFramesDataSet")
      .master("local[*]")
      .getOrCreate()

    val path = "data/fakefriends.csv"
    // Prepare a DS from input data and imply Person class schema.
    import spark.implicits._
    var inputDS = spark.read
      .option("header", "true")
      .option("inferSchema", "true")
      .csv(path).as[Person]

    println("Here is our inferred schema:")
    inputDS.printSchema()
    
    println("Let's select the name column:")
    inputDS.select("name").show()
    
    println("Filter out anyone over 21:")
    inputDS.filter(inputDS("age") < 21).show()
   
    println("Group by age:")
    inputDS.groupBy("age").count().show()
    
    println("Make everyone 10 years older:")
    inputDS.select(inputDS("name"), (inputDS("age") + 10).alias("new_age")).show()
    
    spark.stop()
  }
}