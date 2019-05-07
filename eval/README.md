csv inputs/outputs are of the following format:
`filename, prover, status, wc_time, cpu_time, system, quantification, 
consequence, constants, transformation_parameter_list`

# Misc Files
* `common.py` contains file iterators, paths to problem directories
* `extract_qmltp_info.py` can extract the SZS status information from a QMLTP file 
  that was converted to THF with modal extension.
  Can create csv output and python dictionaries ordered by certain attributes e.g. modal axiomatization
* `filters_for_qmltp.py` contains problem lists of interests e.g. those with equality 
  or other specific attributes
* `run_prover` runs the MET for different sets of embedding/proving configurations

# Evaluation Files
* `check_consistency` takes csv inputs and outputs inconsistencies