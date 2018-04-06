thf( simple_s5 , logic , ( $modal := [
    $constants := $rigid ,
    $quantification := $constant ,
    $consequence := $local ,
    $modalities := [$modal_system_D] ] ) ).

thf(a_type,type,(a : ($i>$o))).
thf(b_type,type,(b : ($i>$o))).

%--------------------------------------------------------------------------
% File     : SYM026+1 : QMLTP v1.1
% Domain   : Syntactic (modal)
% Problem  :
% Version  : Especial.
% English  :
% Refs     : [FM98] M. Fitting, R. L. Mendelsohn. First-Order Modal Logic.
%            Kluwer, 1998.
% Source   : [FM98]
% Names    :
% Status   :      varying      cumulative   constant
%             K   Non-Theorem  Non-Theorem  Non-Theorem   v1.1
%             D   Non-Theorem  Non-Theorem  Non-Theorem   v1.1
%             T   Non-Theorem  Non-Theorem  Non-Theorem   v1.1
%             S4  Non-Theorem  Non-Theorem  Non-Theorem   v1.1
%             S5  Non-Theorem  Non-Theorem  Non-Theorem   v1.1
%
% Rating   :      varying      cumulative   constant
%             K   0.50         0.75         0.75          v1.1
%             D   0.75         0.83         0.83          v1.1
%             T   0.50         0.67         0.67          v1.1
%             S4  0.50         0.67         0.67          v1.1
%             S5  0.50         0.60         0.60          v1.1
%
%  term conditions for all terms: designation: rigid, extension: local
%
% Comments :
%--------------------------------------------------------------------------
thf ( con , conjecture , ( ( ( $dia @ ( ! [ X :$i ] : ( a @ X ) ) ) & ( $box @ ( ? [ X :$i ] : ( ( b @ X ) ) ) ) ) => ( $box @ ( ? [ X :$i ] : ( ( a @ X ) & ( b @ X ) ) ) ) ) ) .