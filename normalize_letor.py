from utils.featureStorage import FeatureStorage
from utils.LETORIterator import LETORIterator, to_letor
import argparse
import glob
import os
from sklearn.preprocessing import normalize
import numpy as np
       

def normalize_features(source_path, target_path):
    letor_feature_iterator = LETORIterator(source_path)

    # Read file and group features per query
    queries = {}
    for query_id, doc_id, rel_score, features in letor_feature_iterator.feature_iterator():
        if query_id not in queries:
            queries[query_id] = [[], [], []]

        queries[query_id][0].append(doc_id)
        queries[query_id][1].append(rel_score)
        queries[query_id][2].append([float(f) for f in features])

    # Normalize all the features
    with open(target_path, "w") as f:
        for query in queries:
            features = np.asarray(queries[query][2])
            norm_features = normalize(features, axis=0)
            for i, (doc_id, rel_score) in enumerate(zip(queries[query][0], queries[query][1])):
                start = "{} qid:{}".format(rel_score, query)
                scores = " ".join([str(i) + ":" + '{0:.10f}'.format(x) for i,x in enumerate(norm_features[i,:])])
                end = "#docid = {}".format(doc_id)
                letor = "{} {} {}\n".format(start, scores, end)
                f.write(letor)

def main():
    normalize_features(FLAGS.source, FLAGS.normalized_target)

if __name__ == '__main__':
    parser = argparse.ArgumentParser()

    parser.add_argument('--source', type=str, default='storage/clueweb12_web_trec/full_set',
                        help='The location of the LETOR file that should be added.')
    parser.add_argument('--normalized_target', type=str, default='storage/clueweb12_web_trec/normalized_set',
                        help='The location where to store and load the context_features.')

    FLAGS, unparsed = parser.parse_known_args()
    print(FLAGS)
    main()