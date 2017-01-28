thf( simple_s5 , logic , ( $modal := [
    $constants := $rigid ,
    $quantification := $constant ,
    $consequence := $global ,
    $modalities := $modal_system_S5 ] ) ).
% semantics
% modalities

% propositions

% individual constants
thf(seventy_type,type,(seventy : ($i))).
thf(ninetyfive_type,type,(ninetyfive : ($i))).
thf(second_type,type,(second : ($i))).
thf(paris_type,type,(paris : ($i))).
thf(first_type,type,(first : ($i))).

% predicates
thf(price_type,type,(price : ($i>$o))).
thf(dest_type,type,(dest : ($i>$o))).
thf(class_type,type,(class : ($i>$o))).

% functions

% converted problem
%--------------------------------------------------------------------------
% File     : APM001+1 : QMLTP v1.1
% Domain   : Applications mixed
% Problem  : Belief Change in man-machine-dialogues
% Version  : Especial.
% English  :
% Refs     : [FHL+98] L. Farinas del Cerro, A. Herzig, D. Longin, O. Rifi.
%             Belief Reconstruction in Cooperative Dialogues. AIMSA 1998,
%             LNCS 1480, pp. 254-266. Springer, 1998.
% Source   : [FHL98]
% Names    :
% Status   :      varying      cumulative   constant
%             K   Theorem      Theorem      Theorem       v1.1
%             D   Theorem      Theorem      Theorem       v1.1
%             T   Theorem      Theorem      Theorem       v1.1
%             S4  Theorem      Theorem      Theorem       v1.1
%             S5  Theorem      Theorem      Theorem       v1.1
%
% Rating   :      varying      cumulative   constant
%             K   0.00         0.00         0.00          v1.1
%             D   0.00         0.17         0.17          v1.1
%             T   0.00         0.00         0.00          v1.1
%             S4  0.00         0.00         0.00          v1.1
%             S5  0.00         0.00         0.00          v1.1
%
%  term conditions for all terms: designation: rigid, extension: local
%
% Comments :
%--------------------------------------------------------------------------
thf ( law1 , axiom , ( $box @ ( ( ( dest @ paris ) & ( class @ first ) ) => ( price @ ninetyfive ) ) ) ) .
thf ( law2 , axiom , ( $box @ ( ( ( dest @ paris ) & ( class @ second ) ) => ( price @ seventy ) ) ) ) .
thf ( law3 , axiom , ( $box @ ( ~ ( ( ( class @ first ) & ( class @ second ) ) ) ) ) ) .
thf ( law4 , axiom , ( $box @ ( ~ ( ( ( price @ ninetyfive ) & ( price @ seventy ) ) ) ) ) ) .
thf ( belief1 , axiom , ( $box @ ( ( dest @ paris ) ) ) ) .
thf ( belief2 , axiom , ( $box @ ( ( class @ second ) ) ) ) .
thf ( con , conjecture , ( $box @ ( ( price @ seventy ) ) ) ) .