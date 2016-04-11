package uk.open.ac.kmi.robo.dka.planner.operator;

import static uk.open.ac.kmi.robo.dka.planner.things.Symbols.At;
import static uk.open.ac.kmi.robo.dka.planner.things.Symbols.Forever;
import static uk.open.ac.kmi.robo.dka.planner.things.Symbols.Location;
import static uk.open.ac.kmi.robo.dka.planner.things.Symbols.Produced;
import static uk.open.ac.kmi.robo.dka.planner.things.Symbols.Quad;
import static uk.open.ac.kmi.robo.dka.planner.things.Symbols.ValidQuad;
import static uk.open.ac.kmi.robo.dka.planner.things.Symbols._;
import static uk.open.ac.kmi.robo.dka.planner.things.Symbols.hasHumidity;
import static uk.open.ac.kmi.robo.dka.planner.things.Symbols.hasPeopleCount;
import static uk.open.ac.kmi.robo.dka.planner.things.Symbols.hasTemperature;
import static uk.open.ac.kmi.robo.dka.planner.things.Symbols.hasWiFiSignal;
import static uk.open.ac.kmi.robo.dka.planner.things.Symbols.type;

import harmony.core.api.condition.Condition;
import harmony.core.api.effect.CompositeEffect;
import harmony.core.api.effect.Effect;
import harmony.core.api.fact.Fact;
import harmony.core.api.operator.OperatorException;
import harmony.core.api.thing.Thing;
import harmony.core.impl.condition.And;
import harmony.core.impl.condition.AssertFact;
import harmony.core.impl.condition.Bool;
import harmony.core.impl.condition.Not;
import harmony.core.impl.effect.BasicEffect;
import harmony.core.impl.effect.CompositeEffectImpl;
import harmony.core.impl.effect.ForallEffect;
import harmony.core.impl.fact.BasicFact;
import uk.open.ac.kmi.robo.dka.planner.ValidityProvider;
import uk.open.ac.kmi.robo.dka.planner.things.QuadProperty;
import uk.open.ac.kmi.robo.dka.planner.things.QuadResource;
import uk.open.ac.kmi.robo.dka.planner.things.QuadResourceImpl;
import uk.open.ac.kmi.robo.dka.planner.things.Validity;

public abstract class Observe extends RoboOperator {
	private int actionCost;
	private ValidityProvider provider;
	private QuadProperty property;
	
	@SuppressWarnings("unchecked")
	public Observe(String name, QuadProperty property, ValidityProvider validityProvider, int actionCostAsValidityDecreaseFactor) {
		super(name, QuadResource.class);
		this.actionCost = actionCostAsValidityDecreaseFactor;
		this.provider = validityProvider;
		this.property = property;
	}

	@Override
	public Condition getPrecondition(Thing... T) throws OperatorException {
		if (usesWildcard(T)) {
			return new Bool(false);
		}
		And and = new And();
		// Robot is in location
		and.append(new AssertFact(At, T[0]));
		// T[0] has to be a location?
		and.append(new AssertFact(Quad, Forever, T[0], type, Location));
		// We do have an observation
		and.append(new AssertFact(Quad, _, T[0], property, _));
		// and observation is invalid
		and.append(new Not(new AssertFact(ValidQuad, T[0], property, _)));
		// and has been never observed before
		and.append(new Not(new AssertFact(Produced, T[0], property, _)));
		return and;
	}

	@Override
	public Effect getEffect(final Thing... T) throws OperatorException {
		CompositeEffect ce = new CompositeEffectImpl();
		
		// Replace temperature with new valid statement
		// Remove old temperature
		@SuppressWarnings("unchecked")
		ForallEffect forAll = new ForallEffect(Validity.class, QuadResource.class) {
			@Override
			public Effect getEffect(Thing... F) {
				return new BasicEffect().toRemove(new BasicFact(Quad, F[0], T[0], property, F[1]));
			}
		};
		ce.append(forAll);
		ce.append(new DecreaseValidityEffect(actionCost){
			@Override
			protected boolean ignore(QuadResource r, QuadProperty p, QuadResource o) {
				// Do not decrease the Quad we are going to remove!
				return r.equals(T[0]) && p.equals(property);
			}
		}); 
		QuadResource temp = new QuadResourceImpl("T" + System.currentTimeMillis());
		Validity validity = provider.getValidity(T[0], property, temp);
		Fact f = new BasicFact(Quad, validity, T[0], property, temp);
		ce.append(new BasicEffect().toAdd(f).toAdd(new BasicFact(Produced, T[0], property, temp)));
		return ce;
	}
	
	public static class Temperature extends Observe {
		public Temperature(ValidityProvider validityProvider) {
			super("Temperature", hasTemperature, validityProvider, 1);
		}
	}

	public static class Humidity extends Observe {
		public Humidity(ValidityProvider validityProvider) {
			super("Humidity", hasHumidity, validityProvider, 2);
		}
	}

	public static class CountPeople extends Observe {
		public CountPeople(ValidityProvider validityProvider) {
			super("CountPeople", hasPeopleCount, validityProvider, 5);
		}
	}
	
	public static class CheckWiFi extends Observe {
		public CheckWiFi(ValidityProvider validityProvider) {
			super("CheckWiFi", hasWiFiSignal, validityProvider, 5);
		}
	}
}
