#!/bin/bash -xe

# Clone test repo
git clone -b new-jupyter-tests https://github.com/venicegeo/gwtest-integration.git

find

cd gwtest-integration
cd ci
cd Jupyter

jupyter nbconvert --to notebook --execute --ExecutePreprocessor.timeout=60 --ExecutePreprocessor.allow_errors=True --output results.ipynb Index.ipynb
python jupyter_tester.py results.ipynb expected_outputs.json