package pl.edu.pwr.lab;

import java.util.HashMap;
import java.util.Map;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import pl.edu.pwr.lab.calculatorParser.ArgLessFunctionContext;
import pl.edu.pwr.lab.calculatorParser.DeclContext;
import pl.edu.pwr.lab.calculatorParser.IfStatementContext;
import pl.edu.pwr.lab.calculatorParser.MltContext;
import pl.edu.pwr.lab.calculatorParser.NoAglContext;
import pl.edu.pwr.lab.calculatorParser.NoMltContext;
import pl.edu.pwr.lab.calculatorParser.PowContext;
import pl.edu.pwr.lab.calculatorParser.SignedAtomContext;
import pl.edu.pwr.lab.calculatorParser.VariableContext;
import pl.edu.pwr.lab.calculatorParser.WhileStatementContext;

public class EmitVisitor extends calculatorBaseVisitor<ST> {

  private STGroup group;
  private Map<String, Integer> varPos = new HashMap<>();
  private int currPtr = 0;

  public EmitVisitor(STGroup group) {
    super();
    this.group = group;
  }

  @Override
  protected ST defaultResult() {
    return group.getInstanceOf("deflt");
  }

  @Override
  protected ST aggregateResult(ST aggregate, ST nextResult) {
    if (nextResult != null) {
      aggregate.add("elem", nextResult);
    }
    return aggregate;
  }

  @Override
  public ST visitDecl(DeclContext ctx) {
    var st = group.getInstanceOf("deklaracja");
    currPtr += 4;
    varPos.put(ctx.varName.getText(), currPtr);
    st.add("x", visit(ctx.alg_expression())).add("pos", currPtr);
    return st;
  }

  @Override
  public ST visitVariable(VariableContext ctx) {
    var st = group.getInstanceOf("zmienna");
    var vari = ctx.VARIABLE().getText();
    st.add("pos", varPos.get(vari));
    return st;
  }

  @Override
  public ST visitAlg(pl.edu.pwr.lab.calculatorParser.AlgContext ctx) {
    var st = group.getInstanceOf("dodaj");
    st
        .add("p1", visit(ctx.alg_expression()))
        .add("p2", visit(ctx.multiplyingExpression()));
    return st;
  }

  @Override
  public ST visitNoAgl(NoAglContext ctx) {
    return visit(ctx.multiplyingExpression());
  }

  @Override
  public ST visitIfStatement(IfStatementContext ctx) {
    var cond = visit(ctx.condition);
    var ifTrue = visit(ctx.ifExpr);
    var ifFalse = visit(ctx.elseExpr);
    var st = group.getInstanceOf("jesli");
    st.add("cond", cond)
        .add("ifTrue", ifTrue)
        .add("ifFalse", ifFalse);
    return st;
  }

  @Override
  public ST visitMlt(MltContext ctx) {
    var st = group.getInstanceOf("pomnoz");
    st
        .add("p1", visit(ctx.powExpression()))
        .add("p2", visit(ctx.multiplyingExpression()));
    return st;
  }

  @Override
  public ST visitNoMlt(NoMltContext ctx) {
    return visit(ctx.powExpression());
  }

  @Override
  public ST visitPow(PowContext ctx) {
    var st = group.getInstanceOf("potega");
    st
        .add("p1", visit(ctx.signedAtom()))
        .add("p2", visit(ctx.powExpression()));
    return st;
  }

  @Override
  public ST visitSignedAtom(SignedAtomContext ctx) {
    ST st;
    if (ctx.SCIENTIFIC_NUMBER() == null) {
      st = visit(ctx.variable());
    }
    else {
      st = group.getInstanceOf("int");
      st.add("i", ctx.getText());
    }
    return st;
  }

  @Override
  public ST visitWhileStatement(WhileStatementContext ctx) {
    var cond = visit(ctx.condition);
    var instruct = ctx.instruct().stream().map(this::visit);
    var st = group.getInstanceOf("while");
    st.add("cond", cond);
    instruct.forEach(x -> st.add("instruct", x));
    return st;
  }

  @Override
  public ST visitArgLessFunction(ArgLessFunctionContext ctx) {
    var funName = ctx.variable().getText();
  }
}
