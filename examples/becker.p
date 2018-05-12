thf(simple_s5,logic,(
    $modal := 
      [ $constants := $rigid, 
        $quantification := $constant, 
        $consequence := $global, 
        $modalities := $modal_system_S5 ] )).

thf(1,conjecture,(
    ! [P: ( $i > $o ),F: ( $i > $i ),X: $i] :
    ? [Q: ( $i > $i )] :
      ( ( $dia @ ( $box @ ( P @ ( F @ X ) ) ) )
     => ( $box @ ( P @ ( Q @ X ) ) ) ) )).
