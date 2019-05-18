# todo before benchmark (necessary)
* CHECK find non-modal problems
* CHECK implement embedding for non-modal problems 
* CHECK test embedding for non-modal problems
* CHECK test syntactic embedding for cumulative
* implement syntactic embedding for decreasing
* test barcan for decreasing
* CHECK dataset: find problems with native =/!=
* test S5U (constant, cumul_sem, cumul_syn, vary)
* dataset: replace =/!= by other symbol for problems with native =
* dataset: test problems with replaced =/!=

# another benchmark (optional)
* dataset: find problems with axiomatized equality
* dataset: replace problems containing axiomatized equality with native equality
* dataset: test problems with native equality
* implement TH1
* find TH1 provers
* test TH1

# benchmark
## Provers
(minimum)
* Leo3 with E
* Nitpick

(optional)
* Leo3
* Leo3 with CVC4
* Leo3 with E+CVC4
* Satallax
* some TH1 provers?

## Configurations
(minimum)
K,D,T,S4,S5,S5U x local x constant,vary,cumul_sem,cumul_syn

(optional)
global, decreasing

## Data Sets
(minimum)
* default: contains problems without =/!= symbols that were replaced by a fresh constant, 
           seven SYM problems containing inadequate equality problems are removed

(optional)
* native_equality_set: contains all equality problems with native equality (remove axioms?)

