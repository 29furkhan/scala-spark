package com.furkhan.examples
import org.apache.spark._
import org.apache.log4j._

object FurkhanRatingsCounter {
 
  def main(args: Array[String]) {

    """
    The below line is important to avoid SPAM error messages and see cleaned output.
    """
    Logger.getLogger("org").setLevel(Level.ERROR)

    // Create a spark context
    val sc = new SparkContext("local[*]", "RatingsCounter")

    // Prepare the rdd from the file.
    val rdd = sc.textFile("data/ml-100k/u.data")
    
    // Convert each line to a string, split it out by tabs, and extract the third field.
    // (The file format is userID movieID rating timestamp)
    val ratings = rdd.map(x => x.split("\t")(2))
    
    // Count up how many times each value (rating) occurs
    val results = ratings.countByValue()
    
    // Sort the ratings in ascending order.
    """
    Breakup:
        toSeq -> This will convert map to a sequence of tuples (key, value).
                 We needed this step as Map is an unordered collection.
        sortBy -> sort the sequence by key specified (_._1) in our case.
    """
    // _ is a tuple (rating, count), using ._1 meaning we are asking sortBy to sort it with first elem.
    val sortedResults = results.toSeq.sortBy(_._1).reverse
    // We can achieve the same using sortByKey() on key-value RDD.
    
    // Print each result on its own line.
    sortedResults.foreach(println)
  }
}

/*
Output:
(5,21201)
(4,34174)
(3,27145)
(2,11370)
(1,6110)
 */