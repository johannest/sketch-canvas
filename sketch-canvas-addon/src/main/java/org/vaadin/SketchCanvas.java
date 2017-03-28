package org.vaadin;

import java.io.Serializable;
import java.util.ArrayList;

import com.vaadin.annotations.JavaScript;
import com.vaadin.annotations.StyleSheet;
import com.vaadin.ui.AbstractJavaScriptComponent;

import elemental.json.JsonArray;

/**
 * Simple collaborative sketching widget
 */
@StyleSheet({ "vaadin://sketchcanvas/css/literallycanvas.css" })
@StyleSheet({ "vaadin://sketchcanvas/css/additionalstyles.css" })
@JavaScript({ "http://cdnjs.cloudflare.com/ajax/libs/react/0.14"
    + ".7/react-with-addons.js",
    "http://cdnjs.cloudflare" + ".com/ajax/libs/react/0.14.7/react-dom.js",
    "vaadin://sketchcanvas/js/literallycanvas.js",
    "vaadin://sketchcanvas/js/sketchcanvas-connector.js" })
public class SketchCanvas extends AbstractJavaScriptComponent {

  /**
   * ColorType
   */
  public enum ColorType {
    PRIMARY, SECONDARY, BACKGROUND;
  }

  ArrayList<DrawingChangeListener> drawingChangeListeners = new ArrayList<DrawingChangeListener>();

  private double margin = 10;

  /**
   * Initialize full sized
   */
  public SketchCanvas() {
    init();
  }

  /**
   * Initialize with given pixel width and height
   *
   * @param widthPx
   * @param heightPx
   */
  public SketchCanvas(int widthPx, int heightPx) {
    init(widthPx, heightPx);
  }

  /**
   * Update canvas with given json snapshot
   *
   * @param json
   */
  public void updateDrawing(JsonArray json) {
    callFunction("updateDrawing", json);
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
  public void setSelectedTool(String toolName) {
    callFunction("setSelectedTool", toolName);
  }

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
   * Listen all the draw updates
   *
   * @param listener
   */
  public void addDrawingChangeListener(DrawingChangeListener listener) {
    drawingChangeListeners.add(listener);
  }

  private void init() {
    init(null, null);
  }

  private void init(Integer widthPx, Integer heightPx) {
    addStyleName("sketch-canvas");
    getState().widthPx = widthPx;
    getState().heightPx = heightPx;

    if (widthPx == null && heightPx == null) {
      setSizeFull();
    } else {
      setWidth(widthPx + margin + "px");
      setHeight(heightPx + margin + "px");
    }

    addFunction("drawingChange", arguments -> {
      drawingChangeListeners
          .forEach((listener) -> listener.drawingChange(arguments));
    });
    addFunction("toolChange", arguments -> {
      System.out.println(arguments.toJson());
      getState().selectedTool = arguments.getString(0);
    });
    addFunction("primaryColorChange", arguments -> {
      getState().primaryColor = arguments.getString(0);
    });
    addFunction("secondaryColorChange", arguments -> {
      getState().secondaryColor = arguments.getString(0);
    });
    addFunction("backgroundColorChange", arguments -> {
      getState().backgroundColor = arguments.getString(0);
    });
  }

  /**
   * Set width of canvas in pixels
   *
   * @param widthPx
   */
  public void setWidth(int widthPx) {
    getState().widthPx = widthPx;
  }

  /**
   * Set height of canvas in pixels
   *
   * @param heightPx
   */
  public void setHeight(int heightPx) {
    getState().heightPx = heightPx;
  }

  /**
   * @return
   * @see #setWidth(int)
   */
  public int getWidthInPx() {
    return getState().widthPx;
  }

  /**
   * @return
   * @see #setHeight(int)
   */
  public int getHeightInPx() {
    return getState().heightPx;
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
}
