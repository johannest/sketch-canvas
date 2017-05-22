package org.vaadin;

import com.example.sharedapi.GraphicTool;
import com.example.sharedapi.GraphicToolsSet;

public class GraphicToolsSetImpl implements GraphicToolsSet {

  @Override
  public GraphicTool getTool(String toolName) {
    if (toolName.equals(SketchCanvas.class.getName())) {
      return new SketchCanvas();
    }
    return null;
  }
}
