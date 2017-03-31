window.org_vaadin_SketchCanvas =
    function () {
        var self = this;
        var element = this.getElement();
        var lc = null;
        var widthpx = null;
        var heightpx = null;
        var loadingSnapshot = false;
        var currentToolName;

        this.onStateChange = function () {
            var state = this.getState();
            widthpx = state.widthPx;
            heightpx = state.heightPx;
        };

        lc = LC.init(
            element,
            {imageURLPrefix: '/VAADIN/sketchcanvas/img'},
            {imageSize: {width: widthpx, height: heightpx}}
        );

        this.updateDrawing = function (snapshot) {
            loadingSnapshot = true;
            lc.loadSnapshot(JSON.parse(snapshot));
            loadingSnapshot = false;
        };

        this.clearDrawing = function () {
            lc.clear();
        };

        function removeSelectedClassNameFromPreviousTool() {
            var previousToolElement = element.querySelector("div.lc-pick-tool[title=" + currentToolName + "]");
            if (previousToolElement) {
                previousToolElement.classList.remove("selected");
            }
        }

        function addSelectedClassNameToTool(tool) {
            var toolElement = element.querySelector("div.lc-pick-tool[title=" + tool + "]");
            toolElement.classList.add("selected");
        }

        this.setSelectedTool = function (tool) {
            // TODO update css selected classname 
            switch (tool) {
                case "Pencil" :
                    var pencil = new LC.tools.Pencil(lc);
                    lc.setTool(pencil);
                    break;
                case "Eraser" :
                    var eraser = new LC.tools.Eraser(lc);
                    lc.setTool(eraser);
                    break;
                case "Line" :
                    var line = new LC.tools.Line(lc);
                    lc.setTool(line);
                    break;
                case "Rectangle" :
                    var rectangle = new LC.tools.Rectangle(lc);
                    lc.setTool(rectangle);
                    break;
                case "Text" :
                    var text = new LC.tools.Text(lc);
                    lc.setTool(text);
                    break;
                case "Polygon" :
                    var polygon = new LC.tools.Polygon(lc);
                    lc.setTool(polygon);
                    break;
                case "Pan" :
                    var pan = new LC.tools.Pan(lc);
                    lc.setTool(pan);
                    break;
                case "Eyedropper" :
                    var eyedropper = new LC.tools.Eyedropper(lc);
                    lc.setTool(eyedropper);
                    break;
            }
            addSelectedClassNameToTool(tool);
        };

        this.setUsedColor = function (colorName, colorValue) {
            console.log(colorName + " " + colorValue);
            lc.setColor(colorName, colorValue);
        };

        var unsubscribeDrawingChange = lc.on('drawingChange', function () {
            if (!loadingSnapshot) {
                self.drawingChange(lc.getSnapshotJSON());
            }
        });

        var unsubscribeToolChange = lc.on('toolChange', function (tool) {
            console.log(tool.tool);
            removeSelectedClassNameFromPreviousTool();
            currentToolName = tool.tool.name;
            self.toolChange(tool.tool.name, tool.tool);
        });

        var unsubscribePrimaryColorChange = lc.on('primaryColorChange', function (color) {
            console.log(color);
            self.primaryColorChange(color);
        });

        var unsubscribeSecondaryColorChange = lc.on('secondaryColorChange', function (color) {
            console.log(color);
            self.secondaryColorChange(color);
        });

        var unsubscribeBackgroundColorChange = lc.on('backgroundColorChange', function (color) {
            console.log(color);
            self.backgroundColorChange(color);
        });
    };