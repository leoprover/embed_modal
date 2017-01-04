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

public class Leo2Wrapper {

    private static final Logger log = Logger.getLogger( "default" );
    public static String leo_binary = "runleo";

    public String stdout = "";
    public String stderr = "";
    public String status = "";
    public boolean timeout = false;
    public double duration = 60.0;

    public void call(Path filename,long timeout,TimeUnit unit) throws WrapperException {
        this.stdout = "";
        this.stderr = "";
        this.status = "";
        this.timeout = false;
        this.duration = 60.0;

        List<String> params = java.util.Arrays.asList(leo_binary,filename.toString());
        try {
            ProcessBuilder leo = new ProcessBuilder(params);
            Instant start = Instant.now();
            Process proc = leo.start();
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
            if (!proc.waitFor(timeout, unit)) {
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


// file not found
    /*
    Error occurred:Not_found
    Raised by primitive operation at file "parser-hotptp/preprocess.ml", line 18, characters 23-40
    Called from file "parser-hotptp/preprocess.ml", line 20, characters 23-40
    Called from file "parser-hotptp/preprocess.ml", line 69, characters 14-32
    Called from file "interfaces/interactive.ml", line 99, characters 4-48
    Called from file "interfaces/interactive.ml", line 1163, characters 41-63
    Called from file "interfaces/cmdline.ml", line 632, characters 6-21
    Called from file "interfaces/strategy_scheduling.ml", line 43, characters 14-71
    Called from file "toplevel/leo.ml", line 356, characters 17-80
    Called from file "list.ml", line 73, characters 12-15
    Called from file "toplevel/leo.ml", line 565, characters 10-22
    */
// parse problem
    /*
    Parse problem occurred at token ')'

    Error occurred:Term.PARSER
    Raised at file "parser-hotptp/htparser.mly", line 173, characters 20-26
    Called from file "parsing.ml", line 140, characters 39-75
    Called from file "parsing.ml", line 162, characters 4-28
    Re-raised at file "parsing.ml", line 181, characters 14-17
    Called from file "interfaces/interactive.ml", line 104, characters 10-45
    Re-raised at file "interfaces/interactive.ml", line 110, characters 22-23
    Called from file "interfaces/interactive.ml", line 1163, characters 41-63
    Called from file "interfaces/cmdline.ml", line 632, characters 6-21
    Called from file "interfaces/strategy_scheduling.ml", line 43, characters 14-71
    Called from file "toplevel/leo.ml", line 356, characters 17-80
    Called from file "list.ml", line 73, characters 12-15
    Called from file "toplevel/leo.ml", line 565, characters 10-22

    line 1, characters 61-62: syntax error
    % SZS status Error

     */

// countersat
    /*
    No.of.Axioms: 0

     Length.of.Defs: 0

     Contains.Choice.Funs: false
    (rf:0,axioms:0,ps:3,u:6,ude:true,rLeibEQ:true,rAndEQ:true,use_choice:true,use_extuni:true,use_extcnf_combined:true,expand_extuni:false,foatp:e,atp_timeout:25,atp_calls_frequency:10,ordering:none,proof_output:1,protocol_output:false,clause_count:2,loop_count:0,foatp_calls:0,translation:fof_full)
    % SZS status CounterSatisfiable for /home/tg/university/bachelor_thesis/software/resources/tptp_files/modal1.p : (rf:0,axioms:0,ps:3,u:6,ude:true,rLeibEQ:true,rAndEQ:true,use_choice:true,use_extuni:true,use_extcnf_combined:true,expand_extuni:false,foatp:e,atp_timeout:25,atp_calls_frequency:10,ordering:none,proof_output:1,protocol_output:false,clause_count:7,loop_count:2,foatp_calls:1,translation:fof_full)

     */