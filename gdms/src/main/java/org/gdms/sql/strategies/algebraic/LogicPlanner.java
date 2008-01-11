/*
 * The GDMS library (Generic Datasources Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...). GDMS is produced  by the geomatic team of the IRSTV
 * Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of GDMS.
 *
 * GDMS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GDMS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with GDMS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-developers/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-users/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.gdms.sql.strategies.algebraic;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.file.FileSourceDefinition;
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.sql.evaluator.EvaluationContext;
import org.gdms.sql.evaluator.Expression;
import org.gdms.sql.evaluator.Field;
import org.gdms.sql.evaluator.Sum;
import org.gdms.sql.instruction.SemanticException;
import org.gdms.sql.parser.ASTSQLAndExpr;
import org.gdms.sql.parser.ASTSQLBetweenClause;
import org.gdms.sql.parser.ASTSQLColRef;
import org.gdms.sql.parser.ASTSQLCompareExpr;
import org.gdms.sql.parser.ASTSQLInClause;
import org.gdms.sql.parser.ASTSQLLeftJoinClause;
import org.gdms.sql.parser.ASTSQLLikeClause;
import org.gdms.sql.parser.ASTSQLNotExpr;
import org.gdms.sql.parser.ASTSQLOrExpr;
import org.gdms.sql.parser.ASTSQLProductExpr;
import org.gdms.sql.parser.ASTSQLRightJoinClause;
import org.gdms.sql.parser.ASTSQLSelect;
import org.gdms.sql.parser.ASTSQLSelectCols;
import org.gdms.sql.parser.ASTSQLSelectList;
import org.gdms.sql.parser.ASTSQLSumExpr;
import org.gdms.sql.parser.ASTSQLTableList;
import org.gdms.sql.parser.ASTSQLUnaryExpr;
import org.gdms.sql.parser.ASTSQLWhere;
import org.gdms.sql.parser.Node;
import org.gdms.sql.parser.SQLEngine;
import org.gdms.sql.parser.SQLEngineConstants;
import org.gdms.sql.parser.SimpleNode;
import org.gdms.sql.parser.Token;

public class LogicPlanner {

	private DataSourceFactory dsf;

	private EvaluationContext ec;

	public LogicPlanner(DataSourceFactory dsf) {
		this.dsf = dsf;
	}

	public static void main(String[] args) throws Exception {
		DataSourceFactory dsf = new DataSourceFactory();
		dsf.registerDataSource("data", new FileSourceDefinition(
				"src/test/resources/alltypes.dbf"));
		String sql = "select FDECIMAL+FDECIMAL as decimal, FSTR as mystr from data;";

		SQLEngine parser = new SQLEngine(new ByteArrayInputStream(sql
				.getBytes()));

		parser.SQLStatement();
		LogicPlanner lp = new LogicPlanner(dsf);
		Operator op = (Operator) lp
				.buildTree((SimpleNode) parser.getRootNode());
		DataSource ds = op.getDataSource();
		System.out.println(op);
		ds.open();
		System.out.println(ds.getAsString());
		ds.cancel();

		ds = dsf.getDataSource("data");
		ds.open();
		System.out.println(ds.getAsString());
		ds.cancel();
	}

	public Operator buildTree(SimpleNode rootNode) throws DriverLoadException,
			SemanticException, NoSuchTableException,
			DataSourceCreationException {
		ec = new EvaluationContext(null, 0);
		return getOperator(rootNode);
	}

	/**
	 * Recursive method that returns the root of the tree of logic operators
	 * that implement the query. Returns null if no operator has to be applied
	 *
	 * @param node
	 * @return
	 * @throws SemanticException
	 * @throws DataSourceCreationException
	 * @throws NoSuchTableException
	 * @throws DriverLoadException
	 */
	private Operator getOperator(Node theNode) throws SemanticException,
			DriverLoadException, NoSuchTableException,
			DataSourceCreationException {
		SimpleNode node = (SimpleNode) theNode;

		if (node instanceof ASTSQLSelect) {
			Operator ret;
			// Scalar product
			Operator escalarProductOp = getOperator(node.jjtGetChild(1));
			ret = escalarProductOp;
			if (node.jjtGetNumChildren() == 3) {
				// Selection of records
				Operator selectionOp = getOperator(node.jjtGetChild(2));
				selectionOp.addChild(escalarProductOp);
				ret = selectionOp;
			}
			// Projection
			Operator projOp = getOperator(node.jjtGetChild(0));
			if (projOp != null) {
				projOp.addChild(ret);
				ret = projOp;
			}

			return ret;
		} else if (node instanceof ASTSQLSelectCols) {
			if (node.first_token.image.equals("*")) {
				ProjectionOp ret = new ProjectionOp();
				return ret;
			} else {
				// SQLSelectList
				return getOperator(node.jjtGetChild(0));
			}
		} else if (node instanceof ASTSQLSelectList) {
			ArrayList<Expression> exprs = new ArrayList<Expression>();
			ArrayList<String> aliases = new ArrayList<String>();
			for (int i = 0; i < node.jjtGetNumChildren(); i++) {
				SimpleNode childNode = (SimpleNode) node.jjtGetChild(i);
				exprs.add(getSQLExpression(childNode));

				String alias = null;
				Token limit = node.last_token;
				if (i < node.jjtGetNumChildren() - 1) {
					limit = ((SimpleNode) node.jjtGetChild(i + 1)).first_token;
				}
				Token token = childNode.first_token;
				while (token != limit) {
					if (token.kind == SQLEngineConstants.AS) {
						alias = token.next.image;
						token = limit;
					} else {
						token = token.next;
					}
				}
				aliases.add(alias);
			}
			ProjectionOp ret = new ProjectionOp(exprs
					.toArray(new Expression[0]), aliases.toArray(new String[0]));
			return ret;
		} else if (node instanceof ASTSQLTableList) {
			ScalarProductOp ret = new ScalarProductOp();
			for (int i = 0; i < node.jjtGetNumChildren(); i++) {
				SimpleNode child = (SimpleNode) node.jjtGetChild(i);
				String tableRef = child.first_token.image;
				String alias = null;
				if (child.first_token != child.last_token) {
					alias = child.last_token.image;
				}
				ret.addTable(dsf, tableRef, alias);
			}
			return ret;
		} else if (node instanceof ASTSQLWhere) {
			SelectionOp ret = new SelectionOp();
			ret.setExpression((Expr) getOperator(node.jjtGetChild(0)));
			return ret;
		} else if (node instanceof ASTSQLOrExpr) {
			if (node.jjtGetNumChildren() == 1) {
				return getOperator(node.jjtGetChild(0));
			} else {
				OrExpr ret = new OrExpr();
				ret.addChilds(getChildOperators(node));
				return ret;
			}
		} else if (node instanceof ASTSQLAndExpr) {
			if (node.jjtGetNumChildren() == 1) {
				return getOperator(node.jjtGetChild(0));
			} else {
				AndExpr ret = new AndExpr();
				ret.addChilds(getChildOperators(node));
				return ret;
			}
		} else if (node instanceof ASTSQLNotExpr) {
			if (node.first_token.kind != SQLEngineConstants.NOT) {
				return getOperator(node.jjtGetChild(0));
			} else {
				NotExpr ret = new NotExpr();
				ret.addChild(getOperator(node));
				return ret;
			}
		} else if (node instanceof ASTSQLCompareExpr) {
			if (node.jjtGetNumChildren() == 1) {
				return getOperator(node.jjtGetChild(0));
			} else {
				// Comparison
				SimpleNode compareExprRight = (SimpleNode) node.jjtGetChild(1);
				if (compareExprRight instanceof ASTSQLLikeClause) {
					LikeOp ret = new LikeOp();
					ret.setExpression((Expr) getOperator(node.jjtGetChild(0)));
					ret.setPattern((Expr) getOperator(compareExprRight
							.jjtGetChild(0)));
					return ret;
				} else if (compareExprRight instanceof ASTSQLInClause) {
					throw new RuntimeException();
				} else if (compareExprRight instanceof ASTSQLLeftJoinClause) {
					throw new RuntimeException();
				} else if (compareExprRight instanceof ASTSQLRightJoinClause) {
					throw new RuntimeException();
				} else if (compareExprRight instanceof ASTSQLBetweenClause) {
					throw new RuntimeException();
				} else {
					SimpleNode operator = (SimpleNode) compareExprRight
							.jjtGetChild(0);
					ComparisonOp ret = new ComparisonOp();
					ret.setArithmeticOperator(operator.first_token.kind);
					ret.setLeftExpression((Expr) getOperator(node
							.jjtGetChild(0)));
					ret.setRightExpression((Expr) getOperator(compareExprRight
							.jjtGetChild(1)));
					return ret;
				}
			}
		} else if (node instanceof ASTSQLProductExpr) {
			if (node.jjtGetNumChildren() == 1) {
				return getOperator(node.jjtGetChild(0));
			} else {
				ProductOp ret = new ProductOp();
				Operator[] expressions = getChildOperators(node);
				ret.addChilds(expressions);
				return ret;
			}
		} else if (node instanceof ASTSQLUnaryExpr) {
			if (!node.first_token.image.equals("-")) {
				return getOperator(node.jjtGetChild(0));
			} else {
				NegativeOp ret = new NegativeOp();
				ret.addChild(getOperator(node));
				return ret;
			}
			// } else if (node instanceof ASTSQLLvalueTerm) {
			// Field ret = new Field(node.first_token.image);
			// return ret;
			// } else if (node instanceof ASTSQLLiteral) {
			// LiteralOp ret = new LiteralOp();
			// ret.setLiteral(ValueFactory.createValue(node.first_token.image,
			// node.first_token.kind));
			// return ret;
		} else {
			/*
			 * Default behavior is by-pass the node
			 */
			if (node.jjtGetNumChildren() != 1) {
				throw new RuntimeException("If a node has not "
						+ " one and only one child it has to do "
						+ "a specific action: " + node);
			}
			return getOperator(node.jjtGetChild(0));
		}
	}

	private Expression getSQLExpression(SimpleNode childNode) {
		Expression ret = getExpression(childNode);
		ret.setEvaluationContext(ec);

		return ret;
	}

	private Expression getExpression(Node theNode) {
		SimpleNode node = (SimpleNode) theNode;
		if (node instanceof ASTSQLSumExpr) {
			// Get expressions
			if (node.jjtGetNumChildren() > 1) {
				Expression left = getExpression(node.jjtGetChild(0));
				Expression right = getExpression(node.jjtGetChild(1));
				return new Sum(left, right);
			}
		} else if (node instanceof ASTSQLColRef) {
			if (node.first_token == node.last_token) {
				return new Field(node.first_token.image);
			} else {
				return new Field(node.first_token.image, node.last_token.image);
			}
		}

		if (node.jjtGetNumChildren() == 1) {
			return getExpression((SimpleNode) node.jjtGetChild(0));
		} else {
			throw new RuntimeException("not implemented: " + node);
		}
	}

	private Operator[] getChildOperators(SimpleNode node)
			throws SemanticException, DriverLoadException,
			NoSuchTableException, DataSourceCreationException {
		Operator[] ret = new Operator[node.jjtGetNumChildren()];
		for (int i = 0; i < node.jjtGetNumChildren(); i++) {
			ret[i] = getOperator(node.jjtGetChild(i));
		}

		return ret;
	}

}