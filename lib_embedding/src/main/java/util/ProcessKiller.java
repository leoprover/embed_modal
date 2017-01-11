package util;

import org.zeroturnaround.process.ProcessUtil;
import org.zeroturnaround.process.Processes;
import org.zeroturnaround.process.SystemProcess;

import java.io.IOException;
import java.lang.reflect.Field;
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
            p.waitFor(60L,TimeUnit.SECONDS);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /*
     *nix platforms only
     */
    public static long getPidOfProcess(Process p) {
        long pid = -1;

        try {
            if (p.getClass().getName().equals("java.lang.UNIXProcess")) {
                Field f = p.getClass().getDeclaredField("pid");
                f.setAccessible(true);
                pid = f.getLong(p);
                f.setAccessible(false);
            }
        } catch (Exception e) {
            pid = -1;
        }
        return pid;
    }

    public static void killProcess(Process p){
        long pid = getPidOfProcess(p);
        //if (pid == -1L){
        //    System.err.println("PID -1 !!!!!!!!!!!!!!");
        //    System.exit(1);
        //}
        String cmd = "kill -9 " + pid;
        String[] params = {"bash","-c",cmd};
        Process killer = null;
        try {
            killer = Runtime.getRuntime().exec(params);
            killer.waitFor(60L,TimeUnit.SECONDS);
            System.out.println(cmd);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
