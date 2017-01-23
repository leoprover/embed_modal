# Build
In the root directory invoke

    mvn clean package

# Usage
To convert a modal problem run

    java -jar embed/target/embed-1.0-SNAPSHOT-shaded.jar 
    -f modal 
    -i <input_file_or_directory>
    -o <output_file_or_directory>

## Parameter Usage
    -f choose embedding
    -i input problem path
    -o output problem path
    -dotin input problem dot path (not required)
    -dotout output problem dot path (not required)
    -dotbin graphviz binary dot (not required)
    -log log file path (not required)
    -h display help (not required)
