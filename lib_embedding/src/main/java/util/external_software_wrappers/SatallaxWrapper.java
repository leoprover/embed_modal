package util.external_software_wrappers;

import exceptions.WrapperException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class SatallaxWrapper {

    private static final Logger log = Logger.getLogger( "default" );
    public static String satallax_binary = "satallax";

    public String stdout = "";
    public String stderr = "";
    public String status = "";
    public boolean timeout = false;
    public double duration = -1;

    public void call(Path filename, long timeout, TimeUnit unit) {
        this.stdout = "";
        this.stderr = "";
        this.status = "";
        this.timeout = false;
        this.duration = timeout;

        List<String> params = java.util.Arrays.asList(satallax_binary,filename.toString());
        try {
            // Call satallax on problem and extract status
            ProcessBuilder satallax = new ProcessBuilder(params);
            Instant start = Instant.now();
            Process proc = satallax.start();
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
            if (!proc.waitFor(timeout, unit)){
                log.fine(filename.toString() + " : Proof Timeout");
                this.timeout = true;
                proc.destroy();
            }
            else{
                Instant end = Instant.now();
                Duration delta = Duration.between(start,end);
                this.duration =  (double) delta.getSeconds() + ( (double) delta.getNano() ) / 1000000000.0;
            }
            String s = null;
            while ((s = stdInput.readLine()) != null) {
                stdout += s;
            }
            while ((s = stdError.readLine()) != null) {
                stderr += s;
            }
            this.status = extractSZSStatus(this.stdout);
            log.fine(filename.toString() + " : SZS: " + this.status);
        } catch (IOException e) {
            if (this.stderr == null) this.stderr = e.getMessage();
            if (this.stdout == null) this.stdout = e.getMessage();
        } catch (InterruptedException e) {
            log.fine(filename.toString() + " : Interrupted Exception.");
            this.timeout = true;
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

    public String getAllout(){
        return stdout + "\n:::::::\n" + this.stderr;
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
        return this.status.contains("Error") && this.stdout.contains("type");
    }

    public boolean hasError(){
        return this.status.contains("Error");
    }

    public boolean hasUnknownStatus(){
        if (!(this.isTheorem() || this.isCounterSatisfiable() || this.hasError())){
            return true;
        }
        return false;
    }

    public String getAbbrevStatus(){
        if (this.isTheorem()) return "THM";
        if (this.isCounterSatisfiable()) return "CSA";
        if (this.hasError()) return "ERR";
        return "UNK";
    }

}

