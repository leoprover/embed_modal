package util.external_software_wrappers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class NitpickWrapper {

    private static final Logger log = Logger.getLogger( "default" );
    public static String nitpick_binary = "runnitpick";

    public String stdout = "";
    public String stderr = "";
    public String status = "";
    public boolean timeout = false;
    public double duration = 60.0;

    public void call(Path filename, long timeout, TimeUnit unit) {
        this.stdout = "";
        this.stderr = "";
        this.status = "";
        this.timeout = false;
        this.duration = 60.0;

        List<String> params = java.util.Arrays.asList(nitpick_binary,String.valueOf(timeout),filename.toString());
        try {
            ProcessBuilder nitpick = new ProcessBuilder(params);
            Instant start = Instant.now();
            Process proc = nitpick.start();
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
            if (!proc.waitFor(timeout + 20, unit)) {
                log.info(filename.toString() + " : Proof Timeout");
                this.timeout = true;
                proc.destroy();
            }else{
                Instant end = Instant.now();
                Duration delta = Duration.between(start,end);
                this.duration = (double) delta.getNano() / 1000000000.0;
            }
            String s = null;
            while ((s = stdInput.readLine()) != null) {
                stdout += s;
            }
            while ((s = stdError.readLine()) != null) {
                stderr += s;
            }
            this.status = extractSZSStatus();
        } catch (IOException e) {
            if (this.stderr == null) this.stderr = e.getMessage();
            if (this.stdout == null) this.stdout = e.getMessage();
        } catch (InterruptedException e) {
            log.info(filename.toString() + " : Interrupted Exception.");
            this.timeout = true;
        }
    }

    private String extractSZSStatus(){
        int szs_start = this.stdout.indexOf("SZS status ");
        szs_start += "SZS status ".length();
        int szs_end = this.stdout.indexOf(" ",szs_start);
        if (szs_end == -1) szs_end = this.stdout.length();
        String status = this.stdout.substring(szs_start,szs_end);
        return status;
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
        return this.stdout.contains("Parse problem");
    }

}
