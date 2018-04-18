#!/bin/bash -xe

# Clone test repo
git clone -b new-jupyter-tests https://github.com/venicegeo/gwtest-integration.git

cd gwtest-integration/ci/Jupyter

python jupyter_tester.py expected_outputs.json