package org.nanosite.conceptgraph.ui.cgviewer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.draw2d.AbsoluteBendpoint;
import org.eclipse.draw2d.BendpointConnectionRouter;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.ConnectionEndpointLocator;
import org.eclipse.draw2d.ConnectionLocator;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.draw2d.PolygonDecoration;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.draw2d.graph.CompoundDirectedGraph;
import org.eclipse.draw2d.graph.CompoundDirectedGraphLayout;
import org.eclipse.draw2d.graph.Edge;
import org.eclipse.draw2d.graph.Node;
import org.eclipse.draw2d.graph.NodeList;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.nanosite.conceptgraph.cg.ActualParam;
import org.nanosite.conceptgraph.cg.Application;
import org.nanosite.conceptgraph.cg.ConceptDef;
import org.nanosite.conceptgraph.cg.Detail;
import org.nanosite.conceptgraph.cg.Model;

public class ConceptGraph2D {

	private Font normalFont;
	private Font miniFont;

	private Model model;
	private ConceptDef focused;
	Map<ConceptDef,Node> cdefs = new HashMap<ConceptDef,Node>();

	MouseListener mouseListener;

	public ConceptGraph2D (Model model, ConceptDef focused, MouseListener mouseListener) {
		this.model = model;
		this.focused = focused;
		this.mouseListener = mouseListener;

		this.normalFont = new Font(null, "Arial", 11, SWT.NONE);
		this.miniFont = new Font(null, "Arial", 9, SWT.NONE);
	}

	public Figure draw() {
		CompoundDirectedGraph graph = createGraph();

		Figure contents = new Figure();
		contents.setBackgroundColor(ColorConstants.white);
		contents.setLayoutManager(new XYLayout());

//		for (int i = 0; i < graph.subgraphs.size(); i++) {
//			Subgraph s = (Subgraph)graph.subgraphs.get(i);
//			buildSubgraphFigure(contents, s);
//		}

		for (int i = 0; i < graph.nodes.size(); i++) {
			Node node = graph.nodes.getNode(i);
			Figure figure = (Figure)node.data;
			if (node.outgoing.isEmpty())
				figure.setBorder(new LineBorder(2));
			contents.add(figure, new Rectangle(node.x, node.y, node.width, node.height));

		}

		for (int i = 0; i < graph.edges.size(); i++) {
			Edge edge = graph.edges.getEdge(i);
			buildEdgeFigure(contents, edge);
		}



		return contents;
	}

	private CompoundDirectedGraph createGraph () {
		CompoundDirectedGraph dg = new CompoundDirectedGraph();

		// collect ConceptDefs to be shown
		Set<ConceptDef> show = new HashSet<ConceptDef>();
		if (focused==null) {
			for(ConceptDef cdef : model.getConcept()) {
				show.add(cdef);
			}
		} else {
			show.add(focused);
			show.addAll(getSuccessors(focused));
			for(ConceptDef cdef : model.getConcept()) {
				if (getSuccessors(cdef).contains(focused)) {
					show.add(cdef);
				}
			}
		}

		String txt = "";
		for(ConceptDef cdef : show) {
			if (! txt.isEmpty())
				txt += ", ";
			txt += cdef.getName();
		}
		System.out.println(txt);

		cdefs.clear();
		for(ConceptDef cdef : show) {
			createNode(cdef, show, dg);
		}

		new CompoundDirectedGraphLayout().visit(dg);
		return dg;
	}


	private Set<ConceptDef> getSuccessors (ConceptDef cdef) {
		Set<ConceptDef> succs = new HashSet<ConceptDef>();
		succs.addAll(cdef.getParent());
		succs.addAll(cdef.getContainer());
		for(Detail detail : cdef.getDetail()) {
			if (detail.getApplication()!=null) {
				Application app = detail.getApplication();
				for(ActualParam param : app.getParam()) {
					if (param.getConcept()!=null)
						succs.add(param.getConcept());
				}
			}
		}
		return succs;
	}


	private Node createNode (ConceptDef cdef, Set<ConceptDef> show, CompoundDirectedGraph dg) {
		if (cdefs.containsKey(cdef))
			return cdefs.get(cdef);

		// prepare graphical representation for this ConceptDef
		Figure figure = createFigure(cdef);
		//figure.setLayoutManager(new XYLayout());
		figure.setFont(normalFont);

		// add ConceptDef as graph-node to graph
		Node n = new Node(figure);
		Dimension size = figure.getPreferredSize();
		n.height= size.height+10;
		n.width = size.width+10;
		dg.nodes.add(n);
		cdefs.put(cdef, n);

		// add parent relations as graph-edges to parent
		for(ConceptDef parentdef : cdef.getParent()) {
			if (show.contains(parentdef)) {
				Node p = createNode(parentdef, show, dg);
				Edge e = new Edge("is-a", n, p);
				dg.edges.add(e);
			}
		}

		// add part-of relations as graph-edges to parent
		for(ConceptDef parentdef : cdef.getContainer()) {
			if (show.contains(parentdef)) {
				Node p = createNode(parentdef, show, dg);
				Edge e = new Edge("part-of", n, p);
				dg.edges.add(e);
			}
		}

		// add (some) details (only "applications") as further graph-edges
		for(Detail detail : cdef.getDetail()) {
			if (detail.getApplication()!=null) {
				Application app = detail.getApplication();
				ConceptDef rel = app.getConcept();
				int ip = 1;
				for(ActualParam param : app.getParam()) {
					ConceptDef pdef = param.getConcept();
					if (pdef!=null && show.contains(pdef)) {
						Node p = createNode(pdef, show, dg);
						String label = rel.getName();
						if (app.getParam().size()>1)
							label += "-" + ip;
						Edge e = new Edge(label, n, p);
						dg.edges.add(e);
					}
					ip++;
				}
			}
		}

		return n;
	}


//	private static void buildSubgraphFigure(Figure contents, Subgraph s) {
//		Figure figure = new Figure();
//		figure.setBorder(new LineBorder(ColorConstants.blue, s.insets.left));
//		contents.add(figure, new Rectangle(s.x, s.y, s.width, s.height));
//	}


	private Figure createFigure(ConceptDef cdef) {
	    Label label = new Label();
		label.setOpaque(true);
		label.setBorder(new LineBorder());

		label.setText(cdef.getName());

		Color bg = ColorConstants.yellow;
		if (cdef.getParam().size()>0)
			bg = ColorConstants.orange;
		label.setBackgroundColor(bg);

		if (cdef.getDetail().isEmpty())
			return label;

		// add all details
		ConceptFigure figure = new ConceptFigure(cdef, label);
		figure.addMouseListener(mouseListener);
		for(Detail detail : cdef.getDetail()) {
			// TODO add annotation (maybe as icon?)
			String txt = "";
			if (detail.getApplication()!=null) {
				Application app = detail.getApplication();
				boolean onlyConceptParams = true;
				for(ActualParam param : app.getParam()) {
					if (param.getConcept()==null)
						onlyConceptParams = false;
				}
				if (! onlyConceptParams) {
					txt = app.getConcept().getName() + "(";
					int ip = 1;
					for(ActualParam param : app.getParam()) {
						if (ip>1)
							txt += ", ";
						if (param.getConcept()!=null) {
							txt += param.getConcept().getName();
						} else if (param.getString()!=null){
							txt += param.getString();
						}
						ip++;
					}
					txt += ")";
				}
			}
			if (detail.getItem()!=null) {
				txt = detail.getItem();
			}

			if (txt.length()>0) {
				if (txt.length()>30)
					txt = txt.substring(0, 30) + "...";
				Label detailLabel = new Label(txt);
				detailLabel.setFont(miniFont);
				//detailLabel.setMaximumSize(new Dimension(80,40));
				figure.getDetailsCompartment().add(detailLabel);
			}
		}

		return figure;
	}


	/**
	 * Builds a figure for the given edge and adds it to contents
	 *
	 * @param contents
	 *            the parent figure to add the edge to
	 * @param edge
	 *            the edge
	 */
	static void buildEdgeFigure(Figure contents, Edge edge) {
		PolylineConnection conn = connection(edge);
		conn.setForegroundColor(ColorConstants.gray);
		PolygonDecoration dec = new PolygonDecoration();
		conn.setTargetDecoration(dec);
		conn.setPoints(edge.getPoints());
		ConnectionLocator midLocator = new ConnectionLocator(conn);
//		midLocator.setUDistance(30);
//		midLocator.setVDistance(-20);
		Label label = new Label(edge.data.toString());
		label.setForegroundColor(ColorConstants.darkGray);
		conn.add(label,midLocator);

		contents.add(conn);
	}

	/**
	 * Builds a connection for the given edge
	 *
	 * @param e
	 *            the edge
	 * @return the connection
	 */
	static PolylineConnection connection(Edge e) {
		PolylineConnection conn = new PolylineConnection();
		conn.setConnectionRouter(new BendpointConnectionRouter());
		List<AbsoluteBendpoint> bends = new ArrayList<AbsoluteBendpoint>();
		NodeList nodes = e.vNodes;
		if (nodes != null) {
			for (int i = 0; i < nodes.size(); i++) {
				Node n = nodes.getNode(i);
				int x = n.x;
				int y = n.y;
				bends.add(new AbsoluteBendpoint(x, y));
				bends.add(new AbsoluteBendpoint(x, y + n.height));
			}
		}
		conn.setRoutingConstraint(bends);
		return conn;
	}


}
