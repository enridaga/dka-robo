package dkarobo.sparql;

import java.util.Iterator;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.sparql.algebra.op.OpBGP;
import org.apache.jena.sparql.algebra.op.OpGraph;
import org.apache.jena.sparql.core.BasicPattern;
import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.engine.ExecutionContext;
import org.apache.jena.sparql.engine.QueryIterator;
import org.apache.jena.sparql.engine.binding.Binding;
import org.apache.jena.sparql.engine.iterator.QueryIteratorWrapper;
import org.apache.jena.sparql.engine.main.OpExecutor;

public class MonitoredOpExecutor extends OpExecutor {
	private QuadListener quadListener;
	public MonitoredOpExecutor(ExecutionContext execCtx, QuadListener quadListener) {
		super(execCtx);
		this.quadListener = quadListener;
	}

	protected QueryIterator execute(final OpGraph opGraph, QueryIterator input) {
		QueryIterator qIter = super.execute(opGraph, input);
		if (qIter.hasNext()) {
			// has matches
			qIter = new QueryIteratorWrapper(qIter){
				@Override
				protected Binding moveToNextBinding() {
					Binding b = super.moveToNextBinding();
					String g = null;
					if(opGraph.getNode().isVariable()){
						g = b.get(Var.alloc(opGraph.getNode().getName())).getURI();
					}
					OpBGP bgp = (OpBGP) opGraph.getSubOp();
					BasicPattern bp = bgp.getPattern();
					Iterator<Triple> it = bp.iterator();
					while(it.hasNext()){
						Triple t = it.next();
						Node s = null;
						Node p = null;
						Node o = null;
						if(t.getSubject().isConcrete()){
							s = t.getSubject();
						}else{
							s = b.get(Var.alloc(t.getSubject()));
						}
						if(t.getPredicate().isConcrete()){
							p = t.getPredicate();
						}else{
							p = b.get(Var.alloc(t.getPredicate()));
						}
						if(t.getObject().isConcrete()){
							o = t.getObject();
						}else{
							o = b.get(Var.alloc(t.getObject()));
						}
						quadListener.quad(g, new Triple(s, p, o));
					}
					return b;
				}
			};
			
		} else {
			// Ignore
		}
		return qIter;
	}
}
