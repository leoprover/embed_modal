# filename, status, time, system, quantification, consequence, constants
import os
from pathlib import Path

problem_directory = "/home/tg/data/QMLTP/qmltp_thf"

def get_problem_file_list(problem_directory):
    ret = []
    for dirpath, dirs, files in os.walk(problem_directory):
        for filename in files:
            path = Path(os.path.join(dirpath, filename))
            if path.is_file() and path.suffix == ".p":
                ret.append(path)
    return ret