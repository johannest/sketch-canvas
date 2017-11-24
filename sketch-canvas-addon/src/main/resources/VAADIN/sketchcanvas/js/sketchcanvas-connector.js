window.org_vaadin_SketchCanvas =
    function () {
        var self = this;
        var element = this.getElement();
        var lc = null;

        var width = null;
        var height = null;
        var enabled = true;

        var canvasWidth = null;
        var canvasHeight = null;
        
        var loadingSnapshot = false;

        var currentToolName;
        var currentPColor = "black";
        var currentSColor = "white";
        var currentBColor = "hsla(0,100%,100%,0)";

        this.onStateChange = function () {
            var state = this.getState();

            if (width !== state.width || height !== state.height) {
                width = state.width;
                height = state.height;

                var lcElement = element.querySelector(".literally");
                lcElement.setAttribute("style", "width:"+width+";height:"+height);

                // trigger resize
                var evt = document.createEvent("UIEvent");
                evt.initEvent("resize", false, true);
                window.dispatchEvent(evt);
            }
            
            if (canvasWidth !== state.canvasWidth || height !== state.canvasHeight) {
            	canvasWidth = state.canvasWidth;
            	canvasHeight = state.canvasHeight;
            	lc.setImageSize(canvasWidth, canvasHeight);
            }

            if (enabled !== state.enabled && state.enabled !== undefined) {
                enabled = state.enabled;

                var toolbarVertical = element.querySelector(".lc-picker");
                var toolbarHorizontal = element.querySelector(".horz-toolbar");
                var drawingArea = element.querySelector(".lc-drawing");

                if (enabled) {
                    toolbarVertical.classList.remove("disabled");
                    toolbarHorizontal.classList.remove("disabled");
                    drawingArea.classList.remove("disabled");
                } else {
                    toolbarVertical.classList.add("disabled");
                    toolbarHorizontal.classList.add("disabled");
                    drawingArea.classList.add("disabled");
                }
            }
        };

        var contextPath = this.getState().contextPath;

        lc = LC.init(
            element,
            {imageURLPrefix: contextPath + '/VAADIN/sketchcanvas/img'},
            {imageSize: {width: width, height: height}}
        );

        this.updateDrawing = function (snapshot) {
            loadingSnapshot = true;
            lc.loadSnapshot(JSON.parse(snapshot));
            restoreOriginalColors();
            loadingSnapshot = false;
        };

        this.updateDrawingWithScale = function (scalingFactor, snapshot) {
            loadingSnapshot = true;
            var scaledSnapshot = snapshot.replace(new RegExp("scale\":[0-9.]*,"),"scale\":"+scalingFactor+",");
            lc.loadSnapshot(JSON.parse(scaledSnapshot));
            restoreOriginalColors();
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

        function restoreOriginalColors() {
            lc.setColor("primary", currentPColor);
            lc.setColor("secondary", currentSColor);
            lc.setColor("background", currentBColor);
        }

        this.setSelectedTool = function (tool, storeWidth) {
            // TODO update css selected classname
            switch (tool) {
                case "Pencil" :
                    var pencil = new LC.tools.Pencil(lc);
                    pencil.strokeWidth = storeWidth;
                    lc.setTool(pencil);
                    break;
                case "Eraser" :
                    var eraser = new LC.tools.Eraser(lc);
                    eraser.strokeWidth = storeWidth;
                    lc.setTool(eraser);
                    break;
                case "Ellipse" :
                    var ellipse = new LC.tools.Ellipse(lc);
                    ellipse.strokeWidth = storeWidth;
                    lc.setTool(ellipse);
                    break;
                case "Line" :
                    var line = new LC.tools.Line(lc);
                    line.strokeWidth = storeWidth;
                    lc.setTool(line);
                    break;
                case "Rectangle" :
                    var rectangle = new LC.tools.Rectangle(lc);
                    rectangle.strokeWidth = storeWidth;
                    lc.setTool(rectangle);
                    break;
                case "Text" :
                    var text = new LC.tools.Text(lc);
                    lc.setTool(text);
                    break;
                case "Polygon" :
                    var polygon = new LC.tools.Polygon(lc);
                    polygon.strokeWidth = storeWidth;
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
                self.drawingChange(JSON.stringify(lc.getSnapshot()));
            }
        });

        var unsubscribeToolChange = lc.on('toolChange', function (tool) {
            console.log(tool.tool);
            if (tool.tool.name != currentToolName) {
                removeSelectedClassNameFromPreviousTool();
                currentToolName = tool.tool.name;
            }
            self.toolChange(tool.tool.name, tool.tool);
        });

        var unsubscribePrimaryColorChange = lc.on('primaryColorChange', function (color) {
            if (!loadingSnapshot) {
                console.log(color);
                currentPColor = color;
                self.primaryColorChange(color);
            }
        });

        var unsubscribeSecondaryColorChange = lc.on('secondaryColorChange', function (color) {
            if (!loadingSnapshot) {
                console.log(color);
                currentSColor = color;
                self.secondaryColorChange(color);
            }
        });

        var unsubscribeBackgroundColorChange = lc.on('backgroundColorChange', function (color) {
            if (!loadingSnapshot) {
                console.log(color);
                currentBColor = color;
                self.backgroundColorChange(color);
            }
        });

        this.requestSVG = function() {
            var svgStr = lc.getSVGString();
            self.setSVGString(svgStr);
        };

        this.requestImage = function() {
            var imgData = lc.getImage().toDataURL();
            self.setImageData(imgData);
        };
        
        this.requestSnapshot = function() {
        	var snapshot= JSON.stringify(lc.getSnapshot());
        	this.setSnapshot(snapshot);
        };
    };
