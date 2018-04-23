#! python

import json, re, sys, subprocess, os

# Convert input files to JSON.
expected_responses_json = json.load(open(sys.argv[1]))
notebook_under_test = expected_responses_json["path"]
expected_command_responses = expected_responses_json["outputs"]
notebook_path = os.path.dirname(notebook_under_test)
# Execute the notebook
subprocess.call(["jupyter", "nbconvert", "--to", "notebook", "--execute", "--ExecutePreprocessor.timeout=600", "--ExecutePreprocessor.interrupt_on_timeout=True",
	"--ExecutePreprocessor.allow_errors=True", "--ExecutePreprocessor.kernel_name=pythonwithpixiedustspark23",
	"--output", "results.ipynb", notebook_under_test
	])
actual_results_notebook = json.load(open(notebook_path + "/results.ipynb"))

# Extract all output fields from the results file.
actual_command_responses = [cell["outputs"] for cell in actual_results_notebook["cells"] if "outputs" in cell]

# Will become 1 if failure occurs
exit_code = 0

if not len(actual_command_responses) == len(expected_command_responses):
	print("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~")
	print("WARNING: There is not an equal number of")
	print("         Results and Matching Patterns!")
	print("         Expected: %d" % len(expected_command_responses))
	print("         Actual:   %d" % len(actual_command_responses))
	print("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~")
	exit_code = 1

for i, expected_command_response in enumerate(expected_command_responses):
	# For each output/regex pair:

	print("")
	print("Checking Response %d" % i)
	print("-----------------")

	# If expected_command_response is empty, do not check any part of the expected_command_response.
	if not expected_command_response:
		print(" - Skipped")
		continue

	try:
		actual_command_response = actual_command_responses[i]
	except:
		# Print failure for each cell without an associated regex.
		print(" - Failure: Output Not Present.")
		exit_code = 1 # Should already be 1.
		continue

	# Check to make sure that this response has the same number of expected and actual outputs.
	if not len(actual_command_response) == len(expected_command_response):
		print(" - Failure: Different number of expected/actual outputs")
		exit_code = 1

		# Print both the expected and actual outputs for this response, if there is a difference.
		for n in range(max(len(actual_command_response), len(expected_command_response))):
			print("   - Output %d" % n)
			print("     ~~~~~~~~")
			try:
				print("     - Actual: %s" % (" "*15).join(actual_command_response[n]["text"]))
			except:
				print("     - Actual: <>")
			try:
				print("     - Expected: %s" % expected_command_response[n])
			except:
				print("     - Expected: <>")

		continue

	for j, expected_output in enumerate(expected_command_response):
		# Check each regex in the list of regexes for the section
		# Each regex section should pair with an output.

		# If expected_output is empty, do not check the actual_output.
		if not expected_output:
			print(" - Skipped")
			continue

		# Get the output and the regex to compare it to for this iteration.
		actual_output = actual_command_response[j]
		p = re.compile(expected_output)

		if "ename" in actual_output:
			# For errors, mark as failure, print.
			exit_code = 1
			print(" - Failure: %s" % actual_output["ename"])
		elif "text" in actual_output:
			text = "\n".join(actual_output["text"])
			if p.search(text):
				# If a match, print success.
				print(" - Passed")
			else:
				# For mismatches, mark as failure, print.
				exit_code = 1
				print(" - Failure: String mismatch")
				print("   - Actual String:")
				for line in actual_output["text"]:
					print("     - ", line)
				print("   - Expected Pattern:")
				print("     - ", expected_output)
		else:
			# Mark as a failure if pass/failure can't be determined.
			exit_code = 1
			print(" - Failure: Could not read output")

# Exit with the result of the test.
sys.exit(exit_code)