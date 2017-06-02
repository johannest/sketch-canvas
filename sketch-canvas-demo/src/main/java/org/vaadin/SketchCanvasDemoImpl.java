package org.vaadin;

import com.vaadin.server.FileDownloader;
import com.vaadin.ui.Button;
import com.vaadin.ui.Notification;

public class SketchCanvasDemoImpl extends SketchCanvasDemo {

    public SketchCanvasDemoImpl() {
        initializeTab1();
        initializeTab2();
        initializeTab3();
    }

    private void initializeTab1() {
        clear.addClickListener(e -> {
            sketchCanvas.clear();
        });
        showCurrentState.addClickListener(e -> {
            Notification.show(
                    sketchCanvas.getColor(SketchCanvas.ColorType.PRIMARY) + " "
                            + sketchCanvas
                                    .getColor(SketchCanvas.ColorType.SECONDARY)
                            + " "
                            + sketchCanvas
                                    .getColor(SketchCanvas.ColorType.BACKGROUND)
                            + " " + "" + sketchCanvas.getSelectedTool() + " "
                            + sketchCanvas.getStrokeWidth());
        });
        setColors.addClickListener(e -> {
            sketchCanvas.setColor(SketchCanvas.ColorType.PRIMARY, "red");
            sketchCanvas.setColor(SketchCanvas.ColorType.SECONDARY, "green");
            sketchCanvas.setColor(SketchCanvas.ColorType.BACKGROUND, "white");
        });
        setToolPencil.addClickListener(e -> {
            sketchCanvas.setSelectedTool("Pencil", 1);
        });
        setToolEllipse.addClickListener(e -> {
            sketchCanvas.setSelectedTool("Ellipse", 10);
        });
        downloadAsSVG.addClickListener(e -> {
            sketchCanvas.requestImageAsSVGString(svg -> {
                FileDownloader fd = new FileDownloader(
                        SketchCanvas.getSVGResource(svg, "my_image.svg"));
                Button downloadButton = new Button("download");
                fd.extend(downloadButton);
                buttonLo1.addComponent(downloadButton);
            });
        });
        downloadAsPNG.addClickListener(e -> {
            sketchCanvas.requestImageAsBase64(img -> {
                FileDownloader fd = new FileDownloader(
                        SketchCanvas.getPNGResource(img, "my_image.png"));
                Button downloadButton = new Button("download");
                fd.extend(downloadButton);
                buttonLo1.addComponent(downloadButton);
            });
        });
        toggleSetEnabled.addClickListener(e -> {
            sketchCanvas.setEnabled(!sketchCanvas.isEnabled());
        });
        setWidth100.addClickListener(e -> {
            sketchCanvas.setWidth("100%");
        });
        setWidth300.addClickListener(e -> {
            sketchCanvas.setWidth("300px");
        });
        setHeight100.addClickListener(e -> {
            sketchCanvas.setHeight("100%");
        });
        setHeight400.addClickListener(e -> {
            sketchCanvas.setHeight("400px");
        });
    }

    private void initializeTab2() {
        sketchCanvas1.addDrawingChangeListener(json -> {
            sketchCanvas2.updateDrawing(json);
        });

        sketchCanvas2.addDrawingChangeListener(json -> {
            sketchCanvas1.updateDrawing(json);
        });
    }

    private void initializeTab3() {
        sketchCanvas3.addDrawingChangeListener(json -> {
            sketchCanvas4.updateDrawing(json);
        });

        sketchCanvas4.addDrawingChangeListener(json -> {
            sketchCanvas3.updateDrawing(json);
        });
    }

}
