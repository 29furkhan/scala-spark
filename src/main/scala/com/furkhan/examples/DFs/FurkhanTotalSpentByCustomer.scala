package com.furkhan.examples.DFs

import org.apache.log4j._
import org.apache.spark.sql._
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types.{DoubleType, IntegerType, StructType}
object FurkhanTotalSpentByCustomer {

  case class Customer (cust_id: Int, item_id: Int, amount_spent: Double)

  def main(args: Array[String]): Unit = {
    Logger.getLogger("org").setLevel(Level.ERROR)

    val spark = SparkSession
      .builder
      .appName("TotalSpentByCustomer")
      .master("local[*]")
      .getOrCreate()
    val path = "data/customer-orders.csv"

    val customerOrderSchema = new StructType()
      .add("cust_id", IntegerType, nullable = true)
      .add("item_id", IntegerType, nullable = true)
      .add("amount_spent", DoubleType, nullable = true)

    import spark.implicits._
    val inputCSV = spark.read
      .schema(customerOrderSchema)
      .csv(path)
      .as[Customer]

    var totalByCustomer = inputCSV.groupBy("cust_id").agg(
      round(sum("amount_spent"), 2).alias("final_amount")
    )

    totalByCustomer = totalByCustomer.sort(col("final_amount").desc)

    totalByCustomer.show(totalByCustomer.count.toInt)

    for(result <- totalByCustomer.take(5)){
      val cust_id = result(0)
      val final_amount = result(1).asInstanceOf[Double]
      val formatted_amount = f"${final_amount}%,.2f"
      println(s"The total amount spent by customer_id: ${cust_id} is ${formatted_amount}")
    }
  }
}

