# CSV format
`filename, prover, status, wc_time, cpu_time, system, quantification, consequence, constants, transformation_parameter_list`

# Misc Files
* `common.py` contains Problem class, Node class for trees, problem iterators, problem dictionary creators, semantics generators, binary wrappers, run result parsers.
  Can create csv output and python dictionaries ordered by certain attributes e.g. modal axiomatization
* `Hmf*` ANTLR files for parsing
* `TreeLimitedRun.c` kills processes after a specified timeout
* `Makefile` compiles TreeLimitedRun
* `README.md` this file
* `todo.md` todo list for benchmarking

# Preprocessing
* `extract_qmltp_info.py` can extract the SZS status information from a QMLTP file 
  that was converted to THF with modal extension.
* `filter_for_equality_operator.py` finds all problems containing the symbols "=" or "!="
* `filter_for_equality_qmltpeq.py` finds all problems containing the symbol qmltpeq
* `identify_common_axiom_names.py` identifies the same axiom names across a set of problems
* `substitute_native_equality.py` replaces equality represented with symbols "=" and "!=" (which are uninterpreted in the QMLTP) by the predicate "customqmltpeq" and "customqmltpineq". Note: "!=" is replaced by negate of "=".
* `substitute_qmltp_equality.py`replaces equality represented by the symbols "qmltpeq", "customqmltpeq" and "customqmltpineq" and exchanges them for the native equality symbol "="
* `remove_equality_axiomatization.py` removes all axioms about equalities. Each such axiom is tested for validity before removal.
* `starexec_create_configurations.py` creates run configurations for StarExec
* `starexec_transform_info_csv.py` transforms a StarExec benchmark info (the csv file) to the csv format described above and used by the evaluation

# Evaluation Files for Sanity Checks
* `check_embedding_error.py` embeds modal problems for different configurations and examines the cli output for runtime errors or exceptions.
* `check_consistency.py` takes multiple csv inputs and examines inconsistencies between run configurations of the same modal problem.
* `check_prover_input_error.py` takes multiple csv inputs and examines if any run configuration contains the SZS status Inputerror.
* `check_satisfiable_consistency.py`takes multiple csv inputs and examines inconsistencies between run configurations of the same modal problem that contain the SZS status satisfiable.
* `check_soundness.py` takes a comma-separated list of reference prover names and multiple csv inputs and examines if the run configurations deliver a sound result with respect to the results of the reference provers.
* `check_unknown_status.py` takes multiple csv inputs and reports all configurations with an SZS status that is not accounted for.
* `check_all.py` applies all checks except for check_embedding_error.py on run configurations

# Evaluation Files for Statistics


# Debugging
* `embed_file.py` embeds a file and allows easy configuration
* `embed_files_with_issues.py` embeds files with multiple embedding versions on all configurations that are found by check_consistency.py. Outputs embedding and original.
* `nitpicK_files_with_issues.py` embeds and nitpicks files with multiple embedding versions on all configurations that are found by check_consistency.py. . Outputs embedding, counter model and original.* `filter_for_problems_without_modal_operators.py` finds all problems not containing any modal operators
* `filters_for_the_qmltp.py` contains lists of problems possessing various properties for testing purposes
* `run_prover.py` runs the MET for different sets of embedding/proving configurations
