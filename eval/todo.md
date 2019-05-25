# todo before benchmark (necessary)
* CHECK find non-modal problems
* CHECK implement embedding for non-modal problems 
* CHECK test embedding for non-modal problems
* CHECK implement syntactic embedding for decreasing
* test vary
* test syn_cumul
* test sem_cumul
* test syn_decr
* test sem_decr
* CHECK dataset: find problems with native =/!=
* CHECK dataset: replace =/!= by other symbol for problems with native =
* CHECK dataset: test problems with replaced =/!=
* CHECK test S5U (constant, cumul_sem, cumul_syn, vary)
* CHECK dataset: create full dataset containing replaced problems and not containing erroneous SYM problems

# another benchmark I (optional)
* dataset: find problems with axiomatized equality
* dataset: find the common axioms
* dataset: replace problems containing axiomatized equality with native equality
* dataset: test problems with native equality

# another benchmark II (optional)
* implement TH1
* find TH1 provers
* test TH1

# benchmark
## Provers
(minimum)
* Leo3 with E+CVC4
* Nitpick

(optional)
* Leo3
* Leo3 with CVC4
* Leo3 with E
* Satallax
* some TH1 provers?

## Configurations
(minimum)
K_syn,D_syn,T_syn,S4_syn,S5_syn,
K_sem,D_sem,T_sem,S4_sem,S5_sem,
S5U (can we optimize here?)
x local x constant,vary,cumul_sem,cumul_syn

(optional)
global, decreasing

## Data Sets
(minimum)
* default: contains problems without =/!= symbols that were replaced by a fresh constant, 
           seven SYM problems containing inadequate equality problems are removed

(optional)
* native_equality_set: contains all equality problems with native equality (remove axioms?)

