package it.polito.tdp.metroparis.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.event.ConnectedComponentTraversalEvent;
import org.jgrapht.event.EdgeTraversalEvent;
import org.jgrapht.event.TraversalListener;
import org.jgrapht.event.VertexTraversalEvent;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.traverse.BreadthFirstIterator;

import it.polito.tdp.metroparis.db.MetroDAO;

public class Model {
	
	Graph<Fermata, DefaultEdge> grafo;
	Map<Fermata, Fermata> prec;
	
	public void creaGrafo(){
		this.grafo = new SimpleGraph<>(DefaultEdge.class);
		
		MetroDAO dao = new MetroDAO();
		List<Fermata> fermate = dao.getAllFermate();
		
		Graphs.addAllVertices(this.grafo, fermate);
		
//		for(Fermata f1: this.grafo.vertexSet()) {
//			for(Fermata f2: this.grafo.vertexSet()) {
//				if(!f1.equals(f2) && dao.fermateCollegate(f1, f2)) {
//					this.grafo.addEdge(f1, f2) ;
//				}
//			}
//		}
		
		List<Connessione> connessioni = dao.getAllConnessioni(fermate) ;
		for(Connessione c: connessioni) {
			this.grafo.addEdge(c.getStazP(), c.getStazA()) ;
		}
		
		System.out.format("Grafo creato con %d vertici e %d archi\n",
				this.grafo.vertexSet().size(),this.grafo.edgeSet().size());		
//		Fermata f;
//		Set <DefaultEdge> archi = this.grafo.edgesOf(f);
//		for(DefaultEdge e : archi) {
//			Fermata f1 = Graphs.getOppositeVertex(this.grafo, e, f);
//		}
		
//		List<Fermata> fermateS = Graphs.predecessorListOf(this.grafo, f);
	}
		
	public List<Fermata> fermateRaggiungibili(Fermata partenza){
		BreadthFirstIterator<Fermata, DefaultEdge> bfv = new BreadthFirstIterator<>(this.grafo, partenza);
		List<Fermata> res = new ArrayList<>();
		
		this.prec = new HashMap<>();
		this.prec.put(partenza, null);
		
		bfv.addTraversalListener(new TraversalListener<Fermata, DefaultEdge>(){

			@Override
			public void connectedComponentFinished(ConnectedComponentTraversalEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void connectedComponentStarted(ConnectedComponentTraversalEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void edgeTraversed(EdgeTraversalEvent<DefaultEdge> e) {
				// TODO Auto-generated method stub
				DefaultEdge arco =e.getEdge();
				Fermata a = grafo.getEdgeSource(arco);
				Fermata b = grafo.getEdgeTarget(arco);
				if(prec.containsKey(b) && !prec.containsKey(a)) {
					prec.put(a, b);
					System.out.println(a + " scoperto da " +b);
				}else if(prec.containsKey(a) && !prec.containsKey(b)){
					prec.put(b, a);
					System.out.println(b + " scoperto da " +a);
				}
				
			}

			@Override
			public void vertexTraversed(VertexTraversalEvent<Fermata> e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void vertexFinished(VertexTraversalEvent<Fermata> e) {
				// TODO Auto-generated method stub
				
			}} );
		
		while(bfv.hasNext()) {
			Fermata f = bfv.next();
			res.add(f); 
		}
		return res;
	}
	
	public Fermata trovaFermata(String n) {
		for(Fermata f: this.grafo.vertexSet()) {
			if(f.getNome().equals(n))
				return f;
		}
		return null;
	}
	
	public List<Fermata> trovaCammino(Fermata p, Fermata a){
		fermateRaggiungibili(p);
		
		List<Fermata> res = new LinkedList<>();
		res.add(a);
		Fermata f = a;
		while(prec.get(f)!=null) {
			f = prec.get(f);
			res.add(0,f);
		}
		
		return res;
		
	}

}
