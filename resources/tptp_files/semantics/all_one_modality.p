%----Standard S5
thf(simple_s5,logic,( $modal :=
    [$constants := $rigid,  %----Default not in a list
     $quantification := $constant,
     $consequence := [$global],  %----Default in a list
     $modalities := $modal_system_S5] )).

%----Standard S5 v2
thf(simple_s5_v2,logic,( [$modal := %----Modal system is part of a list
    [$constants := $rigid,
     $quantification := $constant,
     $consequence := $global,
     $modalities := [$modal_system_S5]]] )). %----Default in a list

%----Standard S5 v3
thf(simple_s5_v3,logic,( $modal :=
    [$constants := $rigid,
     $quantification := $constant,
     $consequence := $global,
     $modalities := [$modal_axiom_K, $modal_axiom_T, $modal_axiom_5]] )).
%----Default modality S5 as list of axioms K + T + 5

%----Constants The constant king_of_france has special semantics
thf(human_type,type,(human:$tType)).
thf(king_of_france_constant,type,king_of_france:human).
thf(my_logic_setting,logic,($modal :=
    [$constants := [$constant, king_of_france := $flexible],
     $quantification := $constant,
     $consequence := $global,
     $modalities := $modal_system_S5] )).

%----Quantification may be different for any base type or compound type e.g.
%----for type plushie
thf(plushie_type,type,plushie:$tType).
thf(my_logic_setting,logic,( $modal :=
    [$constants := $rigid,
     $quantification := [$constant, plushie := $varying],
     $consequence := $global,
     $modalities := $modal_system_S5] )).

%----Consequence. All axioms same consequence except for ax1 which has a
%----special consequence
thf(ax1,axiom,$true).
thf(my_logic_setting,logic,( $modal :=
    [$constants := $rigid,
     $quantification := $constant,
     $consequence := [$default := $global, ax1 := $local],
     $modalities := $modal_system_S5] )).
