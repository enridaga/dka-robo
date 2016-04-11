package uk.open.ac.kmi.robo.dka.labusecase;

import java.util.Iterator;

import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.sparql.algebra.op.OpBGP;
import com.hp.hpl.jena.sparql.algebra.op.OpGraph;
import com.hp.hpl.jena.sparql.core.BasicPattern;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.engine.ExecutionContext;
import com.hp.hpl.jena.sparql.engine.QueryIterator;
import com.hp.hpl.jena.sparql.engine.binding.Binding;
import com.hp.hpl.jena.sparql.engine.iterator.QueryIteratorWrapper;
import com.hp.hpl.jena.sparql.engine.main.OpExecutor;

public class VOpExecutor extends OpExecutor {
	private VQuadListener quadListener;
	public VOpExecutor(ExecutionContext execCtx, VQuadListener quadListener) {
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
						String s = null;
						String p = null;
						String o = null;
						if(t.getSubject().isConcrete()){
							s = t.getSubject().toString(true);
						}else{
							s = b.get(Var.alloc(t.getSubject())).toString(true);
						}
						if(t.getPredicate().isConcrete()){
							p = t.getPredicate().toString(true);
						}else{
							p = b.get(Var.alloc(t.getPredicate())).toString(true);
						}
						if(t.getObject().isConcrete()){
							o = t.getObject().toString(true);
						}else{
							o = b.get(Var.alloc(t.getObject())).toString(true);
						}
						quadListener.quad(g, s, p, o);
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
