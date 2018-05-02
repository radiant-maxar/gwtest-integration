#!/bin/bash -xe

# Clone test repo
git clone -b new-jupyter-tests https://github.com/venicegeo/gwtest-integration.git

# Set maven paths
export M2_HOME=$PWD/apache-maven-3.2.2
export M2=$M2_HOME/bin
export PATH=$M2:$PATH

echo $PATH

geowave --version

which geowave

# Source env vars, before running test
source /mnt/geowave-env.sh

# Determine HBase vs. Accumulo
export db_type="hbase"

# Run tests
cd gwtest-integration/ci/Junit
mvn -B test

# Run all notebook tests
cd ../Jupyter
for file in expected_outputs/$db_type/*
do
	python jupyter_tester.py $file
done