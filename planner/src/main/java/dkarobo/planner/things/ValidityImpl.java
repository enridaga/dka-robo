package dkarobo.planner.things;

import org.apache.commons.lang3.builder.HashCodeBuilder;
/**
 * Validity of something in number of seconds
 *
 */
public class ValidityImpl implements Validity {
	private int validity = 0;
	private int hashCode;

	public ValidityImpl() {
		this(0);
	}

	public ValidityImpl(int validity) {
		this.validity = validity;
		hashCode = new HashCodeBuilder().append(Validity.class).append(validity).toHashCode();
	}

	@Override
	public int asInteger() {
		return validity;
	}

	@Override
	public String getSignature() {
		return new StringBuilder().append("val(").append(Integer.toString(validity)).append(")").toString();
	}

	@Override
	public String toString() {
		return getSignature();
	}

	@Override
	public int hashCode() {
		return hashCode;
	}

	public static class Forever implements Validity {
		@Override
		public int asInteger() {
			return 1000000000;
		}

		@Override
		public String getSignature() {
			return "val(Forever)";
		}

		@Override
		public String toString() {
			return getSignature();
		}
	}
}
