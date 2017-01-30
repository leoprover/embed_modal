# Build
In the root directory invoke

    mvn clean package

# Usage
To convert a modal problem run

    java -jar embed/target/embed-1.0-SNAPSHOT-shaded.jar 
    -f modal 
    -i <input_file>
    -o <output_file>

To convert a directory containing

    java -jar embed/target/embed-1.0-SNAPSHOT-shaded.jar 
    -f modal 
    -i <input_directory>
    -o <output_directory>
    -diroutput <options_are_splitted_or_joint>
    
Add semantics with

    -semantics standard_s5
    -semantics local
    -semantics all
    ...

## Parameter Usage
    -f choose embedding
    -i input problem path
    -o output problem path
    -diroutput (required if input is a directory)
    -dotin input problem dot path (not required)
    -dotout output problem dot path (not required)
    -dotbin graphviz binary dot (not required)
    -log log file path (not required)
    -loglevel (not required)
    -h display help (not required)
