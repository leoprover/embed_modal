package util;

import util.external_software_wrappers.Leo2Wrapper;
import util.external_software_wrappers.NitpickWrapper;
import util.external_software_wrappers.SatallaxWrapper;

import java.nio.file.Path;

public class ThfProblem {
    public Path path;
    public SatallaxWrapper satallax;
    public Leo2Wrapper leo;
    public NitpickWrapper nitpick;

    public ThfProblem(Path path, SatallaxWrapper s){
        this.path = path;
        this.satallax = s;
    }

    public ThfProblem(Path path){
        this.path = path;
    }
}
