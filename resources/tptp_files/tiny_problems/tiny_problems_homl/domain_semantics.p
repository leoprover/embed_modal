thf(standard_s5,logic,( $modal :=
    [$constants := $rigid,
     $quantification := [$constant, a := $varying, b := $cumulative, c := $decreasing],
     $consequence := $global,
     $modalities := [ $modal_system_S5]] )).

thf(a, type, (a: $tType)).
thf(b, type, (b: $tType)).
thf(c, type, (c: $tType)).
thf(d, type, (d: $tType)).

thf(a2, type, (pa : (a > $o))).
thf(b2, type, (pb : (b > $o))).
thf(c2, type, (pc : (c > $o))).
thf(d2, type, (pd : (d > $o))).

thf(1, axiom, ( ! [X:a] : (pa @ X) )).
thf(2, axiom, ( ! [X:b] : (pb @ X) )).
thf(3, axiom, ( ! [X:c] : (pc @ X) )).
thf(4, axiom, ( ! [X:d] : (pd @ X) )).