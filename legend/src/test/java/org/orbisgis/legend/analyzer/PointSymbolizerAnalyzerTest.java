/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.legend.analyzer;

import java.awt.Color;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Test;
import org.orbisgis.core.renderer.se.PointSymbolizer;
import org.orbisgis.core.renderer.se.Style;
import org.orbisgis.core.renderer.se.fill.SolidFill;
import org.orbisgis.core.renderer.se.graphic.MarkGraphic;
import org.orbisgis.core.renderer.se.stroke.PenStroke;
import org.orbisgis.legend.AnalyzerTest;
import org.orbisgis.legend.analyzer.symbolizers.PointSymbolizerAnalyzer;
import org.orbisgis.legend.structure.viewbox.ConstantViewBox;
import org.orbisgis.legend.thematic.constant.UniqueSymbolPoint;
import org.orbisgis.legend.thematic.proportional.ProportionalPoint;

/**
 *
 * @author alexis
 */
public class PointSymbolizerAnalyzerTest extends AnalyzerTest {
    
    private String xml = "src/test/resources/org/orbisgis/legend/constantWKN.se";
    private String xml2 = "src/test/resources/org/orbisgis/legend/proportionalSymbol.se";
    private String xml3 = "src/test/resources/org/orbisgis/legend/constant2DWKN.se";
    private String proportional = "src/test/resources/org/orbisgis/legend/proportionalSymbol.se";

    @Test
    public void testLegendConstructor() throws Exception {
                Style st = getStyle(xml);
                PointSymbolizer ps = (PointSymbolizer) (st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0));
                UniqueSymbolPoint uvp = new UniqueSymbolPoint(ps);
                assertTrue(true);
    }

    @Test
    public void testLegendFromAnalyzer() throws Exception {
                Style st = getStyle(xml);
                PointSymbolizer ps = (PointSymbolizer) (st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0));
                PointSymbolizerAnalyzer psa = new PointSymbolizerAnalyzer(ps);
                assertTrue(psa.getLegend() instanceof UniqueSymbolPoint);

    }
    
    /**
     * We sometimes expect an exception to be thrown.
     * @throws Exception 
     */
    @Test
    public void testLegendConstructorFail() throws Exception {
                Style st = getStyle(xml2);
                PointSymbolizer ps = (PointSymbolizer) (st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0));
                try{
                    UniqueSymbolPoint uvp = new UniqueSymbolPoint(ps);
                    fail();
                } catch(IllegalArgumentException cce){
                    assertTrue(true);
                }
    }

    @Test
    public void testGetFillColor() throws Exception {
        Style st = getStyle(xml);
        PointSymbolizer ps = (PointSymbolizer) (st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0));
        //Let's check the color that is used for this Symbolizer. It should be something
        //like GRAY50
        MarkGraphic mg = (MarkGraphic) ps.getGraphicCollection().getGraphic(0);
        Color c = ((SolidFill)mg.getFill()).getColor().getColor(null, 0);
        assertTrue(c.equals(new Color((int)SolidFill.GRAY50, (int)SolidFill.GRAY50, (int)SolidFill.GRAY50)));
        //We can continue... Let's build the UniqueSymbolPoint
        UniqueSymbolPoint uvp = new UniqueSymbolPoint(ps);
        assertTrue(uvp.getFillColor().equals(new Color((int)SolidFill.GRAY50, (int)SolidFill.GRAY50, (int)SolidFill.GRAY50)));
    }

    @Test
    public void testSetFillColor() throws Exception {
        Style st = getStyle(xml);
        PointSymbolizer ps = (PointSymbolizer) (st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0));
        //Let's check the color that is used for this Symbolizer. It should be something
        //like GRAY50
        MarkGraphic mg = (MarkGraphic) ps.getGraphicCollection().getGraphic(0);
        Color c = ((SolidFill)mg.getFill()).getColor().getColor(null, 0);
        assertTrue(c.equals(new Color((int)SolidFill.GRAY50, (int)SolidFill.GRAY50, (int)SolidFill.GRAY50)));
        //We can continue... Let's build the UniqueSymbolPoint
        UniqueSymbolPoint uvp = new UniqueSymbolPoint(ps);
        uvp.setFillColor(new Color(4,4,4));
        assertTrue(uvp.getFillColor().equals(new Color(4,4,4)));
    }

    @Test
    public void testGetLineWidth() throws Exception {
        Style st = getStyle(xml);
        PointSymbolizer ps = (PointSymbolizer) (st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0));
        //Let's get the current width
        MarkGraphic mg = (MarkGraphic) ps.getGraphicCollection().getGraphic(0);
        double width = ((PenStroke)mg.getStroke()).getWidth().getValue(null, 0);
        assertTrue(width == 1.0);
        //We've checked the width from the symbolizer, let's get it from the Legend
        UniqueSymbolPoint uvp = new UniqueSymbolPoint(ps);
        assertTrue(uvp.getLineWidth() == 1.0);
    }

    @Test
    public void testSetLineWidth() throws Exception {
        Style st = getStyle(xml);
        PointSymbolizer ps = (PointSymbolizer) (st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0));
        //Let's get the current width
        MarkGraphic mg = (MarkGraphic) ps.getGraphicCollection().getGraphic(0);
        double width = ((PenStroke)mg.getStroke()).getWidth().getValue(null, 0);
        assertTrue(width == 1.0);
        //We've checked the width from the symbolizer, let's get it from the Legend
        UniqueSymbolPoint uvp = new UniqueSymbolPoint(ps);
        uvp.setLineWidth(4.0);
        assertTrue(uvp.getLineWidth() == 4.0);
    }

    @Test
    public void testGetLineColor() throws Exception {
        Style st = getStyle(xml);
        PointSymbolizer ps = (PointSymbolizer) (st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0));
        //Let's get the current width
        MarkGraphic mg = (MarkGraphic) ps.getGraphicCollection().getGraphic(0);
        Color col = ((SolidFill)((PenStroke)mg.getStroke()).getFill()).getColor().getColor(null, 0);
        assertTrue(col.equals(Color.BLACK));
        //We've checked the color we searched in the symbolizer, let's get it from the Legend
        UniqueSymbolPoint uvp = new UniqueSymbolPoint(ps);
        assertTrue(uvp.getLineColor().equals(Color.BLACK));
    }

    @Test
    public void testSetLineColor() throws Exception {
        Style st = getStyle(xml);
        PointSymbolizer ps = (PointSymbolizer) (st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0));
        //Let's get the current width
        MarkGraphic mg = (MarkGraphic) ps.getGraphicCollection().getGraphic(0);
        Color col = ((SolidFill)((PenStroke)mg.getStroke()).getFill()).getColor().getColor(null, 0);
        assertTrue(col.equals(Color.BLACK));
        //We've checked the color we searched in the symbolizer, let's get it from the Legend
        UniqueSymbolPoint uvp = new UniqueSymbolPoint(ps);
        uvp.setLineColor(Color.BLUE);
        assertTrue(uvp.getLineColor().equals(Color.BLUE));
    }

    @Test
    public void testGetLineDash() throws Exception {
        Style st = getStyle(xml);
        PointSymbolizer ps = (PointSymbolizer) (st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0));
        //Let's get the current width
        MarkGraphic mg = (MarkGraphic) ps.getGraphicCollection().getGraphic(0);
        Color col = ((SolidFill)((PenStroke)mg.getStroke()).getFill()).getColor().getColor(null, 0);
        assertTrue(col.equals(Color.BLACK));
        //We've checked the color we searched in the symbolizer, let's get it from the Legend
        UniqueSymbolPoint uvp = new UniqueSymbolPoint(ps);
        assertTrue(uvp.getDashArray().isEmpty());
    }

    @Test
    public void testSetLineDash() throws Exception {
        Style st = getStyle(xml);
        PointSymbolizer ps = (PointSymbolizer) (st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0));
        //Let's get the current width
        MarkGraphic mg = (MarkGraphic) ps.getGraphicCollection().getGraphic(0);
        Color col = ((SolidFill)((PenStroke)mg.getStroke()).getFill()).getColor().getColor(null, 0);
        assertTrue(col.equals(Color.BLACK));
        //We've checked the color we searched in the symbolizer, let's get it from the Legend
        UniqueSymbolPoint uvp = new UniqueSymbolPoint(ps);
        uvp.setDashArray("2 2");
        assertTrue(uvp.getDashArray().equals("2 2"));
    }

    @Test
    public void testGetViewBoxDimensions() throws Exception {
        Style st = getStyle(xml3);
        PointSymbolizer ps = (PointSymbolizer) (st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0));
        //Let's get the current width
        MarkGraphic mg = (MarkGraphic) ps.getGraphicCollection().getGraphic(0);
        assertTrue(mg.getViewBox().getHeight().getValue(null, 0) == 5);
        assertTrue(mg.getViewBox().getWidth().getValue(null, 0) == 5);
        UniqueSymbolPoint uvp = new UniqueSymbolPoint(ps);
        assertTrue(uvp.getViewBoxHeight() == 5);
        assertTrue(uvp.getViewBoxWidth() == 5);
    }

    @Test
    public void testGetViewBoxOneDimensionOnly() throws Exception {
        Style st = getStyle(xml);
        PointSymbolizer ps = (PointSymbolizer) (st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0));
        //Let's get the current width
        MarkGraphic mg = (MarkGraphic) ps.getGraphicCollection().getGraphic(0);
        assertTrue(mg.getViewBox().getHeight() == null);
        assertTrue(mg.getViewBox().getWidth().getValue(null, 0) == 5);
        UniqueSymbolPoint uvp = new UniqueSymbolPoint(ps);
        assertTrue(uvp.getViewBoxHeight() == null);
        assertTrue(uvp.getViewBoxWidth() == 5);
    }

    @Test
    public void testSetViewBoxDimensions() throws Exception {
        Style st = getStyle(xml3);
        PointSymbolizer ps = (PointSymbolizer) (st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0));
        //Let's get the current width
        MarkGraphic mg = (MarkGraphic) ps.getGraphicCollection().getGraphic(0);
        assertTrue(mg.getViewBox().getHeight().getValue(null, 0) == 5);
        assertTrue(mg.getViewBox().getWidth().getValue(null, 0) == 5);
        UniqueSymbolPoint uvp = new UniqueSymbolPoint(ps);
        uvp.setViewBoxHeight(8.0);
        uvp.setViewBoxWidth(3.0);
        assertTrue(uvp.getViewBoxHeight() == 8);
        assertTrue(uvp.getViewBoxWidth() == 3);
        assertTrue(mg.getViewBox().getHeight().getValue(null, 0) == 8);
        assertTrue(mg.getViewBox().getWidth().getValue(null, 0) == 3);
        assertTrue(mg.getViewBox().getHeight() == 
                ((ConstantViewBox)uvp.getMarkGraphic().getViewBoxLegend()).getViewBox().getHeight());
        assertTrue(mg.getViewBox().getWidth() ==
                ((ConstantViewBox)uvp.getMarkGraphic().getViewBoxLegend()).getViewBox().getWidth());
    }

    @Test
    public void testSetViewBoxOneDimensionOnly() throws Exception {
        Style st = getStyle(xml);
        PointSymbolizer ps = (PointSymbolizer) (st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0));
        //Let's get the current width
        MarkGraphic mg = (MarkGraphic) ps.getGraphicCollection().getGraphic(0);
        assertTrue(mg.getViewBox().getHeight() == null);
        assertTrue(mg.getViewBox().getWidth().getValue(null, 0) == 5);
        UniqueSymbolPoint uvp = new UniqueSymbolPoint(ps);
        uvp.setViewBoxWidth(15.0);
        assertTrue(uvp.getViewBoxHeight() == null);
        assertTrue(uvp.getViewBoxWidth() == 15.0);
        uvp.setViewBoxHeight(16.0);
        assertTrue(uvp.getViewBoxHeight() == 16.0);
        assertTrue(uvp.getViewBoxWidth() == 15.0);
        uvp.setViewBoxWidth(null);
        assertTrue(uvp.getViewBoxHeight() == 16.0);
        assertTrue(uvp.getViewBoxWidth() == null);
        uvp.setViewBoxWidth(15.0);
        assertTrue(uvp.getViewBoxHeight() == 16.0);
        assertTrue(uvp.getViewBoxWidth() == 15.0);
        uvp.setViewBoxHeight(null);
        assertTrue(uvp.getViewBoxHeight() == null);
        assertTrue(uvp.getViewBoxWidth() == 15.0);
        try{
            uvp.setViewBoxWidth(null);
            fail();
        } catch(IllegalArgumentException iae){
            assertTrue(true);
        }
        uvp.setViewBoxHeight(16.0);
        uvp.setViewBoxWidth(null);
        try{
            uvp.setViewBoxHeight(null);
            fail();
        } catch(IllegalArgumentException iae){
            assertTrue(true);
        }
    }

    @Test
    public void testProportionalPointConstructor() throws Exception {
                Style st = getStyle(proportional);
                PointSymbolizer ps = (PointSymbolizer) (st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0));
                ProportionalPoint uvp = new ProportionalPoint(ps);
                assertTrue(true);
    }

    @Test
    public void testLegendFromAnalyzerProportionalPoint() throws Exception {
                Style st = getStyle(proportional);
                PointSymbolizer ps = (PointSymbolizer) (st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0));
                PointSymbolizerAnalyzer psa = new PointSymbolizerAnalyzer(ps);
                assertTrue(psa.getLegend() instanceof ProportionalPoint);

    }
    
    @Test
    public void testProportionalPointConstructorFail() throws Exception {
                Style st = getStyle(xml);
                PointSymbolizer ps = (PointSymbolizer) (st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0));
                try{
                    ProportionalPoint uvp = new ProportionalPoint(ps);
                    fail();
                } catch(IllegalArgumentException cce){
                    assertTrue(true);
                }
    }

    @Test
    public void testProportionalGetFirstData() throws Exception {
        Style st = getStyle(proportional);
        PointSymbolizer ps = (PointSymbolizer) (st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0));
        ProportionalPoint uvp = new ProportionalPoint(ps);
        assertTrue(uvp.getFirstData() == .0);
    }
    @Test
    public void testProportionalGetSecondData() throws Exception {
        Style st = getStyle(proportional);
        PointSymbolizer ps = (PointSymbolizer) (st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0));
        ProportionalPoint uvp = new ProportionalPoint(ps);
        double youhou = uvp.getSecondData();
        assertTrue(youhou == 1000.0);
    }

    @Test
    public void testProportionalSetFirstData() throws Exception {
        Style st = getStyle(proportional);
        PointSymbolizer ps = (PointSymbolizer) (st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0));
        ProportionalPoint uvp = new ProportionalPoint(ps);
        uvp.setFirstData(25.2);
        assertTrue(uvp.getFirstData() == 25.2);
    }
    @Test
    public void testProportionalSetSecondData() throws Exception {
        Style st = getStyle(proportional);
        PointSymbolizer ps = (PointSymbolizer) (st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0));
        ProportionalPoint uvp = new ProportionalPoint(ps);
        uvp.setSecondData(42.0);
        assertTrue(uvp.getSecondData() == 42.0);
    }

    @Test
    public void testProportionalGetFirstValue() throws Exception {
        Style st = getStyle(proportional);
        PointSymbolizer ps = (PointSymbolizer) (st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0));
        ProportionalPoint uvp = new ProportionalPoint(ps);
        assertTrue(uvp.getFirstValue() == .0);
    }
    @Test
    public void testProportionalGetSecondValue() throws Exception {
        Style st = getStyle(proportional);
        PointSymbolizer ps = (PointSymbolizer) (st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0));
        ProportionalPoint uvp = new ProportionalPoint(ps);
        assertTrue( uvp.getSecondValue()== 200.0);
    }

    @Test
    public void testProportionalSetFirstValue() throws Exception {
        Style st = getStyle(proportional);
        PointSymbolizer ps = (PointSymbolizer) (st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0));
        ProportionalPoint uvp = new ProportionalPoint(ps);
        uvp.setFirstValue(24.0);
        assertTrue(uvp.getFirstValue() == 24.0);
    }
    @Test
    public void testProportionalSetSecondValue() throws Exception {
        Style st = getStyle(proportional);
        PointSymbolizer ps = (PointSymbolizer) (st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0));
        ProportionalPoint uvp = new ProportionalPoint(ps);
        uvp.setSecondValue(250.0);
        assertTrue( uvp.getSecondValue()== 250.0);
    }

    /**
     * We just test the default constructor
     * @throws Exception 
     */
    @Test
    public void testDefaultConstructorUniqueSymbol() throws Exception {
        UniqueSymbolPoint usp = new UniqueSymbolPoint();
        assertTrue(usp.getLineWidth() == 0.1);
        assertTrue(usp.getLineColor().equals(Color.BLACK));
    }
}