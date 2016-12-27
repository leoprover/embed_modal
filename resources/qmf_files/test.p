tpi(6,set_logic,modal([cumulative,flexible,local],
                      [(pour_a,k),(pour_b,k)])).


qmf(pour_ab_make_axiom_1,axiom,
((#box(pour_a) : (#box(pour_b) : acid)) => (#box(make_c) : acid))).

qmf(pour_ab_make_axiom_2,axiom,
((#box(pour_a) : (#box(pour_b) : (~ acid))) => (#box(make_c) : (~ acid)))).

qmf(pour_ba_make_axiom_1,axiom,
((#box(pour_b) : (#box(pour_a) : acid)) => (#box(make_c) : acid))).

qmf(pour_ba_make_axiom_2,axiom,
((#box(pour_b) : (#box(pour_a) : (~ acid))) => (#box(make_c) : (~ acid)))).

qmf(pour_a_acid,axiom,
(#box(pour_a) : (~ acid))).

qmf(make_c_acid,axiom,
(#dia(make_c) : acid)).

qmf(conj,conjecture,
 ((#dia(pour_a) : (~ acid)) & (#dia(pour_a) : (#dia(pour_b) : acid)))).
