/* Generated By:JJTree: Do not edit this line. ../src/main/java/org/gdms/sql/parser/SQLEngineVisitor.java */

package org.gdms.sql.parser;

public interface SQLEngineVisitor
{
  public Object visit(SimpleNode node, Object data);
  public Object visit(ASTSQLAndExpr node, Object data);
  public Object visit(ASTSQLBetweenClause node, Object data);
  public Object visit(ASTSQLColRef node, Object data);
  public Object visit(ASTSQLCompareExpr node, Object data);
  public Object visit(ASTSQLCompareExprRight node, Object data);
  public Object visit(ASTSQLCompareOp node, Object data);
  public Object visit(ASTSQLCall node, Object data);
  public Object visit(ASTSQLCallFrom node, Object data);
  public Object visit(ASTSQLCallArgLiteral node, Object data);
  public Object visit(ASTSQLCallArgs node, Object data);
  public Object visit(ASTSQLDelete node, Object data);
  public Object visit(ASTSQLExistsClause node, Object data);
  public Object visit(ASTSQLFunction node, Object data);
  public Object visit(ASTSQLFunctionArgs node, Object data);
  public Object visit(ASTSQLGroupBy node, Object data);
  public Object visit(ASTSQLInClause node, Object data);
  public Object visit(ASTSQLInsert node, Object data);
  public Object visit(ASTSQLIsClause node, Object data);
  public Object visit(ASTSQLLeftJoinClause node, Object data);
  public Object visit(ASTSQLLikeClause node, Object data);
  public Object visit(ASTSQLLiteral node, Object data);
  public Object visit(ASTSQLLvalue node, Object data);
  public Object visit(ASTSQLLvalueTerm node, Object data);
  public Object visit(ASTSQLNotExpr node, Object data);
  public Object visit(ASTSQLOrderBy node, Object data);
  public Object visit(ASTSQLOrderByElem node, Object data);
  public Object visit(ASTSQLOrderByList node, Object data);
  public Object visit(ASTSQLOrderDirection node, Object data);
  public Object visit(ASTSQLOrExpr node, Object data);
  public Object visit(ASTSQLPattern node, Object data);
  public Object visit(ASTSQLProductExpr node, Object data);
  public Object visit(ASTSQLRightJoinClause node, Object data);
  public Object visit(ASTSQLUnion node, Object data);
  public Object visit(ASTSQLSelect node, Object data);
  public Object visit(ASTSQLSelectCols node, Object data);
  public Object visit(ASTSQLSelectList node, Object data);
  public Object visit(ASTSQLStatement node, Object data);
  public Object visit(ASTSQLSumExpr node, Object data);
  public Object visit(ASTSQLTableList node, Object data);
  public Object visit(ASTSQLTableRef node, Object data);
  public Object visit(ASTSQLTerm node, Object data);
  public Object visit(ASTSQLUnaryExpr node, Object data);
  public Object visit(ASTSQLUpdate node, Object data);
  public Object visit(ASTSQLUpdateAssignment node, Object data);
  public Object visit(ASTSQLLValueElement node, Object data);
  public Object visit(ASTSQLLValueList node, Object data);
  public Object visit(ASTSQLWhere node, Object data);
  public Object visit(ASTSQLCreate node, Object data);
  public Object visit(ASTSQLCreateArgsList node, Object data);
  public Object visit(ASTSQLColumnDefinition node, Object data);
  public Object visit(ASTSQLDataTypeConstraint node, Object data);
}
