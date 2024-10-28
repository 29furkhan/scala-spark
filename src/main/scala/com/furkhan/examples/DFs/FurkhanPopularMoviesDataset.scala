package com.furkhan.examples.DFs

import org.apache.log4j._
import org.apache.spark.sql._
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types.{IntegerType, LongType, StructType}
import scala.io.{Codec, Source}

object FurkhanPopularMoviesDataset {
  case class Movie(user_id: Int, movie_id: Int, rating: Int, timestamp: Long)

  // Defines a function to create a Map of movie_id -> Movie_name.
  def loadMovieMapperTable(): Map[Int, String] = {
    // Define the encoding of a file.
    implicit val codec: Codec = Codec("ISO-8859-1") // Encoding is not UTF-8

    // Create an  empty map
    var movieMap: Map[Int, String] = Map()

    // Reading the file using Scala
    var lines = Source.fromFile("data/ml-100k/u.item")
    for (line <- lines.getLines()){
      val fields = line.split('|')
      if (fields.length > 1){
        movieMap += (fields(0).toInt -> fields(1))
      }
    }
    // Close the file object.
    lines.close()

    // Return movieMap
    movieMap
  }

  def main(args: Array[String]): Unit = {
    Logger.getLogger("org").setLevel(Level.ERROR)

    val spark = SparkSession
      .builder
      .appName("FurkhanPopularMoviesDS")
      .master("local[*]")
      .getOrCreate()

    val schema = new StructType()
      .add("user_id", IntegerType, nullable = false)
      .add("movie_id", IntegerType, nullable = false)
      .add("rating", IntegerType, nullable = true)
      .add("timestamp", LongType, nullable = true)

    import spark.implicits._
    val inputCV = spark.read
      .option("sep", "\t")
      .schema(schema)
      .csv("data/ml-100k/u.data")
      .as[Movie]

    // Prepare a broadcast variable out of our Mapper.
    val nameDict = spark.sparkContext.broadcast(loadMovieMapperTable())

    // Find the top 10 most popular movies.
    val top10 = inputCV.groupBy("movie_id").agg(
      count("movie_id").alias("count")
    ).orderBy(desc("count"))

    // Prepare a lookup function and register as UDF
    val lookup = udf((movieID: Int) => {
      nameDict.value(movieID)
    })

    top10.withColumn("movie_name", lookup(col("movie_id")))
      .select("movie_id", "movie_name")
      .show(10, truncate=false)
  }
}