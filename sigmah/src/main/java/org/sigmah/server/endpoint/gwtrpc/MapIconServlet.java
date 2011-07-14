/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.server.endpoint.gwtrpc;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.sigmah.server.report.renderer.image.ImageMapRenderer;
import org.sigmah.shared.report.content.PieMapMarker;
import org.sigmah.shared.report.content.PieMapMarker.SliceValue;
import org.sigmah.shared.report.model.layers.PiechartMapLayer.Slice;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Servlet that generates map icons for use in a Google Maps
 * {@link com.google.gwt.maps.client.overlay.Icon}
 * <p/>
 * The query parameter "t" indicates the type of icon to be generated:
 * <p/>
 * <b>t=bubble</b>: Generates a semi-transparent circle
 *
 * TODO: security checks. Manual parsing of arguments just screams for some sec flaws.
 *
 * @author Alex Bertram
 * @param r Radius, in pixels, of the bubble
 * @param c Color, as an RGB integer, of the bubble
 */
@Singleton
public class MapIconServlet extends HttpServlet {


    private ImageMapRenderer renderer;

    @Inject
    public MapIconServlet(ImageMapRenderer renderer) {
        this.renderer = renderer;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        if (req.getParameter("t").equals("bubble")) {

            int radius = Integer.parseInt(req.getParameter("r"));
            int color;

            try {
                color = Integer.parseInt(req.getParameter("c"));
            } catch (NumberFormatException e) {
                color = Color.decode(req.getParameter("c")).getRGB();
            }

            BufferedImage icon = new BufferedImage(radius * 2, radius * 2, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = icon.createGraphics();

            g2d.setPaint(new Color(255, 255, 255, 0));
            g2d.fillRect(0, 0, radius * 2, radius * 2);

            renderer.drawBubble(g2d, color, radius, radius, radius);

            resp.setContentType("image/png");
            ImageIO.write(icon, "PNG", resp.getOutputStream());
        } else {
        	if (req.getParameter("t").equals("piechart")) {

        		int radius = Integer.parseInt(req.getParameter("r"));

                BufferedImage icon = new BufferedImage(radius * 2, radius * 2, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2d = icon.createGraphics();

                PieMapMarker pmm = new PieMapMarker();
                pmm.setRadius(radius);
                
                String[] values = req.getParameterValues("value");
                String[] colors = req.getParameterValues("color");
                
                if (colors.length != values.length) {
                	String error = "Expected same amount of colors & values. Amount of Colors: [{0}]. Amount of values: [{1}].";
                	error = String.format(error, colors.length, values.length);
                	throw new ServletException(error);
                }
                
                for (int i=0; i<colors.length; i++) {
                    int color;
                    double value=0.0;

                    try {
                        color = Integer.parseInt(colors[i]);
                        value = Double.parseDouble(values[i]);
                    } catch (NumberFormatException e) {
                        color = Color.decode(colors[i]).getRGB();
                    }
                	SliceValue slice = new SliceValue();
                	slice.setColor(color);
                	slice.setValue(value);
                    pmm.getSlices().add(slice);
                }
                
                g2d.setPaint(new Color(255, 255, 255, 0));
                g2d.fillRect(0, 0, radius * 2, radius * 2);

                renderer.drawPieMarker(g2d, pmm);

                resp.setContentType("image/png");
                ImageIO.write(icon, "PNG", resp.getOutputStream());
        	}
        }
    }
}
