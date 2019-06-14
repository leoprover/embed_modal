# todo before benchmark (necessary)
* CHECK find non-modal problems
* CHECK implement embedding for non-modal problems 
* CHECK test embedding for non-modal problems
* CHECK implement syntactic embedding for decreasing
* CHECK test syn/sem quantifications for cumul and vary
* CHECK dataset: find problems with native =/!=
* CHECK dataset: replace =/!= by other symbol for problems with native =
* CHECK dataset: test problems with replaced =/!=
* CHECK test S5U (constant, cumul_sem, cumul_syn, vary)
* CHECK dataset: create full dataset containing replaced problems and not containing erroneous SYM problems

# another benchmark I (optional)
* CHECK dataset: find problems with axiomatized equality
* CHECK dataset: find the common axioms
* CHECK dataset: replace problems containing axiomatized equality with native equality
* CHECK dataset: probably remove (common) axioms
* dataset: test problems with native equality

# another benchmark II (optional)
* implement TH1
* CHECK find TH1 provers
* test TH1

# another benchmark III (optional)
* CHECK implement const_syn
* test const_syn

# benchmark
## Provers
* Leo3 with E+CVC4
* Nitpick
* Satallax

(optional)
* Leo3
* Leo3 with CVC4
* Leo3 with E
* HolyHammer

## Configurations
K_syn,D_syn,T_syn,S4_syn,S5_syn,
K_sem,D_sem,T_sem,S4_sem,S5_sem,
S5U (can we optimize here?) x 
local x 
rigid x 
const_sem,const_syn,vary,cumul_sem,cumul_syn

## Data Sets
(minimum)
* default: contains problems without =/!= symbols that were replaced by a fresh constant, 
           seven SYM problems containing inadequate equality problems are removed
* native_equality: contains all equality problems with native equality and equality axioms removed

