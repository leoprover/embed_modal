package util;

import org.zeroturnaround.process.ProcessUtil;
import org.zeroturnaround.process.Processes;
import org.zeroturnaround.process.SystemProcess;

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
}
