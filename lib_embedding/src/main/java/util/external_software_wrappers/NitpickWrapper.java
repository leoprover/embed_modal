package util.external_software_wrappers;

import util.ProcessKiller;

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
    public double duration = -1;
    private double durationNitpick = -1;

    public void call(Path filename, long timeout, TimeUnit unit) {
        this.stdout = "";
        this.stderr = "";
        this.status = "";
        this.timeout = false;
        this.duration = timeout;
        this.durationNitpick = timeout;

        List<String> params = java.util.Arrays.asList(nitpick_binary,String.valueOf(timeout),filename.toString());
        Process proc = null;
        try {
            ProcessBuilder nitpick = new ProcessBuilder(params);
            Instant start = Instant.now();
            proc = nitpick.start();
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
            if (!proc.waitFor(timeout + 20, unit)) {
                log.fine(filename.toString() + " : Proof Timeout");
                this.timeout = true;
                ProcessKiller.destroyProc(proc, 3000L);
            }else{
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
        } catch (IOException e) {
            if (this.stderr == null) this.stderr = e.getMessage();
            if (this.stdout == null) this.stdout = e.getMessage();
        } catch (InterruptedException e) {
            log.fine(filename.toString() + " : Interrupted Exception.");
            if (this.stderr == null) this.stderr = e.getMessage();
            if (this.stdout == null) this.stdout = e.getMessage();
            this.timeout = true;
        }finally {
            this.status = extractSZSStatus(this.stdout);
            this.durationNitpick = this.extractNitpickDuration();
            System.out.println(this.status);

            if (proc != null) ProcessKiller.destroyProc(proc, 3000L);
        }
    }

    private double extractNitpickDuration(){
        try {
            int start = this.stdout.indexOf("Total time: ");
            if (start == -1) return -1;
            start = start + 12;
            String res = this.stdout.substring(start).trim();
            int end = res.indexOf(" ");
            double value = Double.valueOf(res.substring(0, end));
            String unit = res.substring(end + 1, end + 3);
            if (unit.contains("ms")) {
                value = value / 1000.0;
            }
            return value;
        } catch (Exception e){
            return -1;
        }
    }

    private String extractSZSStatus(String consoleOutput){
        int szs_start = consoleOutput.indexOf("SZS status ");
        if (szs_start == -1) return "NOSTATUS";
        szs_start += "SZS status ".length();
        int szs_end = consoleOutput.indexOf(" ",szs_start);
        if (szs_end == -1) szs_end = consoleOutput.length();
        String status = consoleOutput.substring(szs_start,szs_end);
        return status;
    }

    public double getNitpickDuration(){
        if (this.durationNitpick == -1) return this.duration;
        return this.durationNitpick;
    }

    public String getSZSStatus(){
        return this.status;
    }

    public boolean isCounterSatisfiable(){
        return this.status.contains("CounterSatisfiable");
    }

    public boolean hasTypeError(){
        return this.stdout.contains("error") && this.stdout.contains("Type error");
    }

    public boolean hasParserError(){
        return this.stdout.contains("syntax error");
    }

    public boolean hasError(){
        return this.stdout.contains("error");
    }

    public String getAbbrevStatus(){
        if (this.isCounterSatisfiable()) return "CSA";
        if (this.hasError()) return "ERR";
        return "UNK";
    }

}
