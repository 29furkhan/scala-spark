/*
Input data format:
ID, name, Age, number_of_friends

We need to find the average numbers of friends for a given age.
(Age, No_of_friends)
 */

package com.furkhan.examples

import org.apache.log4j._
import org.apache.spark._

object FurkhanFriendsByAge {

  def parser(row: String): (Int, Int) = {
    """
    Purpose: This function will give the key-value Sequence out of input data.
    (AGE, No_of_friends)
    Params:
      Row: Each single line of file given as an input.
    Return:
      (Int, Int): The tuple of key-value pairs of type integers.
  """
    val columns = row.split(",")
    val age = columns(2).toInt // Input is always string
    val numFriends = columns(3).toInt // Input is always string

    (age, numFriends) // Return statement
  }
  
  def main(args: Array[String]) {
    Logger.getLogger("org").setLevel(Level.ERROR)
        
    val sc = new SparkContext("local[*]", "FriendsByAge")
    val linesRDD = sc.textFile("data/fakefriends-noheader.csv")
    
    // Use our parseLines function to convert to (age, numFriends) tuples
    val rdd = linesRDD.map(parser)
    // We have values like (33, 200), (33, 2)
    // But to find the average of them we need to transform it -> (33, (202, 2))
    // -> For age 33 we have total 202 friends and 2 people with age 33 in dataset

    val newRdd = rdd.mapValues(input => (input, 1))
    // Here we added 1 as initial count -> (33, (200, 1)), (33, (2, 1))

    val reducedRdd = newRdd.reduceByKey((x, y) => (x._1 + y._1, x._2 + y._2))
    // We get (33, (202, 2)) -> Total is 202 and count of 33 is 2.

    val averagesByAge = reducedRdd.mapValues(x => x._1 / x._2)
    // To compute the average we divide sum / count for each age.

    val results = averagesByAge.take(5).sorted
    // sorted method will sort the map on keys

    results.sorted.foreach(println)
  }
    
}

/* Output
(22,206)
(34,245)
(52,340)
(56,306)
(66,276)
*/
  