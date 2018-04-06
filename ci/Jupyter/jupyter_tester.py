#! python

import json, re, sys

# Convert input files to JSON.
results = json.load(open(sys.argv[1]))
regexes = json.load(open(sys.argv[2]))

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

	p = re.compile(regex)
	try:
		cell_output = cell_outputs[i]
	except:
		# Print failure for each cell without an associated regex.
		print(" - Failure: Output Not Present.")
		exit_code = 1 # Should already be 1.
		continue

	for output in cell_output:
		# Check each element in the output list
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
				print("     - ", regex)
		else:
			# Mark as a failure if pass/failure can't be determined.
			exit_code = 1
			print(" - Failure: Could not read output")

# Exit with the result of the test.
sys.exit(exit_code)