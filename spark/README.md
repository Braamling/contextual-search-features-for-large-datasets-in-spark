# Scala Spark ClueWeb12 content feature extractor. 
This directory contains Scala Spark and Python code for creating a LETOR formatted file with the TF, IDF, TFIDF, BM25, average document length and pagerank score for the ClueWeb12 collection. In order to get started with this code, you will need to have a configured Hadoop cluster with both the ClueWeb12 collection and ClueWeb12 pagerank scores ready. Gathering all the features took more than 24 hours on 100 executors with 19gb memory and 4 computational cores each. 

## Compiling the Scala code.
If only java 7 has been installed, then the code can be compiled by simply running:

```
sbt assembly
```

If another java version is also installed, explicitly setting the java-home to java-7 might help.


```
sbt -java-home /usr/lib/jvm/java-7-openjdk-amd64/ assembly
```

## Running the code
The scala code consists of the steps. The steps are described in the sections below

### Step 1) Building the model
To avoid requiring terabytes worth of storage to calculate all the features for the full collection, we start by fitting a TF and IDF model for the whole collection. The file `FitModels.scala` should be configured to point to the right location of ClueWeb12 on your hdfs and give the locations to store the models (might change this to configurable arguments in the future). The script will output a Pipeline model file that can be used in step 2 and a parquet file with the average document/title length.

```
./bin/spark-submit --class main.FitModels \
    --master yarn \
    --deploy-mode cluster \
    --num-executors 100 --executor-cores 4 --executor-memory 19G
    target/scala-2.11/spark-assembly-0.0.1.jar
```

### Step 2) Gathering the sparse vectors
To get all the sparse vectors and pagerank scores, we do another pass over the full dataset to fetch the required documents. A file with on each line the document id of the documents to be fetched should be provided and configured in `GetFeatures.scala`. This scripts will result in in a parquet file with all the documents, sparse vectors for TF & IDF, document length and pagerank score. 

```
./bin/spark-submit --class main.GetFeatures \
    --master yarn \
    --deploy-mode cluster \
    --num-executors 100 --executor-cores 4 --executor-memory 19G
    target/scala-2.11/spark-assembly-0.0.1.jar
```

The final output is a set of Spark parquet DataFrame containing the TF and IDF for each words in the document as a SparseVector. 


### Step 3) Converting the parquet file to a pandas dataframe
Using the PySpark implementation, it's quite straight forward to convert a parquet file to pandas (simply call .toPandas() on the dataframe).

It is advisable to only convert the columns that are actually needed for conversion. The required columns are: 
```
docID, pagerank, contentTF, contentIDF, titleTF, titleIDF, contentLength, titleLength
```

### Step 4) Convert the pandas dataframe to a LETOR formatted files with scores.
This step is easy, just run all the code in `DataframeToLETORScores.ipynb`.