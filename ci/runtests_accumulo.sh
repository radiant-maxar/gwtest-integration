#!/bin/bash -xe

# Clone test repo
git clone https://github.com/venicegeo/gwtest-integration.git

# Set maven paths
export M2_HOME=$PWD/apache-maven-3.5.2
export M2=$M2_HOME/bin
export PATH=$M2:$PATH

# Source env vars, before running test
source /mnt/geowave-env.sh

# Determine HBase vs. Accumulo
export db_type="accumulo"

# Run tests
cd gwtest-integration/ci/Junit
mvn test
