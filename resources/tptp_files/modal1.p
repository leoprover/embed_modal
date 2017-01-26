thf( simple_s5 , logic , ( $modal := [
    $constants := $rigid ,
    $quantification := $constant ,
    $consequence := $global ,
    $modalities := $modal_system_S5 ] ) ).

thf( p_decl , type , ( p : $o ) ).
thf( p_is_valid, axiom, (p)).
thf( necessitation , conjecture , ( $box @ p ) ).
%thf( necessitation , conjecture , ( $box_int @ 1 @ p ) ).