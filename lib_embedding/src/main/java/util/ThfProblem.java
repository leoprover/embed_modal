package util;

import util.external_software_wrappers.SatallaxWrapper;

import java.nio.file.Path;

public class ThfProblem {
    public Path path;
    public SatallaxWrapper s;

    public ThfProblem(Path path, SatallaxWrapper s){
        this.path = path;
        this.s = s;
    }
}
