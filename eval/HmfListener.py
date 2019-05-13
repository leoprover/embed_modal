# Generated from Hmf.g4 by ANTLR 4.7.2
from antlr4 import *
if __name__ is not None and "." in __name__:
    from .HmfParser import HmfParser
else:
    from HmfParser import HmfParser

# This class defines a complete listener for a parse tree produced by HmfParser.
class HmfListener(ParseTreeListener):

    # Enter a parse tree produced by HmfParser#tPTP_file.
    def enterTPTP_file(self, ctx:HmfParser.TPTP_fileContext):
        pass

    # Exit a parse tree produced by HmfParser#tPTP_file.
    def exitTPTP_file(self, ctx:HmfParser.TPTP_fileContext):
        pass


    # Enter a parse tree produced by HmfParser#tPTP_input.
    def enterTPTP_input(self, ctx:HmfParser.TPTP_inputContext):
        pass

    # Exit a parse tree produced by HmfParser#tPTP_input.
    def exitTPTP_input(self, ctx:HmfParser.TPTP_inputContext):
        pass


    # Enter a parse tree produced by HmfParser#include.
    def enterInclude(self, ctx:HmfParser.IncludeContext):
        pass

    # Exit a parse tree produced by HmfParser#include.
    def exitInclude(self, ctx:HmfParser.IncludeContext):
        pass


    # Enter a parse tree produced by HmfParser#comment.
    def enterComment(self, ctx:HmfParser.CommentContext):
        pass

    # Exit a parse tree produced by HmfParser#comment.
    def exitComment(self, ctx:HmfParser.CommentContext):
        pass


    # Enter a parse tree produced by HmfParser#annotated_formula.
    def enterAnnotated_formula(self, ctx:HmfParser.Annotated_formulaContext):
        pass

    # Exit a parse tree produced by HmfParser#annotated_formula.
    def exitAnnotated_formula(self, ctx:HmfParser.Annotated_formulaContext):
        pass


    # Enter a parse tree produced by HmfParser#logic_defn_element.
    def enterLogic_defn_element(self, ctx:HmfParser.Logic_defn_elementContext):
        pass

    # Exit a parse tree produced by HmfParser#logic_defn_element.
    def exitLogic_defn_element(self, ctx:HmfParser.Logic_defn_elementContext):
        pass


    # Enter a parse tree produced by HmfParser#logic_defn_rule.
    def enterLogic_defn_rule(self, ctx:HmfParser.Logic_defn_ruleContext):
        pass

    # Exit a parse tree produced by HmfParser#logic_defn_rule.
    def exitLogic_defn_rule(self, ctx:HmfParser.Logic_defn_ruleContext):
        pass


    # Enter a parse tree produced by HmfParser#logic_defn_lhs.
    def enterLogic_defn_lhs(self, ctx:HmfParser.Logic_defn_lhsContext):
        pass

    # Exit a parse tree produced by HmfParser#logic_defn_lhs.
    def exitLogic_defn_lhs(self, ctx:HmfParser.Logic_defn_lhsContext):
        pass


    # Enter a parse tree produced by HmfParser#logic_defn_rhs.
    def enterLogic_defn_rhs(self, ctx:HmfParser.Logic_defn_rhsContext):
        pass

    # Exit a parse tree produced by HmfParser#logic_defn_rhs.
    def exitLogic_defn_rhs(self, ctx:HmfParser.Logic_defn_rhsContext):
        pass


    # Enter a parse tree produced by HmfParser#logic_defn_value.
    def enterLogic_defn_value(self, ctx:HmfParser.Logic_defn_valueContext):
        pass

    # Exit a parse tree produced by HmfParser#logic_defn_value.
    def exitLogic_defn_value(self, ctx:HmfParser.Logic_defn_valueContext):
        pass


    # Enter a parse tree produced by HmfParser#thf_annotated.
    def enterThf_annotated(self, ctx:HmfParser.Thf_annotatedContext):
        pass

    # Exit a parse tree produced by HmfParser#thf_annotated.
    def exitThf_annotated(self, ctx:HmfParser.Thf_annotatedContext):
        pass


    # Enter a parse tree produced by HmfParser#formula_role.
    def enterFormula_role(self, ctx:HmfParser.Formula_roleContext):
        pass

    # Exit a parse tree produced by HmfParser#formula_role.
    def exitFormula_role(self, ctx:HmfParser.Formula_roleContext):
        pass


    # Enter a parse tree produced by HmfParser#thf_formula.
    def enterThf_formula(self, ctx:HmfParser.Thf_formulaContext):
        pass

    # Exit a parse tree produced by HmfParser#thf_formula.
    def exitThf_formula(self, ctx:HmfParser.Thf_formulaContext):
        pass


    # Enter a parse tree produced by HmfParser#thf_logic_formula.
    def enterThf_logic_formula(self, ctx:HmfParser.Thf_logic_formulaContext):
        pass

    # Exit a parse tree produced by HmfParser#thf_logic_formula.
    def exitThf_logic_formula(self, ctx:HmfParser.Thf_logic_formulaContext):
        pass


    # Enter a parse tree produced by HmfParser#thf_binary_formula.
    def enterThf_binary_formula(self, ctx:HmfParser.Thf_binary_formulaContext):
        pass

    # Exit a parse tree produced by HmfParser#thf_binary_formula.
    def exitThf_binary_formula(self, ctx:HmfParser.Thf_binary_formulaContext):
        pass


    # Enter a parse tree produced by HmfParser#thf_binary_pair.
    def enterThf_binary_pair(self, ctx:HmfParser.Thf_binary_pairContext):
        pass

    # Exit a parse tree produced by HmfParser#thf_binary_pair.
    def exitThf_binary_pair(self, ctx:HmfParser.Thf_binary_pairContext):
        pass


    # Enter a parse tree produced by HmfParser#thf_binary_tuple.
    def enterThf_binary_tuple(self, ctx:HmfParser.Thf_binary_tupleContext):
        pass

    # Exit a parse tree produced by HmfParser#thf_binary_tuple.
    def exitThf_binary_tuple(self, ctx:HmfParser.Thf_binary_tupleContext):
        pass


    # Enter a parse tree produced by HmfParser#thf_or_formula.
    def enterThf_or_formula(self, ctx:HmfParser.Thf_or_formulaContext):
        pass

    # Exit a parse tree produced by HmfParser#thf_or_formula.
    def exitThf_or_formula(self, ctx:HmfParser.Thf_or_formulaContext):
        pass


    # Enter a parse tree produced by HmfParser#thf_and_formula.
    def enterThf_and_formula(self, ctx:HmfParser.Thf_and_formulaContext):
        pass

    # Exit a parse tree produced by HmfParser#thf_and_formula.
    def exitThf_and_formula(self, ctx:HmfParser.Thf_and_formulaContext):
        pass


    # Enter a parse tree produced by HmfParser#thf_apply_formula.
    def enterThf_apply_formula(self, ctx:HmfParser.Thf_apply_formulaContext):
        pass

    # Exit a parse tree produced by HmfParser#thf_apply_formula.
    def exitThf_apply_formula(self, ctx:HmfParser.Thf_apply_formulaContext):
        pass


    # Enter a parse tree produced by HmfParser#thf_unitary_formula.
    def enterThf_unitary_formula(self, ctx:HmfParser.Thf_unitary_formulaContext):
        pass

    # Exit a parse tree produced by HmfParser#thf_unitary_formula.
    def exitThf_unitary_formula(self, ctx:HmfParser.Thf_unitary_formulaContext):
        pass


    # Enter a parse tree produced by HmfParser#thf_quantified_formula.
    def enterThf_quantified_formula(self, ctx:HmfParser.Thf_quantified_formulaContext):
        pass

    # Exit a parse tree produced by HmfParser#thf_quantified_formula.
    def exitThf_quantified_formula(self, ctx:HmfParser.Thf_quantified_formulaContext):
        pass


    # Enter a parse tree produced by HmfParser#thf_quantification.
    def enterThf_quantification(self, ctx:HmfParser.Thf_quantificationContext):
        pass

    # Exit a parse tree produced by HmfParser#thf_quantification.
    def exitThf_quantification(self, ctx:HmfParser.Thf_quantificationContext):
        pass


    # Enter a parse tree produced by HmfParser#thf_variable_list.
    def enterThf_variable_list(self, ctx:HmfParser.Thf_variable_listContext):
        pass

    # Exit a parse tree produced by HmfParser#thf_variable_list.
    def exitThf_variable_list(self, ctx:HmfParser.Thf_variable_listContext):
        pass


    # Enter a parse tree produced by HmfParser#thf_variable.
    def enterThf_variable(self, ctx:HmfParser.Thf_variableContext):
        pass

    # Exit a parse tree produced by HmfParser#thf_variable.
    def exitThf_variable(self, ctx:HmfParser.Thf_variableContext):
        pass


    # Enter a parse tree produced by HmfParser#thf_typed_variable.
    def enterThf_typed_variable(self, ctx:HmfParser.Thf_typed_variableContext):
        pass

    # Exit a parse tree produced by HmfParser#thf_typed_variable.
    def exitThf_typed_variable(self, ctx:HmfParser.Thf_typed_variableContext):
        pass


    # Enter a parse tree produced by HmfParser#thf_unary_formula.
    def enterThf_unary_formula(self, ctx:HmfParser.Thf_unary_formulaContext):
        pass

    # Exit a parse tree produced by HmfParser#thf_unary_formula.
    def exitThf_unary_formula(self, ctx:HmfParser.Thf_unary_formulaContext):
        pass


    # Enter a parse tree produced by HmfParser#thf_atom.
    def enterThf_atom(self, ctx:HmfParser.Thf_atomContext):
        pass

    # Exit a parse tree produced by HmfParser#thf_atom.
    def exitThf_atom(self, ctx:HmfParser.Thf_atomContext):
        pass


    # Enter a parse tree produced by HmfParser#thf_function.
    def enterThf_function(self, ctx:HmfParser.Thf_functionContext):
        pass

    # Exit a parse tree produced by HmfParser#thf_function.
    def exitThf_function(self, ctx:HmfParser.Thf_functionContext):
        pass


    # Enter a parse tree produced by HmfParser#thf_plain_term.
    def enterThf_plain_term(self, ctx:HmfParser.Thf_plain_termContext):
        pass

    # Exit a parse tree produced by HmfParser#thf_plain_term.
    def exitThf_plain_term(self, ctx:HmfParser.Thf_plain_termContext):
        pass


    # Enter a parse tree produced by HmfParser#thf_defined_term.
    def enterThf_defined_term(self, ctx:HmfParser.Thf_defined_termContext):
        pass

    # Exit a parse tree produced by HmfParser#thf_defined_term.
    def exitThf_defined_term(self, ctx:HmfParser.Thf_defined_termContext):
        pass


    # Enter a parse tree produced by HmfParser#thf_system_term.
    def enterThf_system_term(self, ctx:HmfParser.Thf_system_termContext):
        pass

    # Exit a parse tree produced by HmfParser#thf_system_term.
    def exitThf_system_term(self, ctx:HmfParser.Thf_system_termContext):
        pass


    # Enter a parse tree produced by HmfParser#thf_conditional.
    def enterThf_conditional(self, ctx:HmfParser.Thf_conditionalContext):
        pass

    # Exit a parse tree produced by HmfParser#thf_conditional.
    def exitThf_conditional(self, ctx:HmfParser.Thf_conditionalContext):
        pass


    # Enter a parse tree produced by HmfParser#thf_let.
    def enterThf_let(self, ctx:HmfParser.Thf_letContext):
        pass

    # Exit a parse tree produced by HmfParser#thf_let.
    def exitThf_let(self, ctx:HmfParser.Thf_letContext):
        pass


    # Enter a parse tree produced by HmfParser#thf_let_defns.
    def enterThf_let_defns(self, ctx:HmfParser.Thf_let_defnsContext):
        pass

    # Exit a parse tree produced by HmfParser#thf_let_defns.
    def exitThf_let_defns(self, ctx:HmfParser.Thf_let_defnsContext):
        pass


    # Enter a parse tree produced by HmfParser#thf_let_defn_list.
    def enterThf_let_defn_list(self, ctx:HmfParser.Thf_let_defn_listContext):
        pass

    # Exit a parse tree produced by HmfParser#thf_let_defn_list.
    def exitThf_let_defn_list(self, ctx:HmfParser.Thf_let_defn_listContext):
        pass


    # Enter a parse tree produced by HmfParser#thf_let_defn.
    def enterThf_let_defn(self, ctx:HmfParser.Thf_let_defnContext):
        pass

    # Exit a parse tree produced by HmfParser#thf_let_defn.
    def exitThf_let_defn(self, ctx:HmfParser.Thf_let_defnContext):
        pass


    # Enter a parse tree produced by HmfParser#thf_let_quantified_defn.
    def enterThf_let_quantified_defn(self, ctx:HmfParser.Thf_let_quantified_defnContext):
        pass

    # Exit a parse tree produced by HmfParser#thf_let_quantified_defn.
    def exitThf_let_quantified_defn(self, ctx:HmfParser.Thf_let_quantified_defnContext):
        pass


    # Enter a parse tree produced by HmfParser#thf_let_plain_defn.
    def enterThf_let_plain_defn(self, ctx:HmfParser.Thf_let_plain_defnContext):
        pass

    # Exit a parse tree produced by HmfParser#thf_let_plain_defn.
    def exitThf_let_plain_defn(self, ctx:HmfParser.Thf_let_plain_defnContext):
        pass


    # Enter a parse tree produced by HmfParser#thf_let_defn_LHS.
    def enterThf_let_defn_LHS(self, ctx:HmfParser.Thf_let_defn_LHSContext):
        pass

    # Exit a parse tree produced by HmfParser#thf_let_defn_LHS.
    def exitThf_let_defn_LHS(self, ctx:HmfParser.Thf_let_defn_LHSContext):
        pass


    # Enter a parse tree produced by HmfParser#thf_arguments.
    def enterThf_arguments(self, ctx:HmfParser.Thf_argumentsContext):
        pass

    # Exit a parse tree produced by HmfParser#thf_arguments.
    def exitThf_arguments(self, ctx:HmfParser.Thf_argumentsContext):
        pass


    # Enter a parse tree produced by HmfParser#thf_type_formula.
    def enterThf_type_formula(self, ctx:HmfParser.Thf_type_formulaContext):
        pass

    # Exit a parse tree produced by HmfParser#thf_type_formula.
    def exitThf_type_formula(self, ctx:HmfParser.Thf_type_formulaContext):
        pass


    # Enter a parse tree produced by HmfParser#thf_typeable_formula.
    def enterThf_typeable_formula(self, ctx:HmfParser.Thf_typeable_formulaContext):
        pass

    # Exit a parse tree produced by HmfParser#thf_typeable_formula.
    def exitThf_typeable_formula(self, ctx:HmfParser.Thf_typeable_formulaContext):
        pass


    # Enter a parse tree produced by HmfParser#thf_subtype.
    def enterThf_subtype(self, ctx:HmfParser.Thf_subtypeContext):
        pass

    # Exit a parse tree produced by HmfParser#thf_subtype.
    def exitThf_subtype(self, ctx:HmfParser.Thf_subtypeContext):
        pass


    # Enter a parse tree produced by HmfParser#thf_top_level_type.
    def enterThf_top_level_type(self, ctx:HmfParser.Thf_top_level_typeContext):
        pass

    # Exit a parse tree produced by HmfParser#thf_top_level_type.
    def exitThf_top_level_type(self, ctx:HmfParser.Thf_top_level_typeContext):
        pass


    # Enter a parse tree produced by HmfParser#thf_unitary_type.
    def enterThf_unitary_type(self, ctx:HmfParser.Thf_unitary_typeContext):
        pass

    # Exit a parse tree produced by HmfParser#thf_unitary_type.
    def exitThf_unitary_type(self, ctx:HmfParser.Thf_unitary_typeContext):
        pass


    # Enter a parse tree produced by HmfParser#thf_binary_type.
    def enterThf_binary_type(self, ctx:HmfParser.Thf_binary_typeContext):
        pass

    # Exit a parse tree produced by HmfParser#thf_binary_type.
    def exitThf_binary_type(self, ctx:HmfParser.Thf_binary_typeContext):
        pass


    # Enter a parse tree produced by HmfParser#thf_mapping_type.
    def enterThf_mapping_type(self, ctx:HmfParser.Thf_mapping_typeContext):
        pass

    # Exit a parse tree produced by HmfParser#thf_mapping_type.
    def exitThf_mapping_type(self, ctx:HmfParser.Thf_mapping_typeContext):
        pass


    # Enter a parse tree produced by HmfParser#thf_xprod_type.
    def enterThf_xprod_type(self, ctx:HmfParser.Thf_xprod_typeContext):
        pass

    # Exit a parse tree produced by HmfParser#thf_xprod_type.
    def exitThf_xprod_type(self, ctx:HmfParser.Thf_xprod_typeContext):
        pass


    # Enter a parse tree produced by HmfParser#thf_union_type.
    def enterThf_union_type(self, ctx:HmfParser.Thf_union_typeContext):
        pass

    # Exit a parse tree produced by HmfParser#thf_union_type.
    def exitThf_union_type(self, ctx:HmfParser.Thf_union_typeContext):
        pass


    # Enter a parse tree produced by HmfParser#thf_sequent.
    def enterThf_sequent(self, ctx:HmfParser.Thf_sequentContext):
        pass

    # Exit a parse tree produced by HmfParser#thf_sequent.
    def exitThf_sequent(self, ctx:HmfParser.Thf_sequentContext):
        pass


    # Enter a parse tree produced by HmfParser#thf_tuple.
    def enterThf_tuple(self, ctx:HmfParser.Thf_tupleContext):
        pass

    # Exit a parse tree produced by HmfParser#thf_tuple.
    def exitThf_tuple(self, ctx:HmfParser.Thf_tupleContext):
        pass


    # Enter a parse tree produced by HmfParser#thf_formula_list.
    def enterThf_formula_list(self, ctx:HmfParser.Thf_formula_listContext):
        pass

    # Exit a parse tree produced by HmfParser#thf_formula_list.
    def exitThf_formula_list(self, ctx:HmfParser.Thf_formula_listContext):
        pass


    # Enter a parse tree produced by HmfParser#thf_conn_term.
    def enterThf_conn_term(self, ctx:HmfParser.Thf_conn_termContext):
        pass

    # Exit a parse tree produced by HmfParser#thf_conn_term.
    def exitThf_conn_term(self, ctx:HmfParser.Thf_conn_termContext):
        pass


    # Enter a parse tree produced by HmfParser#thf_pair_connective.
    def enterThf_pair_connective(self, ctx:HmfParser.Thf_pair_connectiveContext):
        pass

    # Exit a parse tree produced by HmfParser#thf_pair_connective.
    def exitThf_pair_connective(self, ctx:HmfParser.Thf_pair_connectiveContext):
        pass


    # Enter a parse tree produced by HmfParser#thf_unary_connective.
    def enterThf_unary_connective(self, ctx:HmfParser.Thf_unary_connectiveContext):
        pass

    # Exit a parse tree produced by HmfParser#thf_unary_connective.
    def exitThf_unary_connective(self, ctx:HmfParser.Thf_unary_connectiveContext):
        pass


    # Enter a parse tree produced by HmfParser#subtype_sign.
    def enterSubtype_sign(self, ctx:HmfParser.Subtype_signContext):
        pass

    # Exit a parse tree produced by HmfParser#subtype_sign.
    def exitSubtype_sign(self, ctx:HmfParser.Subtype_signContext):
        pass


    # Enter a parse tree produced by HmfParser#assoc_connective.
    def enterAssoc_connective(self, ctx:HmfParser.Assoc_connectiveContext):
        pass

    # Exit a parse tree produced by HmfParser#assoc_connective.
    def exitAssoc_connective(self, ctx:HmfParser.Assoc_connectiveContext):
        pass


    # Enter a parse tree produced by HmfParser#defined_type.
    def enterDefined_type(self, ctx:HmfParser.Defined_typeContext):
        pass

    # Exit a parse tree produced by HmfParser#defined_type.
    def exitDefined_type(self, ctx:HmfParser.Defined_typeContext):
        pass


    # Enter a parse tree produced by HmfParser#constant.
    def enterConstant(self, ctx:HmfParser.ConstantContext):
        pass

    # Exit a parse tree produced by HmfParser#constant.
    def exitConstant(self, ctx:HmfParser.ConstantContext):
        pass


    # Enter a parse tree produced by HmfParser#functor.
    def enterFunctor(self, ctx:HmfParser.FunctorContext):
        pass

    # Exit a parse tree produced by HmfParser#functor.
    def exitFunctor(self, ctx:HmfParser.FunctorContext):
        pass


    # Enter a parse tree produced by HmfParser#defined_atom.
    def enterDefined_atom(self, ctx:HmfParser.Defined_atomContext):
        pass

    # Exit a parse tree produced by HmfParser#defined_atom.
    def exitDefined_atom(self, ctx:HmfParser.Defined_atomContext):
        pass


    # Enter a parse tree produced by HmfParser#defined_constant.
    def enterDefined_constant(self, ctx:HmfParser.Defined_constantContext):
        pass

    # Exit a parse tree produced by HmfParser#defined_constant.
    def exitDefined_constant(self, ctx:HmfParser.Defined_constantContext):
        pass


    # Enter a parse tree produced by HmfParser#defined_functor.
    def enterDefined_functor(self, ctx:HmfParser.Defined_functorContext):
        pass

    # Exit a parse tree produced by HmfParser#defined_functor.
    def exitDefined_functor(self, ctx:HmfParser.Defined_functorContext):
        pass


    # Enter a parse tree produced by HmfParser#system_constant.
    def enterSystem_constant(self, ctx:HmfParser.System_constantContext):
        pass

    # Exit a parse tree produced by HmfParser#system_constant.
    def exitSystem_constant(self, ctx:HmfParser.System_constantContext):
        pass


    # Enter a parse tree produced by HmfParser#system_functor.
    def enterSystem_functor(self, ctx:HmfParser.System_functorContext):
        pass

    # Exit a parse tree produced by HmfParser#system_functor.
    def exitSystem_functor(self, ctx:HmfParser.System_functorContext):
        pass


    # Enter a parse tree produced by HmfParser#variable.
    def enterVariable(self, ctx:HmfParser.VariableContext):
        pass

    # Exit a parse tree produced by HmfParser#variable.
    def exitVariable(self, ctx:HmfParser.VariableContext):
        pass


    # Enter a parse tree produced by HmfParser#name.
    def enterName(self, ctx:HmfParser.NameContext):
        pass

    # Exit a parse tree produced by HmfParser#name.
    def exitName(self, ctx:HmfParser.NameContext):
        pass


    # Enter a parse tree produced by HmfParser#atomic_word.
    def enterAtomic_word(self, ctx:HmfParser.Atomic_wordContext):
        pass

    # Exit a parse tree produced by HmfParser#atomic_word.
    def exitAtomic_word(self, ctx:HmfParser.Atomic_wordContext):
        pass


    # Enter a parse tree produced by HmfParser#atomic_defined_word.
    def enterAtomic_defined_word(self, ctx:HmfParser.Atomic_defined_wordContext):
        pass

    # Exit a parse tree produced by HmfParser#atomic_defined_word.
    def exitAtomic_defined_word(self, ctx:HmfParser.Atomic_defined_wordContext):
        pass


    # Enter a parse tree produced by HmfParser#atomic_system_word.
    def enterAtomic_system_word(self, ctx:HmfParser.Atomic_system_wordContext):
        pass

    # Exit a parse tree produced by HmfParser#atomic_system_word.
    def exitAtomic_system_word(self, ctx:HmfParser.Atomic_system_wordContext):
        pass


    # Enter a parse tree produced by HmfParser#number.
    def enterNumber(self, ctx:HmfParser.NumberContext):
        pass

    # Exit a parse tree produced by HmfParser#number.
    def exitNumber(self, ctx:HmfParser.NumberContext):
        pass


    # Enter a parse tree produced by HmfParser#file_name.
    def enterFile_name(self, ctx:HmfParser.File_nameContext):
        pass

    # Exit a parse tree produced by HmfParser#file_name.
    def exitFile_name(self, ctx:HmfParser.File_nameContext):
        pass


    # Enter a parse tree produced by HmfParser#single_quoted.
    def enterSingle_quoted(self, ctx:HmfParser.Single_quotedContext):
        pass

    # Exit a parse tree produced by HmfParser#single_quoted.
    def exitSingle_quoted(self, ctx:HmfParser.Single_quotedContext):
        pass


    # Enter a parse tree produced by HmfParser#distinct_object.
    def enterDistinct_object(self, ctx:HmfParser.Distinct_objectContext):
        pass

    # Exit a parse tree produced by HmfParser#distinct_object.
    def exitDistinct_object(self, ctx:HmfParser.Distinct_objectContext):
        pass


    # Enter a parse tree produced by HmfParser#upper_word.
    def enterUpper_word(self, ctx:HmfParser.Upper_wordContext):
        pass

    # Exit a parse tree produced by HmfParser#upper_word.
    def exitUpper_word(self, ctx:HmfParser.Upper_wordContext):
        pass


    # Enter a parse tree produced by HmfParser#lower_word.
    def enterLower_word(self, ctx:HmfParser.Lower_wordContext):
        pass

    # Exit a parse tree produced by HmfParser#lower_word.
    def exitLower_word(self, ctx:HmfParser.Lower_wordContext):
        pass


    # Enter a parse tree produced by HmfParser#real.
    def enterReal(self, ctx:HmfParser.RealContext):
        pass

    # Exit a parse tree produced by HmfParser#real.
    def exitReal(self, ctx:HmfParser.RealContext):
        pass


    # Enter a parse tree produced by HmfParser#signed_real.
    def enterSigned_real(self, ctx:HmfParser.Signed_realContext):
        pass

    # Exit a parse tree produced by HmfParser#signed_real.
    def exitSigned_real(self, ctx:HmfParser.Signed_realContext):
        pass


    # Enter a parse tree produced by HmfParser#unsigned_real.
    def enterUnsigned_real(self, ctx:HmfParser.Unsigned_realContext):
        pass

    # Exit a parse tree produced by HmfParser#unsigned_real.
    def exitUnsigned_real(self, ctx:HmfParser.Unsigned_realContext):
        pass


    # Enter a parse tree produced by HmfParser#rational.
    def enterRational(self, ctx:HmfParser.RationalContext):
        pass

    # Exit a parse tree produced by HmfParser#rational.
    def exitRational(self, ctx:HmfParser.RationalContext):
        pass


    # Enter a parse tree produced by HmfParser#signed_rational.
    def enterSigned_rational(self, ctx:HmfParser.Signed_rationalContext):
        pass

    # Exit a parse tree produced by HmfParser#signed_rational.
    def exitSigned_rational(self, ctx:HmfParser.Signed_rationalContext):
        pass


    # Enter a parse tree produced by HmfParser#unsigned_rational.
    def enterUnsigned_rational(self, ctx:HmfParser.Unsigned_rationalContext):
        pass

    # Exit a parse tree produced by HmfParser#unsigned_rational.
    def exitUnsigned_rational(self, ctx:HmfParser.Unsigned_rationalContext):
        pass


    # Enter a parse tree produced by HmfParser#integer.
    def enterInteger(self, ctx:HmfParser.IntegerContext):
        pass

    # Exit a parse tree produced by HmfParser#integer.
    def exitInteger(self, ctx:HmfParser.IntegerContext):
        pass


    # Enter a parse tree produced by HmfParser#signed_integer.
    def enterSigned_integer(self, ctx:HmfParser.Signed_integerContext):
        pass

    # Exit a parse tree produced by HmfParser#signed_integer.
    def exitSigned_integer(self, ctx:HmfParser.Signed_integerContext):
        pass


    # Enter a parse tree produced by HmfParser#unsigned_integer.
    def enterUnsigned_integer(self, ctx:HmfParser.Unsigned_integerContext):
        pass

    # Exit a parse tree produced by HmfParser#unsigned_integer.
    def exitUnsigned_integer(self, ctx:HmfParser.Unsigned_integerContext):
        pass


    # Enter a parse tree produced by HmfParser#decimal.
    def enterDecimal(self, ctx:HmfParser.DecimalContext):
        pass

    # Exit a parse tree produced by HmfParser#decimal.
    def exitDecimal(self, ctx:HmfParser.DecimalContext):
        pass


    # Enter a parse tree produced by HmfParser#positive_decimal.
    def enterPositive_decimal(self, ctx:HmfParser.Positive_decimalContext):
        pass

    # Exit a parse tree produced by HmfParser#positive_decimal.
    def exitPositive_decimal(self, ctx:HmfParser.Positive_decimalContext):
        pass


    # Enter a parse tree produced by HmfParser#decimal_exponent.
    def enterDecimal_exponent(self, ctx:HmfParser.Decimal_exponentContext):
        pass

    # Exit a parse tree produced by HmfParser#decimal_exponent.
    def exitDecimal_exponent(self, ctx:HmfParser.Decimal_exponentContext):
        pass


    # Enter a parse tree produced by HmfParser#decimal_fraction.
    def enterDecimal_fraction(self, ctx:HmfParser.Decimal_fractionContext):
        pass

    # Exit a parse tree produced by HmfParser#decimal_fraction.
    def exitDecimal_fraction(self, ctx:HmfParser.Decimal_fractionContext):
        pass


    # Enter a parse tree produced by HmfParser#dot_decimal.
    def enterDot_decimal(self, ctx:HmfParser.Dot_decimalContext):
        pass

    # Exit a parse tree produced by HmfParser#dot_decimal.
    def exitDot_decimal(self, ctx:HmfParser.Dot_decimalContext):
        pass


    # Enter a parse tree produced by HmfParser#exp_integer.
    def enterExp_integer(self, ctx:HmfParser.Exp_integerContext):
        pass

    # Exit a parse tree produced by HmfParser#exp_integer.
    def exitExp_integer(self, ctx:HmfParser.Exp_integerContext):
        pass


    # Enter a parse tree produced by HmfParser#signed_exp_integer.
    def enterSigned_exp_integer(self, ctx:HmfParser.Signed_exp_integerContext):
        pass

    # Exit a parse tree produced by HmfParser#signed_exp_integer.
    def exitSigned_exp_integer(self, ctx:HmfParser.Signed_exp_integerContext):
        pass


    # Enter a parse tree produced by HmfParser#unsigned_exp_integer.
    def enterUnsigned_exp_integer(self, ctx:HmfParser.Unsigned_exp_integerContext):
        pass

    # Exit a parse tree produced by HmfParser#unsigned_exp_integer.
    def exitUnsigned_exp_integer(self, ctx:HmfParser.Unsigned_exp_integerContext):
        pass


    # Enter a parse tree produced by HmfParser#sign.
    def enterSign(self, ctx:HmfParser.SignContext):
        pass

    # Exit a parse tree produced by HmfParser#sign.
    def exitSign(self, ctx:HmfParser.SignContext):
        pass


