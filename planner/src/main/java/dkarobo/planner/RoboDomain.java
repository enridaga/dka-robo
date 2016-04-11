package dkarobo.planner;

import dkarobo.planner.operator.Move;
import dkarobo.planner.operator.Observe.CheckWiFi;
import dkarobo.planner.operator.Observe.CountPeople;
import dkarobo.planner.operator.Observe.Humidity;
import dkarobo.planner.operator.Observe.Temperature;
import dkarobo.planner.things.Symbols;
import harmony.core.api.domain.Domain;
import harmony.core.api.operator.Operator;
import harmony.core.api.property.Property;

public class RoboDomain implements Domain {

	private MoveCostProvider provider;
	private ValidityProvider validityProvider;

	public RoboDomain(MoveCostProvider provider, ValidityProvider validityProvider) {
		this.provider = provider;
		this.validityProvider = validityProvider;
	}

	@Override
	public Operator[] getOperators() {
		return new Operator[] { new Move(provider), new Temperature(validityProvider), new Humidity(validityProvider),
				new CheckWiFi(validityProvider), new CountPeople(validityProvider) };
	}

	@Override
	public Property[] getProperty() {
		return new Property[] { Symbols.Quad, Symbols.At, Symbols.Produced, Symbols.ValidQuad };
	};
}
