package util;

public class ReflectionUtils {
    public static String getMethodName(){
        String method = null;
        try{
             method = Thread.currentThread().getStackTrace()[2].getMethodName();
        }
        catch (Exception e){
            method = "MNF";
        }
        return method;
    }
}
