grammar calculator;
@header {package pl.edu.pwr.lab;}

equation
   : (instruct)+ EOF
   ;

instruct
   : alg_expression
   | statement
   | decl
   | funcDecl
   ;

funcDecl
   : 'fun' variable '() [' instruct ']' #ArgLessFunction
   | 'fun' variable '(' variable+? ') [' instruct ']' #ArgFullFunction
 ;

decl
   : varName=variable '=' alg_expression
 ;

statement
    : 'if' '(' condition=alg_expression ') [' ifExpr=instruct ']' ('else' '[' elseExpr=instruct+ ']')? #IfStatement
    | 'while' '(' condition=alg_expression ') [' instruct+ ']' #WhileStatement
    | 'for' '(' condition=alg_expression ') [' instruct+ ']' #ForStatement
    ;

alg_expression
   : multiplyingExpression (PLUS | MINUS) alg_expression #Alg
   | multiplyingExpression #NoAgl
   ;

multiplyingExpression
   : powExpression (TIMES | DIV) multiplyingExpression #Mlt
   | powExpression #NoMlt
   ;
powExpression
   : signedAtom POW powExpression #Pow
   | signedAtom #NoPow
   ;

signedAtom
   : SCIENTIFIC_NUMBER
   | variable
   ;

variable
   : VARIABLE
   ;

fragment STRING_VAL
  : ( '\\' [\\"] | ~[\\"\r\n] )*
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
