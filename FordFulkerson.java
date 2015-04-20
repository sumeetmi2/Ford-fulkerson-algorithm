import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Stack;

public class FordFulkerson {

	public static void main(String[] args) {
		String[] prodUnits = new String[]{"c2","c5","c7"};
		String[] exportUnits = new String[]{"c4","c10"};
		String[] road = new String[]{"c1#c2#6","c2#c3#12","c2#c4#3","c3#c5#22","c3#c6#23","c4#c7#13","c5#c8#16","c6#c8#11","c6#c9#9","c7#c9#12","c9#c10#15","c8#c10#7"};
		System.out.println(production_value(prodUnits, exportUnits, road));

	}
	
	static HashMap<String, Edge> edgeCap = new HashMap<String, Edge>();
	static HashMap<String,Vertex> vertices = new HashMap<String,Vertex>();

	static Vertex getVertex(String id){
		if(!vertices.containsKey(id)){
			vertices.put(id,new Vertex(id));
		}
		return vertices.get(id);
	}
	
	static class Vertex {
		String id;

		@Override
		public String toString() {
			return id;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((id == null) ? 0 : id.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Vertex other = (Vertex) obj;
			if (id == null) {
				if (other.id != null)
					return false;
			} else if (!id.equals(other.id))
				return false;
			return true;
		}

		public Vertex(String id) {
			this.id = id;
		}

		ArrayList<Edge> adjacencies = new ArrayList<Edge>();
	}

	static class Edge {
		String edgeId;

		@Override
		public String toString() {
			return edgeId;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((edgeId == null) ? 0 : edgeId.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Edge other = (Edge) obj;
			if (edgeId == null) {
				if (other.edgeId != null)
					return false;
			} else if (!edgeId.equals(other.edgeId))
				return false;
			return true;
		}

		Vertex sourceNode;
		Vertex targetNode;

		public Edge(Vertex sourceNode, Vertex targetNode, double capacity) {
			edgeId = sourceNode.id + "to" + targetNode.id;
			this.sourceNode = sourceNode;
			this.capacity = capacity;
			this.targetNode = targetNode;
			edgeCap.put(edgeId, this);
		}

		double capacity = 0;
		double flow = 0;
	}

	public static int production_value(String[] prod,String[] exp,String[] road)
    {
		Vertex source = getVertex("s");
		Vertex sink = getVertex("t");
		for(String s: prod){
			Vertex x = getVertex(s);
			x.adjacencies.add(new Edge(x,source,Double.POSITIVE_INFINITY));
			source.adjacencies.add(new Edge(source,x,Double.POSITIVE_INFINITY));
		}
		
		for(String s:exp){
			Vertex x = getVertex(s);
			x.adjacencies.add(new Edge(x,sink,Double.POSITIVE_INFINITY));
			sink.adjacencies.add(new Edge(sink,x,Double.POSITIVE_INFINITY));
		}
		
		try{
			for(String s:road){
				if(s!=null){
					String[] tmp = s.split("#");
					if(tmp.length!=3){
						throw new Exception();
					}
					if(tmp.length == 3){
						String sourceNode = tmp[0];
						String destNode = tmp[1];
						Double capacity = Double.valueOf(tmp[2]);
						Vertex x = getVertex(sourceNode);
						Vertex y = getVertex(destNode);
						x.adjacencies.add(new Edge(x,y,capacity));
						y.adjacencies.add(new Edge(y,x,capacity));
					}
				}
			}
		}catch(Exception e){
			
		}
		
		double result = maxFlow(source, sink);
		return (int) result;
    }

	static double maxFlow(Vertex src, Vertex dest) {
		Stack<LinkedList<Edge>> allpaths = new Stack<LinkedList<Edge>>();
		allpaths.push(findPath(src, dest, new LinkedList<Edge>()));
		while (!allpaths.isEmpty()) {
			LinkedList<Edge> path = allpaths.pop();
			if(path == null){
				break;
			}
			double minCap = Double.POSITIVE_INFINITY;
			for (int i = 0; i < path.size(); i++) {
				Edge e = edgeCap.get(path.get(i).edgeId);
				double residual = e.capacity-e.flow;
				if (residual < minCap
						&& residual != Double.POSITIVE_INFINITY) {
					minCap = residual;
				}
			}
			for (int i = 0; i < path.size(); i++) {
				Edge e = edgeCap.get(path.get(i).edgeId);
				Edge oe = edgeCap.get(e.targetNode.id+"to"+e.sourceNode.id);
				e.flow += minCap;
				oe.flow -= minCap;
			}
			allpaths.push(findPath(src, dest, new LinkedList<Edge>()));
		}
		double sum =0;
		for(Edge e: src.adjacencies){
			sum+=edgeCap.get(e.edgeId).flow;
		}
		return sum;
	}

	private static LinkedList<Edge> findPath(Vertex s, Vertex t,
			LinkedList<Edge> path) {
		LinkedList<Edge> result = null;
		if (s.equals(t)) {
			return path;
		} else {
			for (Edge edge1 : s.adjacencies) {
				Edge edge=edgeCap.get(edge1.edgeId);
				double residual = edge.capacity -  edge.flow;
				if (residual > 0 || ( edge.capacity == Double.POSITIVE_INFINITY)) {
					Edge oppEdge =  edgeCap.get(edge.targetNode+"to"+edge.sourceNode);
					if (!path.contains(edge) && !path.contains(oppEdge)) {
						LinkedList<Edge> subpath = new LinkedList<Edge>(path);
						subpath.add(edge);
						result = findPath(edge.targetNode, t, subpath);
						if (result != null) {
							break;
						}
					}
				}
			}
			return result;
		}
	}
}
