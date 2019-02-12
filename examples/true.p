thf(simple_s5,logic,(
    $modal :=
      [ $constants := $rigid,
        $quantification := $cumulative,
        $consequence := $global,
        $modalities := $modal_system_S4
      ] )).

%thf(1,type,p: $i > $o).
%thf(2,axiom,![X:$i]: p(X)).
%thf(3,conjecture,$box @ (![X:$i]: (p @ X))).
%thf(1,conjecture,($box @ $true)).

thf(4,axiom, (![Phi:($i>$o)]: (
    ( $box @ ( ![X:$i] : (Phi @ X) ) )
    =>
    ( ![X:$i] : ( $box @ (Phi @ X) ) )
))).

