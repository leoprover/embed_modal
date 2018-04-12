# Contents
A set of tools to semantically embed higher-order modal logic problems formulated
in the TPTP [1] dialect THF into higher-order logic formulated in THF. It contains
* a standalone tool ```embed``` for the semantical embedding.
* a conversion tool ```convert_qmltp_to_thf``` for translating 
first-order modal logic problems formulated in the dialect of the QMLTP [2]
to higher-order logic problems formulated in THF. A translated version of 
the QMLTP can be found on Github [3].
* a library ```embedding_lib``` you can include in your project to semantically 
embed problems. Contains wrapper functions for easier handling.

This software is based on the theoretical work of Benzmüller and Paulson [4].

Details of the embedding's procedure, a theoretical overview and some performance tests are available in Gleißner, Steen and Benzmüller [5].

# Requirements
* Maven 3.5 or higher
* Java 8 or higher

# Build
In the root directory invoke

    mvn clean package

# Usage
#### Converting a problem
To convert a modal problem run

    java -jar embed/target/embed-1.0-SNAPSHOT-shaded.jar 
    -i <INPUT_FILE>
    -o <OUTPUT_FILE>

#### Converting a directory
To recursively convert a directory containing modal problems run

    java -jar embed/target/embed-1.0-SNAPSHOT-shaded.jar 
    -i <INPUT_DIRECTORY>
    -o <OUTPUT_DIRECTORY>
    -diroutput <OUTPUT_OPTIONS>

This will preserve the directory structure in the output. If more than semantic is specified
the choices for ```<OUTPUT_OPTIONS>``` become relevant:
* joint: exactly one duplicate directory structure and filenames contain a string representing 
the semantics used during the embedding
* splitted: one duplicate directory structure for every semantic

#### Adding Semantics
Specify semantics (which overrides any semantics specified in the problem) using

    -consequences <CONSEQUENCES>
    -constants <CONSTANTS>
    -domains <DOMAINS>
    -systems <SYSTEMS>
    
Valid choices for the semantics parameters are
* ```<CONSEQUENCES>```: global,local
* ```<CONSTANTS>```: rigid
* ```<DOMAINS>```: constant, cumulative, decreasing, varying
* ```<SYSTEMS>```: K, KB, K4, K5, K45, KB5, KB5_KB5, KB5_KB4, D, DB, D4, D5, D45, T, B, S4, 
                   S5, S5_KT5, S5_KTB5, S5_KT45, S5_KT4B, S5_KD4B, S5_KD4B5, S5_KDB5, S5U
                   
Note that systems with the same prefix are different axiomatizations of the same semantics.

Specify multiple semantics that creates multiple embedded problems by separating the 
parameters with a comma e.g. 

    -domains constant,cumulative
    
This will create embeddings for all
semantics contained by the cross-product of ```<CONSEQUENCES>``` x ```<CONSTANTS>``` x ```<DOMAINS>``` x ```<SYSTEMS>```

#### Transformation Parameters
Specify special parameters using

    -t <OPTIONS>
    
Valid choices for ```<OPTIONS>``` are
* semantical_modality_axiomatization: embed modality semantics as frame conditions
* syntactical_modality_axiomatization: embed modality semantics as axioms about modal operators

# References
[1] Geoff Sutcliffe - 
    The TPTP Problem Library and Associated Infrastructure. From CNF to TH0, TPTP v6.4.0 - 
    2017 - 
    Journal of Automated Reasoning -
    http://www.tptp.org

[2] Thomas Raths and Jens Otten - 
    The QMLTP Problem Library for First-Order Modal Logics -
    2012 -
    Automated Reasoning, IJCAR 2012 - 
    http://www.jens-otten.de/papers/qmltp_ijcar12.pdf - 
    http://www.iltp.de/qmltp

[3] Tobias Gleißner - QMLTP Mirror and QMLTP in THF - https://github.com/TobiasGleissner/QMLTP

[4] Christoph Benzmüller and Lawrence C. Paulson - 
    Quantified Multimodal Logics in Simple Type Theory - 
    2013 - 
    Logica Universalis (Special Issue on Multimodal Logics) -
    http://christoph-benzmueller.de/papers/J23.pdf

[5] Tobias Gleißner, Alexander Steen and Christoph Benzmüller - 
    Theorem Provers For Every Normal Modal Logic - 
    2017 - 
    LPAR-21. 21st International Conference on Logic for Programming, Artificial Intelligence and Reasoning -
    https://easychair.org/publications/paper/340346
    