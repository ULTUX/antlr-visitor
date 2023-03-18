package pl.edu.pwr.lab;

import pl.edu.pwr.lab.calculatorParser.*;

import java.util.logging.Level;
import java.util.logging.Logger;

public class CalculatorVisitorImpl extends calculatorBaseVisitor<Float> {

    Logger logger = Logger.getLogger(this.getClass().getName());

    @Override
    public Float visitEquation(EquationContext ctx) {
        for (ExpressionContext exp : ctx.expression()) {
            if (exp.alg_expression() != null) visitAlg_expression(exp.alg_expression());
            else if (exp.statement() != null) {
                var statCtx = exp.statement();
                if (statCtx instanceof If_statContext) return visitIf_stat((If_statContext) statCtx);
                if (statCtx instanceof For_statContext) return visitFor_stat((For_statContext) statCtx);
                if (statCtx instanceof While_statContext) return visitWhile_stat((While_statContext) statCtx);
            }
        }
        return null;
    }

    @Override
    public Float visitIf_stat(If_statContext ctx) {
        float cond = visit(ctx.condition);
        if (cond != 0) {
            return visit(ctx.ifExpr);
        } else {
            return visit(ctx.elseExpr);
        }
    }


    @Override
    public Float visitAlg_expression(Alg_expressionContext ctx) {
        var children = ctx.children.toArray(new org.antlr.v4.runtime.tree.ParseTree[0]);
        var result = visitMultiplyingExpression((MultiplyingExpressionContext) children[0]);

        for (int i = 0; i < children.length - 1; i += 2) {
            switch (children[i + 1].getText()) {
                case "+":
                    result += visitMultiplyingExpression((MultiplyingExpressionContext) children[i + 2]);
                    break;
                case "-":
                    result -= visitMultiplyingExpression((MultiplyingExpressionContext) children[i + 2]);
                    break;
                default:
            }
        }
        return result;
    }

    @Override
    public Float visitMultiplyingExpression(MultiplyingExpressionContext ctx) {
        var children = ctx.children.toArray(new org.antlr.v4.runtime.tree.ParseTree[0]);
        var result = visitPowExpression((PowExpressionContext) children[0]);

        for (int i = 0; i < children.length - 1; i += 2) {
            switch (children[i + 1].getText()) {
                case "*" -> result *= visitPowExpression((PowExpressionContext) children[i + 2]);
                case "/" -> result /= visitPowExpression((PowExpressionContext) children[i + 2]);
                default -> {
                }
            }
        }
        return result;
    }

    @Override
    public Float visitPowExpression(PowExpressionContext ctx) {
        var children = ctx.children.toArray(new org.antlr.v4.runtime.tree.ParseTree[0]);
        var result = visitSignedAtom((SignedAtomContext) children[children.length - 1]);

        for (int i = children.length - 1; i > 1; i -= 2) {
            if (children[i - 1].getText().equals("^")) {
                result = (float) Math.pow(visitSignedAtom((SignedAtomContext) children[i - 2]), result);
            }
        }
        return result;
    }

    @Override
    public Float visitScientific(ScientificContext ctx) {
        return Float.parseFloat(ctx.getText());
    }
}
