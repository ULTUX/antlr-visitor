package pl.edu.pwr.lab;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import pl.edu.pwr.lab.calculatorParser.Alg_expressionContext;
import pl.edu.pwr.lab.calculatorParser.ArgLessFunctionCallContext;
import pl.edu.pwr.lab.calculatorParser.ArgLessFunctionContext;
import pl.edu.pwr.lab.calculatorParser.DeclContext;
import pl.edu.pwr.lab.calculatorParser.IfStatementContext;
import pl.edu.pwr.lab.calculatorParser.InstructContext;
import pl.edu.pwr.lab.calculatorParser.MultiplyingExpressionContext;
import pl.edu.pwr.lab.calculatorParser.PowExpressionContext;
import pl.edu.pwr.lab.calculatorParser.ScientificContext;
import pl.edu.pwr.lab.calculatorParser.ShellInstructContext;
import pl.edu.pwr.lab.calculatorParser.SignedAtomContext;
import pl.edu.pwr.lab.calculatorParser.VariableContext;
import pl.edu.pwr.lab.calculatorParser.WhileStatementContext;

public class CalculatorVisitorImpl extends calculatorBaseVisitor<Float> {

  private Logger logger = Logger.getLogger(this.getClass().getName());
  public Map<String, Float> declaratedVars = new HashMap<>();
  public Map<String, InstructContext> declaredFuncs = new HashMap<>();

  @Override
  public Float visitIfStatement(IfStatementContext ctx) {
    float cond = visit(ctx.condition);
    if (cond != 0) {
      return visit(ctx.ifExpr);
    } else {
      return visit(ctx.elseExpr);
    }
  }

  @Override
  public Float visitWhileStatement(WhileStatementContext ctx) {
    float condition = visit(ctx.condition);
    while (condition != 0) {
      for (var inst : ctx.instruct()) {
        visit(inst);
      }
      condition = visit(ctx.condition);
    }
    return null;
  }

  @Override
  public Float visitShellInstruct(ShellInstructContext ctx) {
    String command = ctx.STRING().getText();
    command = command.substring(1, command.length() - 1);
    ProcessBuilder builder = new ProcessBuilder();
    builder
        .inheritIO()
        .command("sh", "-c", command);
    try {
      builder.start();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return null;
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
  public Float visitDecl(DeclContext ctx) {
    var varName = ctx.varName.getText();
    Float assignment =
        ctx.assignmentVar == null ? visit(ctx.alg_expression()) : visit(ctx.assignmentVar);
    declaratedVars.put(varName, assignment);
    return assignment;
  }

  @Override
  public Float visitVariable(VariableContext ctx) {
    return declaratedVars.get(ctx.VARIABLE().getText());
  }

  @Override
  public Float visitScientific(ScientificContext ctx) {
    return Float.parseFloat(ctx.getText());
  }

  @Override
  public Float visitArgLessFunction(ArgLessFunctionContext ctx) {
    String funName = ctx.variable().getText();
    declaredFuncs.put(funName, ctx.instruct());
    return null;
  }

  @Override
  public Float visitArgLessFunctionCall(ArgLessFunctionCallContext ctx) {
    String funName = ctx.variable().getText();
    visit(declaredFuncs.get(funName));
    return null;
  }
}
