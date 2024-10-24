/*
Input: Count the words in a book.
 */
package com.furkhan.examples

import org.apache.log4j._
import org.apache.spark._

object FurkhanWordCountBetter {
 
  def main(args: Array[String]) {
   
    Logger.getLogger("org").setLevel(Level.ERROR)
    val sc = new SparkContext("local[*]", "WordCountBetter")
    val input = sc.textFile("data/book.txt")
    
    /* Split using a regular expression that extracts words
      \\W -> Non-word character or non-alphanumerc or non-underscore chars
      My name-is 'furkhan shaikh
      Split(" ") ->
        My
        name-is
        'furkhan
        shaikh
      Split("\\W+") ->
        My
        name
        is
        furkhan
        shaikh
    */
    val words = input.flatMap(x => x.split("\\W+"))
    
    // Normalize everything to lowercase
    val lowercaseWords = words.map(x => x.toLowerCase())
    
    // Count of the occurrences of each word
    val wordCounts = lowercaseWords.countByValue()
    
    // Print the results
    wordCounts.foreach(println)
  }
  
}

