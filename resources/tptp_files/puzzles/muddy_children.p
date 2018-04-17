% from http://web.cecs.pdx.edu/~mperkows/PERKOWSKI_PRESENTATIONS/Perkowski.Seminar.SySc.2011.pdf

thf(muddy_children_semantics, logic , ( $modal := [
    $constants := $rigid ,
    $quantification := $constant ,
    $consequence := $local ,
    $modalities := [
    	$modal_system_K
	]
] ) ).

% Two children a and b
thf(a_type, type, (a : $i)).
thf(a_type, type, (b : $i)).
%thf(a_not_b, axiom, (a!=b)).

% Muddy predicate
thf(muddy_type, type, (muddy : ($i>$o))).

thf(axiom_1b, axiom, ($box_int @ 1 @ ((~(muddy @ a)) => ($box_int @ 2 @ (~(muddy @ a)))))).

thf(axiom_2a, axiom, ($box_int @ 1 @ ($box_int @ 2 @ ((muddy @ a) | (muddy @ b))))).

thf(axiom_3, axiom, ($box_int @ 1 @ (~($box_int @ 2 @ (muddy @ b))))).

thf(c, conjecture, ($box_int @ 1 @ (muddy @ a))).