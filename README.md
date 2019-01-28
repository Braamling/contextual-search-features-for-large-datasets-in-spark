### Clueweb12 contextual feature calculator
This repository contains of a Scala and Python component used to create content features for the ClueWeb12 collection. 
The scala implementation has two compontents: i) a Spark scripts that creates a TF and IDF model for the full collection. and, ii) a spark script that creates a Parquet file with a TF sparse vector, IDF sparse vector, Pagerank Score and word count for all documents given as an argument 
The python implementation has two components: i) a notebook that converts a pandas version of the parquet file to a LETOR formatted file with TF, IDF, TFIDF, BM25, Document Length and pagerank and, ii) a set of scripts that normalize and split the generated LETOR file into folds. 

More information about how to use the Scala Spark implementation, please check the readme in the Spark directory.

#### Usage
This directory consists of various python files that are able to add queries, documents and contextual features to a .h5 file. This file can then be used for training a learning to rank model. 

- `add_pyndri_index.py`
- `add_pagerank.py`
- `add_anchor_baselines.py`
