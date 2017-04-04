package org.vaadin;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Push;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.FileDownloader;
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

    SketchCanvas canvas1 = new SketchCanvas(600, 400);
    SketchCanvas canvas2 = new SketchCanvas(600, 400);

    canvas1.addDrawingChangeListener(json -> {
      canvas2.updateDrawing(json);
    });

    canvas2.addDrawingChangeListener(json -> {
      canvas1.updateDrawing(json);
    });

    HorizontalLayout buttonLayout = new HorizontalLayout();
    HorizontalLayout buttonLayout2 = new HorizontalLayout();
    Button clearButton = new Button("Clear", clickEvent -> {
      canvas1.clear();
    });

    Button showStateButton = new Button("showCurrentState", clickEvent -> {
      Notification.show(
          canvas1.getColor(SketchCanvas.ColorType.PRIMARY) + " " + canvas1
              .getColor(SketchCanvas.ColorType.SECONDARY) + " " + canvas1
              .getColor(SketchCanvas.ColorType.BACKGROUND)+" "
              + ""+canvas1.getSelectedTool()+" "+canvas1.getStrokeWidth());
    });

    Button setColorsButton = new Button("setColors (red/green/white)",
        clickEvent -> {
          canvas1.setColor(SketchCanvas.ColorType.PRIMARY, "red");
          canvas1.setColor(SketchCanvas.ColorType.SECONDARY, "green");
          canvas1.setColor(SketchCanvas.ColorType.BACKGROUND, "white");
        });

    Button setTool1 = new Button("setTool (Pencil:1)", clickEvent -> {
      canvas1.setSelectedTool("Pencil", 1);
    });
    Button setTool2 = new Button("setTool (Ellipse:10)", clickEvent -> {
      canvas1.setSelectedTool("Ellipse", 10);
    });
    Button downloadSVG = new Button("Download as SVG", clickEvent -> {
      canvas1.requestImageAsSVGString(svg -> {
        FileDownloader fd = new FileDownloader(SketchCanvas.getSVGResource(svg, "my_image.svg"));
        Button downloadButton = new Button("download");
        fd.extend(downloadButton);
        buttonLayout2.addComponent(downloadButton);
      });
    });
    Button downloadPNG = new Button("Download as PNG", clickEvent -> {
      canvas1.requestImageAsBase64(img -> {
        FileDownloader fd = new FileDownloader(SketchCanvas.getPNGResource(img,"my_image.png"));
        Button downloadButton = new Button("download");
        fd.extend(downloadButton);
        buttonLayout2.addComponent(downloadButton);
      });
    });

    buttonLayout
        .addComponents(clearButton, showStateButton, setColorsButton,
            setTool1, setTool2);
    buttonLayout2.addComponents(downloadSVG, downloadPNG);
    layout.addComponents(buttonLayout, buttonLayout2, canvas1, new Label("Modifications are "
        + "collaborated between these two canvases"
        + ""), canvas2);

    setContent(layout);
  }

  @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
  @VaadinServletConfiguration(ui = MyUI.class, productionMode = false)
  public static class MyUIServlet extends VaadinServlet {
  }
}
