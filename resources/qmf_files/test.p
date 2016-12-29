--------------------------------------------------------

qmf(ax1, axiom,
(#box : (! [S,V] : (? [O] : ((#box : ((closed(S) & combo(S,V) & h(O)) => (#box : (open(S))))) &
                            (#box : ((closed(S) & (~ combo(S,V)) & h(o)) => (#box : (closed(S)))))))))).

qmf(ax2, axiom,
(#box : (closed(d)))).

qmf(ax3, axiom,
(#box : (combo(d,n) | (~ combo(d,n))))).

qmf(ax4,axiom,
(#box: (! [S] : (~ (open(S) & closed(S)))))).

qmf(ax5,axiom,
(? [V] : (#box : (combo(d,V))))).

qmf(con, conjecture,
(#box : (? [V,O] : ((#box : (combo(d,V) & h(O))) => (#box : (open(d))))))).