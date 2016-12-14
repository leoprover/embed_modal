% Scott's version of Goedel's Ontological Proof of the Existence of God

% positive constant
% maps a proposition to boolean
thf( positive_type , type , ( positive: ( $i > $o ) > $o ) ).

% godlike constant
% maps an individual to boolean
thf( godlike_type , type , ( godlike: $i > $o ) ).

% essence constant
% maps a proposition and an individual to boolean
thf( essence_type , type , ( essence: ( $i > $o ) > $i > $o ) ).

% necessary existence constant
% maps an individual to boolean  
thf( ne_type , type , ( ne: $i > $o ) ).

% A1: Either the property or its negation are positive, but not both.
thf( a1 , axiom , ( ! [Phi:$i>$o] : ( ( positive @ ( ^ [X:$i] : ( ~ ( Phi @ X ) ) ) ) <=> ( ~ ( positive @ Phi ) ) ) ) ).

% A2: A property necessarily implied by a positive property is positive.
thf( a2 , axiom , ( ! [Phi:$i>$o,Psi:$i>$o] :  ( ( ( positive @ Phi ) & ( $box @ ( ! [X:$i] : ( ( ( Phi @ X ) => ( Psi @ X ) ) ) ) ) ) => ( positive @ Psi ) ) ) ).

% T1: Positive properties are possibly exemplified.
thf( t1 , conjecture , ( ! [Phi:$i>$o] : ( ( positive @ Phi ) => ( $dia @ ( ? [X:$i] : ( Phi @ X ) ) ) ) ) ).

% D1: A God-like being possesses all positive properties.
thf( d1 , definition , ( godlike = ( ^ [X:$i] : ( ! [Phi:$i>$o] : ( ( positive @ Phi ) => ( Phi @ X ) ) ) ) ) ).

% A3: The property of being God-like is positive.
thf( a3 , axiom , ( positive @ godlike ) ).

% C: Possibly, God exists.
thf ( c , conjecture , ( $dia @ ( ? [X:$i] : ( godlike @ X ) ) ) ).

% A4: Positive properties are necessary positive properties.
thf( a4 , axiom , ( ! [Phi:$i>$o] : ( ( positive @ Phi ) => ( $box @ ( positive @ Phi ) ) ) ) ).

% D2: An essence of an individual is a property possessed by it and necessarily implying any of its properties.
thf( d2 , definition , ( essence = ( ^ [Phi:$i>$o,X:$i] : ( ( Phi @ X ) & ( ! [Psi:$i>$o] : ( ( Psi @ X ) => ( $box @ ( ! [Y:$i] : ( ( Phi @ Y ) => ( Psi @ Y ) ) ) ) ) ) ) ) ) ).

% T2: Being God-like is an essence of any God-like being.
thf( t2 , conjecture , ( ![X:$i] : ( ( godlike @ X ) => ( essence @ godlike @ X ) ) ) ).

% D3: Necessary existence of an individual is the necessary exemplification of all its essences
thf( d3 , definition , ( ne = ( ^ [X:$i] : ( ! [Phi:$i>$o] : ( ( essence @ Phi @ X ) => ( $box @ ( ? [Y:$i] : ( Phi @ Y ) ) ) ) ) ) ) ).

% A5: Necessary existence is positive.
thf( a5 , axiom , ( positive @ ne ) ).

% T3: Necessarily God exists.
thf( t3 , conjecture , ( $box @ ( ? [X:$i] : ( godlike @ X ) ) ) ).
