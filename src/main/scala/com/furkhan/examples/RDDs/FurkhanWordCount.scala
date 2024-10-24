package com.furkhan.examples.RDDs

import org.apache.log4j._
import org.apache.spark._

object FurkhanWordCount {
 
  def main(args: Array[String]) {
    // Setup the logger
    Logger.getLogger("org").setLevel(Level.ERROR)

    val sc = new SparkContext("local[*]", "WordCount")
    val input = sc.textFile("data/book.txt")

    /* For each line in a book we create words and flatten them.
       Ex. My name is khan =>
       My
       name
       is
       khan
    */

    val words = input.flatMap(x => x.split(" "))

    // Count up the occurrences of each word
    val wordCounts = words.countByValue()

    // Print the results.
    /* We have converted it to a sequence so we can sort the map by values.*/
    wordCounts.toSeq.sortBy(_._2).reverse.take(15).foreach(println)
  }
}

/*
Output:
(to,1789)
("your,1339)
(you-and,1267)
(the,1176)
(a,1148)
 */

