% box_int @ 0 indicates common knowledge
% box_int @ x where x != 0 indicates agent x knows ...

thf(wise_men_puzzle_semantics, logic , ( $modal := [
    $constants := $rigid ,
    $quantification := $constant ,
    $consequence := $global ,
    $modalities := [
    	$modal_system_K
	]
] ) ).

% $i type models the agents's hats
thf(agent_a, type, (a: $i)).
thf(agent_b, type, (b: $i)).
thf(agent_c, type, (c: $i)).

% Property of an agent's hat
thf(white_spot, type, (ws: ($i>$o))).

% Introspection
%%%%%%%%%%thf(intro_a, axiom, (![X:$o]: ((~($box_int @ 1 @ X)) => ($box_int @ 1 @ (~($box_int @ 1 @ X)))))).
%%%%%%%%%%thf(intro_b, axiom, (![X:$o]: ((~($box_int @ 2 @ X)) => ($box_int @ 2 @ (~($box_int @ 2 @ X)))))).
%%%%%%%%%%thf(intro_c, axiom, (![X:$o]: ((~($box_int @ 3 @ X)) => ($box_int @ 3 @ (~($box_int @ 3 @ X)))))).


% The agents hats are different from each other (A0a)
% can be added
%thf(axiom_0a, axiom, ($box_int @ 0 @ ((a != b) & (a != c) & (b != c)))).
% add manually (not needed)
%thf(axiom_0a, axiom, ((a != b) & (a != c) & (b != c))).

% modalities are different (A0b)
% add manually
%thf(axiom_0b, axiom, ((mbox_int_0 != mbox_int_1) & (mbox_int_0 != mbox_int_3) & (mbox_int_0 != mbox_int_2) & (mbox_int_1 != mbox_int_2) & (mbox_int_1 != mbox_int_3) & (mbox_int_2 != mbox_int_3))).

% At least one agent has a white spot (A1)
thf(axiom_1, axiom, ($box_int @ 0 @ ((ws @ a) | (ws @ b) | (ws @ c)))).

% If one agent has a white spot all other agents can see this (A2)
thf(axiom_2ab, axiom, ($box_int @ 0 @ ((ws @ a) => ($box_int @ 2 @ (ws @ a))))).
thf(axiom_2ac, axiom, ($box_int @ 0 @ ((ws @ a) => ($box_int @ 3 @ (ws @ a))))).
thf(axiom_2ba, axiom, ($box_int @ 0 @ ((ws @ b) => ($box_int @ 1 @ (ws @ b))))).
thf(axiom_2bc, axiom, ($box_int @ 0 @ ((ws @ b) => ($box_int @ 3 @ (ws @ b))))).
thf(axiom_2ca, axiom, ($box_int @ 0 @ ((ws @ c) => ($box_int @ 1 @ (ws @ c))))).
thf(axiom_2cb, axiom, ($box_int @ 0 @ ((ws @ c) => ($box_int @ 2 @ (ws @ c))))).

% If one agent has a black spot all other agents can see this (A3)
thf(axiom_3ab, axiom, ($box_int @ 0 @ ((~(ws @ a)) => ($box_int @ 2 @ (~(ws @ a)))))).
thf(axiom_3ac, axiom, ($box_int @ 0 @ ((~(ws @ a)) => ($box_int @ 3 @ (~(ws @ a)))))).
thf(axiom_3ba, axiom, ($box_int @ 0 @ ((~(ws @ b)) => ($box_int @ 1 @ (~(ws @ b)))))).
thf(axiom_3bc, axiom, ($box_int @ 0 @ ((~(ws @ b)) => ($box_int @ 3 @ (~(ws @ b)))))).
thf(axiom_3ca, axiom, ($box_int @ 0 @ ((~(ws @ c)) => ($box_int @ 1 @ (~(ws @ c)))))).
thf(axiom_3cb, axiom, ($box_int @ 0 @ ((~(ws @ c)) => ($box_int @ 2 @ (~(ws @ c)))))).

% A4, A5 should come from axiomatization S5

% Every agent has common knowledge (A6)
thf(axiom_6a, axiom, (![X:$o]: (($box_int @ 0 @ X) => ($box_int @ 1 @ X)))).
thf(axiom_6b, axiom, (![X:$o]: (($box_int @ 0 @ X) => ($box_int @ 2 @ X)))).
thf(axiom_6c, axiom, (![X:$o]: (($box_int @ 0 @ X) => ($box_int @ 3 @ X)))).

% Agents a and b do not know their hat color (A9,A10)
thf(axiom_9, axiom, ($box_int @ 0 @ (~($box_int @ 1 @ (ws @ a))))).
thf(axiom_10, axiom, ($box_int @ 0 @ (~($box_int @ 2 @ (ws @ b))))).

% Agent c can deduce the color of his hat (C)
thf(con, conjecture, ($box_int @ 3 @ (ws @ c))).

