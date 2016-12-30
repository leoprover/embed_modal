%--------------------------------------------------------------------------
% File     : GSV090+1 : QMLTP v1.1
% Domain   : GSV (Goedel translation of Software Verification)             
% Problem  : Goedel translation of SWV090+1 (from TPTP-v5.0.0)      
% Version  : Especial.
% English  :

% Refs     : [TPTP] G. Sutcliffe. TPTP library v2.7.0. http://www.tptp.org 
%            [SS98] G. Sutcliffe, C.B. Suttner. The TPTP Problem Library:  
%                   CNF Release v1.2.1. Journal of Automated Reasoning,    
%                   21(2):177--203, 1998.                                  
%            [Goe69] K. Goedel. An interpretation of the intuitionistic    
%                    sentential logic. In J. Hintikka, Ed., The Philosophy 
%                    of Mathematics, pp~128--129. Oxford University Press, 
%                    1969.                                                 
% Source   : [TPTP], [Goe69]
% Names    :

% Status   :      varying      cumulative   constant   
%             K   Unsolved     Unsolved     Unsolved      v1.1
%             D   Non-Theorem  Non-Theorem  Non-Theorem   v1.1
%             T   Unsolved     Unsolved     Unsolved      v1.1
%             S4  Unsolved     Unsolved     Unsolved      v1.1
%             S5  Unsolved     Unsolved     Unsolved      v1.1
%
% Rating   :      varying      cumulative   constant   
%             K   1.00         1.00         1.00          v1.1
%             D   0.75         0.83         0.83          v1.1
%             T   1.00         1.00         1.00          v1.1
%             S4  1.00         1.00         1.00          v1.1
%             S5  1.00         1.00         1.00          v1.1
%
%  term conditions for all terms: designation: rigid, extension: local
%
% Comments : equality axioms included
%--------------------------------------------------------------------------

qmf(reflexivity,axiom,
( #box : ( ! [X] : (#box : (X = X))))).

qmf(symmetry,axiom,
( #box : ( ! [X] : ( #box : ( ! [Y] : ( #box : ((#box : (X = Y)) => (#box : (Y = X)) ))))))).

qmf(transitivity,axiom,
( #box : ( ! [X] : ( #box : ( ! [Y] : ( #box : ( ! [Z] : ( #box : (( (#box : (X = Y)) & (#box : (Y = Z)) ) => (#box : (X = Z)) ))))))))).

qmf(a_select2_substitution_1,axiom,
( #box : ( ! [A] : ( #box : ( ! [B] : ( #box : ( ! [C] : ( #box : ((#box : (A = B)) => (#box : (a_select2(A, C) = a_select2(B, C))) ))))))))).

qmf(a_select2_substitution_2,axiom,
( #box : ( ! [A] : ( #box : ( ! [B] : ( #box : ( ! [C] : ( #box : ((#box : (A = B)) => (#box : (a_select2(C, A) = a_select2(C, B))) ))))))))).

qmf(a_select3_substitution_1,axiom,
( #box : ( ! [A] : ( #box : ( ! [B] : ( #box : ( ! [C] : ( #box : ( ! [D] : ( #box : ((#box : (A = B)) => (#box : (a_select3(A, C, D) = a_select3(B, C, D))) ))))))))))).

qmf(a_select3_substitution_2,axiom,
( #box : ( ! [A] : ( #box : ( ! [B] : ( #box : ( ! [C] : ( #box : ( ! [D] : ( #box : ((#box : (A = B)) => (#box : (a_select3(C, A, D) = a_select3(C, B, D))) ))))))))))).

qmf(a_select3_substitution_3,axiom,
( #box : ( ! [A] : ( #box : ( ! [B] : ( #box : ( ! [C] : ( #box : ( ! [D] : ( #box : ((#box : (A = B)) => (#box : (a_select3(C, D, A) = a_select3(C, D, B))) ))))))))))).

qmf(dim_substitution_1,axiom,
( #box : ( ! [A] : ( #box : ( ! [B] : ( #box : ( ! [C] : ( #box : ((#box : (A = B)) => (#box : (dim(A, C) = dim(B, C))) ))))))))).

qmf(dim_substitution_2,axiom,
( #box : ( ! [A] : ( #box : ( ! [B] : ( #box : ( ! [C] : ( #box : ((#box : (A = B)) => (#box : (dim(C, A) = dim(C, B))) ))))))))).

qmf(inv_substitution_1,axiom,
( #box : ( ! [A] : ( #box : ( ! [B] : ( #box : ((#box : (A = B)) => (#box : (inv(A) = inv(B))) ))))))).

qmf(minus_substitution_1,axiom,
( #box : ( ! [A] : ( #box : ( ! [B] : ( #box : ( ! [C] : ( #box : ((#box : (A = B)) => (#box : (minus(A, C) = minus(B, C))) ))))))))).

qmf(minus_substitution_2,axiom,
( #box : ( ! [A] : ( #box : ( ! [B] : ( #box : ( ! [C] : ( #box : ((#box : (A = B)) => (#box : (minus(C, A) = minus(C, B))) ))))))))).

qmf(plus_substitution_1,axiom,
( #box : ( ! [A] : ( #box : ( ! [B] : ( #box : ( ! [C] : ( #box : ((#box : (A = B)) => (#box : (plus(A, C) = plus(B, C))) ))))))))).

qmf(plus_substitution_2,axiom,
( #box : ( ! [A] : ( #box : ( ! [B] : ( #box : ( ! [C] : ( #box : ((#box : (A = B)) => (#box : (plus(C, A) = plus(C, B))) ))))))))).

qmf(pred_substitution_1,axiom,
( #box : ( ! [A] : ( #box : ( ! [B] : ( #box : ((#box : (A = B)) => (#box : (pred(A) = pred(B))) ))))))).

qmf(succ_substitution_1,axiom,
( #box : ( ! [A] : ( #box : ( ! [B] : ( #box : ((#box : (A = B)) => (#box : (succ(A) = succ(B))) ))))))).

qmf(sum_substitution_1,axiom,
( #box : ( ! [A] : ( #box : ( ! [B] : ( #box : ( ! [C] : ( #box : ( ! [D] : ( #box : ((#box : (A = B)) => (#box : (sum(A, C, D) = sum(B, C, D))) ))))))))))).

qmf(sum_substitution_2,axiom,
( #box : ( ! [A] : ( #box : ( ! [B] : ( #box : ( ! [C] : ( #box : ( ! [D] : ( #box : ((#box : (A = B)) => (#box : (sum(C, A, D) = sum(C, B, D))) ))))))))))).

qmf(sum_substitution_3,axiom,
( #box : ( ! [A] : ( #box : ( ! [B] : ( #box : ( ! [C] : ( #box : ( ! [D] : ( #box : ((#box : (A = B)) => (#box : (sum(C, D, A) = sum(C, D, B))) ))))))))))).

qmf(tptp_const_array1_substitution_1,axiom,
( #box : ( ! [A] : ( #box : ( ! [B] : ( #box : ( ! [C] : ( #box : ((#box : (A = B)) => (#box : (tptp_const_array1(A, C) = tptp_const_array1(B, C))) ))))))))).

qmf(tptp_const_array1_substitution_2,axiom,
( #box : ( ! [A] : ( #box : ( ! [B] : ( #box : ( ! [C] : ( #box : ((#box : (A = B)) => (#box : (tptp_const_array1(C, A) = tptp_const_array1(C, B))) ))))))))).

qmf(tptp_const_array2_substitution_1,axiom,
( #box : ( ! [A] : ( #box : ( ! [B] : ( #box : ( ! [C] : ( #box : ( ! [D] : ( #box : ((#box : (A = B)) => (#box : (tptp_const_array2(A, C, D) = tptp_const_array2(B, C, D))) ))))))))))).

qmf(tptp_const_array2_substitution_2,axiom,
( #box : ( ! [A] : ( #box : ( ! [B] : ( #box : ( ! [C] : ( #box : ( ! [D] : ( #box : ((#box : (A = B)) => (#box : (tptp_const_array2(C, A, D) = tptp_const_array2(C, B, D))) ))))))))))).

qmf(tptp_const_array2_substitution_3,axiom,
( #box : ( ! [A] : ( #box : ( ! [B] : ( #box : ( ! [C] : ( #box : ( ! [D] : ( #box : ((#box : (A = B)) => (#box : (tptp_const_array2(C, D, A) = tptp_const_array2(C, D, B))) ))))))))))).

qmf(tptp_madd_substitution_1,axiom,
( #box : ( ! [A] : ( #box : ( ! [B] : ( #box : ( ! [C] : ( #box : ((#box : (A = B)) => (#box : (tptp_madd(A, C) = tptp_madd(B, C))) ))))))))).

qmf(tptp_madd_substitution_2,axiom,
( #box : ( ! [A] : ( #box : ( ! [B] : ( #box : ( ! [C] : ( #box : ((#box : (A = B)) => (#box : (tptp_madd(C, A) = tptp_madd(C, B))) ))))))))).

qmf(tptp_mmul_substitution_1,axiom,
( #box : ( ! [A] : ( #box : ( ! [B] : ( #box : ( ! [C] : ( #box : ((#box : (A = B)) => (#box : (tptp_mmul(A, C) = tptp_mmul(B, C))) ))))))))).

qmf(tptp_mmul_substitution_2,axiom,
( #box : ( ! [A] : ( #box : ( ! [B] : ( #box : ( ! [C] : ( #box : ((#box : (A = B)) => (#box : (tptp_mmul(C, A) = tptp_mmul(C, B))) ))))))))).

qmf(tptp_msub_substitution_1,axiom,
( #box : ( ! [A] : ( #box : ( ! [B] : ( #box : ( ! [C] : ( #box : ((#box : (A = B)) => (#box : (tptp_msub(A, C) = tptp_msub(B, C))) ))))))))).

qmf(tptp_msub_substitution_2,axiom,
( #box : ( ! [A] : ( #box : ( ! [B] : ( #box : ( ! [C] : ( #box : ((#box : (A = B)) => (#box : (tptp_msub(C, A) = tptp_msub(C, B))) ))))))))).

qmf(tptp_update2_substitution_1,axiom,
( #box : ( ! [A] : ( #box : ( ! [B] : ( #box : ( ! [C] : ( #box : ( ! [D] : ( #box : ((#box : (A = B)) => (#box : (tptp_update2(A, C, D) = tptp_update2(B, C, D))) ))))))))))).

qmf(tptp_update2_substitution_2,axiom,
( #box : ( ! [A] : ( #box : ( ! [B] : ( #box : ( ! [C] : ( #box : ( ! [D] : ( #box : ((#box : (A = B)) => (#box : (tptp_update2(C, A, D) = tptp_update2(C, B, D))) ))))))))))).

qmf(tptp_update2_substitution_3,axiom,
( #box : ( ! [A] : ( #box : ( ! [B] : ( #box : ( ! [C] : ( #box : ( ! [D] : ( #box : ((#box : (A = B)) => (#box : (tptp_update2(C, D, A) = tptp_update2(C, D, B))) ))))))))))).

qmf(tptp_update3_substitution_1,axiom,
( #box : ( ! [A] : ( #box : ( ! [B] : ( #box : ( ! [C] : ( #box : ( ! [D] : ( #box : ( ! [E] : ( #box : ((#box : (A = B)) => (#box : (tptp_update3(A, C, D, E) = tptp_update3(B, C, D, E))) ))))))))))))).

qmf(tptp_update3_substitution_2,axiom,
( #box : ( ! [A] : ( #box : ( ! [B] : ( #box : ( ! [C] : ( #box : ( ! [D] : ( #box : ( ! [E] : ( #box : ((#box : (A = B)) => (#box : (tptp_update3(C, A, D, E) = tptp_update3(C, B, D, E))) ))))))))))))).

qmf(tptp_update3_substitution_3,axiom,
( #box : ( ! [A] : ( #box : ( ! [B] : ( #box : ( ! [C] : ( #box : ( ! [D] : ( #box : ( ! [E] : ( #box : ((#box : (A = B)) => (#box : (tptp_update3(C, D, A, E) = tptp_update3(C, D, B, E))) ))))))))))))).

qmf(tptp_update3_substitution_4,axiom,
( #box : ( ! [A] : ( #box : ( ! [B] : ( #box : ( ! [C] : ( #box : ( ! [D] : ( #box : ( ! [E] : ( #box : ((#box : (A = B)) => (#box : (tptp_update3(C, D, E, A) = tptp_update3(C, D, E, B))) ))))))))))))).

qmf(trans_substitution_1,axiom,
( #box : ( ! [A] : ( #box : ( ! [B] : ( #box : ((#box : (A = B)) => (#box : (trans(A) = trans(B))) ))))))).

qmf(uniform_int_rnd_substitution_1,axiom,
( #box : ( ! [A] : ( #box : ( ! [B] : ( #box : ( ! [C] : ( #box : ((#box : (A = B)) => (#box : (uniform_int_rnd(A, C) = uniform_int_rnd(B, C))) ))))))))).

qmf(uniform_int_rnd_substitution_2,axiom,
( #box : ( ! [A] : ( #box : ( ! [B] : ( #box : ( ! [C] : ( #box : ((#box : (A = B)) => (#box : (uniform_int_rnd(C, A) = uniform_int_rnd(C, B))) ))))))))).

qmf(geq_substitution_1,axiom,
( #box : ( ! [A] : ( #box : ( ! [B] : ( #box : ( ! [C] : ( #box : (( (#box : (A = B)) & ( #box : (geq(A, C))) ) => ( #box : (geq(B, C))) ))))))))).

qmf(geq_substitution_2,axiom,
( #box : ( ! [A] : ( #box : ( ! [B] : ( #box : ( ! [C] : ( #box : (( (#box : (A = B)) & ( #box : (geq(C, A))) ) => ( #box : (geq(C, B))) ))))))))).

qmf(gt_substitution_1,axiom,
( #box : ( ! [A] : ( #box : ( ! [B] : ( #box : ( ! [C] : ( #box : (( (#box : (A = B)) & ( #box : (gt(A, C))) ) => ( #box : (gt(B, C))) ))))))))).

qmf(gt_substitution_2,axiom,
( #box : ( ! [A] : ( #box : ( ! [B] : ( #box : ( ! [C] : ( #box : (( (#box : (A = B)) & ( #box : (gt(C, A))) ) => ( #box : (gt(C, B))) ))))))))).

qmf(leq_substitution_1,axiom,
( #box : ( ! [A] : ( #box : ( ! [B] : ( #box : ( ! [C] : ( #box : (( (#box : (A = B)) & ( #box : (leq(A, C))) ) => ( #box : (leq(B, C))) ))))))))).

qmf(leq_substitution_2,axiom,
( #box : ( ! [A] : ( #box : ( ! [B] : ( #box : ( ! [C] : ( #box : (( (#box : (A = B)) & ( #box : (leq(C, A))) ) => ( #box : (leq(C, B))) ))))))))).

qmf(lt_substitution_1,axiom,
( #box : ( ! [A] : ( #box : ( ! [B] : ( #box : ( ! [C] : ( #box : (( (#box : (A = B)) & ( #box : (lt(A, C))) ) => ( #box : (lt(B, C))) ))))))))).

qmf(lt_substitution_2,axiom,
( #box : ( ! [A] : ( #box : ( ! [B] : ( #box : ( ! [C] : ( #box : (( (#box : (A = B)) & ( #box : (lt(C, A))) ) => ( #box : (lt(C, B))) ))))))))).

qmf(totality,axiom,
( #box : ( ! [X] : ( #box : ( ! [Y] : ( ( #box : (gt(X, Y))) | ( ( #box : (gt(Y, X))) | (#box : (X = Y)) ) )))))).

qmf(transitivity_gt,axiom,
( #box : ( ! [X] : ( #box : ( ! [Y] : ( #box : ( ! [Z] : ( #box : (( ( #box : (gt(X, Y))) & ( #box : (gt(Y, Z))) ) => ( #box : (gt(X, Z))) ))))))))).

qmf(irreflexivity_gt,axiom,
( #box : ( ! [X] : ( #box : (~ ( #box : (gt(X, X))) ))))).

qmf(reflexivity_leq,axiom,
( #box : ( ! [X] : ( #box : (leq(X, X)))))).

qmf(transitivity_leq,axiom,
( #box : ( ! [X] : ( #box : ( ! [Y] : ( #box : ( ! [Z] : ( #box : (( ( #box : (leq(X, Y))) & ( #box : (leq(Y, Z))) ) => ( #box : (leq(X, Z))) ))))))))).

qmf(lt_gt,axiom,
( #box : ( ! [X] : ( #box : ( ! [Y] : (( #box : (( #box : (lt(X, Y))) => ( #box : (gt(Y, X))) )) & (#box : (( #box : (gt(Y, X))) => ( #box : (lt(X, Y))) )))))))).

qmf(leq_geq,axiom,
( #box : ( ! [X] : ( #box : ( ! [Y] : (( #box : (( #box : (geq(X, Y))) => ( #box : (leq(Y, X))) )) & (#box : (( #box : (leq(Y, X))) => ( #box : (geq(X, Y))) )))))))).

qmf(leq_gt1,axiom,
( #box : ( ! [X] : ( #box : ( ! [Y] : ( #box : (( #box : (gt(Y, X))) => ( #box : (leq(X, Y))) ))))))).

qmf(leq_gt2,axiom,
( #box : ( ! [X] : ( #box : ( ! [Y] : ( #box : (( ( #box : (leq(X, Y))) & ( #box : (~ (#box : (X = Y)) )) ) => ( #box : (gt(Y, X))) ))))))).

qmf(leq_gt_pred,axiom,
( #box : ( ! [X] : ( #box : ( ! [Y] : (( #box : (( #box : (leq(X, pred(Y)))) => ( #box : (gt(Y, X))) )) & (#box : (( #box : (gt(Y, X))) => ( #box : (leq(X, pred(Y)))) )))))))).

qmf(gt_succ,axiom,
( #box : ( ! [X] : ( #box : (gt(succ(X), X)))))).

qmf(leq_succ,axiom,
( #box : ( ! [X] : ( #box : ( ! [Y] : ( #box : (( #box : (leq(X, Y))) => ( #box : (leq(X, succ(Y)))) ))))))).

qmf(leq_succ_gt_equiv,axiom,
( #box : ( ! [X] : ( #box : ( ! [Y] : (( #box : (( #box : (leq(X, Y))) => ( #box : (gt(succ(Y), X))) )) & (#box : (( #box : (gt(succ(Y), X))) => ( #box : (leq(X, Y))) )))))))).

qmf(uniform_int_rand_ranges_hi,axiom,
( #box : ( ! [X] : ( #box : ( ! [C] : ( #box : (( #box : (leq(n0, X))) => ( #box : (leq(uniform_int_rnd(C, X), X))) ))))))).

qmf(uniform_int_rand_ranges_lo,axiom,
( #box : ( ! [X] : ( #box : ( ! [C] : ( #box : (( #box : (leq(n0, X))) => ( #box : (leq(n0, uniform_int_rnd(C, X)))) ))))))).

qmf(const_array1_select,axiom,
( #box : ( ! [I] : ( #box : ( ! [L] : ( #box : ( ! [U] : ( #box : ( ! [Val] : ( #box : (( ( #box : (leq(L, I))) & ( #box : (leq(I, U))) ) => (#box : (a_select2(tptp_const_array1(dim(L, U), Val), I) = Val)) ))))))))))).

qmf(const_array2_select,axiom,
( #box : ( ! [I] : ( #box : ( ! [L1] : ( #box : ( ! [U1] : ( #box : ( ! [J] : ( #box : ( ! [L2] : ( #box : ( ! [U2] : ( #box : ( ! [Val] : ( #box : (( ( #box : (leq(L1, I))) & ( ( #box : (leq(I, U1))) & ( ( #box : (leq(L2, J))) & ( #box : (leq(J, U2))) ) ) ) => (#box : (a_select3(tptp_const_array2(dim(L1, U1), dim(L2, U2), Val), I, J) = Val)) ))))))))))))))))).

qmf(matrix_symm_trans,axiom,
( #box : ( ! [A] : ( #box : ( ! [N] : ( #box : (( #box : ( ! [I] : ( #box : ( ! [J] : ( #box : (( ( #box : (leq(n0, I))) & ( ( #box : (leq(I, N))) & ( ( #box : (leq(n0, J))) & ( #box : (leq(J, N))) ) ) ) => (#box : (a_select3(A, I, J) = a_select3(A, J, I))) )))))) => ( #box : ( ! [I] : ( #box : ( ! [J] : ( #box : (( ( #box : (leq(n0, I))) & ( ( #box : (leq(I, N))) & ( ( #box : (leq(n0, J))) & ( #box : (leq(J, N))) ) ) ) => (#box : (a_select3(trans(A), I, J) = a_select3(trans(A), J, I))) )))))) ))))))).

qmf(matrix_symm_inv,axiom,
( #box : ( ! [A] : ( #box : ( ! [N] : ( #box : (( #box : ( ! [I] : ( #box : ( ! [J] : ( #box : (( ( #box : (leq(n0, I))) & ( ( #box : (leq(I, N))) & ( ( #box : (leq(n0, J))) & ( #box : (leq(J, N))) ) ) ) => (#box : (a_select3(A, I, J) = a_select3(A, J, I))) )))))) => ( #box : ( ! [I] : ( #box : ( ! [J] : ( #box : (( ( #box : (leq(n0, I))) & ( ( #box : (leq(I, N))) & ( ( #box : (leq(n0, J))) & ( #box : (leq(J, N))) ) ) ) => (#box : (a_select3(inv(A), I, J) = a_select3(inv(A), J, I))) )))))) ))))))).

qmf(matrix_symm_update_diagonal,axiom,
( #box : ( ! [A] : ( #box : ( ! [N] : ( #box : (( #box : ( ! [I] : ( #box : ( ! [J] : ( #box : (( ( #box : (leq(n0, I))) & ( ( #box : (leq(I, N))) & ( ( #box : (leq(n0, J))) & ( #box : (leq(J, N))) ) ) ) => (#box : (a_select3(A, I, J) = a_select3(A, J, I))) )))))) => ( #box : ( ! [I] : ( #box : ( ! [J] : ( #box : ( ! [K] : ( #box : ( ! [VAL] : ( #box : (( ( #box : (leq(n0, I))) & ( ( #box : (leq(I, N))) & ( ( #box : (leq(n0, J))) & ( ( #box : (leq(J, N))) & ( ( #box : (leq(n0, K))) & ( #box : (leq(K, N))) ) ) ) ) ) => (#box : (a_select3(tptp_update3(A, K, K, VAL), I, J) = a_select3(tptp_update3(A, K, K, VAL), J, I))) )))))))))) ))))))).

qmf(matrix_symm_add,axiom,
( #box : ( ! [A] : ( #box : ( ! [B] : ( #box : ( ! [N] : ( #box : (( ( #box : ( ! [I] : ( #box : ( ! [J] : ( #box : (( ( #box : (leq(n0, I))) & ( ( #box : (leq(I, N))) & ( ( #box : (leq(n0, J))) & ( #box : (leq(J, N))) ) ) ) => (#box : (a_select3(A, I, J) = a_select3(A, J, I))) )))))) & ( #box : ( ! [I] : ( #box : ( ! [J] : ( #box : (( ( #box : (leq(n0, I))) & ( ( #box : (leq(I, N))) & ( ( #box : (leq(n0, J))) & ( #box : (leq(J, N))) ) ) ) => (#box : (a_select3(B, I, J) = a_select3(B, J, I))) )))))) ) => ( #box : ( ! [I] : ( #box : ( ! [J] : ( #box : (( ( #box : (leq(n0, I))) & ( ( #box : (leq(I, N))) & ( ( #box : (leq(n0, J))) & ( #box : (leq(J, N))) ) ) ) => (#box : (a_select3(tptp_madd(A, B), I, J) = a_select3(tptp_madd(A, B), J, I))) )))))) ))))))))).

qmf(matrix_symm_sub,axiom,
( #box : ( ! [A] : ( #box : ( ! [B] : ( #box : ( ! [N] : ( #box : (( ( #box : ( ! [I] : ( #box : ( ! [J] : ( #box : (( ( #box : (leq(n0, I))) & ( ( #box : (leq(I, N))) & ( ( #box : (leq(n0, J))) & ( #box : (leq(J, N))) ) ) ) => (#box : (a_select3(A, I, J) = a_select3(A, J, I))) )))))) & ( #box : ( ! [I] : ( #box : ( ! [J] : ( #box : (( ( #box : (leq(n0, I))) & ( ( #box : (leq(I, N))) & ( ( #box : (leq(n0, J))) & ( #box : (leq(J, N))) ) ) ) => (#box : (a_select3(B, I, J) = a_select3(B, J, I))) )))))) ) => ( #box : ( ! [I] : ( #box : ( ! [J] : ( #box : (( ( #box : (leq(n0, I))) & ( ( #box : (leq(I, N))) & ( ( #box : (leq(n0, J))) & ( #box : (leq(J, N))) ) ) ) => (#box : (a_select3(tptp_msub(A, B), I, J) = a_select3(tptp_msub(A, B), J, I))) )))))) ))))))))).

qmf(matrix_symm_aba1,axiom,
( #box : ( ! [A] : ( #box : ( ! [B] : ( #box : ( ! [N] : ( #box : (( #box : ( ! [I] : ( #box : ( ! [J] : ( #box : (( ( #box : (leq(n0, I))) & ( ( #box : (leq(I, N))) & ( ( #box : (leq(n0, J))) & ( #box : (leq(J, N))) ) ) ) => (#box : (a_select3(B, I, J) = a_select3(B, J, I))) )))))) => ( #box : ( ! [I] : ( #box : ( ! [J] : ( #box : (( ( #box : (leq(n0, I))) & ( ( #box : (leq(I, N))) & ( ( #box : (leq(n0, J))) & ( #box : (leq(J, N))) ) ) ) => (#box : (a_select3(tptp_mmul(A, tptp_mmul(B, trans(A))), I, J) = a_select3(tptp_mmul(A, tptp_mmul(B, trans(A))), J, I))) )))))) ))))))))).

qmf(matrix_symm_aba2,axiom,
( #box : ( ! [A] : ( #box : ( ! [B] : ( #box : ( ! [N] : ( #box : ( ! [M] : ( #box : (( #box : ( ! [I] : ( #box : ( ! [J] : ( #box : (( ( #box : (leq(n0, I))) & ( ( #box : (leq(I, M))) & ( ( #box : (leq(n0, J))) & ( #box : (leq(J, M))) ) ) ) => (#box : (a_select3(B, I, J) = a_select3(B, J, I))) )))))) => ( #box : ( ! [I] : ( #box : ( ! [J] : ( #box : (( ( #box : (leq(n0, I))) & ( ( #box : (leq(I, N))) & ( ( #box : (leq(n0, J))) & ( #box : (leq(J, N))) ) ) ) => (#box : (a_select3(tptp_mmul(A, tptp_mmul(B, trans(A))), I, J) = a_select3(tptp_mmul(A, tptp_mmul(B, trans(A))), J, I))) )))))) ))))))))))).

qmf(matrix_symm_joseph_update,axiom,
( #box : ( ! [A] : ( #box : ( ! [B] : ( #box : ( ! [C] : ( #box : ( ! [D] : ( #box : ( ! [E] : ( #box : ( ! [F] : ( #box : ( ! [N] : ( #box : ( ! [M] : ( #box : (( ( #box : ( ! [I] : ( #box : ( ! [J] : ( #box : (( ( #box : (leq(n0, I))) & ( ( #box : (leq(I, M))) & ( ( #box : (leq(n0, J))) & ( #box : (leq(J, M))) ) ) ) => (#box : (a_select3(D, I, J) = a_select3(D, J, I))) )))))) & ( ( #box : ( ! [I] : ( #box : ( ! [J] : ( #box : (( ( #box : (leq(n0, I))) & ( ( #box : (leq(I, N))) & ( ( #box : (leq(n0, J))) & ( #box : (leq(J, N))) ) ) ) => (#box : (a_select3(A, I, J) = a_select3(A, J, I))) )))))) & ( #box : ( ! [I] : ( #box : ( ! [J] : ( #box : (( ( #box : (leq(n0, I))) & ( ( #box : (leq(I, N))) & ( ( #box : (leq(n0, J))) & ( #box : (leq(J, N))) ) ) ) => (#box : (a_select3(F, I, J) = a_select3(F, J, I))) )))))) ) ) => ( #box : ( ! [I] : ( #box : ( ! [J] : ( #box : (( ( #box : (leq(n0, I))) & ( ( #box : (leq(I, N))) & ( ( #box : (leq(n0, J))) & ( #box : (leq(J, N))) ) ) ) => (#box : (a_select3(tptp_madd(A, tptp_mmul(B, tptp_mmul(tptp_madd(tptp_mmul(C, tptp_mmul(D, trans(C))), tptp_mmul(E, tptp_mmul(F, trans(E)))), trans(B)))), I, J) = a_select3(tptp_madd(A, tptp_mmul(B, tptp_mmul(tptp_madd(tptp_mmul(C, tptp_mmul(D, trans(C))), tptp_mmul(E, tptp_mmul(F, trans(E)))), trans(B)))), J, I))) )))))) ))))))))))))))))))).

qmf(sum_plus_base,axiom,
( #box : ( ! [Body] : (#box : (sum(n0, tptp_minus_1, Body) = n0))))).

qmf(sum_plus_base_float,axiom,
( #box : ( ! [Body] : (#box : (tptp_float_0_0 = sum(n0, tptp_minus_1, Body)))))).

qmf(succ_tptp_minus_1,axiom,
(#box : (succ(tptp_minus_1) = n0))).

qmf(succ_plus_1_r,axiom,
( #box : ( ! [X] : (#box : (plus(X, n1) = succ(X)))))).

qmf(succ_plus_1_l,axiom,
( #box : ( ! [X] : (#box : (plus(n1, X) = succ(X)))))).

qmf(succ_plus_2_r,axiom,
( #box : ( ! [X] : (#box : (plus(X, n2) = succ(succ(X))))))).

qmf(succ_plus_2_l,axiom,
( #box : ( ! [X] : (#box : (plus(n2, X) = succ(succ(X))))))).

qmf(succ_plus_3_r,axiom,
( #box : ( ! [X] : (#box : (plus(X, n3) = succ(succ(succ(X)))))))).

qmf(succ_plus_3_l,axiom,
( #box : ( ! [X] : (#box : (plus(n3, X) = succ(succ(succ(X)))))))).

qmf(succ_plus_4_r,axiom,
( #box : ( ! [X] : (#box : (plus(X, n4) = succ(succ(succ(succ(X))))))))).

qmf(succ_plus_4_l,axiom,
( #box : ( ! [X] : (#box : (plus(n4, X) = succ(succ(succ(succ(X))))))))).

qmf(succ_plus_5_r,axiom,
( #box : ( ! [X] : (#box : (plus(X, n5) = succ(succ(succ(succ(succ(X)))))))))).

qmf(succ_plus_5_l,axiom,
( #box : ( ! [X] : (#box : (plus(n5, X) = succ(succ(succ(succ(succ(X)))))))))).

qmf(pred_minus_1,axiom,
( #box : ( ! [X] : (#box : (minus(X, n1) = pred(X)))))).

qmf(pred_succ,axiom,
( #box : ( ! [X] : (#box : (pred(succ(X)) = X))))).

qmf(succ_pred,axiom,
( #box : ( ! [X] : (#box : (succ(pred(X)) = X))))).

qmf(leq_succ_succ,axiom,
( #box : ( ! [X] : ( #box : ( ! [Y] : (( #box : (( #box : (leq(succ(X), succ(Y)))) => ( #box : (leq(X, Y))) )) & (#box : (( #box : (leq(X, Y))) => ( #box : (leq(succ(X), succ(Y)))) )))))))).

qmf(leq_succ_gt,axiom,
( #box : ( ! [X] : ( #box : ( ! [Y] : ( #box : (( #box : (leq(succ(X), Y))) => ( #box : (gt(Y, X))) ))))))).

qmf(leq_minus,axiom,
( #box : ( ! [X] : ( #box : ( ! [Y] : ( #box : (( #box : (leq(minus(X, Y), X))) => ( #box : (leq(n0, Y))) ))))))).

qmf(sel3_update_1,axiom,
( #box : ( ! [X] : ( #box : ( ! [U] : ( #box : ( ! [V] : ( #box : ( ! [VAL] : (#box : (a_select3(tptp_update3(X, U, V, VAL), U, V) = VAL))))))))))).

qmf(sel3_update_2,axiom,
( #box : ( ! [I] : ( #box : ( ! [J] : ( #box : ( ! [U] : ( #box : ( ! [V] : ( #box : ( ! [X] : ( #box : ( ! [VAL] : ( #box : ( ! [VAL2] : ( #box : (( ( #box : (~ (#box : (I = U)) )) & ( (#box : (J = V)) & (#box : (a_select3(X, U, V) = VAL)) ) ) => (#box : (a_select3(tptp_update3(X, I, J, VAL2), U, V) = VAL)) ))))))))))))))))).

qmf(sel3_update_3,axiom,
( #box : ( ! [I] : ( #box : ( ! [J] : ( #box : ( ! [U] : ( #box : ( ! [V] : ( #box : ( ! [X] : ( #box : ( ! [VAL] : ( #box : (( ( #box : ( ! [I0] : ( #box : ( ! [J0] : ( #box : (( ( #box : (leq(n0, I0))) & ( ( #box : (leq(n0, J0))) & ( ( #box : (leq(I0, U))) & ( #box : (leq(J0, V))) ) ) ) => (#box : (a_select3(X, I0, J0) = VAL)) )))))) & ( ( #box : (leq(n0, I))) & ( ( #box : (leq(I, U))) & ( ( #box : (leq(n0, J))) & ( #box : (leq(J, V))) ) ) ) ) => (#box : (a_select3(tptp_update3(X, U, V, VAL), I, J) = VAL)) ))))))))))))))).

qmf(sel2_update_1,axiom,
( #box : ( ! [X] : ( #box : ( ! [U] : ( #box : ( ! [VAL] : (#box : (a_select2(tptp_update2(X, U, VAL), U) = VAL))))))))).

qmf(sel2_update_2,axiom,
( #box : ( ! [I] : ( #box : ( ! [U] : ( #box : ( ! [X] : ( #box : ( ! [VAL] : ( #box : ( ! [VAL2] : ( #box : (( ( #box : (~ (#box : (I = U)) )) & (#box : (a_select2(X, U) = VAL)) ) => (#box : (a_select2(tptp_update2(X, I, VAL2), U) = VAL)) ))))))))))))).

qmf(sel2_update_3,axiom,
( #box : ( ! [I] : ( #box : ( ! [U] : ( #box : ( ! [X] : ( #box : ( ! [VAL] : ( #box : (( ( #box : ( ! [I0] : ( #box : (( ( #box : (leq(n0, I0))) & ( #box : (leq(I0, U))) ) => (#box : (a_select2(X, I0) = VAL)) )))) & ( ( #box : (leq(n0, I))) & ( #box : (leq(I, U))) ) ) => (#box : (a_select2(tptp_update2(X, U, VAL), I) = VAL)) ))))))))))).

qmf(ttrue,axiom,
( #box : (true))).

qmf(defuse,axiom,
( #box : (~ (#box : (def = use)) ))).

qmf(quaternion_ds1_inuse_0002,conjecture,
( #box : (( (#box : (a_select2(rho_defuse, n0) = use)) & ( (#box : (a_select2(rho_defuse, n1) = use)) & ( (#box : (a_select2(rho_defuse, n2) = use)) & ( (#box : (a_select2(sigma_defuse, n0) = use)) & ( (#box : (a_select2(sigma_defuse, n1) = use)) & ( (#box : (a_select2(sigma_defuse, n2) = use)) & ( (#box : (a_select2(sigma_defuse, n3) = use)) & ( (#box : (a_select2(sigma_defuse, n4) = use)) & ( (#box : (a_select2(sigma_defuse, n5) = use)) & ( (#box : (a_select3(u_defuse, n0, n0) = use)) & ( (#box : (a_select3(u_defuse, n1, n0) = use)) & ( (#box : (a_select3(u_defuse, n2, n0) = use)) & ( (#box : (a_select2(xinit_defuse, n3) = use)) & ( (#box : (a_select2(xinit_defuse, n4) = use)) & (#box : (a_select2(xinit_defuse, n5) = use)) ) ) ) ) ) ) ) ) ) ) ) ) ) ) => ( (#box : (a_select2(rho_defuse, n0) = use)) & ( (#box : (a_select2(rho_defuse, n1) = use)) & ( (#box : (a_select2(rho_defuse, n2) = use)) & ( (#box : (a_select2(sigma_defuse, n0) = use)) & ( (#box : (a_select2(sigma_defuse, n1) = use)) & ( (#box : (a_select2(sigma_defuse, n2) = use)) & ( (#box : (a_select2(sigma_defuse, n3) = use)) & ( (#box : (a_select2(sigma_defuse, n4) = use)) & ( (#box : (a_select2(sigma_defuse, n5) = use)) & ( (#box : (a_select3(u_defuse, n0, n0) = use)) & ( (#box : (a_select3(u_defuse, n1, n0) = use)) & ( (#box : (a_select3(u_defuse, n2, n0) = use)) & ( (#box : (a_select2(xinit_defuse, n3) = use)) & ( (#box : (a_select2(xinit_defuse, n4) = use)) & ( (#box : (a_select2(xinit_defuse, n5) = use)) & ( (#box : (a_select2(tptp_update2(tptp_update2(tptp_update2(tptp_update2(tptp_update2(tptp_update2(xinit_mean_defuse, n0, use), n1, use), n2, use), n3, use), n4, use), n5, use), n0) = use)) & ( (#box : (a_select2(tptp_update2(tptp_update2(tptp_update2(tptp_update2(tptp_update2(tptp_update2(xinit_mean_defuse, n0, use), n1, use), n2, use), n3, use), n4, use), n5, use), n1) = use)) & ( (#box : (a_select2(tptp_update2(tptp_update2(tptp_update2(tptp_update2(tptp_update2(tptp_update2(xinit_mean_defuse, n0, use), n1, use), n2, use), n3, use), n4, use), n5, use), n2) = use)) & ( (#box : (a_select2(tptp_update2(tptp_update2(tptp_update2(tptp_update2(tptp_update2(tptp_update2(xinit_mean_defuse, n0, use), n1, use), n2, use), n3, use), n4, use), n5, use), n3) = use)) & ( (#box : (a_select2(tptp_update2(tptp_update2(tptp_update2(tptp_update2(tptp_update2(tptp_update2(xinit_mean_defuse, n0, use), n1, use), n2, use), n3, use), n4, use), n5, use), n4) = use)) & (#box : (a_select2(tptp_update2(tptp_update2(tptp_update2(tptp_update2(tptp_update2(tptp_update2(xinit_mean_defuse, n0, use), n1, use), n2, use), n3, use), n4, use), n5, use), n5) = use)) ) ) ) ) ) ) ) ) ) ) ) ) ) ) ) ) ) ) ) ) ))).

qmf(gt_5_4,axiom,
( #box : (gt(n5, n4)))).

qmf(gt_4_tptp_minus_1,axiom,
( #box : (gt(n4, tptp_minus_1)))).

qmf(gt_5_tptp_minus_1,axiom,
( #box : (gt(n5, tptp_minus_1)))).

qmf(gt_0_tptp_minus_1,axiom,
( #box : (gt(n0, tptp_minus_1)))).

qmf(gt_1_tptp_minus_1,axiom,
( #box : (gt(n1, tptp_minus_1)))).

qmf(gt_2_tptp_minus_1,axiom,
( #box : (gt(n2, tptp_minus_1)))).

qmf(gt_3_tptp_minus_1,axiom,
( #box : (gt(n3, tptp_minus_1)))).

qmf(gt_4_0,axiom,
( #box : (gt(n4, n0)))).

qmf(gt_5_0,axiom,
( #box : (gt(n5, n0)))).

qmf(gt_1_0,axiom,
( #box : (gt(n1, n0)))).

qmf(gt_2_0,axiom,
( #box : (gt(n2, n0)))).

qmf(gt_3_0,axiom,
( #box : (gt(n3, n0)))).

qmf(gt_4_1,axiom,
( #box : (gt(n4, n1)))).

qmf(gt_5_1,axiom,
( #box : (gt(n5, n1)))).

qmf(gt_2_1,axiom,
( #box : (gt(n2, n1)))).

qmf(gt_3_1,axiom,
( #box : (gt(n3, n1)))).

qmf(gt_4_2,axiom,
( #box : (gt(n4, n2)))).

qmf(gt_5_2,axiom,
( #box : (gt(n5, n2)))).

qmf(gt_3_2,axiom,
( #box : (gt(n3, n2)))).

qmf(gt_4_3,axiom,
( #box : (gt(n4, n3)))).

qmf(gt_5_3,axiom,
( #box : (gt(n5, n3)))).

qmf(finite_domain_4,axiom,
( #box : ( ! [X] : ( #box : (( ( #box : (leq(n0, X))) & ( #box : (leq(X, n4))) ) => ( (#box : (X = n0)) | ( (#box : (X = n1)) | ( (#box : (X = n2)) | ( (#box : (X = n3)) | (#box : (X = n4)) ) ) ) ) ))))).

qmf(finite_domain_5,axiom,
( #box : ( ! [X] : ( #box : (( ( #box : (leq(n0, X))) & ( #box : (leq(X, n5))) ) => ( (#box : (X = n0)) | ( (#box : (X = n1)) | ( (#box : (X = n2)) | ( (#box : (X = n3)) | ( (#box : (X = n4)) | (#box : (X = n5)) ) ) ) ) ) ))))).

qmf(finite_domain_0,axiom,
( #box : ( ! [X] : ( #box : (( ( #box : (leq(n0, X))) & ( #box : (leq(X, n0))) ) => (#box : (X = n0)) ))))).

qmf(finite_domain_1,axiom,
( #box : ( ! [X] : ( #box : (( ( #box : (leq(n0, X))) & ( #box : (leq(X, n1))) ) => ( (#box : (X = n0)) | (#box : (X = n1)) ) ))))).

qmf(finite_domain_2,axiom,
( #box : ( ! [X] : ( #box : (( ( #box : (leq(n0, X))) & ( #box : (leq(X, n2))) ) => ( (#box : (X = n0)) | ( (#box : (X = n1)) | (#box : (X = n2)) ) ) ))))).

qmf(finite_domain_3,axiom,
( #box : ( ! [X] : ( #box : (( ( #box : (leq(n0, X))) & ( #box : (leq(X, n3))) ) => ( (#box : (X = n0)) | ( (#box : (X = n1)) | ( (#box : (X = n2)) | (#box : (X = n3)) ) ) ) ))))).

qmf(successor_4,axiom,
(#box : (succ(succ(succ(succ(n0)))) = n4))).

qmf(successor_5,axiom,
(#box : (succ(succ(succ(succ(succ(n0))))) = n5))).

qmf(successor_1,axiom,
(#box : (succ(n0) = n1))).

qmf(successor_2,axiom,
(#box : (succ(succ(n0)) = n2))).

qmf(successor_3,axiom,
(#box : (succ(succ(succ(n0))) = n3))).


%--------------------------------------------------------------------------