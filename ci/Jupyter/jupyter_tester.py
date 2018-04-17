#! python

import json, re, sys, subprocess, os

# Convert input files to JSON.
expected_file = json.load(open(sys.argv[1]))
orig_notebook = expected_file["path"]
regexes = expected_file["outputs"]
notebook_path = os.path.dirname(orig_notebook)
# Execute the notebook
subprocess.call(["jupyter", "nbconvert", "--to", "notebook", "--execute", "--ExecutePreprocessor.timeout=600", "--ExecutePreprocessor.interrupt_on_timeout=True",
	"--ExecutePreprocessor.allow_errors=True", "--ExecutePreprocessor.kernel_name=pythonwithpixiedustspark21",
	"--output", "results.ipynb", orig_notebook
	])
results = json.load(open(notebook_path + "/results.ipynb"))

# Extract all output fields from the results file.
cell_outputs = [cell["outputs"] for cell in results["cells"] if "outputs" in cell]

# Will become 1 if failure occurs
exit_code = 0

if not len(cell_outputs) == len(regexes):
	print("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~")
	print("WARNING: There is not an equal number of")
	print("         Results and Matching Patterns!")
	print("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~")
	exit_code = 1

for i, regex in enumerate(regexes):
	# For each output/regex pair:

	print("")
	print("Checking Output %d" % i)
	print("-----------------")

	if not regex:
		print(" - Skipped")
		continue

	try:
		cell_output = cell_outputs[i]
	except:
		# Print failure for each cell without an associated regex.
		print(" - Failure: Output Not Present.")
		exit_code = 1 # Should already be 1.
		continue

	for i, regex_section in enumerate(regex):
		# Check each regex in the list of regexes for the section
		# Each regex section should pair with an output.

		# Get the output and the regex to compare it to for this iteration.
		output = cell_output[i]
		p = re.compile(regex_section)

		if "ename" in output:
			# For errors, mark as failure, print.
			exit_code = 1
			print(" - Failure: %s" % output["ename"])
		elif "text" in output:
			text = "\n".join(output["text"])
			if p.search(text):
				# If a match, print success.
				print(" - Passed")
			else:
				# For mismatches, mark as failure, print.
				exit_code = 1
				print(" - Failure: String mismatch")
				print("   - Actual String:")
				for line in output["text"]:
					print("     - ", line)
				print("   - Expected Pattern:")
				print("     - ", regex_section)
		else:
			# Mark as a failure if pass/failure can't be determined.
			exit_code = 1
			print(" - Failure: Could not read output")

# Exit with the result of the test.
sys.exit(exit_code)