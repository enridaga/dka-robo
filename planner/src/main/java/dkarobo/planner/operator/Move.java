package dkarobo.planner.operator;

import static dkarobo.planner.things.Symbols.At;
import static dkarobo.planner.things.Symbols.Forever;
import static dkarobo.planner.things.Symbols.Location;
import static dkarobo.planner.things.Symbols.Produced;
import static dkarobo.planner.things.Symbols.Quad;
import static dkarobo.planner.things.Symbols.ValidQuad;
import static dkarobo.planner.things.Symbols._anything;
import static dkarobo.planner.things.Symbols.type;

import dkarobo.planner.MoveCostProvider;
import dkarobo.planner.things.QuadProperty;
import dkarobo.planner.things.QuadResource;
import dkarobo.planner.things.Validity;
import harmony.core.api.condition.Condition;
import harmony.core.api.effect.CompositeEffect;
import harmony.core.api.effect.Effect;
import harmony.core.api.operator.OperatorException;
import harmony.core.api.thing.Thing;
import harmony.core.impl.condition.And;
import harmony.core.impl.condition.AssertFact;
import harmony.core.impl.condition.Bool;
import harmony.core.impl.condition.Equality;
import harmony.core.impl.condition.Exists;
import harmony.core.impl.condition.Not;
import harmony.core.impl.effect.BasicEffect;
import harmony.core.impl.effect.CompositeEffectImpl;
import harmony.core.impl.fact.BasicFact;

/**
 * 
 * Move(T, R, From, To)
 *
 */
public class Move extends RoboOperator {
	private MoveCostProvider momentProvider;

	@SuppressWarnings("unchecked")
	public Move(MoveCostProvider provider) {
		super("Move", QuadResource.class, QuadResource.class);
		this.momentProvider = provider;
	}

	@Override
	public Condition getPrecondition(final Thing... T) throws OperatorException {
		if (usesWildcard(T)) {
			return new Bool(false);
		}
		And and = new And();
		and.append(new Not(new Equality(T[0], T[1])));
		// T[0] has to be a location?
		and.append(new AssertFact(Quad, Forever, T[0], type, Location));
		// T[1] has to be a location?
		and.append(new AssertFact(Quad, Forever, T[1], type, Location));
		// Robot must be at T[0] 
		and.append(new AssertFact(At, T[0]));
		// There is not something here that still is invalid and was not Produced
		@SuppressWarnings("unchecked")
		Exists ex1 = new Exists(Validity.class, QuadProperty.class, QuadResource.class) {
			@Override
			public Condition getCondition(Thing... H) {
				And and = new And();
				// there is some observation here
				and.append(new AssertFact(Quad, H[0], T[0], H[1], H[2]));
				and.append(new Not(new AssertFact(ValidQuad, T[0], H[1], H[2])));
				and.append(new Not(new AssertFact(Produced, T[0], H[1], H[2])));
				return and;
			}
		};
		and.append(new Not(ex1));
		// There exist something invalid to be sensed that was not Produced and is there!
		@SuppressWarnings("unchecked")
		Exists ex = new Exists(QuadResource.class, QuadProperty.class, QuadResource.class) {
			@Override
			public Condition getCondition(Thing... H) {
				And and = new And();
				and.append(new Not(new Equality(T[0], H[0])));
				and.append(new Equality(T[1], H[0]));
				and.append(new AssertFact(Quad, _anything, H[0], H[1], H[2]));
				and.append(new Not(new AssertFact(ValidQuad, H[0], H[1], H[2])));
				and.append(new Not(new AssertFact(Produced, H[0], H[1], H[2])));
				return and;
			}
		};
		and.append(ex);
		return and;
	}

	@Override
	public Effect getEffect(final Thing... T) throws OperatorException {
		final CompositeEffect composite = new CompositeEffectImpl();
		// Remove the Robot from that location
		composite.append(new BasicEffect().toRemove(new BasicFact(At, T[0])));
		final int cost = momentProvider.validityDecreaseFactor(T[0].getSignature(), T[1].getSignature());
		composite.append(new DecreaseValidityEffect(cost));
		composite.append(new BasicEffect().toAdd(new BasicFact(At, T[1])));
		return composite;
	}

}
