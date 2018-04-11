thf( simple_s5 , logic , ( $modal := [
    $constants := $rigid ,
    $quantification := $constant ,
    $consequence := [$global,a1 := $local,c1:=$local] ,
    $modalities := $modal_system_S5 ] ) ).

thf( a1 , axiom , ( ? [X:$o,Y:$o,Z:$o] : ( X & Y & Z ))).
thf( a2 , axiom , ( ? [X:$o,Y:$o,Z:$o] : ( X & Y & Z ))).
thf( c1 , conjecture , ( ? [X:$o,Y:$o,Z:$o] : ( X & Y & Z ))).
thf( c2 , conjecture , ( ? [X:$o,Y:$o,Z:$o] : ( X & Y & Z ))).

thf(6, axiom, ($box @ $true)). % ensure $box is used at least once