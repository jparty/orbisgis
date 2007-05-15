package org.gdms.sql.function.spatial.geometryProperties;

import org.gdms.data.values.NumericValue;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.spatial.GeometryValue;
import org.gdms.spatial.PTTypes;
import org.gdms.sql.function.Function;
import org.gdms.sql.function.FunctionException;

import com.vividsolutions.jts.geom.Geometry;

public class Length implements Function {

	public Function cloneFunction() {
		return new Length();
	}

	public Value evaluate(Value[] args) throws FunctionException {
		GeometryValue gv = (GeometryValue) args[0];		
		return ValueFactory.createValue(gv.getGeom().getLength());
	}

	public String getName() {
		return "Length";
	}

	public int getType() {
		return Value.DOUBLE;
	}

	public boolean isAggregate() {
		return false;
	}

}
