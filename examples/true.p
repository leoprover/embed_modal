thf(simple_s5,logic,(
    $modal :=
        [ $constants := $rigid,
            $quantification := $decreasing,
            $consequence := $local,
            $modalities := $modal_system_S5U
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


thf(decre_syntactic__o__d_i_t__o_mworld_t__d_o_c__c_,axiom,((! [ P : (($i>(mworld>$o))>(mworld>$o)) ] :
(mvalid@((mimplies@((mforall_vary__o__d_i_t__o_mworld_t__d_o_c__c_@(^ [ X : ($i>(mworld>$o)) ] :
((mbox@(P@X))))))@((mbox@(mforall_vary__o__d_i_t__o_mworld_t__d_o_c__c_@(^ [ X : ($i>(mworld>$o)) ] : ((P@X)))))))))))).

thf(decre_syntactic__o__d_i_c_,axiom,((! [ P : ($i>(mworld>$o)) ] :
(mvalid@((mimplies@((mforall_vary__o__d_i_c_@(^ [ X : ($i) ] :
((mbox@(P@X))))))@((mbox@(mforall_vary__o__d_i_c_@(^ [ X : ($i) ] : ((P@X)))))))))))).