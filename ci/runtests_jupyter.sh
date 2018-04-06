#!/bin/bash -xe

# Clone test repo
git clone https://github.com/venicegeo/gwtest-integration.git

cd Jupyter

jupyter nbconvert --to notebook --execute --ExecutePreprocessor.timeout=60 --ExecutePreprocessor.allow_errors=True --output results.ipynb Index.ipynb
python jupyter_tester.py results.ipynb expected_outputs.json