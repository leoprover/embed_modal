grammar Qmf;

// ----v6.4.0.0 (TPTP version.internal development number)
// ----v6.4.0.1 Noted that numbers may not be used in CNF or FOF.
// ----v6.4.0.2 Added tcf
// ----v6.4.0.3 Moved ^ into TH0_QUANTIFIER
// ----v6.4.0.4 Changed thf_top_level_type and thf_unitary_type to be more
// ----         precise. thf_binary_type removed from thf_binary_formula.
// ----v6.4.0.5 Specified that the quantification in a $let must be universal.
// ----         Added ()s around the thf_let_plain_defn in a
// ----         thf_let_quantified_defn (to avoid binding ambiguity).
// ------------------------------------------------------------------------------
// ----README ... this header provides important meta- and usage information
// ----
// ----Intended uses of the various parts of the TPTP syntax are explained
// ----in the TPTP technical manual, linked from www.tptp.org.
// ----
// ----Four kinds of separators are used, to indicate different types of rules:
// ----  : is used for regular grammar rules, for syntactic parsing.
// ----  : is used for semantic grammar rules. These define specific values
// ----      that make semantic sense when more general syntactic rules apply.
// ----  : is used for rules that produce tokens.
// ----  : is used for rules that define character classes used in the
// ----       construction of tokens.
// ----
// ----White space may occur between any two tokens. White space is not specified
// ----in the grammar, but there are some restrictions to ensure that the grammar
// ----is compatible with standard Prolog: a TPTP_file should be readable with
// ----read/1.
// ----
// ----The syntax of comments is defined by the comment rule. Comments may
// ----occur between any two tokens, but do not act as white space. Comments
// ----will normally be discarded at the lexical level, but may be processed
// ----by systems that understand them (e.g., if the system comment convention
// ----is followed).
// ----
// ----Four languages are defined first - THF, TFF, FOF, and CNF. Depending on
// ----your need, you can implement just the one(s) you need. The common rules
// ----for atoms, terms, etc, come after the modalSymbolDefinitions of the languages, and
// ----they are all needed for all four languages (except for the core THF0,
// ----which is a defined subset of THF).
// ----Top of Page---------------------------------------------------------------
// ----Files. Empty file is OK.

// TODO
// numbers
// comments
// annotations
// tpi
// restructure semantics grammar2
// include
// remove last cases from rules using defined expressions only (and check its actual usage before) e.g. remove lower_word from formula_role
// restructure lexer rules especially from middle section and end, maybe move all lexer rules to the end
// check whitespace usages for whole grammar, tptp requires whitespaces at some points which is excluded from this grammar but could be introduced
// check for antlr's fragment applicability on lexer rules e.g. SQ_CHAR and DO_CHAR

// ==============================================================================
// FILE LAYER
// ==============================================================================


tPTP_file            : tPTP_input*
                     ;
tPTP_input           : annotated_formula
                     | tpi_semantics
                     | include
                     | comment
                     ;
include              : 'include' L_PAREN .*? R_PAREN '.' // SIMPLIFIED INCLUDE // TODO original include: include(<file_name><formula_selection>).
                     ;
comment              : COMMENT
                     ;

// ==============================================================================
// SUPPORTED FORMATS
// ==============================================================================


annotated_formula           : qmf_annotated
                            ;

// ==============================================================================
// TPI GENERAL RULES
// ==============================================================================
// TODO add TPI


tpi_semantics : 'tpi' L_PAREN name COMMA formula_role COMMA tpi_sem_formula R_PAREN '.';
tpi_sem_formula: 'modal' L_PAREN L_SQUARE_BRACKET modal_keyword_list R_SQUARE_BRACKET ',' L_SQUARE_BRACKET modality_pair_list R_SQUARE_BRACKET R_PAREN;

modal_keyword_list: modal_keyword | modal_keyword ',' modal_keyword_list;
modal_keyword : lower_word;
//MODAL_KEYWORD : 'cumulative'|'varying'|'constant'|'decreasing'
//               |'rigid'|'flexible'
//               |'global'|'local';
modality_pair_list : modality_pair | modality_pair ',' modality_pair_list;
modality_pair: L_PAREN fof_modal_identifier ',' modal_system R_PAREN;
modal_system: lower_word;
fof_modal_identifier : lower_word;


/*
tpi_annotated               : 'tpi' L_PAREN name COMMA formula_role COMMA tpi_formula R_PAREN '.' // annotation is not supported
                            ;
tpi_formula                 : modal_tpi_formula_setlogic
                            | tpi_formula_other
                            ;
tpi_formula_other           : .*? // SIMPLIFIED OTHER TPI INSTRUCTIONS
                            ;
                            */
//annotations                 : COMMA .*? // SIMPLIFIED ANNOTATIONS // <source> <optional_info> | <null>
//                            ;

// todo annotation support
qmf_annotated               : 'qmf' L_PAREN name COMMA formula_role COMMA fof_formula R_PAREN '.' // annotation is not supported
//qmf_annotated               : 'qmf' L_PAREN name COMMA formula_role COMMA  R_PAREN '.' // annotation is not supported
                            ;

formula_role                : lower_word
                            ;


//----Top of Page---------------------------------------------------------------
//----FOF formulae.

fof_formula          : fof_logic_formula; //| fof_sequent;
fof_logic_formula    : fof_binary_formula | fof_unitary_formula | fof_modal | fof_multimodal;

fof_modal: MODAL_OPERATOR ':' fof_modal_body;
fof_multimodal : MODAL_OPERATOR L_PAREN fof_modal_identifier R_PAREN ':' fof_modal_body;
fof_modal_body : L_PAREN fof_logic_formula R_PAREN
         //| MODAL_OPERATOR ':' fof_logic_formula;
         | constant
         | defined_constant
         | system_constant
         | plain_term;
MODAL_OPERATOR : '#box'|'#dia';

//----Future answer variable ideas | <answer_formula
fof_binary_formula   : fof_binary_nonassoc | fof_binary_assoc;
//----Only some binary connectives are associative
//----There's no precedence among binary connectives
fof_binary_nonassoc  : fof_unitary_formula BINARY_CONNECTIVE fof_unitary_formula;
//----Associative connectives & and | are in binary_assoc;

fof_binary_assoc     : fof_or_formula | fof_and_formula;
fof_or_formula       : fof_unitary_formula VLINE fof_unitary_formula |
                           fof_or_formula VLINE fof_unitary_formula;
fof_and_formula      : fof_unitary_formula '&' fof_unitary_formula |
                           fof_and_formula '&' fof_unitary_formula;
//----fof_unitary_formula are in ()s or do not have a binary_connective> at
//----the top level.
fof_unitary_formula  : fof_quantified_formula | fof_unary_formula |
                           atomic_formula | L_PAREN fof_logic_formula R_PAREN;

fof_quantified_formula : FOL_QUANTIFIER L_SQUARE_BRACKET fof_variable_list R_SQUARE_BRACKET ':' fof_unitary_formula;
fof_variable_list    : variable | variable ',' fof_variable_list;
fof_unary_formula    : UNARY_CONNECTIVE fof_unitary_formula | fol_infix_unary;

fol_infix_unary      : term INFIX_INEQUALITY term;

atomic_formula       : plain_atomic_formula | defined_atomic_formula |
                           system_atomic_formula;
plain_atomic_formula : plain_term;
defined_atomic_formula : defined_plain_formula | defined_infix_formula;
defined_plain_formula : defined_plain_term;

system_atomic_formula : system_term;
defined_infix_formula : term defined_infix_pred term;
defined_infix_pred   : INFIX_EQUALITY;

term                 : function_term | variable; // | conditional_term | let_term;
function_term        : plain_term | defined_term | system_term;
plain_term           : constant | functor L_PAREN arguments R_PAREN;
constant             : functor;
functor              : atomic_word;
//----Defined terms have TPTP specific interpretations
defined_term         : defined_atom | defined_atomic_term;
//----<numbers> may not be used on CNF and FOF
defined_atom         : distinct_object; //number;
defined_atomic_term  : defined_plain_term;
//----None yet             | defined_infix_term
//----None yet defined_infix_term ::= <term defined_infix_func> <term
//----None yet defined_infix_func> ::=
defined_plain_term   : defined_constant | defined_functor L_PAREN arguments R_PAREN;
defined_constant     : defined_functor;
defined_functor      : atomic_defined_word;

system_term          : system_constant | system_functor L_PAREN arguments R_PAREN;
system_constant      : system_functor;
system_functor       : atomic_system_word;
//----Variables, and only variables, start with uppercase
variable             : upper_word;
//----Let terms should be used by only TFF. $let_ft is for use when there is
//----a $ite_t in the <term>. See the commentary for $let_tf and $let_ff.
//----Arguments recurse back up to terms (this is the FOF world here)
arguments            : term | term ',' arguments;


ASSIGNMENT                  : ':='
                            ;
FOL_QUANTIFIER              : '!'
                            | '?'
                            ;
BINARY_CONNECTIVE           : '<=>'
                            | '=>'
                            | '<='
                            | '<~>'
                            | '~' VLINE
                            | '~&'
                            ;
assoc_connective            : VLINE
                            | '&'
                            ;
UNARY_CONNECTIVE            : '~'
                            ;

// ----The seqent arrow

GENTZEN_ARROW               : '-->'
                            ;

INFIX_INEQUALITY            : '!='
                            ;
INFIX_EQUALITY              : '='
                            ;




// ============================================================================
// GENERAL PURPOSE STUFF e.g. words
// formula_role should be placed here aswell
// ============================================================================

// ----General purpose
name                        : atomic_word
                            | integer
                            ;

// ----Integer names are expected to be unsigned

atomic_word                 : lower_word
                            | single_quoted
                            ;

// ----SINGLE_QUOTED tokens do not include their outer quotes, therefore the
// ----LOWER_WORD atomic_word cat and the SINGLE_QUOTED atomic_word 'cat'
// ----are the same. Quotes must be removed from a SINGLE_QUOTED atomic_word
// ----if doing so produces a LOWER_WORD atomic_word. Note that numberss
// ----and variables are not LOWER_WORDs, so '123' and 123, and 'X' and X,
// ----are different.

atomic_defined_word         : DOLLAR_WORD
                            ;
atomic_system_word          : DOLLAR_DOLLAR_WORD
                            ;

// ----Numbers are always interpreted as themselves, and are thus implicitly
// ----distinct if they have different values, e.g., 1 != 2 is an implicit axiom.
// ----All numbers are base 10 at the moment.

file_name                   : single_quoted
                            ;
//null                 : '' LEFT OUT, CANNOT BE EMPTY

// ----Top of Page---------------------------------------------------------------
// ----Rules from here on down are for defining tokens (terminal symbols) of the
// ----grammar, assuming they will be recognized by a lexical scanner.
// ----A : rule defines a token, a : rule defines a macro that is not a
// ----token. Usual regexp notation is used. Single characters are always placed
// ----in []s to disable any special meanings (for uniformity this is done to
// ----all characters, not only those with special meanings).

// ----These are tokens that appear in the syntax rules above. No rules
// ----defined here because they appear explicitly in the syntax rules,
// ----except that VLINE, star, PLUS denote "|", "*", "+", respectively.
// ----Keywords:    fof cnf thf tff include
// ----Punctuation: ( ) , . [ ] :
// ----Operators:   ! ? ~ & | = = = ~ ~| ~& * +
// ----Predicates:  = != $true $false


// ----For lex/yacc there cannot be spaces on either side of the | here


COMMENT                     : '%' .*? '\r'? '\n' // SIMPLIFIED
                            | '%' .*? EOF
                            ;
// TODO include all kinds of comments (line/block/end of line)
/*
comment              : comment_line|comment_block>
comment_line         : [// ]printable_char*
comment_block        : [/][*]not_star_slash>[*][*]*[/]
not_star_slash       : ([^*]*[*][*]*[^/*])*[^*]*
*/

// ----Defined comments are a convention used for annotations that are used as
// ----additional input for systems. They look like comments, but start with // $
// ----or /*$. A wily user of the syntax can notice the $ and extract information
// ----from the "comment" and pass that on as input to the system. They are
// ----analogous to pragmas in programming languages. To extract these separately
// ----from regular comments, the rules are:
// ----  defined_comment    : def_comment_line|def_comment_block>
// ----  def_comment_line   : [// ]dollarprintable_char*
// ----  def_comment_block  : [/][*]dollarnot_star_slash>[*][*]*[/]
// ----A string that matches both defined_comment and comment should be
// ----recognized as defined_comment, so put these before comment.
// ----Defined comments that are in use include:
// ----    TO BE ANNOUNCED
// ----System comments are a convention used for annotations that may used as
// ----additional input to a specific system. They look like comments, but start
// ----with // $$ or /*$$. A wily user of the syntax can notice the $$ and extract
// ----information from the "comment" and pass that on as input to the system.
// ----The specific system for which the information is intended should be
// ----identified after the $$, e.g., /*$$Otter 3.3: Demodulator */
// ----To extract these separately from regular comments, the rules are:
// ----  system_comment     : sys_comment_line|sys_comment_block>
// ----  sys_comment_line   : [// ]dollardollarprintable_char*
// ----  sys_comment_block  : [/][*]dollardollarnot_star_slash>[*][*]*[/]
// ----A string that matches both system_comment and defined_comment should
// ----be recognized as system_comment, so put these before defined_comment.


single_quoted               : SINGLE_QUOTED;
SINGLE_QUOTED               : SINGLE_QUOTE SQ_CHAR+ SINGLE_QUOTE // match all valid ascii codes ([\40-\46\50-\133\135-\176]|[\\]['\\])
                            ;


// ----SINGLE_QUOTEDs contain visible characters. \ is the escape character for
// ----' and \, i.e., \' is not the end of the SINGLE_QUOTED.
// ----The token does not include the outer quotes, e.g., 'cat' and cat are the
// ----same. See atomic_word for information about stripping the quotes.
distinct_object             : DISTINCT_OBJECT;
DISTINCT_OBJECT             : DOUBLE_QUOTE DO_CHAR+ DOUBLE_QUOTE // match all valid ascii codes ([\40-\41\43-\133\135-\176]|[\\]["\\])
                            ;


// ---Space and visible characters upto ~, except " and \
// ----DISTINCT_OBJECTs contain visible characters. \ is the escape character
// ----for " and \, i.e., \" is not the end of the DISTINCT_OBJECT.
// ----DISTINCT_OBJECTs are different from (but may be equal to) other tokens,
// ----e.g., "cat" is different from 'cat' and cat. Distinct objects are always
// ----interpreted as themselves, so if they are different they are unequal,
// ----e.g., "Apple" != "Microsoft" is implicit.

DOLLAR                      : '$'
                            ;
L_PAREN                     : '('
                            ;
R_PAREN                     : ')'
                            ;
L_SQUARE_BRACKET            : '['
                            ;
R_SQUARE_BRACKET            : ']'
                            ;
COMMA                       : ','
                            ;

DOLLAR_DOLLAR_WORD          : DOLLAR DOLLAR LOWER_WORD // make lower word token for the word
                            ;
DOLLAR_WORD                 : DOLLAR LOWER_WORD // make lower word token for the word
                            ;
upper_word                  : UPPER_WORD // necessary since it should be one token
                            ;
UPPER_WORD                  : UPPER_ALPHA ALPHA_NUMERIC*
                            ;
lower_word                  : LOWER_WORD // necessary since it should be one token
                            ;
LOWER_WORD                  : LOWER_ALPHA ALPHA_NUMERIC*
                            ;
// ----Tokens used in syntax, and cannot be character classes

VLINE                       : '|'
                            ;
STAR                        : '*'
                            ;
PLUS                        : '+'
                            ;
MINUS                       : '-'
                            ;
ARROW                       : '>'
                            ;
LESS_SIGN                   : '<'
                            ;

// ----Numbers. Signs are made part of the same token here.
// TODO Number section should be carefully reworked

real                        : signed_real
                            | unsigned_real
                            ;
signed_real                 : sign unsigned_real
                            ;
unsigned_real               : decimal_fraction
                            | decimal_exponent
                            ;
rational                    : signed_rational
                            | unsigned_rational
                            ;
signed_rational             : sign unsigned_rational
                            ;
unsigned_rational           : decimal SLASH positive_decimal
                            ;
integer                     : signed_integer
                            | unsigned_integer
                            ;
signed_integer              : sign unsigned_integer
                            ;
unsigned_integer            : decimal
                            ;
decimal                     : ZERO_NUMERIC
                            | positive_decimal
                            ;
positive_decimal            : NON_ZERO_NUMERIC (NON_ZERO_NUMERIC|ZERO_NUMERIC)*
                            ;
decimal_exponent            : decimal R_PAREN EXPONENT exp_integer
                            | decimal_fraction R_PAREN EXPONENT exp_integer
                            ;
decimal_fraction            : decimal dot_decimal
                            ;
dot_decimal                 : DOT (NON_ZERO_NUMERIC|ZERO_NUMERIC)+
                            ;
exp_integer                 : signed_exp_integer
                            | unsigned_exp_integer
                            ;
signed_exp_integer          : sign unsigned_exp_integer
                            ;
unsigned_exp_integer        : (NON_ZERO_NUMERIC|ZERO_NUMERIC)+
                            ;

/*
<real>                 ::- (<signed_real>|<unsigned_real>)
<signed_real>          ::- <sign><unsigned_real>
<unsigned_real>        ::- (decimal_fraction>|decimal_exponent)
<rational>             ::- (<signed_rational>|<unsigned_rational>)
<signed_rational>      ::- <sign><unsigned_rational>
<unsigned_rational>    ::- decimal><slash><positive_decimal>
<integer>              ::- (<signed_integer>|<unsigned_integer>)
<signed_integer>       ::- <sign><unsigned_integer>
<unsigned_integer>     ::- decimal>
decimal>              ::- (<zero_numeric|<positive_decimal>)
<positive_decimal>     ::- <non_zero_numeric<numeric*
decimal_exponent     ::- (decimal>|decimal_fraction>)<exponent<exp_integer>
decimal_fraction>     ::- decimal>dot_decimal>
dot_decimal>          ::- dot<numeric<numeric*
<exp_integer>          ::- (<signed_exp_integer>|<unsigned_exp_integer>)
<signed_exp_integer>   ::- <sign><unsigned_exp_integer>
<unsigned_exp_integer> ::- <numeric<numeric*
*/

// Number related signs

sign                        : PLUS
                            | MINUS
                            ;
DOT                         : '.'
                            ;
EXPONENT                    : [Ee]
                            ;
SLASH                       : '/'
                            ;
ZERO_NUMERIC                : [0]
                            ;
NON_ZERO_NUMERIC            : [1-9]
                            ;







LOWER_ALPHA                 : [a-z]
                            ;
UPPER_ALPHA                 : [A-Z]
                            ;

ALPHA_NUMERIC               : LOWER_ALPHA
                            | UPPER_ALPHA
                            | NON_ZERO_NUMERIC
                            | ZERO_NUMERIC
                            | '_'
                            ;

// ----Character classes

DOUBLE_QUOTE                : '"'
                            ;
DO_CHAR                     : [\u0028-\u0029\u002B-\u0085\u0087-\u00B0] // for double_quoted
                            ;
SINGLE_QUOTE                : '\''
                            ;
// ---Space and visible characters upto ~, except ' and \
SQ_CHAR                     : [\u0028-\u002E\u0032-\u0085\u0087-\u00B0] // for single_quoted
                            ;

//printable_char       : .
// ----printable_char is any printable ASCII character, codes 32 (space) to 126
// ----(tilde). printable_char does not include tabs, newlines, bells, etc. The
// ----use of . does not not exclude tab, so this is a bit loose.
//viewable_char        : [.\n]


// ----Top of Page---------------------------------------------------------------


// assume annotated formulas with pattern name(****). do not need line breaks
NEWLINE                     : '\r'? '\n' -> skip
                            ;
WS                          : [ \t]+ -> skip
                            ;













































































