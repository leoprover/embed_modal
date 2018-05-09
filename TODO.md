# Features
* more kinds of consequence
* TH1 support
* more kinds of constants

# Misc
* parser grammar: full THF compliance
* more documentation
* check if there are already any identifiers which are used in the embedding meta-symbols
* better logging and provide more useful information for the user
* embedding of problems not containing modal operators
* check embedding for thf sentences containing the definition role
* normalize parentheses in output problem
* substitute ugly quantifier names

# Equality
* support and explore various equalities

# Mixed Modal Logics
* test and explore mixed semantics e.g. ```$quantification = [$constant, $i := varying]```

# Multiple Modalities
* test all already implemented aspects of multi-modal logics
* support and explore cumulative and decreasing domains
* support and explore S5U
* support cli semantics arguments for multiple modalities
* support generalized domains: a tuple of a domain and a modality should be assigned const/cumul/decr/vary
* support polymorphic modalities: $box_P @ index_type @ index @ formula
* explore polymorphic modalities and add mechanisms access the meta-level and quantify over modalities 

# Code Quality
* remove suffixrelation and replace by relation where possible
* introduce type/relation/modaloperator classes

# Unit Tests
* semantics analysis
* untested operators

# Other Tests
* set up CI
* automate QMLTP testing with some tool
* automate CLI input options test
