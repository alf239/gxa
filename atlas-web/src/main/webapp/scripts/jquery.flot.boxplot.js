/**
 * { series: [
 *     {
 *       boxes: { show: true },
 *       data: [
 *           {
 *             id: "DUSP1:Kaposi's sarcoma-associated herpesvirus",
 *             x: 0,
 *             min: 7.975854873657227,
 *             median: 8.196444511413574,
 *             max: 8.518889427185059,
 *             lq: 8.16000747680664,
 *             uq: 8.400483131408691
 *           },
 *           {
 *             id: "DUSP1:uninfected",
 *             x: 5,
 *             min: 7.975854873657227,
 *             median: 8.196444511413574,
 *             max: 8.518889427185059,
 *             lq:8.16000747680664,
 *             uq:8.400483131408691
 *            },
 *            ...
 *       ]
 *     },
 *     {
 *       boxes: { show: true },
 *       data: [
 *           {
 *             id: "DUSP1:Kaposi's sarcoma-associated herpesvirus",
 *             x: 1,
 *             min: 7.010293006896973,
 *             median: 7.403378963470459,
 *             max: 7.66507625579834,
 *             lq: 7.36135196685791,
 *             uq:7.616357803344727
 *           },
 *           ...
 *       ]
 *     }
 *     ...
 * }
 *
 */
(function ($) {
    var options = {
        series: {
            lines: {
                show: false
            },
            boxes: {
                show: false
            }
        },
        boxes: {
            hoverable: false
        }
    };

    function init(plot) {

        var boxes = [];

        function onMouseMove(e) {
            var pos = getPos(e);
            for(var i=0; i<boxes.length; i++) {
                var box = boxes[i];
                if (box.contains(pos.x, pos.y)) {
                    triggerBoxHoverEvent(e, box.x);
                    return;
                }
            }

            triggerBoxOutEvent(pos);
        }

        function triggerBoxHoverEvent(e, x) {
            plot.getPlaceholder().trigger("boxhover", [ e, x ]);
        }

        function triggerBoxOutEvent(pos) {
            plot.getPlaceholder().trigger("boxout", []);
        }

        function getPos(e) {
            var offset = plot.getPlaceholder().offset();
            var plotOffset = plot.getPlotOffset();
            return {
                x: clamp(0, e.pageX - offset.left - plotOffset.left, plot.width()),
                y: clamp(0, e.pageY - offset.top - plotOffset.top, plot.height())
            };
        }

        function clamp(min, value, max) {
            return value < min ? min : (value > max ? max : value);
        }

        function drawBox(x1, x2, min, lq, median, uq, max, axisx, axisy, color, ctx) {

            ctx.lineWidth = 2;
            ctx.strokeStyle = color;
            ctx.fillStyle = "#eee";

            var d = Math.abs(x2 - x1) / 2.0;

            if (x1 > axisx.max || x1 + 2*d < axisx.min || max < axisy.min || min > axisy.max) {
                return;
            }

            var bx1, bx2, by1, by2;

            bx1 = x1 + d;

            if (bx1 >= axisx.min && bx1 <= axisx.max) {                
                ctx.beginPath();
                ctx.moveTo(axisx.p2c(bx1), axisy.p2c(min));
                ctx.lineTo(axisx.p2c(bx1), axisy.p2c(max));
                ctx.closePath();
                ctx.stroke();
            }

            bx1 = axisx.p2c(Math.max(axisx.min, x1));
            bx2 = axisx.p2c(Math.min(axisx.max, x1 + 2 * d));
            by1 = axisy.p2c(Math.max(axisy.min, lq));
            by2 = axisy.p2c(Math.min(axisy.max, uq));

            ctx.beginPath();
            ctx.moveTo(bx1, by1);
            ctx.lineTo(bx1, by2);
            ctx.lineTo(bx2, by2);
            ctx.lineTo(bx2, by1);
            ctx.closePath();
            ctx.fill();
            ctx.stroke();  

            boxes.push({x1: bx1, x2: bx2, y1: by1, y2: by2, x: x1, contains: function(x,y) {return this.x1 <= x && this.x2 >= x && (this.y1 + 2) >= y && (this.y2 - 2) <= y;} });

            if (median >= axisy.min && median <= axisy.max) {
                ctx.beginPath();
                ctx.moveTo(bx1, axisy.p2c(median));
                ctx.lineTo(bx2, axisy.p2c(median));
                ctx.closePath();
                ctx.stroke();
            }

        }
        
        function drawSeriesBoxes(ctx, plotOffset, series) {
            
            function plotBoxes(datapoints, axisx, axisy, color) {
                var points = datapoints.points, ps = datapoints.pointsize;
                
                for (var i = 0; i < points.length; i += ps*5) {
                    if (points[i] == null) 
                    continue;
                    drawBox(points[i], points[i + 2], points[i + 1], points[i + 3], points[i + 5], points[i + 7], points[i + 9], axisx, axisy, color, ctx);
                }
            }

            ctx.save();
            ctx.translate(plotOffset.left, plotOffset.top);

            plotBoxes(series.datapoints, series.xaxis, series.yaxis, series.color);

            ctx.restore();
        }
        
        function drawSeries(plot, ctx) {
            var series = plot.getData();
            var offset = plot.getPlotOffset();
                
            for(var i=0; i<series.length; i++) { 
                if (series[i].boxes.show) { 
                    drawSeriesBoxes(ctx, offset, series[i]);
                }
            }
        }
        
        function processRawData(plot, series, seriesData, datapoints) {
            if (!series.boxes.show) {
                return;
            }
            
            var points = [];
            var boxWidth = 0.6;
                       
            for(var i=0; i<seriesData.length; i++) {
               var d = seriesData[i];

               if (d.x == undefined) {
                   continue;
               }

               points.push([d.x, d.min]);
               
               points.push([d.x + boxWidth, d.lq]);
               
               points.push([d.x, d.median]);
               
               points.push([d.x, d.uq]);
               
               points.push([d.x, d.max]);
               
            } 

            if (points.length > 0) {
                series.data = points;
            }
        }

        function bindEvents(aPlot, eventHolder) {
            var options = aPlot.getOptions();
            if (options.boxes.hoverable) {
                eventHolder.mousemove(onMouseMove);
            }
        }

        plot.hooks.processRawData.push(processRawData);
        plot.hooks.draw.push(drawSeries);
        plot.hooks.bindEvents.push(bindEvents);
    }
    
    $.plot.plugins.push({
        init: init,
        options: options,
        name: "boxplot",
        version: "1.0"
    });
})(jQuery);
