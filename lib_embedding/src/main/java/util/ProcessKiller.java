package util;

import org.zeroturnaround.process.ProcessUtil;
import org.zeroturnaround.process.Processes;
import org.zeroturnaround.process.SystemProcess;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class ProcessKiller {
    public static void destroyProc(Process proc, long millis){
        try{
            proc.destroy();
        } catch (Exception e){
            //do nothing
        }
        try {

            Thread.sleep(millis);
        } catch (Exception e) {
            //do nothing
        }
        try {
            proc.destroyForcibly();
        }catch (Exception e){
            //do nothing
        }
        try {
            SystemProcess process = Processes.newStandardProcess(proc);
            ProcessUtil.destroyGracefullyOrForcefullyAndWait(process, millis, TimeUnit.SECONDS, millis, TimeUnit.SECONDS);
        } catch (Exception e){
            // do nothing
        }
    }

    public static void killAll(String grep){
        Process p = null;
        String cmd = "kill -9 $(ps aux | grep '[" +
                grep.substring(0,1) +
                "]" +
                grep.substring(1) +
                "' | awk '{print $2}')";
        //System.out.println(cmd);
        String[] params = {"bash","-c",cmd};
        try {
            p = Runtime.getRuntime().exec(params);
            p.waitFor();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
