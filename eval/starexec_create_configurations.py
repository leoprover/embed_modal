import sys
from pathlib import Path
import enum

class ProverInstance(enum.Enum):
    LEO                = "java -Xss128m -Xmx4g -Xms1g -jar ../leo3.jar embeddedProblem -t $STAREXEC_CPU_LIMIT"
    LEO_E              = "java -Xss128m -Xmx4g -Xms1g -jar ../leo3.jar embeddedProblem -t $STAREXEC_CPU_LIMIT --atp e=../externals/eprover"
    LEO_E_CVC4         = "java -Xss128m -Xmx4g -Xms1g -jar ../leo3.jar embeddedProblem -t $STAREXEC_CPU_LIMIT --atp e=../externals/eprover --atp cvc4=../externals/cvc4"
    NITPICK            = "../isabelle/bin/isabelle tptp_nitpick $STAREXEC_CPU_LIMIT embeddedProblem | grep -n \'% SZS status\'"
    SATALLAX_E_PICOMUS = "../satallax -M ../modes -E ../externals/eprover -P ../externals/picomus -t $STAREXEC_CPU_LIMIT embeddedProblem"
    HOLYHAMMER         = ""

def createStarExecBinConfiguration(prover:ProverInstance,consequence,constants,quantification,system,param_cumu_quant,param_decr_quant,param_mod):
    sb = []
    sb.append("#!/bin/bash")
    sb.append("")
    sb.append("HERE=`dirname $0`")
    sb.append("TPTP=`dirname $1`")
    sb.append("")
    sb.append("CONSEQUENCES=\"" + consequence + "\"")
    sb.append("CONSTANTS=\"" + constants + "\"")
    sb.append("DOMAINS=\"" + quantification + "\"")
    sb.append("SYSTEMS=\"" + system + "\"")
    sb.append("PARAM_CUMU_QUANT=\"" + param_cumu_quant + "\"")
    sb.append("PARAM_DECR_QUANT=\"" + param_decr_quant + "\"")
    sb.append("PARAM_MOD=\"" + param_mod + "\"")
    sb.append("")
    sb.append("java -Xss128m -Xmx2g -Xms1g -jar ../embed.jar -i $1 -o embeddedProblem -consequences $CONSEQUENCES -constants $CONSTANTS -domains $DOMAINS -systems $SYSTEMS -t $PARAM_DECR_QUANT,$PARAM_CUMU_QUANT,$PARAM_MOD")
    sb.append("")
    sb.append(prover.value)
    sb.append("")
    return "\n".join(sb)

def get_transformation_abbreviation(transformation_param):
    if transformation_param == "semantic_monotonic_quantification":
        return "semcumul"
    if transformation_param == "semantic_antimonotonic_quantification":
        return "semdecr"
    if transformation_param == "semantic_modality_axiomatization":
        return "semmod"
    if transformation_param == "semantic_constant_quantification":
        return "semconst"
    if transformation_param == "syntactic_monotonic_quantification":
        return "syncumul"
    if transformation_param == "syntactic_antimonotonic_quantification":
        return "syndecr"
    if transformation_param == "syntactic_modality_axiomatization":
        return "synmod"
    if transformation_param == "syntactic_constant_quantification":
        return "synconst"

def createConfigurations(outPath, prover:ProverInstance,
        consequence_list, constants_list, quantification_list, system_list,
        transformation_param_cumu_quant,transformation_param_decr_quant,transformation_param_mod):
    out_dir_path = Path(outPath)
    for consequence_property in consequence_list:
        for constant_property in constants_list:
            for quantification_property in quantification_list:
                for system_property in system_list:
                    configuration = createStarExecBinConfiguration(prover,
                                                                   consequence_property,
                                                                   constant_property,
                                                                   quantification_property,
                                                                   system_property,
                                                                   transformation_param_cumu_quant,
                                                                   transformation_param_decr_quant,
                                                                   transformation_param_mod)
                    configuration_name = "_".join([consequence_property,
                                                   constant_property,
                                                   quantification_property,
                                                   system_property,
                                                   get_transformation_abbreviation(transformation_param_cumu_quant),
                                                   get_transformation_abbreviation(transformation_param_decr_quant),
                                                   get_transformation_abbreviation(transformation_param_mod)])
                    configuration_filename = "starexec_run_" + configuration_name
                    out_file = out_dir_path / configuration_filename
                    print(out_file)
                    with open(out_file,"w+") as fh:
                        fh.write(configuration)

def createAllConfigurations(outPath,prover:ProverInstance):
    consequence_list_list = [
        ["local"],["local"],["local"],["local"]
    ]
    constants_list_list = [
        ["rigid"],["rigid"],["rigid"],["rigid"]
    ]
    quantification_list_list = [
        ["constant","varying","cumulative","decreasing"],
        ["constant","varying","cumulative","decreasing"],
        ["cumulative","decreasing"],
        ["cumulative","decreasing"]
    ]
    system_list_list = [
        ["K","D","T","S4","S5","S5U"],
        ["K","D","T","S4","S5","S5U"],
        ["K","D","T","S4","S5","S5U"],
        ["K","D","T","S4","S5","S5U"]
    ]
    transformation_param_cumu_quant_list = [
        "semantic_antimonotonic_quantification",
        "semantic_antimonotonic_quantification",
        "syntactic_antimonotonic_quantification",
        "syntactic_antimonotonic_quantification"
    ]
    transformation_param_decr_quant_list = [
        "semantic_monotonic_quantification",
        "semantic_monotonic_quantification",
        "syntactic_monotonic_quantification",
        "syntactic_monotonic_quantification"
    ]
    transformation_param_mod_list = [
        "semantic_modality_axiomatization",
        "syntactic_modality_axiomatization",
        "semantic_modality_axiomatization",
        "syntactic_modality_axiomatization"
    ]
    for consequence_list, constants_list, quantification_list, system_list, \
        transformation_param_cumu_quant, transformation_param_decr_quant, \
        transformation_param_mod \
        in zip(consequence_list_list,constants_list_list,quantification_list_list,system_list_list, \
               transformation_param_cumu_quant_list,transformation_param_decr_quant_list, \
               transformation_param_mod_list):
        createConfigurations(outPath,prover, \
                             consequence_list,constants_list,quantification_list,system_list, \
                             transformation_param_cumu_quant,transformation_param_decr_quant, \
                             transformation_param_mod)

def main(out_dir):
    prover = ProverInstance.SATALLAX_E_PICOMUS
    createAllConfigurations(out_dir,prover)

if __name__ == '__main__':
    main(sys.argv[1])