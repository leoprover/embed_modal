package util;

import util.external_software_wrappers.Leo2Wrapper;
import util.external_software_wrappers.MLeanCopWrapper;
import util.external_software_wrappers.NitpickWrapper;
import util.external_software_wrappers.SatallaxWrapper;

import java.nio.file.Path;

public class QmlProblem {
    public Path path;
    public MLeanCopWrapper mleancop;
    public String name;

    public QmlProblem(Path path){
        this.path = path;
    }
}
