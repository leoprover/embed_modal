thf( simple_s5 , logic , ( $modal := [
    $constants := $rigid ,
    $quantification := $cumulative ,
    $consequence := $local ,
    $modalities := $modal_system_S5 ] ) ).

thf(c_type,type,(c : ($i))).
thf(p_type,type,(p : ($i>$o))).
thf(1,axiom,(![X:$i]:$true)).
thf ( con , conjecture , ( ( $box @ ( ( p @ c ) ) ) => ( ? [ X :$i ] : ( $box @ ( ( p @ X ) ) ) ) ) ) .