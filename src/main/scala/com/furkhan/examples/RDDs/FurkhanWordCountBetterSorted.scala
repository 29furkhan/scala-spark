package com.furkhan.examples.RDDs

import org.apache.log4j._
import org.apache.spark._

object FurkhanWordCountBetterSorted {
 
  def main(args: Array[String]) {
    Logger.getLogger("org").setLevel(Level.ERROR)

    val sc = new SparkContext("local[*]", "WordCountBetterSorted")
    val input = sc.textFile("data/book.txt")
    
    // Split using a regular expression that extracts words
    var words = input.flatMap(x => x.split("\\W+"))
    words = words.map(x => x.toLowerCase())
    
    val wordCounts = words.map(x => (x, 1)).reduceByKey( (x,y) => x + y )
    // Count of the occurrences of each word

    val wordCountsSorted = wordCounts.sortBy(_._2, ascending = false)
    // Sort by count which is _2 in the input wordCounts

    wordCountsSorted.foreach(println)
    for(word <- wordCountsSorted.take(5).reverse) {
      println(s"${word._1}: ${word._2}")
    }
  }
  
}

