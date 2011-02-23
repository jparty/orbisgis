package org.orbisgis.core.renderer.se.stroke;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.RenderedImage;

import java.io.IOException;
import java.util.ArrayList;

import javax.media.jai.RenderableGraphics;
import javax.xml.bind.JAXBElement;
import org.gdms.data.SpatialDataSourceDecorator;
import org.orbisgis.core.map.MapTransform;

import org.orbisgis.core.renderer.persistance.se.GraphicStrokeType;
import org.orbisgis.core.renderer.persistance.se.ObjectFactory;
import org.orbisgis.core.renderer.se.GraphicNode;
import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;

import org.orbisgis.core.renderer.se.common.RelativeOrientation;
import org.orbisgis.core.renderer.se.common.ShapeHelper;
import org.orbisgis.core.renderer.se.common.Uom;
import org.orbisgis.core.renderer.se.graphic.GraphicCollection;
import org.orbisgis.core.renderer.se.graphic.MarkGraphic;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.SeParameterFactory;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;
import org.orbisgis.core.renderer.se.parameter.real.RealParameterContext;

public final class GraphicStroke extends Stroke implements GraphicNode {

    public final static double MIN_LENGTH = 1; // In pixel !
    private GraphicCollection graphic;
    private RealParameter length;
    private RelativeOrientation orientation;

    GraphicStroke(JAXBElement<GraphicStrokeType> elem) throws InvalidStyle {
        super();
        GraphicStrokeType gst = elem.getValue();

        if (gst.getGraphic() != null) {
            this.setGraphicCollection(new GraphicCollection(gst.getGraphic(), this));
        }

        if (gst.getLength() != null) {
            this.setLength(SeParameterFactory.createRealParameter(gst.getLength()));
        }

        if (gst.getRelativeOrientation() != null) {
            this.setRelativeOrientation(RelativeOrientation.readFromToken(gst.getRelativeOrientation().value()));
        } else {
            this.setRelativeOrientation(RelativeOrientation.NORMAL);
        }

    }

    public GraphicStroke() {
        super();
        this.graphic = new GraphicCollection();
        MarkGraphic mg = new MarkGraphic();
        mg.setTo3mmCircle();
        graphic.addGraphic(mg);
    }

    @Override
    public void setGraphicCollection(GraphicCollection graphic) {
        this.graphic = graphic;
    }

    @Override
    public GraphicCollection getGraphicCollection() {
        return graphic;
    }

    public void setLength(RealParameter length) {
        this.length = length;
        if (this.length != null) {
            this.length.setContext(RealParameterContext.nonNegativeContext);
        }
    }

    public RealParameter getLength() {
        return length;
    }

    public void setRelativeOrientation(RelativeOrientation orientation) {
        this.orientation = orientation;
    }

    public RelativeOrientation getRelativeOrientation() {
        if (orientation != null) {
            return orientation;
        } else {
            return RelativeOrientation.PORTRAYAL;
        }
    }

    @Override
    public double getNaturalLength(SpatialDataSourceDecorator sds, long fid, Shape shp, MapTransform mt) throws ParameterException, IOException {

        double naturalLength;
        double lineLength = ShapeHelper.getLineLength(shp);

        RelativeOrientation rOrient = this.getRelativeOrientation();

        if (length != null) {

            naturalLength = Uom.toPixel(length.getValue(sds, fid), getUom(), mt.getDpi(), mt.getScaleDenominator(), lineLength); // TODO 100%
            if (naturalLength <= GraphicStroke.MIN_LENGTH || naturalLength > lineLength) {
                naturalLength = lineLength;
            }
        } else {
            RenderableGraphics g;
            g = graphic.getGraphic(sds, fid, false, mt);
            double gWidth = g.getWidth();
            double gHeight = g.getHeight();

            switch (rOrient) {
                case NORMAL:
                case NORMAL_UP:
                    naturalLength = gWidth;
                    break;
                case LINE:
                case LINE_UP:
                    naturalLength = gHeight;
                    break;
                case PORTRAYAL:
                default:
                    naturalLength = Math.sqrt(gWidth * gWidth + gHeight * gHeight);
                    break;

            }
        }
        return naturalLength;

    }

    @Override
    public void draw(Graphics2D g2, SpatialDataSourceDecorator sds, long fid,
            Shape shape, boolean selected, MapTransform mt, double offset)
            throws ParameterException, IOException {
        RenderableGraphics g = graphic.getGraphic(sds, fid, selected, mt);
        RenderedImage createRendering = g.createRendering(mt.getCurrentRenderContext());

        if (g != null) {

            ArrayList<Shape> shapes;
            // if not using offset rapport, compute perpendiculat offset first
            if (!this.isOffsetRapport() && Math.abs(offset) > 0.0) {
                shapes = ShapeHelper.perpendicularOffset(shape, offset);
                // Setting offset to 0.0 let be sure the offset will never been applied twice!
                offset = 0.0;
            } else {
                shapes = new ArrayList<Shape>();
                shapes.add(shape);
            }


            for (Shape shp : shapes) {
                double segLength = getNaturalLength(sds, fid, shp, mt);

                double gWidth = g.getWidth();
                double lineLength = ShapeHelper.getLineLength(shp);

                RelativeOrientation rOrient = this.getRelativeOrientation();
                ArrayList<Shape> segments = null;


                double nbSegments;

                int nbToDraw;

                if (this.isLengthRapport()) {
                    nbSegments = (int) ((lineLength / segLength) + 0.5);
                    segments = ShapeHelper.splitLine(shp, (int) nbSegments);
                    segLength = lineLength / nbSegments;
                    nbToDraw = (int) nbSegments;
                } else {
                    nbSegments = lineLength / segLength;
                    segLength = lineLength / nbSegments;
                    // Effective number of graphic to draw (skip the last one if not space left...)
                    nbToDraw = (int) nbSegments;
                    if (nbToDraw > 0) {
                        // TODO remove half of extra space at the beginning of the line
                        //shp = ShapeHelper.splitLine(shp, (nbSegments - nbToDraw)/2.0).get(1);
                        segments = ShapeHelper.splitLineInSeg(shp, segLength);
                    }
                }

                int i = 0;
                if (segments != null) {
                    for (Shape seg : segments) {
                        if (i == nbToDraw) {
                            break;
                        }
                        i++;

                        ArrayList<Shape> oSegs;
                        if (this.isOffsetRapport() && Math.abs(offset) > 0.0) {
                            oSegs = ShapeHelper.perpendicularOffset(seg, offset);
                        } else {
                            oSegs = new ArrayList<Shape>();
                            oSegs.add(seg);
                        }

                        for (Shape oSeg : oSegs) {
                            if (oSeg != null) {
                                Point2D.Double pt = ShapeHelper.getPointAt(oSeg, segLength / 2);
                                AffineTransform at = AffineTransform.getTranslateInstance(pt.x, pt.y);


                                if (rOrient != RelativeOrientation.PORTRAYAL) {
                                    Point2D.Double ptA = ShapeHelper.getPointAt(oSeg, 0.5 * (segLength - gWidth));
                                    Point2D.Double ptB = ShapeHelper.getPointAt(oSeg, 0.75 * (segLength - gWidth));

                                    double theta = Math.atan2(ptB.y - ptA.y, ptB.x - ptA.x);
                                    //System.out.println("("+ ptA.x + ";" + ptA.y +")"  + "(" + ptB.x + ";" + ptB.y+ ")" + "   => Angle: " + (theta/0.0175));

                                    switch (rOrient) {
                                        case LINE:
                                            theta += 0.5 * Math.PI;
                                            break;
                                        case NORMAL_UP:
                                            if (theta < -Math.PI / 2 || theta > Math.PI / 2) {
                                                theta += Math.PI;
                                            }
                                            break;
                                    }

                                    at.concatenate(AffineTransform.getRotateInstance(theta));
                                }
                                g2.drawRenderedImage(createRendering, at);
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public double getMaxWidth(SpatialDataSourceDecorator sds, long fid, MapTransform mt) throws IOException, ParameterException {
        RenderableGraphics g = graphic.getGraphic(sds, fid, false, mt);
        if (g != null) {
            return Math.max(Math.max(g.getWidth(), Math.abs(g.getMinX())), Math.max(g.getHeight(), Math.abs(g.getMinY())));
        } else {
            return 0.0;
        }
        //return graphic.getMaxWidth(sds, fid, mt);
        //return Math.max(Math.abs( g.getMinX() ), Math.abs(g.getMinY()));
    }

    @Override
    public double getMinLength(SpatialDataSourceDecorator sds, long fid, MapTransform mt) throws ParameterException, IOException {
        if (length != null) {
            return length.getValue(sds, fid);
        } else {
            return graphic.getMaxWidth(sds, fid, mt);
        }
    }

    @Override
    public String dependsOnFeature() {
        String result = "";
        if (graphic != null) {
            result += " " + graphic.dependsOnFeature();
        }
        if (length != null) {
            result += " " + length.dependsOnFeature();
        }

        return result.trim();
    }

    @Override
    public JAXBElement<GraphicStrokeType> getJAXBElement() {
        ObjectFactory of = new ObjectFactory();
        return of.createGraphicStroke(this.getJAXBType());
    }

    private GraphicStrokeType getJAXBType() {
        GraphicStrokeType s = new GraphicStrokeType();

        this.setJAXBProperties(s);

        if (graphic != null) {
            s.setGraphic(graphic.getJAXBElement());
        }

        if (length != null) {
            s.setLength(length.getJAXBParameterValueType());
        }

        if (orientation != null) {
            s.setRelativeOrientation(orientation.getJaxbType());
        }
        return s;
    }
}
