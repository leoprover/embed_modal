%thf( simple_s5 , logic , ( $modal := [
%    $constants := $rigid ,
%    $quantification := $constant ,
%    $consequence := $global ,
%    $modalities := [$modal_system_K, $box_int @ 1 := $modal_system_T, $box_int@ 2 := $modal_system_D, $box_int @ 3 := [$modal_axiom_T, $modal_axiom_4] ] ] ) ).

thf( complicated_s5 , logic , ( $modal := [
      $constants := [ $rigid , myconstant := $flexible ] ,
      $quantification := [ $constant , human := $varying ] ,
      $consequence := [ $global , myaxiom := $local ] ,
      $modalities := [ [ $modal_axiom_T, $modal_axiom_D ] , $box_int @ 1 := $modal_system_S5, $box_int@2 := [$modal_axiom_D, $modal_axiom_4] ] ] ) ).

%thf( p_decl , type , ( p : $o ) ).
%thf( p_is_valid, axiom, (p)).
%thf( mydomain_type , type , ( human : $tType ) ).
thf( necessitation , conjecture , ( $box @ p ) ).
thf( necessitation , conjecture , ( $box_int @ 1 @ p ) ).
thf( necessitation , conjecture , ( $box_int @ 2 @ p ) ).


