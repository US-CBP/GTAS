function graph(d3, domElem) {
    //step 0, new graph() ,import "http://d3js.org/d3.v3.min.js" to get d3
    //step 1, custom the config
    this.config = {
        bg_size: {
            width: 800,
            height: 600
        },
        edge_def_width: 5,
        edge_show_arrow: true,
        node_draggable: true,
        show_performance_bar: false,
    }

    var self = this;
    var cluster = d3.layout.cluster().size([self.config.bg_size.height, self.config.bg_size.width - 160]);
    /// step 2, custom the actions
    var showTitleAction;
    var showSubheadAction;
    var showPathDesc;

    this.showTitle = function (f) {
        showTitleAction = f;
    }

    this.showSubhead = function (f) {
        showSubheadAction = f;
    }

    this.showPathDesc = function (f) {
        showPathDesc = f;
    }
    /// final step , bind some data
    this.bind = function (data) {
        var conv2tree = function (data) {
            var root = self.getRoot(data);
            var hasParentFlag = {};
            hasParentFlag[root.id] = true;
            self.traverseEdge(data, function (source, target) {
                if (!hasParentFlag[target.id] && source.id != target.id) {
                    if (!source.children) {
                        source.children = [];
                    }
                    source.children.push(target);
                    hasParentFlag[target.id] = true;
                }
            });
            return root;
        }
        var buildNodes = function (tree) {
            return cluster.nodes(tree);
        }
        var buildLinks = function (data) {
            var result = [];
            self.traverseEdge(data, function (source, target, ref) {
                result.push({
                    'source': source,
                    'target': target,
                    'ref': ref
                });
            });
            return result;
        }
        var merge = function (nodes, links) {
            var oldData = [];
            if (self.nodes) {
                self.nodes.forEach(function (d) {
                    oldData[d.id] = d;
                });
            }
            if (oldData) {
                nodes.forEach(function (d) {
                    if (oldData[d.id]) {
                        d.x = oldData[d.id].x;
                        d.y = oldData[d.id].y;
                    }
                });
            }
            self.nodes = nodes;
            self.links = links;
        }
        //1)temporarily convert a connectivity to a tree
        var tree = conv2tree(data);
        //2)caculate for nodes' coords with <code>cluster.nodes(tree);</code>
        var nodes = buildNodes(tree);
        //3)fill in all the edges(links) of the connectivity
        var links = buildLinks(data);
        //4)do merge to keep info like node's position
        merge(nodes, links);
        //5)redraw
        self.redraw();
    }
    /// call redraw() if necessary (reconfig,recostom the actions, etc. )
    this.redraw = function () {
        var fontSize = 8
        var lineSpace = 2
        var boxHeight = 50
        var boxWidth = 85

        var width = self.config.bg_size.width;
        var height = self.config.bg_size.height;

        var yscale_performancebar = d3.scale.linear()
            .domain([0, 1])
            .rangeRound([boxHeight / 2, -boxHeight / 2])


        var diagonal = d3.svg.diagonal()
            .projection(function (d) {
            return [d.y - boxWidth / 2, d.x];
        });

        var _clear = function () {
            d3.select("svg").remove();

            svg = d3.select(domElem).append("svg")
                .attr("width", width)
                .attr("height", height)
                .append("g")
                .attr("transform", "translate(80,0)");

            svg.append("svg:defs").selectAll("marker")
                .data(["suit"])
                .enter().append("svg:marker")
                .attr("id", "idArrow")
                .attr("viewBox", "0 -5 10 10")
                .attr("refX", 15)
                .attr("refY", -1.5)
                .attr("markerWidth", 6)
                .attr("markerHeight", 6)
                .attr("orient", "auto")
                .append("svg:path")
                .attr("d", "M0,-5L10,0L0,5");
        }

        var _redrawEdges = function () {
            var linksWithArrow = self.links;
            //to show arrow at the end of the path with fixed size, we have to copy each path with .stroke-width=1
            if (self.config.edge_show_arrow) {
                linksWithArrow = [];
                self.links.forEach(function (d) {
                    var fake = {};
                    for (prop in d) {
                        fake[prop] = d[prop];
                    }
                    fake.faked = true; //copy each path with .faked=true as flag
                    linksWithArrow.push(fake);
                    linksWithArrow.push(d);
                })
            }
            var path = svg.selectAll(".link").data(linksWithArrow);
            // when new path arrives
            path.enter().insert("path", ":first-child")
                .attr("marker-end", function (d) {
                if (d.faked) return "url(#idArrow)";
            })
                .attr("id", function (d) {
                if (!d.faked) return "link" + d.ref.from + "-" + d.ref.to;
            })
                .attr("class", function (d) {
                return "link" + " link-" + d.ref.from + " link-" + d.ref.to;
            })
                .attr("d", diagonal)
                .transition()
                .duration(1000)
                .style("stroke-width", function (d) {
                if (d.faked) {
                    return 1;
                }
                if (d.ref.edge_width) return Math.max(1, boxHeight / 2 * d.ref.edge_width); //won't become invisible if too thin
                else return self.config.edge_def_width; //default value
            })

            // when path changes
            path.attr("d", diagonal)

            // when path's removed
            path.exit().remove();
        }
        _clear();
        _redrawEdges();
        ///show description on each path(edge)
        if (showPathDesc) {
            svg.selectAll(".abc").data(self.links).enter().append("text").append("textPath")
                .attr("xlink:xlink:href", function (d) {
                return "#link" + d.ref.from + "-" + d.ref.to;
            })
            .attr("startOffset", "50%")
                .text(showPathDesc)
        }
        ///show each node with text
        var existingNodes = svg.selectAll(".node").data(self.nodes);
        //draw rectangle
        var newNodes = existingNodes.enter().append("g");

        newNodes.attr("class", "node")
            .attr("id", function (d) {
            return "node-" + d.id
        })
            .attr("transform", function (d) {
            return "translate(" + d.y + "," + d.x + ")";
        })
            //.append("rect") //make nodes as rectangles OR:
        .append("circle").attr('r',50) //make nodes as circles
        .attr('class', 'nodebox')
            .attr("x", -boxWidth / 2)
            .attr("y", -boxHeight / 2)
            .attr("width", boxWidth)
            .attr("height", boxHeight)

        if (self.config.node_draggable) {
            newNodes.call(d3.behavior.drag().origin(Object).on("drag", function (d) {
                //translate the node
                function translate(x, y) {
                    return {
                        'x': x,
                        'y': y
                    }
                }
                var coord = eval(d3.select(this).attr("transform"));
                d3.select(this)
                    .attr("transform", "translate(" + (coord.x + d3.event.dx) + "," + (coord.y + d3.event.dy) + ")")
                //update node's coord ,then redraw affected edges
                d.x = d.x + d3.event.dy;
                d.y = d.y + d3.event.dx;

                _redrawEdges();
            }));
        }
        //show performance bar
        if (self.config.show_performance_bar) {
            newNodes.append("rect")
                .attr('class', 'performancebar')
                .attr("x", boxWidth / 2 * 1.05)
                .attr("width", boxWidth / 10)
                .style("fill", "red")
                .style("stroke", "red")
                .attr("y", boxHeight / 2)
                .attr("height", 0)

            existingNodes.select('.performancebar')
                .transition()
                .duration(1000)
                .attr("y", function (d) {
                return yscale_performancebar(d.load)
            })
                .attr("height", function (d) {
                return boxHeight / 2 - yscale_performancebar(d.load)
            })
        }

        //node titles
        newNodes.append("text")
            .attr("class", "nodeTitle")
            .attr("y", -boxHeight / 2 + fontSize + 2 * lineSpace)
            .attr("text-anchor", "middle");

        newNodes.append("text")
            .attr("class", "nodeText f1Text")
            .attr("y", -boxHeight / 2 + 2 * fontSize + 3 * lineSpace)
            .attr("text-anchor", "middle")
            .text(function(d) {
              return "paxId: " + d.subhead["paxId"]
            });
        newNodes.append("text")
            .attr("class", "nodeText f1Text")
            .attr("y", -boxHeight / 2 + 3.5 * fontSize + 3 * lineSpace)
            .attr("text-anchor", "middle")
            .text(function(d) {
              return "flightId: " + d.subhead["flightId"]
        });
        existingNodes.select(".nodeTitle").text(showTitleAction ? showTitleAction : function (d) {
            return d.id + ")" + d.name
        });
    }
    /*
      return the root node
    */
    this.getRoot = function (data) {
        return data['0'];
    };

    /*
      traverse all nodes
      callback(node)
    */
    this.traverse = function (data, callback) {
        if (!data) console.error('data is null')

        function _init() {
            var i;
            for (i in data) {
                data[i].visited = false;
            }
        }

        function _traverse(pt, callback) {
            if (!pt) {
                return;
            }
            pt.visited = true;
            console.debug("traverse node:" + pt.id);
            callback(pt);
            if (pt.ref) {
                pt.ref.forEach(function (ref) {
                    var childNode = data[ref.to.toString()];
                    if (childNode && !childNode.visited) {
                        _traverse(childNode, callback);
                    }
                })
            }
        }

        _init();
        _traverse(self.getRoot(data), callback);
    };

    /*
      traverse all edges
      callback(sourceNode,targetNode,ref)
    */
    this.traverseEdge = function (data, callback) {
        if (!data) console.error('data is null')

        self.traverse(data, function (node) {
            if (node.ref) {
                node.ref.forEach(function (ref) {
                    var childNode = data[ref.to.toString()];
                    if (childNode) {
                        console.debug("traverse edge:" + node.id + "-" + childNode.id);
                        callback(node, childNode, ref);
                    }
                });
            }
        });
    }
}

//Data structure for graph
function init_page(myGraph, rootPax, paxLinks){
    var data = {};
    var ref=[];

    if(paxLinks.length>0) {
      for(var i=1; i<paxLinks.length; i++){

        //Setup connections for root
        ref[i] = {
          from: 0,
          to: i,
          edge_width: paxLinks[i].score,
          connections: Object.keys(paxLinks[i].highlightMatch)
        }

        //Setup nodes
        data[""+i] = {
          id: i,
          name: paxLinks[i].firstName +" " + paxLinks[i].lastName,
          subhead: {
            "paxId": paxLinks[i].passengerId,
            "flightId": paxLinks[i].flightId
          },
          ref: []
        }
      }
    }

    //Setup root
    data["0"] = {
      id: 0,
      name: rootPax.firstName + " " + rootPax.lastName,
      subhead: {
        "paxId": rootPax.passengerId,
        "flightId": rootPax.flightId
      },
      ref: ref
    };

    //customize anything here
    myGraph.showTitle(function (d) {
        return d.name;
    });

    myGraph.showSubhead(function (d) {
        return d.subhead;
    });

    myGraph.showPathDesc(function (d) {
        return '(' + d.ref.edge_width.toFixed(2)+ ")  " + d.ref.connections;
    });

    myGraph.bind(data);
}
