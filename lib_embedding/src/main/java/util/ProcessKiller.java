package util;

import org.zeroturnaround.process.ProcessUtil;
import org.zeroturnaround.process.Processes;
import org.zeroturnaround.process.SystemProcess;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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
        System.out.println(cmd);
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

    public static void killAllOlderThan(int timeout , String grep){
        Process p = null;
        List<Integer> pids = new ArrayList<>();
        String cmd = "ps -eo pid,etime,comm | grep " + grep;
        String[] params = {"bash","-c",cmd};
        try {
            p = Runtime.getRuntime().exec(params);
            p.waitFor(60L,TimeUnit.SECONDS);
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = null;
            while ((line = stdInput.readLine()) != null) {
                String[] columns = line.split("\\s+");
                int pid = Integer.parseInt(columns[0]);
                int seconds = 0;
                String etime = columns[1];
                // d-hh:mm:ss
                if (etime.contains("-")){
                    String[] d = etime.split("-");
                    if (d.length != 2) System.err.println("not matching days-time: " + etime);
                    String[] components = d[1].split(":");
                    // hh:mm:ss
                    if (components.length == 3) seconds = Integer.parseInt(components[0]) * 3600 + Integer.parseInt(components[1]) * 60 + Integer.parseInt(components[0]);
                    // mm:ss
                    else if (components.length == 2) seconds = Integer.parseInt(components[0]) * 60 + Integer.parseInt(components[1]);
                    else System.err.println("could not parse time components without days: " + etime);
                }
                // hh:mm:ss or mm:ss
                else{
                    String[] components = etime.split(":");
                    // hh:mm:ss
                    if (components.length == 3) seconds = Integer.parseInt(components[0]) * 3600 + Integer.parseInt(components[1]) * 60 + Integer.parseInt(components[0]);
                    // mm:ss
                    else if (components.length == 2) seconds = Integer.parseInt(components[0]) * 60 + Integer.parseInt(components[1]);
                    else System.err.println("could not parse time components without days: " + etime);
                }
                if (timeout <= seconds){
                    pids.add(pid);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Process p2 = null;
        String cmd2 = "kill -9 " + pids.stream().map(s->s.toString()).collect(Collectors.joining(" "));
        System.out.println(cmd2);
        String[] params2 = {"bash","-c",cmd2};
        try {
            p2 = Runtime.getRuntime().exec(params2);
            p2.waitFor(60L,TimeUnit.SECONDS);
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
        //System.out.println(pid);
        if (pid == -1L) {
            return;
        }
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
