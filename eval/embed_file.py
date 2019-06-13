from common import embed
import sys
from pathlib import Path

def main(infile,outfile):
    bin_treelimitedrun = "/home/tg/embed_modal/eval/TreeLimitedRun"
    bin_embed = "java -jar /home/tg/embed_modal/embed/target/embed-1.0-SNAPSHOT-shaded.jar" # dev

    quantification = "$varying"
    system = "$modal_system_S5"
    params = ["semantic_constant_quantification",
              "semantic_cumulative_quantification",
              "semantic_decreasing_quantification",
              "semantic_modality_axiomatization"]
    infile = "/home/tg/embed_modal/eval/datasets/qmltp_thf_standard/GLC/GLC414+1.p"
    outfile = "/home/tg/embed_modal/examples/out.p"

    sem = {"system":system,"quantification":quantification,"consequence":"$local","constants":"$rigid"}
    with open(infile,"r") as fh:
        problem = fh.read()
        outfilepath = Path(outfile)
        if outfilepath.exists():
            print(str(outfilepath) + " already exists and will be overwritten.")
        e = embed(bin_treelimitedrun, bin_embed,problem,params,sem,120,120)
        with open(outfilepath,"w+")as fw:
            fw.write(e['embedded_problem'])
        outOriginal = outfilepath.with_suffix(".original")
        with open(outOriginal,"w+")as fw:
            fw.write("% " + str(sem))
            fw.write(problem)

if __name__ == "__main__":
    main(sys.argv[1],sys.argv[2])
    #main(None,None)
