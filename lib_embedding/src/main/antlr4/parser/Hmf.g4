grammar Hmf;

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
// "asd" and 'asd' not working well, test with syn1 and syn2

// ==============================================================================
// FILE LAYER
// ==============================================================================


tPTP_file            : tPTP_input*
                     ;
tPTP_input           : annotated_formula
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


annotated_formula           : thf_annotated
                            //| tpi_annotated tpi are not supported
                            // UNSUPPORTED FORMATS: | tff_annotated | tcf_annotated | fof_annotated | cnf_annotated
                            ;

// ==============================================================================
// TPI GENERAL RULES
// ==============================================================================
// TODO add TPI

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

// ==============================================================================
// SEMANTICAL RULES
// ==============================================================================
// TODO maybe provide a more intuitive structure

logic_defn_element          : logic_defn_value
                            | logic_defn_rule
                            ;
logic_defn_rule             : logic_defn_lhs ASSIGNMENT logic_defn_rhs
                            ;
//%----The $constants, $quantification, and $consequence apply to all of the
//%----$modalities. Each of these may be specified only once, but not necessarily
//%----in a single annotated formula.
logic_defn_lhs              : LOGIC_OPTIONS
                            | MODAL_OPTIONS
                            | thf_function
                            | thf_apply_formula
                            | thf_top_level_type
                            ;
logic_defn_rhs              : thf_logic_formula
                            | logic_defn_value
                            ;
logic_defn_value            : thf_function
                            | MODAL_CONSTANT_MODES
                            | MODAL_QUANTIFICATION_MODES
                            | MODAL_CONSEQUENCE_MODES
                            | MODAL_AXIOM
                            | MODAL_AXIOM_SYSTEM
                            ;
LOGIC_OPTIONS               : '$modal'
                            ;
MODAL_OPTIONS               : '$constants'
                            | '$quantification'
                            | '$consequence'
                            | '$modalities'
                            ;
MODAL_AXIOM                 : '$modal_axiom_K' // box(s -> t) -> (box s -> box t)
                            | '$modal_axiom_T' // box s -> s
                            | '$modal_axiom_B' // s -> box dia s
                            | '$modal_axiom_D' // box s -> dia s
                            | '$modal_axiom_4' // box s -> box box s
                            | '$modal_axiom_5' // dia s -> box dia s
                            | '$modal_axiom_CD' // dia s -> box s
                            | '$modal_axiom_BOXM' // box (box s -> s)
                            | '$modal_axiom_C4' // box box s -> box s
                            | '$modal_axiom_C' // dia box s -> box dia s
                            ;
MODAL_AXIOM_SYSTEM          : '$modal_system_K' // Axiom: K
                            | '$modal_system_T' // Axiom: K + T
                            | '$modal_system_D' // Axiom: K + D
                            | '$modal_system_S4' // Axiom K + T + 4
                            | '$modal_system_S5' // Axiom K + T + 5
                            ;
MODAL_CONSTANT_MODES        : '$rigid'
                            | '$flexible'
                            ;
MODAL_QUANTIFICATION_MODES  : '$constant'
                            | '$varying'
                            | '$cumulative'
                            | '$decreasing'
                            ;
MODAL_CONSEQUENCE_MODES     : '$local'
                            | '$global'
                            ;

// ==============================================================================
// FORMULA INPUT TYPES
// ==============================================================================

// todo annotation support
thf_annotated               : 'thf' L_PAREN name COMMA formula_role COMMA thf_formula R_PAREN '.' // annotation is not supported
                            ;
/* // UNSUPPORTED FORMATS
// ----Future languages may include ...  english | efof | tfof | mathml | ...
tff_annotated        : tff(name,formula_role,tff_formula
                           annotations).
tcf_annotated        : tcf(name,formula_role,tcf_formula
                           annotations).
fof_annotated        : fof(name,formula_role,fof_formula
                           annotations).
cnf_annotated        : cnf(name,formula_role,cnf_formula
                           annotations).
*/


// ----In derivations the annotated formulae names must be unique, so that
// ----parent references (see inference_record) are unambiguous.

// ----Types for problems.
// ----Note: The previous source_type from ...
// ----   formula_role : user_role-source
// ----... is now gone. Parsers may choose to be tolerant of it for backwards
// ----compatibility.

// ==============================================================================
// FORMULA_ROLE (is for some reason placed here and not in general section)
// leave it like this in order to be able to compare this with tptp grammar
// ==============================================================================

formula_role                : 'axiom'
                            | 'hypothesis'
                            | 'definition'
                            | 'assumption'
                            | 'lemma'
                            | 'theorem'
                            | 'corollary'
                            | 'conjecture'
                            | 'negated_conjecture'
                            | 'plain'
                            | 'type'
                            | 'fi_domain'
                            | 'fi_functors'
                            | 'fi_predicates'
                            | 'logic'
                            | 'unknown'
                            | lower_word
                            ;

// ----"axiom"s are accepted, without proof. There is no guarantee that the
// ----axioms of a problem are consistent.
// ----"hypothesis"s are assumed to be true for a particular problem, and are
// ----used like "axiom"s.
// ----"definition"s are intended to define symbols. They are either universally
// ----quantified equations, or universally quantified equivalences with an
// ----atomic lefthand side. They can be treated like "axiom"s.
// ----"assumption"s can be used like axioms, but must be discharged before a
// ----derivation is complete.
// ----"lemma"s and "theorem"s have been proven from the "axiom"s. They can be
// ----used like "axiom"s in problems, and a problem containing a non-redundant
// ----"lemma" or theorem" is ill-formed. They can also appear in derivations.
// ----"theorem"s are more important than "lemma"s from the user perspective.
// ----"conjecture"s are to be proven from the "axiom"(-like) formulae. A problem
// ----is solved only when all "conjecture"s are proven.
// ----"negated_conjecture"s are formed from negation of a "conjecture" (usually
// ----in a FOF to CNF conversion).
// ----"plain"s have no specified user semantics.
// ----"fi_domain", "fi_functors", and "fi_predicates" are used to record the
// ----domain, interpretation of functors, and interpretation of predicates, for
// ----a finite interpretation.
// ----"type" defines the type globally for one symbol; treat as $true.
// ----"unknown"s have unknown role, and this is an error situation.
// ----Top of Page---------------------------------------------------------------
// ----THF formulae.

// ==============================================================================
// THF
// ==============================================================================

thf_formula                 : thf_logic_formula
                            | thf_sequent
                            ;
thf_logic_formula           : thf_binary_formula
                            | thf_unitary_formula
                            | thf_type_formula
                            | thf_subtype
                            | logic_defn_element
                            ;
thf_binary_formula          : thf_binary_pair
                            | thf_binary_tuple
                            ;

// ----Only some binary connectives can be written without ()s.
// ----There's no precedence among binary connectives

thf_binary_pair             : thf_unitary_formula thf_pair_connective thf_unitary_formula
                            ;
thf_binary_tuple            : thf_or_formula
                            | thf_and_formula
                            | thf_apply_formula
                            ;
thf_or_formula              : thf_unitary_formula VLINE thf_unitary_formula
                            | thf_or_formula VLINE thf_unitary_formula
                            ;
thf_and_formula             : thf_unitary_formula '&' thf_unitary_formula
                            | thf_and_formula '&' thf_unitary_formula
                            ;
thf_apply_formula           : thf_unitary_formula '@' thf_unitary_formula
                            | thf_apply_formula '@' thf_unitary_formula
                            ;

// ----thf_unitary_formula are in ()s or do not have a BINARY_CONNECTIVE at
// ----the top level. Essentially, any lambda expression that "has enough ()s" to
// ----be used inside a larger lambda expression. However, lambda notation might
// ----not be used.

thf_unitary_formula         : thf_quantified_formula
                            | thf_unary_formula
                            | thf_atom
                            | thf_conditional
                            | thf_let
                            | thf_tuple
                            | L_PAREN thf_logic_formula R_PAREN
                            ;

// ----Note that thf_atom allows defined_types as terms, which will fail
// ----type checking. The user must take care with this liberal syntax!

thf_quantified_formula      : thf_quantification thf_unitary_formula
                            ;
thf_quantification          : THF_QUANTIFIER L_SQUARE_BRACKET thf_variable_list R_SQUARE_BRACKET ':'
                            ;

// ----@ (denoting apply) is left-associative and lambda is right-associative.
// ----^ [X] : ^ [Y] : f @ g (where f is a thf_apply_formula and g is a
// ----thf_unitary_formula) should be parsed as: (^ [X] : (^ [Y] : f)) @ g.
// ----That is, g is not in the scope of either lambda.

thf_variable_list           : thf_variable
                            | thf_variable COMMA thf_variable_list
                            ;
thf_variable                : thf_typed_variable
                            | variable
                            ;
thf_typed_variable          : variable ':' thf_top_level_type
                            ;

// ----Unary connectives bind more tightly than binary. The negated formula
// ----must be ()ed because a ~ is also a term.

thf_unary_formula           : thf_unary_connective L_PAREN thf_logic_formula R_PAREN
                            ;
thf_atom                    : thf_function
                            | variable
                            | thf_conn_term
                            ;
thf_function                : thf_plain_term
                            | thf_defined_term
                            | thf_system_term
                            ;
thf_plain_term              : constant
                            | functor L_PAREN thf_arguments R_PAREN
                            ;

// ----Defined terms have TPTP specific interpretations
thf_defined_term            : defined_atom
                            | defined_constant
                            | defined_functor L_PAREN thf_arguments R_PAREN
                            ;

// ----System terms have system specific interpretations

thf_system_term             : system_constant
                            | system_functor L_PAREN thf_arguments R_PAREN
                            ;
thf_conditional             : '$ite' L_PAREN thf_logic_formula COMMA thf_logic_formula COMMA thf_logic_formula R_PAREN
                            ;

// ----The LHS of a term or formula binding must be a non-variable term that
// ----is flat with pairwise distinct variable arguments, and the variables in
// ----the LHS must be exactly those bound in the universally quantified variable
// ----list, in the same order. Let modalSymbolDefinitions are not recursive: a non-variable
// ----symbol introduced in the LHS of a let definition cannot occur in the RHS.
// ----If a symbol with the same signature as the one in the LHS of the binding
// ----is declared above the let expression (at the top level or in an
// ----encompassing let) then it can be used in the RHS of the binding, but it is
// ----not accessible in the term or formula of the let expression. Let
// ----expressions can be eliminated by a simple definition expansion.

thf_let                     : '$let' L_PAREN thf_let_defns COMMA thf_formula R_PAREN
                            ;
thf_let_defns               : thf_let_defn
                            | L_SQUARE_BRACKET thf_let_defn_list R_SQUARE_BRACKET
                            ;
thf_let_defn_list           : thf_let_defn
                            | thf_let_defn COMMA thf_let_defn_list
                            ;
thf_let_defn                : thf_let_quantified_defn
                            | thf_let_plain_defn
                            ;
thf_let_quantified_defn     : thf_quantification L_PAREN thf_let_plain_defn R_PAREN
                            ;
thf_let_plain_defn          : thf_let_defn_LHS ASSIGNMENT thf_formula
                            ;
thf_let_defn_LHS            : thf_plain_term
                            | thf_tuple
                            ;

// ----Arguments recurse back up to formulae (this is the THF world_type_declaration here)

thf_arguments               : thf_formula_list
                            ;

// ----A thf_type_formula is an assertion that the formula is in this type.

thf_type_formula            : thf_typeable_formula ':' thf_top_level_type //::=
                            | constant ':' thf_top_level_type // :==
                            ;
thf_typeable_formula        : thf_atom
                            | L_PAREN thf_logic_formula R_PAREN
                            ;
thf_subtype                 : constant subtype_sign constant
                            ;

// ----In a formula with role 'type', thf_type_formula is a global declaration
// ----that constant is in this thf_top_level_type, i.e., the rule is ...

// ----All symbols must be typed exactly one before use, and all atomic types
// ----must be declared (of type $tType) before use.

// ----thf_top_level_type appears after ":", where a type is being specified
// ----for a term or variable. thf_unitary_type includes thf_unitary_formula,
// ----so the syntax allows just about any lambda expression with "enough"
// ----parentheses to serve as a type. The expected use of this flexibility is
// ----parametric polymorphism in types, expressed with lambda abstraction.
// ----Mapping is right-associative: o  o  o means o  (o  o).
// ----Xproduct is left-associative: o * o * o means (o * o) * o.
// ----Union is left-associative: o + o + o means (o + o) + o.

thf_top_level_type          : thf_unitary_type
                            | thf_mapping_type
                            ;
thf_unitary_type            : thf_unitary_formula
                            | L_PAREN thf_binary_type R_PAREN
                            ;
thf_binary_type             : thf_mapping_type
                            | thf_xprod_type
                            | thf_union_type
                            ;
thf_mapping_type            : thf_unitary_type ARROW thf_unitary_type
                            | thf_unitary_type ARROW thf_mapping_type
                            ;
thf_xprod_type              : thf_unitary_type STAR thf_unitary_type
                            | thf_xprod_type STAR thf_unitary_type
                            ;
thf_union_type              : thf_unitary_type PLUS thf_unitary_type
                            | thf_union_type PLUS thf_unitary_type
                            ;

// ----Sequents using the Gentzen arrow

thf_sequent                 : thf_tuple GENTZEN_ARROW thf_tuple
                            | L_PAREN thf_sequent R_PAREN
                            ;

thf_tuple                   : '[]'
                            | L_SQUARE_BRACKET thf_formula_list R_SQUARE_BRACKET
                            ;
thf_formula_list            : thf_logic_formula
                            | thf_logic_formula COMMA thf_formula_list
                            ;


thf_conn_term               : thf_pair_connective
                            | assoc_connective
                            | thf_unary_connective
                            ;


// ----Note that syntactically this allows (p @ =), but for = the first
// ----argument must be known to infer the type of =, so that's not
// ----allowed, i.e., only (= @ p).

// ----Connectives - THF

THF_QUANTIFIER              : FOL_QUANTIFIER
                            | TH0_QUANTIFIER
                            | TH1_QUANTIFIER
                            ;

// ----TH0 quantifiers are also available in TH1

TH0_QUANTIFIER              : '^'
                            | '@+'
                            | '@-'
                            ;
TH1_QUANTIFIER              : '!'
                            | '?*'
                            ;
thf_pair_connective         : INFIX_EQUALITY
                            | INFIX_INEQUALITY
                            | BINARY_CONNECTIVE
                            | ASSIGNMENT
                            ;


// thf_unary_connective definition
thf_unary_connective        : UNARY_CONNECTIVE
                            | TH1_UNARY_CONNECTIVE
                            ;
TH1_UNARY_CONNECTIVE        : '!!'
                            | '??'
                            | '@@+'
                            | '@@-'
                            | '@='
                            ;

// ----Connectives - THF and TFF

subtype_sign                : LESS_SIGN LESS_SIGN
                            ;

// ----Connectives - FOF

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

// ----Types for THF and TFF

defined_type                : '$oType'
                            | '$o'
                            | '$iType'
                            | '$i'
                            | '$tType'
                            | '$real'
                            | '$rat'
                            | '$int'
                            | '$w'
                            | atomic_defined_word
                            ;

// ----$oType/$o is the Boolean type, i.e., the type of $true and $false.
// ----$iType/$i is non-empty type of individuals, which may be finite or
// ----infinite. $tType is the type of all types. $real is the type of reals.
// ----$rat is the type of rationals. $int is the type of signed_integers
// ----and unsigned_integers.

// system_type          : atomic_system_word // NOT IN USE

// ----First order atoms
/*
atomic_formula       : plain_atomic_formula | defined_atomic_formula |
                           system_atomic_formula
plain_atomic_formula : plain_term
plain_atomic_formula : proposition | predicate(arguments)
proposition          : predicate
predicate            : atomic_word
// ----Using plain_term removes a reduce/reduce ambiguity in lex/yacc.
// ----Note: "defined" means a word starting with one $ and "system" means $$.
defined_atomic_formula : defined_plain_formula | defined_infix_formula
defined_plain_formula : defined_plain_term
defined_plain_formula : defined_prop | defined_pred(arguments)
defined_prop         : atomic_defined_word
defined_prop         : $true | $false
// ----Pure CNF should not use $true or $false in problems, and use $false only
// ----at the roots of a refutation.
defined_pred         : atomic_defined_word
defined_pred         : $distinct |
                           $less | $lesseq | $greater | $greatereq |
                           $is_int | $is_rat
*/

// ----$distinct means that each of it's constant arguments are pairwise !=. It
// ----is part of the TFF syntax. It can be used only as a fact, not under any
// ----connective.

// defined_infix_formula : term defined_infix_pred term
// defined_infix_pred   : infix_equality
INFIX_INEQUALITY            : '!='
                            ;
INFIX_EQUALITY              : '='
                            ;


// ----Some systems still interpret equal/2 as equality. The use of equal/2
// ----for other purposes is therefore discouraged. Please refrain from either
// ----use. Use infix '=' for equality. Note: term != term is equivalent
// ----to ~ term = term
// ----System terms have system specific interpretations
//system_atomic_formula : system_term
// ----system_atomic_formulas are used for evaluable predicates that are
// ----available in particular tools. The predicate names are not controlled
// ----by the TPTP syntax, so use with due care. The same is true for
// ----system_terms.

// ----First order terms.
// ----logic in TFF.

//term                        : function_term
//                            | variable
                            // | conditional_term LEFT OUT
                            // | let_term LEFT OUT
//                            ;
//function_term               : plain_term
//                            | defined_term
//                            | system_term
//                            ;
//plain_term                  : constant
//                            | functor L_PAREN arguments R_PAREN
//                            ;
constant                    : functor
                            ;
functor                     : atomic_word
                            ;

// ----Defined terms have TPTP specific interpretations

//defined_term                : defined_atom
//                            | defined_atomic_term
//                            ;

// ----numbers may not be used on CNF and FOF

defined_atom                : number
                            | distinct_object
                            ;
//defined_atomic_term         : defined_plain_term
//                            ;

// ----None yet             | defined_infix_term
// ----None yet defined_infix_term : term defined_infix_func term
// ----None yet defined_infix_func :

//defined_plain_term          : defined_constant
//                            | defined_functor L_PAREN arguments R_PAREN
//                            ;
defined_constant            : defined_functor //::=
                            | defined_type //:==
                            ;
defined_functor             : '$box_P'
                            | '$box_i'
                            | '$box_int'
                            | '$box'
                            | '$dia_P'
                            | '$dia_i'
                            | '$dia_int'
                            | '$dia'
                            | '$uminus'  //:==
                            | '$sum'
                            | '$difference'
                            | '$product'
                            | '$quotient'
                            | '$quotient_e'
                            | '$quotient_t'
                            | '$quotient_f'
                            | '$remainder_e'
                            | '$remainder_t'
                            | '$remainder_f'
                            | '$floor'
                            | '$ceiling'
                            | '$truncate'
                            | '$round'
                            | '$to_int'
                            | '$to_rat'
                            | '$to_real'
                            | atomic_defined_word
                            ;

// ----Add $tuple for tuples, because [arguments] doesn't work.
// ----System terms have system specific interpretations

//system_term                 : system_constant
//                            | system_functor L_PAREN arguments R_PAREN
//                            ;
system_constant             : system_functor
                            ;
system_functor              : atomic_system_word
                            ;

// ----Variables, and only variables, start with uppercase

variable                    : upper_word
                            ;

/*
// ----Conditional terms should be used by only TFF.

conditional_term     : $ite_t(tff_logic_formula,term,term)

// ----Let terms should be used by only TFF. $let_ft is for use when there is
// ----a $ite_t in the term. See the commentary for $let_tf and $let_ff.
let_term             : $let_ft(tff_let_formula_defns,term) |
                           $let_tt(tff_let_term_defns,term)
*/

// ----Arguments recurse back up to terms (this is the FOF world_type_declaration here)

//arguments                   : term | term COMMA arguments
//                            ;

// ----Principal symbols are predicates, functions, variables
//principal_symbol            : functor
//                            | variable
//                            ;

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
number                      : integer
                            | rational
                            | real
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
<unsigned_real>        ::- (<decimal_fraction>|<decimal_exponent>)
<rational>             ::- (<signed_rational>|<unsigned_rational>)
<signed_rational>      ::- <sign><unsigned_rational>
<unsigned_rational>    ::- <decimal><slash><positive_decimal>
<integer>              ::- (<signed_integer>|<unsigned_integer>)
<signed_integer>       ::- <sign><unsigned_integer>
<unsigned_integer>     ::- <decimal>
<decimal>              ::- (<zero_numeric>|<positive_decimal>)
<positive_decimal>     ::- <non_zero_numeric><numeric>*
<decimal_exponent>     ::- (<decimal>|<decimal_fraction>)<exponent><exp_integer>
<decimal_fraction>     ::- <decimal><dot_decimal>
<dot_decimal>          ::- <dot><numeric><numeric>*
<exp_integer>          ::- (<signed_exp_integer>|<unsigned_exp_integer>)
<signed_exp_integer>   ::- <sign><unsigned_exp_integer>
<unsigned_exp_integer> ::- <numeric><numeric>*
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













































































