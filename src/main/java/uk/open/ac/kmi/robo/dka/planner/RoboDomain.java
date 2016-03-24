package uk.open.ac.kmi.robo.dka.planner;

import harmony.core.api.domain.Domain;
import harmony.core.api.operator.Operator;
import harmony.core.api.property.Property;
import uk.open.ac.kmi.robo.dka.planner.operator.Move;
import uk.open.ac.kmi.robo.dka.planner.operator.Observe.CheckWiFi;
import uk.open.ac.kmi.robo.dka.planner.operator.Observe.CountPeople;
import uk.open.ac.kmi.robo.dka.planner.operator.Observe.Humidity;
import uk.open.ac.kmi.robo.dka.planner.operator.Observe.Temperature;
import uk.open.ac.kmi.robo.dka.planner.things.Symbols;

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
