#!/bin/bash -xe

# Clone test repo
git clone https://github.com/venicegeo/gwtest-integration.git /home/hadoop/gwtest-integration

# Set maven paths
export M2_HOME=/home/hadoop/apache-maven-3.5.2
export M2=$M2_HOME/bin
export PATH=$M2:$PATH

# Run tests
cd /home/hadoop/gwtest-integration/ci/Junit
mvn test
