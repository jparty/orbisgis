/* Generated By:JJTree: Do not edit this line. ASTSQLOrderByFunction.java Version 4.1 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package org.gdms.sql.parser;

public
class ASTSQLOrderByFunction extends SimpleNode {
  public ASTSQLOrderByFunction(int id) {
    super(id);
  }

  public ASTSQLOrderByFunction(SQLEngine p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(SQLEngineVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=beac02a04080a8ca346212a9a3403a31 (do not edit this line) */