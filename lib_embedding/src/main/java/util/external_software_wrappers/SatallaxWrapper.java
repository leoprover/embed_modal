package util.external_software_wrappers;

import exceptions.WrapperException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class SatallaxWrapper {
    public String stdout = "";
    public String stderr = "";
    public String stdoutSAT = "";
    public String stderrSAT = "";
    public String status = "";
    public String sat = "";

    private static final Logger log = Logger.getLogger( "default" );

    public void call(Path filename,long timeout,TimeUnit unit) throws WrapperException, InterruptedException {
        this.stdout = "";
        this.stderr = "";
        this.status = "";
        List<String> params = java.util.Arrays.asList("satallax",filename.toString());
        try {
            // Call satallax on problem and extract status
            ProcessBuilder satallax = new ProcessBuilder(params);
            Process proc = satallax.start();
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
            if (!proc.waitFor(timeout, unit)){
                log.info(filename.toString() + " : Proof Timeout");
                proc.destroy();
            }
            String s = null;
            while ((s = stdInput.readLine()) != null) {
                stdout += s;
            }
            while ((s = stdError.readLine()) != null) {
                stderr += s;
            }
            this.status = extractSZSStatus(this.stdout);
            log.info(filename.toString() + " : SZS: " + this.status);
        } catch (IOException e) {
            this.stdout = null;
            //e.printStackTrace();
            //throw new WrapperException(e.getMessage()+"\nStacktrace:\n"+e.getStackTrace().toString());
        }

        try{
            // Call satallax on problem without conjecture and extract status (for sat)
            List<String> problem = Files.readAllLines(filename);
            String problemWithoutConjecture = problem.stream().filter(p->!p.contains(", conjecture ,")).collect(Collectors.joining("\n"));
            Path tempFile = Files.createTempFile(null,null);
            Files.write(tempFile,problemWithoutConjecture.getBytes());
            List<String> paramsSAT = java.util.Arrays.asList("satallax",tempFile.toString());
            ProcessBuilder satallaxSAT = new ProcessBuilder(paramsSAT);
            Process procSAT = satallaxSAT.start();
            BufferedReader stdInputSAT = new BufferedReader(new InputStreamReader(procSAT.getInputStream()));
            BufferedReader stdErrorSAT = new BufferedReader(new InputStreamReader(procSAT.getErrorStream()));
            if (!procSAT.waitFor(timeout, unit)){
                log.info(filename.toString() + " : SAT Timeout");
                procSAT.destroy();
            }
            String s = null;
            while ((s = stdInputSAT.readLine()) != null) {
                stdoutSAT += s;
            }
            while ((s = stdErrorSAT.readLine()) != null) {
                stderrSAT += s;
            }
            this.sat = extractSZSStatus(this.stdoutSAT);
            log.info(filename.toString() + " : SAT-SZS: " + this.sat);
            //System.out.println("status:"+this.status);
            //System.out.println("status:"+this.sat);
            //System.out.println(problemWithoutConjecture);
        } catch (IOException e) {
            this.stdout = null;
            //e.printStackTrace();
            //throw new WrapperException(e.getMessage()+"\nStacktrace:\n"+e.getStackTrace().toString());
        }
    }

    private String extractSZSStatus(String consoleOutput){
        int szs_start = consoleOutput.indexOf("SZS status ");
        szs_start += "SZS status ".length();
        int szs_end = consoleOutput.indexOf(" ",szs_start);
        if (szs_end == -1) szs_end = consoleOutput.length();
        String status = consoleOutput.substring(szs_start,szs_end);
        return status;
    }

    public boolean hasStdout(){
        return this.stdout.length() > 0;
    }

    public boolean hasStderr(){
        return this.stderr.length() > 0;
    }

    public String getSZSStatus(){
        return this.status;
    }

    public boolean isTheorem(){
        return this.status.contains("Theorem");
    }

    public boolean isCounterSatisfiable(){
        return this.status.contains("CounterSatisfiable");
    }

    public boolean hasParserError(){
        return this.status.contains("Error") && this.stdout.contains("syntax error");
    }

    public boolean hasTypeError(){
        return this.status.contains("Error") && this.stdout.contains("expected type");
    }

    public boolean hasUnknownStatus(){
        if (!(this.isTheorem() || this.isCounterSatisfiable() || this.hasParserError())){
            return true;
        }
        return false;
    }

    public boolean isSatisfiable(){
        return this.sat.contains("Satisfiable");
    }
}

