from collections import deque
import itertools
from utils.LETORIterator import LETORIterator
import argparse
import numpy
import math
import os

def load_file(path):
    LETOR_iterator = LETORIterator(path)
    queries = {}
    for query_id, line in LETOR_iterator.line_iterator():
        if query_id not in queries:
            queries[query_id] = []
        queries[query_id].append(line)

    return queries

def write_queries_to_file(file, query_ids, queries):
    for query_id in query_ids:
        for line in queries[query_id]:
            file.write(line)


def main():
    queries = load_file(FLAGS.target_dir.format(FLAGS.full_set_file))

    # Make the split
    query_ids = list(queries.keys())

    chunk_size = int(len(query_ids) / FLAGS.folds)
    numpy.random.shuffle(query_ids)

    # Create n chunks with equal amount of queries.
    start = 0
    end = chunk_size
    splits = []
    for i in range(1, FLAGS.folds + 1):
        splits.append(query_ids[start:end])
        with open(FLAGS.target_dir.format("S{}".format(i)), "w") as f:
            write_queries_to_file(f, query_ids[start:end], queries)

        start = end
        end += chunk_size

    # Create the folds by shifting shifting the list of chunks to the right and 
    # using the first three for traing, fourth for test and fifth for validation
    items = deque(splits)
    for i in range(1, FLAGS.folds + 1):
        directory = FLAGS.target_dir.format("Fold{}".format(i))
        if not os.path.exists(directory):
            os.makedirs(directory)

        with open(os.path.join(directory, "train.txt"), "w") as f:
            train_fold = list(itertools.islice(items, 0, FLAGS.folds - 2))
            train_fold_ids = list(itertools.chain.from_iterable(train_fold))
            write_queries_to_file(f, train_fold_ids, queries)

        with open(os.path.join(directory, "test.txt"), "w") as f:
            test_fold_ids = items[-2]
            write_queries_to_file(f, test_fold_ids, queries)

        with open(os.path.join(directory, "vali.txt"), "w") as f:
            vali_fold_ids = items[-1]
            write_queries_to_file(f, vali_fold_ids, queries)

        items.rotate(1)


if __name__ == '__main__':
    parser = argparse.ArgumentParser()

    parser.add_argument('--target_dir', type=str, default='storage/clueweb12_web_trec/{}',
                        help='The location where to create the .')
    parser.add_argument('--folds', type=int, default=5,
                        help='The amount of folds to create.')
    parser.add_argument('--full_set_file', type=str, default="full_set",
                        help='The file in the target directory with the full set')


    FLAGS, unparsed = parser.parse_known_args()

    main()