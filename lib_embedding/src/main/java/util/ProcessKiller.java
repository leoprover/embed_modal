package util;

public class ProcessKiller {
    public static void destroyProc(Process proc, long millis){
        proc.destroy();
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            //do nothing
            //e.printStackTrace();
        }
        proc.destroyForcibly();
    }
}
