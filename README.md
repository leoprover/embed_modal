# Build
In the root directory invoke

    mvn clean package

# Usage
To convert a modal problem run

    java -jar embed/target/embed-1.0-SNAPSHOT-shaded.jar 
    -f modal 
    -i /home/tg/university/bachelor_thesis/software/resources/tptp_files/modal1.p 
    -o /home/tg/university/bachelor_thesis/software/output/output.p 
    -dotin /home/tg/university/bachelor_thesis/software/output/output.in.dot 
    -dotout /home/tg/university/bachelor_thesis/software/output/output.out.dot 
    -dotbin dot 
    -log /home/tg/university/bachelor_thesis/software/output/log.log

## Parameter Usage
    -f choose embedding
    -i input problem path
    -o output problem path
    -dotin input problem dot path (not required)
    -dotout output problem dot path (not required)
    -dotbin graphviz binary dot (not required)
    -log log file path (not required)
    -h display help (not required)
