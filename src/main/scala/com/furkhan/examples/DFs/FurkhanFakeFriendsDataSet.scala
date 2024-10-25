package com.furkhan.examples.DFs

import org.apache.spark.sql._
import org.apache.spark.sql.functions._
import org.apache.log4j._

object FurkhanFakeFriendsDataSet{

  case class Friend (id: Int, name: String, age: Int, friends: Int)

  // Main method does not return anything.
  def main(args: Array[String]) {
    Logger.getLogger("org").setLevel(Level.ERROR)

    val spark = SparkSession
      .builder
      .appName("FakeFriendsDataSet")
      .master("local[*]")
      .getOrCreate()

    import spark.implicits._
    val inputDS = spark.read
      .option("header", "true")
      .option("inferSchema", "true")
      .csv("data/fakefriends.csv")
      .as[Friend]

    // Approach-1:
    /*
    1. Create two columns, one to hold sum of group and other to hold the count.
    2. Final result column will have average calculated from above 2 columns.
    */
    inputDS.groupBy("age").agg(
      sum("friends").alias("total_friends"),
      count("age").alias("total_instances")
    ).withColumn("average", round(col("total_friends")/col("total_instances"), 2).alias("average")).show(5)

    // Approach-1:
    /*
    1. Use the average function on friends column of agg.
    */
    inputDS.groupBy("age").agg(
      round(avg("friends").alias("average"), 2).alias("average")
    ).sort(col("age").desc).show(5)
  }
}