package org.gdms.sql.function.spatial.operators;

import org.gdms.data.values.NumericValue;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.spatial.GeometryValue;
import org.gdms.spatial.PTTypes;
import org.gdms.sql.function.Function;
import org.gdms.sql.function.FunctionException;

import com.vividsolutions.jts.geom.Geometry;

public class Intersection implements Function {

	public Function cloneFunction() {
		return new Intersection();
	}

	public Value evaluate(Value[] args) throws FunctionException {
		GeometryValue gv = (GeometryValue) args[0];
		GeometryValue gv1 = (GeometryValue) args[1];
		Geometry intersection = gv.getGeom().intersection(gv1.getGeom());
		return ValueFactory.createValue(intersection);
	}

	public String getName() {
		return "Intersection";
	}

	public int getType() {
		return PTTypes.GEOMETRY;
	}

	public boolean isAggregate() {
		return false;
	}

}
