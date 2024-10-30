package com.furkhan.examples.DFs

import org.apache.log4j._
import org.apache.spark.sql._

object FurkhanPerformanceTester {
  Logger.getLogger("org").setLevel(Level.ERROR)
  val start = System.nanoTime()

  def main(args: Array[String]) = {
    val spark = SparkSession
      .builder
      .appName("FurkhanPerformanceTester")
      .master("local[*]")
      .getOrCreate()

    var employees = spark
      .read
      .option("header", true)
      .option("inferSchema", true)
      .csv("data/employees.csv")

    var departments = spark
      .read
      .option("header", true)
      .option("inferSchema", true)
      .csv("data/departments.csv")

    departments.printSchema()
    employees.printSchema()
    // Join - Option - 1
    val joined_df = employees.join(
      departments,
      departments("ID") === employees("Dept_ID"),
      "full_outer"
    ).select(departments("ID"),departments("Dept_name"),employees("EmployeeNumber"),employees("GivenName"), employees("Surname"))

    // End the timer
    var end = System.nanoTime()
    val duration = (end - start) / 1e9d
    joined_df.show()
    joined_df.explain()
    println(s"Option 1 Duration: $duration seconds")

//     Join - Option - 2
//    departments = departments.select("ID","Dept_name")
//    employees = employees.select("Dept_ID","EmployeeNumber","GivenName","Surname")
//
//    val joined_df = employees.join(
//      departments,
//      departments("ID") === employees("Dept_ID"),
//      "full_outer"
//    )
//
//    // End the timer
//    val end = System.nanoTime()
//    val duration = (end - start) / 1e9d
//    joined_df.show()
//    joined_df.explain()
//    println(s"Option 2 Duration: $duration seconds")

    spark.stop()
  }

}