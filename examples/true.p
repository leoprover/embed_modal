thf(simple_s5,logic,(
    $modal :=
        [ $constants := $rigid,
            $quantification := $decreasing,
            $consequence := $local,
            $modalities := $modal_system_S4
        ]
)).

%thf(1,type,p: $i > $o).
%thf(2,axiom,![X:$i]: p(X)).
%thf(3,conjecture,$box @ (![X:$i]: (p @ X))).
%thf(1,conjecture,($box @ $true)).

%thf(converse_barcan,conjecture, (![Phi:($i>$o)]: (
%    ( $box @ ( ![X:$i] : (Phi @ X) ) )
%    =>
%    ( ![X:$i] : ( $box @ (Phi @ X) ) )
%))).

thf(barcan,conjecture, (![Phi:($i>$o)]: (
    ( ![X:$i] : ( $box @ (Phi @ X) ) )
    =>
    ( $box @ ( ![X:$i] : (Phi @ X) ) )
))).
