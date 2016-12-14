package main;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class ConvertQmltpToThf {

    public static String resultPath = null;

    public static void convert(Path inPath){
        System.out.println("processing " + inPath);
        Path directory = Paths.get(resultPath,inPath.getParent().getFileName().toString());
        if (!Files.exists(directory)){
            try {
                Files.createDirectories(directory);
            } catch (IOException e) {
                System.err.println("Could not create directory "+ directory.toString());
                e.printStackTrace();
            }
        }
        Path outPath = Paths.get(directory.toString(),inPath.getFileName().toString());
        try {
            String problem = new String(Files.readAllBytes(inPath));
            if (problem.contains("meets") ||
                    problem.contains("a_truth") ||
                    problem.contains("equiv") ||
                    problem.contains("implies") ||
                    problem.contains("is_a_theorem") ||
                    problem.contains("a_truth") ||
                    problem.contains("a_truth") ||
                    problem.contains("a_truth") ||
                    problem.contains("a_truth") ||
                    problem.contains("a_truth") ||
                    problem.contains("a_truth") ||

                    problem.contains("qmltpeq")){
                return;
            }
            problem = problem.replaceAll("#box[ ]*: ","\\$box @ ").replaceAll("qmf","thf");
            try{
                Files.write(outPath,problem.getBytes());
            }catch (IOException f){
                System.err.println("Could not write to file " + outPath.toString());
                f.printStackTrace();
            }
        } catch (IOException e) {
            System.err.println("Could not open file " + inPath.toString());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        if (args.length != 2){
            System.err.println("Unmatched argument size\nTwo arguments needed: \n" +
                    "/path/to/tptp/Problems/directory\n" +
                    "/path/to/result\n");
            System.exit(1);
        }

        String qmltpPath = args[0]; // "/home/tg/university/bachelor_thesis/QMLTP-v1.1/";
        String resultPath = args[1]; // "/home/tg/university/bachelor_thesis/software/output/convert_qmltp_to_thf/";
        ConvertQmltpToThf.resultPath = resultPath;

        if (!Files.isDirectory(Paths.get(qmltpPath))){
            System.err.println("qmltp path is not a directory\nTwo arguments needed: \n" +
                    "/path/to/tptp/Problems/directory\n" +
                    "/path/to/result/");
            System.err.println(qmltpPath + " is not a valid directory");
        }

        if (!Files.isDirectory(Paths.get(resultPath))){
            System.err.println("result path is not a directory\nTwo arguments needed: \n" +
                    "/path/to/tptp/Problems/directory\n" +
                    "/path/to/result/");
            System.err.println(resultPath + " is not a valid directory");
        }

        Path problemsPath = Paths.get(qmltpPath,"Problems");
        try (Stream<Path> paths = Files.walk(problemsPath)) {
            paths.filter(Files::isRegularFile).filter(p->!p.toString().contains("/MML/")).forEach(ConvertQmltpToThf::convert);
        } catch (IOException e) {
            System.err.println("Could not scan directory properly");
            e.printStackTrace();
        }

    }

}
