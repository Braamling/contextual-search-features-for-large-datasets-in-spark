package main

import org.apache.spark.ml.feature._
import org.apache.spark.ml.feature.{CountVectorizerModel, IDFModel}
import org.apache.spark.sql.{SQLContext, SaveMode, SparkSession}
import nl.surfsara.warcutils.WarcInputFormat
import org.apache.hadoop.io.{LongWritable, Text}
import org.apache.spark.ml.{Pipeline, PipelineModel}
import org.jwat.warc.WarcRecord
import org.apache.spark.sql.functions._

import scala.io.Source
import utils.Records.{getPagerankRecord, getWarcRecord}

import scala.util.{Success, Try}

object GetFeatures {
  def main(args: Array[String]) {

    // Prepares test data
    val spark: SparkSession = SparkSession.builder.getOrCreate
    val sc = spark.sparkContext
    val docIDs = sc.textFile("all_ids").collect().toSet

    val path = "/data/private/clueweb12/Disk[0-4]*/*/*/*.warc.gz"

    // Read all Warc records that have a TREC-ID.
    val warcRdd = sc.newAPIHadoopFile[LongWritable, WarcRecord, WarcInputFormat](path).
      filter(x => (null != x._2.getHeader("WARC-TREC-ID"))).
      filter(x => docIDs.contains(x._2.getHeader("WARC-TREC-ID").value)).
      map(x => Try(getWarcRecord(x._2, docIDs))).collect{ case Success(df) => df }

    // Create a dataframe with only the required Warc Files.
    var filteredWarcDf = spark.createDataFrame(warcRdd).toDF()

    // Load the pre-trained tf and idf models
    val model = PipelineModel.read.load("pipeline-model.parquet")

    var output = model.transform(filteredWarcDf)

    val countTokens = udf { (words: Seq[String]) => words.length }

    output = output.withColumn("contentLength", countTokens(col("contentWords")))
    output = output.withColumn("titleLength", countTokens(col("titleWords")))

    output.write.mode(SaveMode.Overwrite).save("all_ids.parquet")

    val pagerank = "/data/private/clueweb12/pagerank/full/pagerank.docNameOrder.bz2"
    val pagerankRdd = sc.textFile(pagerank).map(x => getPagerankRecord(x))
    var pagerankDf = spark.createDataFrame(pagerankRdd).toDF()
    var filteredPagerankDf = pagerankDf.filter(pagerankDf("docID").isin(docIDs.toList: _*))

    val joinedDf = filteredPagerankDf.join(output, "docID")

    joinedDf.write.mode(SaveMode.Overwrite).save("pagerank_all_ids.parquet")
  }
}
