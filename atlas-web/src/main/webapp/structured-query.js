var counter = 0;
var geneCounter = 0;

function escapeHtml(s) {
    return s.replace(/\"/g,"&quot;")
            .replace(/</g,"&lt;")
            .replace(/>/g,"&gt;")
            .replace(/&/g,"&amp;");
}

(function($){

     function createRemoveButton(callback)
     {
         return $('<td />').append($('<input type="button" value="-" />').click(callback));
     }

     function createNotButton(name,checked)
     {
         return $('<td><select name="' + name + '"><option ' + (checked ? '' : 'selected="selected"') + 'value="">has</option><option'
                  + (checked ? ' selected="selected"' : '') + ' value="1">has not</option></select></td>');
     }

     function createSelect(name, options, optional, value) {
         var e = document.createElement("select");
         if (name) {e.name = name;e.id=name;}
         if (optional) e.options[0] = new Option("(any)", "");
         if (options) {
             var selected = 0;
             for (var i = 0; i < options.length; i++) {
                 var option;
                 if (typeof(options[i]) == "object") {
                     option = new Option(options[i][1], options[i][0]);
                 } else {
                     option = new Option(options[i], options[i]);
                 }
                 if(value == option.value){
                     selected = e.options.length;
                 }
                 e.options[e.options.length] = option;
             }
             e.selectedIndex = selected;
         }
         return e;
     }

     function addSpecie(specie) {
         ++counter;
         var sel = $('#species').get(0);
         var found = false;
         for(var i = 0; i < sel.options.length; ++i)
             if(sel.options[i].value.toLowerCase() == specie.toLowerCase()) {
                 specie = sel.options[i].value;
                 sel.options[i] = null;
                 found = true;
                 break;
             }

         if(!found)
            return;         

         var value = $('<td class="specval">' + specie + '<input class="specval" type="hidden" name="specie_' + counter + '" value="' + specie + '"></td>');
         var remove = createRemoveButton(function () {
                                             var tr = $(this).parents('tr:first');
                                             var rmvalue = $('input.specval', tr).val();
                                             var tbody = tr.parents('tbody:first');
                                             var specs = $('tr.speccond', tbody);
                                             if(specs.length > 1 && specs.index(tr.get(0)) == 0) {
                                                 var nextr = tr.next('tr');
                                                 $('td.specval', tr).replaceWith($('td.specval', nextr));
                                                 nextr.remove();
                                             } else {
                                                 tr.remove();
                                             }
                                             var i;
                                             var sel = $('#species').get(0);
                                             for(i = 0; i < sel.options.length; ++i)
                                                 if(sel.options[i].value >= rmvalue)
                                                     break;
                                             sel.options.add(new Option(rmvalue,rmvalue), i);
                                         });

         var tbody = $('#conditions');
         var tr = $('tr.speccond:last', tbody);
         if(tr.length > 0) {
             tr.after($('<tr class="speccond"><td colspan="2">or</td></tr>').append(value).append(remove).append($('<td />')));
         } else {
             tbody.prepend($('<tr class="speccond"><td colspan="2">species is</td></tr>')
                     .append(value).append(remove).append($('<td />')));
         }
     }

     function addExpFactor(factor,expression,values,expansion) {
         var selopt = $('#factors').get(0).options;
         var factorLabel = factor;
         for(var i = 0; i < selopt.length; ++i)
             if(selopt[i].value == factor) {
                 factorLabel = selopt[i].text;
             }

         if(factor == "")
             factorLabel = "";

         ++counter;


         var input = $('<input type="text" class="value"/>')
             .attr('name', "fval_" + counter)
             .autocomplete("fval", {
                               minChars:0,
                               matchCase: false,
                               matchSubset: false,
                               selectFirst: false,
                               multiple: false,
                               multipleSeparator: " ",
                               multipleQuotes: true,
                               scroll: false,
                               scrollHeight: 180,
                               max: 20,
                               extraParams: { type: 'efv', 'factor' : factor },
                               formatItem: function(row) { return row[0]; },
                               formatResult: function(row) { return row[0].indexOf(' ') >= 0 ? '"' + row[0] + '"' : row[0]; }
                           })
             .flushCache();

         var tr = $('<tr class="efvcond" />')
             .append($('<td />').append(createSelect("fexp_" + counter, options['expressions'], false, expression)))
             .append($('<td />').text('in ' + factorLabel).append($('<input type="hidden" name="fact_' + counter + '" value="'+ factor +'">')))
             .append($('<td />').append(input))
             .append(createRemoveButton(function () {
                                            var tr = $(this).parents('tr:first');
                                            var tbody = tr.parents('tbody:first').get(0);
                                            tr.remove();
                                        }))
             .append($('<td class="expansion" />').html(expansion != null ? expansion : ""));

         if(values != null) {
             input.val(values);
             input.keyup(function () { if(values != this.value) tr.find("td.expansion").text(''); });
         }

         var t = $('tr.efvcond:last,tr.speccond:last', $('#conditions'));
         if(t.length)
             t.eq(0).after(tr);
         else
             $('#conditions').append(tr);
     }

     function getPropLabel(property)
     {
         var selopt = $('#geneprops').get(0).options;
         var propertyLabel = property;
         for(var i = 0; i < selopt.length; ++i)
             if(selopt[i].value == property) {
                 propertyLabel = selopt[i].text;
             }

         if(property == "")
             propertyLabel = "property";

         return 'gene ' + propertyLabel;
     }

     function addGeneQuery(property,values,not) {

         ++counter;

         var input = $('<input type="text" class="value"/>')
             .attr('name', "gval_" + counter)
             .val(values != null ? values : "")
             .autocomplete("fval", {
                               minChars:0,
                               matchCase: false,
                               matchSubset: false,
                               selectFirst: false,
                               multiple: false,
                               multipleSeparator: " ",
                               multipleQuotes: true,
                               scroll: false,
                               scrollHeight: 180,
                               max: 20,
                               extraParams: { type: 'gene', 'factor' : property },
                               formatItem: property=="" ? function(row) { return row[0] + ': ' + row[1] + ' (' + row[2] + ')'; } : function(row) { return row[1] + ' (' + row[2] + ')'; } ,
                               formatResult: function(row) { return row[1].indexOf(' ') >= 0 ? '"' + row[1] + '"' : row[1]; }
                           })
             .flushCache()
             .result(function (unused, res) {
                         var newprop = res[0];
                         var tr = $(this).parents('tr:first');
                         tr.find('input[type=hidden]').val(newprop);
                         tr.find('td.gprop').text(getPropLabel(newprop));
                         $(this).setOptions({extraParams: { type: 'gene', factor: newprop }}).flushCache();
                     });

         var tr = $('<tr class="genecond" />')
             .append(createNotButton('gnot_' + counter, not))
             .append($('<td class="gprop" />').text(getPropLabel(property)))
             .append($('<td />').append($('<input type="hidden" name="gprop_' + counter + '" value="'+ property +'">')).append(input))
             .append(createRemoveButton(function () {
                                            var tr = $(this).parents('tr:first');
                                            var tbody = tr.parents('tbody:first').get(0);
                                            tr.remove();
                                        }))
             .append($('<td />'));

         $('#conditions').append(tr);
     }

     window.initQuery = function() {

         var oldval = $('#gene0').val();
         $("#gene0")
             .defaultvalue("(all genes)","(all genes)")
             .autocomplete("fval", {
                               minChars:1,
                               matchCase: false,
                               matchSubset: false,
                               multiple: false,
                               selectFirst: false,
                               extraParams: { type: 'gene' },
                               formatItem: function(row) { return row[0] + ': ' + row[1] + ' (' + row[2] + ')'; },
                               formatResult: function(row) { return row[1].indexOf(' ') >= 0 ? '"' + row[1] + '"' : row[1]; }
                           })
             .result(function (unused, res) {
                         var newprop = res[0];
                         $('#gprop0').val(newprop);
                         var oldval = $(this).val();
                         this.onkeyup = function () { if(oldval != this.value) $('#gprop0').val(''); };
                       //  $(this).setOptions({extraParams: { type: 'gene', factor: newprop }}).flushCache();
                     }).get(0).onkeyup = function () { if(oldval != this.value) $('#gprop0').val(''); };


         var fval0old  = $("#fval0")
             .defaultvalue("(all conditions)")
             .autocomplete("fval", {
                               minChars:1,
                               matchCase: false,
                               matchSubset: false,
                               multiple: false,
                               selectFirst: false,
                               multipleQuotes: true,
                               multipleSeparator: " ",
                               scroll: false,
                               scrollHeight: 180,
                               max: 10,
                               extraParams: { type: 'efv', factor: '' },
                               formatItem: function(row) { return row[0]; },
                               formatResult: function(row) { return row[0].indexOf(' ') >= 0 ? '"' + row[0] + '"' : row[0]; }
                           })
             .keyup(function (e) { if(this.value != fval0old) $("#simpleform .expansion").remove(); }).val();


         $('#geneprops').change(function () {
                                    if(this.selectedIndex == 0)
                                        return;

                                    var property = this.options[this.selectedIndex].value;
                                    addGeneQuery(property);
                                    this.selectedIndex = 0;
                                });


         $('#species').change(function () {
                                  if(this.selectedIndex == 0)
                                      return;

                                  var specie = this.options[this.selectedIndex].value;
                                  addSpecie(specie);

                                  this.selectedIndex = 0;
                              });

         $('#factors').change(function () {
                                  if(this.selectedIndex == 0)
                                      return;

                                  var factor = this.options[this.selectedIndex].value;
                                  addExpFactor(factor);
                                  this.selectedIndex = 0;
                              });


         if(lastquery && lastquery.species.length) {
             for(var i = 0; i < lastquery.species.length; ++i)
                 addSpecie(lastquery.species[i]);
         }

         if(lastquery && lastquery.conditions.length) {
             for(var i = 0; i < lastquery.conditions.length; ++i)
                 addExpFactor(lastquery.conditions[i].factor,
                              lastquery.conditions[i].expression,
                              lastquery.conditions[i].values,
                              lastquery.conditions[i].expansion);
         }

         if(lastquery && lastquery.genes.length) {
             for(var i = 0; i < lastquery.genes.length; ++i)
         	 addGeneQuery(lastquery.genes[i].property,
                              lastquery.genes[i].query,
                              lastquery.genes[i].not);
         }
     };

     window.renumberAll = function() {
         var i = 0;
         $('input.specval').each(function(){ this.name = this.name.replace(/_\d+/, '_' + (++i)); });

         i = 0;
         $('#conditions tr.efvcond,#conditions tr.genecond').each(function(){
                                      $('input,select', this).each(function(){ this.name = this.name.replace(/_\d+/, '_' + i); });
                                      ++i;
                                  });
         $('#species,#geneprops,#factors').remove();
     };


     window.hmc = function (igene, iefv, tdd) {
         $("div.expopup .closebox").click();

         var td = $(tdd);

         var gene = resultGenes[igene];
         var efv = resultEfvs[iefv];

         var waiter = $('<div/>').addClass('waiter').append($('<img/>').attr('src','expandwait.gif'));

         td.prepend(waiter);
         td.addClass('counter').removeClass('acounter');
         td.unbind('click');

         $.ajax({
             mode: "queue",
             port: "sexpt",
             url: 'experiments.jsp',
             dataType: "html",
             data: { gene:gene.geneAtlasId, ef: efv.ef, efv: efv.efv },
             complete: function(resp) {
                 waiter.remove();

                 var popup = $("<div/>").addClass('expopup')
                         .html(resp.responseText)
                         .prepend($("<div/>").addClass('closebox')
                         .click(
                         function(e) {
                             td.click(function() { hmc(igene, iefv, tdd); });
                             td.addClass('acounter').removeClass('counter');
                             popup.hide('normal',function() { popup.remove(); });
                             e.stopPropagation();
                             return false;
                         }).text('close'))
                         .click(function(e){e.stopPropagation();})
                         .hide()
                         .attr('title','');

                 td.prepend(popup);
                 popup.show('normal');
             }
         });
     };

     window.drawEfvNames = function () {
         $(".genename a").tooltip({
             bodyHandler: function () {
                 return $(this).next('.gtooltip').html();
             },
             showURL: false
         });

         var cs = 0.707106781186548;
         var attr = {"font": '12px sans-serif', 'text-anchor': 'start'};

         var testR = Raphael(0,0,10,10);
         var maxH = 0;
         var lastW = 0;
         for(var k = 0; k < resultEfvs.length; ++k)
         {
             var txt = testR.text(0, 0, resultEfvs[k].efv).attr(attr);
             var bw = txt.getBBox().width * cs;
             if(maxH < bw)
                 maxH = bw;
             if(k == resultEfvs.length - 1)
                 lastW = bw;
         }
         testR.remove();

         var ff = document.getElementById("fortyfive");
         var sq = document.getElementById("squery");

         var R = Raphael("fortyfive", sq.offsetWidth + Math.round(lastW) + 20, Math.round(maxH) + 20);

         var colors = ['#000000','#999999'];

         k = 0;
         var cp = -1;
         var curef = null;
         $("#squery tbody tr:first td:gt(1)").each(function () {
                                                       if(this.className != 'counter' && this.className != 'acounter')
                                                           return;
                                                       if(curef == null || curef != resultEfvs[k].ef)
                                                       {
                                                           if(++cp == colors.length)
                                                               cp = 0;
                                                           curef = resultEfvs[k].ef;
                                                       }
                                                       var x = this.offsetLeft;
                                                       var txt = R.text(x + 5, R.height - 5, resultEfvs[k].efv).attr(attr).attr({fill: colors[cp]});
                                                       var bb = txt.getBBox();
                                                       txt.matrix(cs, cs, -cs, cs, bb.x - cs * bb.x - cs * bb.y, bb.y + cs * bb.x - cs * bb.y);
                                                       R.path({stroke: "#cdcdcd", 'stroke-width': 2}).moveTo(x - 1, R.height).lineTo(x - 1, R.height - 20);
                                                       ++k;
                                                   });

     };

     window.structuredMode = function() {
         $("#simpleform").hide('fast');
         $("#structform").show('fast');
     };

     window.simpleMode = function() {
         $("#structform").hide('fast');
         $("#simpleform").show('fast');
     };

 })(jQuery);
