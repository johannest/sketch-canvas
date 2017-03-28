package org.vaadin;

import com.vaadin.shared.ui.JavaScriptComponentState;

public class SketchCanvasState extends JavaScriptComponentState {
  public int widthPx;
  public int heightPx;

  public String selectedTool = "Line";

  public String primaryColor = "black";
  public String secondaryColor = "white";
  public String backgroundColor = "hsla(0%, 0%, 0%, 0)";

}
