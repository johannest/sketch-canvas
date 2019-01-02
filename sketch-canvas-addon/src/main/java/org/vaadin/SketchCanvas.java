package org.vaadin;

import com.vaadin.annotations.JavaScript;
import com.vaadin.annotations.StyleSheet;
import com.vaadin.server.Resource;
import com.vaadin.server.ResourceReference;
import com.vaadin.server.StreamResource;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.AbstractJavaScriptComponent;
import elemental.json.JsonArray;
import elemental.json.JsonObject;
import elemental.json.JsonString;
import org.apache.commons.io.IOUtils;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Optional;

/**
 * Simple collaborative sketching widget
 */
@StyleSheet({ "vaadin://sketchcanvas/css/literallycanvas.css" })
@StyleSheet({ "vaadin://sketchcanvas/css/additionalstyles.css" })
@JavaScript({ "vaadin://sketchcanvas/js/react-with-addons.js",
    "vaadin://sketchcanvas/js/react-dom.js",
    "vaadin://sketchcanvas/js/literallycanvas.js",
    "vaadin://sketchcanvas/js/sketchcanvas-connector.js" })
public class SketchCanvas extends AbstractJavaScriptComponent {

  /**
   * Image data consumer
   * @param <T> svg, base64 image or the snapshot of the canvas as a JSON string
   */
  public interface ImageDataConsumer<T> {
    void consume(T imageData);
  }

  /**
   * ColorType
   */
  public enum ColorType {
    PRIMARY, SECONDARY, BACKGROUND;
  }

  /**
   * Tool type
   */
  public enum Tool {
    ELLIPSE("Ellipse"), ERASER("Eraser"), EYEDROPPER("Eyedropper"), LINE("Line"), PAN("Pan"), PENCIL("Pencil"), POLYGON("Polygon"), RECTANGLE("Rectangle"),
        TEXT("Text");

    private String toolName;

    Tool(String toolName) {
      this.toolName = toolName;
    }

    public String getName() {
      return toolName;
    }
  }

  private ArrayList<DrawingChangeListener> drawingChangeListeners = new ArrayList<DrawingChangeListener>();

  private Optional<ImageDataConsumer<String>> optionalSVGConsumer = Optional.empty();
  private Optional<ImageDataConsumer<String>> optionalImageConsumer = Optional.empty();
  private Optional<ImageDataConsumer<String>> optionalDrawingSnapshotConsumer = Optional.empty();

  /**
   * Initialize full sized
   */
  public SketchCanvas() {
    init();
  }

  /**
   * Update canvas with given json snapshot
   *
   * @param json
   */
  public void updateDrawing(JsonArray json) {
    callFunction("updateDrawing", json.get(0), true);
  }

  /**
   * Set the image in the given url as a background image
   *
   * @param url
   */
  public void setBackgroundImage(String url) {
    if (url != null) {
        try {
            StreamResource resource = getStreamResource(url);
            setResource("download", resource);
            ResourceReference rr = ResourceReference.create(resource, this, "download");
            callFunction("setBackgroundImage", rr.getURL());
        } catch (Exception e) {
            // TODO add better error handling
            e.printStackTrace();
            callFunction("setBackgroundImage", url);
        }
    }
  }

  private StreamResource getStreamResource(final String url) {
    return new StreamResource((StreamResource.StreamSource) () -> {
      InputStream in = null;
      try {
        URL fileURL = new URL(url);
        URLConnection connection = fileURL.openConnection();
        connection.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:25.0) Gecko/20100101 Firefox/25.0");
        in = new BufferedInputStream(connection.getInputStream());
        byte[] bytes = IOUtils.toByteArray(in);
        return new ByteArrayInputStream(bytes);
      } catch (Exception e) {
        // TODO add better error handling
        e.printStackTrace();
      }
      return in;
    },"background."+((url.toLowerCase().contains(".png"))?"png":"jpg"));
  }

  /**
   * Remove the background image from the c
   */
  public void clearBackgroundImage() {
    callFunction("setBackgroundImage", "");
  }

  /**
   * Update canvas with given json snapshot
   *
   * @param json
   * @param ignoreColorChanges false if you want the colors of the snapshot to be set for the canvas
   */
  public void updateDrawing(JsonArray json, boolean ignoreColorChanges) {
    callFunction("updateDrawing", json.get(0), ignoreColorChanges);
  }

  /**
   * Update canvas with given json snapshot and scalingFactor
   *
   * @param json
   */
  public void updateDrawingWithScalingFactor(JsonArray json, double scalingFactor) {
    callFunction("updateDrawingWithScale", scalingFactor, json.get(0));
  }

  /**
   * Clear drawing
   */
  public void clear() {
    callFunction("clearDrawing");
  }

  /**
   * Select given tool with given parameters
   *
   * @param toolName
   *     such as Pencil
   */
  public void setSelectedTool(String toolName, Integer strokeWidth) {
    callFunction("setSelectedTool", toolName, strokeWidth);
  }

  /**
   * @see #setSelectedTool(String, Integer)
   * @param tool
   * @param strokeWidth
   */
  public void setSelectedTool(Tool tool, Integer strokeWidth) {
    callFunction("setSelectedTool", tool.getName(), strokeWidth);
  }

  /**
   * Returns currently selected Tool
   * @return
   */
  public Tool getSelectedTool() {
    return Enum.valueOf(Tool.class, getState().selectedTool.toUpperCase());
  }

  /**
   * Current color for given color type
   * @param colorType
   * @return
   */
  public String getColor(ColorType colorType) {
    switch (colorType) {
    case PRIMARY:
      return getState().primaryColor;
    case SECONDARY:
      return getState().secondaryColor;
    case BACKGROUND:
      return getState().backgroundColor;
    }
    return null;
  }

  public void setColor(ColorType type, String color) {
    callFunction("setUsedColor", type.name().toLowerCase(), color);
  }

  /**
   * Current stroke width
   * @return
   */
  public int getStrokeWidth() {
    return getState().strokeWidth;
  }

  /**
   * Request current drawing as an SVG String
   * @param svgConsumer
   */
  public void requestImageAsSVGString(ImageDataConsumer<String> svgConsumer) {
    this.optionalSVGConsumer = Optional.ofNullable(svgConsumer);
    callFunction("requestSVG");
  }

  /**
   * Request current drawing as base64 encoded String
   * @param imageConsumer
   */
  public void requestImageAsBase64(ImageDataConsumer<String> imageConsumer) {
    this.optionalImageConsumer = Optional.ofNullable(imageConsumer);
    callFunction("requestImage");
  }

  public void requestCanvasSnapshot(ImageDataConsumer<String> drawingSnapshotConsumer) {
	this.optionalDrawingSnapshotConsumer = Optional.ofNullable(drawingSnapshotConsumer);
	callFunction("requestSnapshot");
  }

  /**
   * Listen all the draw updates
   *
   * @param listener
   */
  public void addDrawingChangeListener(DrawingChangeListener listener) {
    drawingChangeListeners.add(listener);
  }


  private void init() {
    addStyleName("sketch-canvas");
    getState().contextPath = VaadinServlet.getCurrent().getServletContext().getContextPath();

    addFunction("drawingChange", arguments -> {
      drawingChangeListeners
          .forEach((listener) -> listener.drawingChange(arguments));
    });
    addFunction("toolChange", arguments -> {
      System.out.println(arguments.toJson());
      getState().selectedTool = arguments.getString(0);
      final JsonObject strokeObject = arguments.getObject(1);
      if (strokeObject!=null && strokeObject.hasKey("strokeWidth")) {
        final double strokeWidth = strokeObject.getNumber("strokeWidth");
        getState().strokeWidth = (int)strokeWidth;
      }
    });
    addFunction("primaryColorChange", arguments -> {
      if (arguments.get(0) instanceof JsonString) {
        getState().primaryColor = arguments.getString(0);
      }
    });
    addFunction("secondaryColorChange", arguments -> {
      if (arguments.get(0) instanceof JsonString) {
        getState().secondaryColor = arguments.getString(0);
      }
    });
    addFunction("backgroundColorChange", arguments -> {
      if (arguments.get(0) instanceof JsonString) {
        getState().backgroundColor = arguments.getString(0);
      }
    });
    addFunction("setSVGString", arguments -> {
      optionalSVGConsumer.ifPresent(consumer -> {
        consumer.consume(arguments.getString(0));
      });
    });
    addFunction("setImageData", arguments -> {
      optionalImageConsumer.ifPresent(consumer -> {
        consumer.consume(arguments.getString(0));
      });
    });
    addFunction("setSnapshot", snapshot -> {
    	optionalDrawingSnapshotConsumer.ifPresent(consumer -> {
    		consumer.consume(snapshot.getString(0));
    	});
    });
  }

  @Override
  protected SketchCanvasState getState() {
    return (SketchCanvasState) super.getState();
  }

  /**
   * Listener for drawing changes
   */
  public interface DrawingChangeListener extends Serializable {
    /**
     * Drawing was changed
     *
     * @param json
     *     current json snapshot of the drawing
     */
    void drawingChange(JsonArray json);
  }

  /**
   * Create Vaadin Resource out of svg String
   * @param svgData
   * @param fileName
   * @return
   */
  public static Resource getSVGResource(String svgData, String fileName) {
    return new StreamResource(() -> {
      return new ByteArrayInputStream(svgData.getBytes());
    }, fileName);
  }

  /**
   * Create Vaadin Resource out of base 64 String
   * @param imgData
   * @param fileName
   * @return
   */
  public static Resource getPNGResource(String imgData, String fileName) {
    return new StreamResource(() -> {
      return new ByteArrayInputStream(Base64.getDecoder().decode(imgData.split(",")[1].getBytes(StandardCharsets.UTF_8)));
    }, fileName);
  }

  /**
   * Set the width of the canvas
   * @param width the width of the canvas or {@code null} for infinite
   */
  public void setCanvasWidth(Integer width) {
	getState().canvasWidth = width;
  }

  /**
   * Set the height of the canvas
   * @param height the height of the canvas or {@code null} for infinite
   */
  public void setCanvasHeight(Integer height) {
	getState().canvasHeight = height;
  }

}
