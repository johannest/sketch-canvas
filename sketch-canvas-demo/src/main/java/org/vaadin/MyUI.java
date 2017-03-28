package org.vaadin;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Push;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@Push
public class MyUI extends UI {

  @Override
  protected void init(VaadinRequest vaadinRequest) {
    final VerticalLayout layout = new VerticalLayout();

    SketchCanvas canvas1 = new SketchCanvas(600, 390);
    SketchCanvas canvas2 = new SketchCanvas(600, 390);

    canvas1.addDrawingChangeListener(json -> {
      canvas2.updateDrawing(json);
    });

    canvas2.addDrawingChangeListener(json -> {
      canvas1.updateDrawing(json);
    });

    HorizontalLayout buttonLayout = new HorizontalLayout();
    Button clearButton = new Button("Clear", clickEvent -> {
      canvas1.clear();
    });

    Button showColorsButton = new Button("showColors", clickEvent -> {
      Notification.show(
          canvas1.getColor(SketchCanvas.ColorType.PRIMARY) + " " + canvas1
              .getColor(SketchCanvas.ColorType.SECONDARY) + " " + canvas1
              .getColor(SketchCanvas.ColorType.BACKGROUND));
    });

    Button setColorsButton = new Button("setColors (red/green/white)",
        clickEvent -> {
          canvas1.setColor(SketchCanvas.ColorType.PRIMARY, "red");
          canvas1.setColor(SketchCanvas.ColorType.SECONDARY, "green");
          canvas1.setColor(SketchCanvas.ColorType.BACKGROUND, "white");
        });

    Button setTool = new Button("setTool (pencil)", clickEvent -> {
      canvas1.setSelectedTool("Pencil");
    });

    buttonLayout
        .addComponents(clearButton, showColorsButton, setColorsButton, setTool);
    layout.addComponents(buttonLayout, canvas1, new Label("Modifications are "
        + "collaborated between these two canvases"
        + ""), canvas2);

    setContent(layout);
  }

  @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
  @VaadinServletConfiguration(ui = MyUI.class, productionMode = false)
  public static class MyUIServlet extends VaadinServlet {
  }
}
