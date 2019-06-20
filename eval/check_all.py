import sys
import check_consistency
import check_prover_input_error
import check_soundness

def main(reference_prover_name_list,csv_file_list):
    print("\n#########################################################################################################\n")
    check_consistency.main(csv_file_list)
    print("\n#########################################################################################################\n")
    check_soundness.main(reference_prover_name_list,csv_file_list)
    print("\n#########################################################################################################\n")
    check_prover_input_error.main(csv_file_list)

if __name__ == "__main__":
    main(sys.argv[1].strip().split(","),sys.argv[2:])