grammar calculator;
@header {package pl.edu.pwr.lab;}

equation
   : (instruct)+ EOF
   ;

instruct
   : alg_expression
   | statement
   | decl
   | shellInstruct
   | funcDecl
   | funCall
   ;

decl
  : varName=variable '=' (alg_expression | assignmentVar=variable)
  ;

alg_expression
   : multiplyingExpression ((PLUS | MINUS) multiplyingExpression)*
   ;

statement
   : 'if' '(' condition=alg_expression ') [' ifExpr=instruct ']' ('else' '[' elseExpr=instruct+ ']')? #IfStatement
   | 'while' '(' condition=alg_expression ') [' instruct+ ']' #WhileStatement
   | 'for' '(' condition=alg_expression ') [' instruct+ ']' #ForStatement
   ;

multiplyingExpression
   : powExpression ((TIMES | DIV) powExpression)*
   ;

funcDecl
  : 'fun' variable '() [' instruct ']' #ArgLessFunction
  | 'fun' variable '(' variable+? ') [' instruct ']' #ArgFullFunction
  ;

funCall
  : variable '()' #ArgLessFunctionCall
  | variable '('variable+?')' #ArgFullFunctionCall
  ;

shellInstruct
  : 'run [' STRING ']'
  ;

powExpression
   : signedAtom (POW signedAtom)*
   ;

signedAtom
   : PLUS signedAtom
   | MINUS signedAtom
   | atom
   ;

atom
   : scientific
   | variable
   | LPAREN instruct RPAREN
   ;

scientific
   : SCIENTIFIC_NUMBER
   ;

STRING
  : '"' STRING_VAL '"'
  ;
fragment STRING_VAL
  : ( '\\' [\\"] | ~[\\"\r\n] )*
  ;

variable
   : VARIABLE
   ;

relop
   : EQ
   ;

LPAREN
   : '('
   ;

RPAREN
   : ')'
   ;


PLUS
   : '+'
   ;


MINUS
   : '-'
   ;


TIMES
   : '*'
   ;


DIV
   : '/'
   ;


GT
   : '>'
   ;


LT
   : '<'
   ;


EQ
   : '='
   ;


POW
   : '^'
   ;


VARIABLE
   : VALID_ID_START VALID_ID_CHAR*
   ;


fragment VALID_ID_START
   : ('a' .. 'z') | ('A' .. 'Z') | '_'
   ;


fragment VALID_ID_CHAR
   : VALID_ID_START | ('0' .. '9')
   ;


SCIENTIFIC_NUMBER
   : NUMBER ((E1 | E2) SIGN? NUMBER)?
   ;


fragment NUMBER
   : ('0' .. '9') + ('.' ('0' .. '9') +)?
   ;


fragment E1
   : 'E'
   ;


fragment E2
   : 'e'
   ;


fragment SIGN
   : ('+' | '-')
   ;


WS
   : [ \r\n\t] + -> skip
   ;
