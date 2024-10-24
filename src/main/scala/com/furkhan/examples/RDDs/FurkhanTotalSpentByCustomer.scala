package com.furkhan.examples.RDDs

import org.apache.log4j._
import org.apache.spark._

/*
Count the total amount spent by the customer.

Algorithm.
1. Split each comma-delimited line into columns.
2. Create a tuple (customerID, Amount)
3. Add up the amounts for each customerID
4. Display result
*/

object FurkhanTotalSpentByCustomer {

  def parser(row: String) : (Int, Double) = {
    var columns = row.split(",")
    (columns(0).toInt, columns(2).toDouble)
  }

  def main(args: Array[String]) {
    Logger.getLogger("org").setLevel(Level.ERROR)
    val sc = new SparkContext("local[*]", "TotalSpentByCustomer")
    val input = sc.textFile("data/customer-orders.csv")
    val mappedInput = input.map(parser)
    
    val totalByCustomer = mappedInput.reduceByKey((x, y) => x+y)

    // Print the totalOrder of each customer sorted in reverse order.
    totalByCustomer.collect().toSeq.sortBy(_._2).reverse.foreach(println)

    // Sort by key
    totalByCustomer.sortByKey().collect().toSeq.reverse.foreach(println)
  }
  
}

