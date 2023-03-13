package pl.edu.pwr.lab;

import java.util.logging.Level;
import java.util.logging.Logger;
import pl.edu.pwr.lab.calculatorParser.EquationContext;
import pl.edu.pwr.lab.calculatorParser.ExpressionContext;
import pl.edu.pwr.lab.calculatorParser.MultiplyingExpressionContext;
import pl.edu.pwr.lab.calculatorParser.PowExpressionContext;
import pl.edu.pwr.lab.calculatorParser.ScientificContext;
import pl.edu.pwr.lab.calculatorParser.SignedAtomContext;

public class CalculatorVisitorImpl extends calculatorBaseVisitor<Float> {

  Logger logger = Logger.getLogger(this.getClass().getName());

  @Override
  public Float visitEquation(EquationContext ctx) {
    return visitExpression(ctx.expression(0));
  }

  @Override
  public Float visitExpression(ExpressionContext ctx) {
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
        case "*":
          result *= visitPowExpression((PowExpressionContext) children[i + 2]);
          break;
        case "/":
          result /= visitPowExpression((PowExpressionContext) children[i + 2]);
          break;
        default:
      }
    }
    return result;
  }

  @Override
  public Float visitPowExpression(PowExpressionContext ctx) {
    var children = ctx.children.toArray(new org.antlr.v4.runtime.tree.ParseTree[0]);
    var result = visitSignedAtom((SignedAtomContext) children[children.length - 1]);

    for (int i = children.length - 1; i > 1; i -= 2) {
      switch (children[i - 1].getText()) {
        case "^":
          result = (float) Math.pow(visitSignedAtom((SignedAtomContext) children[i - 2]), result);
          break;
        default:
      }
    }
    return result;
  }

  @Override
  public Float visitScientific(ScientificContext ctx) {
    logger.log(Level.INFO, "Visiting number: {0}", ctx.getText());
    return Float.parseFloat(ctx.getText());
  }
}
