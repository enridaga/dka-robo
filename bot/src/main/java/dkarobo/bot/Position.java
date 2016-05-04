package dkarobo.bot;

import org.apache.commons.lang3.builder.HashCodeBuilder;

public class Position implements Coordinates {
	private float X, Y, Z;
	private int hashCode;

	private Position(float X, float Y, float Z) {
		this.X = X;
		this.Y = Y;
		this.Z = Z;
		hashCode = new HashCodeBuilder(7, 37).append(X).append(Y).append(Z).append(Position.class).toHashCode();
	}

	@Override
	public float getX() {
		return X;
	}

	@Override
	public float getY() {
		return Y;
	}

	@Override
	public float getZ() {
		return Z;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Position) {
			Position op = ((Position) obj);
			return op.X == this.X && op.Y == this.Y && op.Z == op.Z;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return hashCode;
	}

	/**
	 * Factory methods
	 * 
	 **/

	/**
	 * Origin
	 * 
	 * @return
	 */
	public final static Position origin() {
		return new Position(0, 0, 0);
	}

	public final static Position shift(Position p, float amountX, float amountY, float amountZ) {
		return new Position(p.getX() + amountX, p.getY() + amountY, p.getZ() + amountZ);
	}

	public final static Position shiftXY(Position p, float amountX, float amountY) {
		return new Position(p.getX() + amountX, p.getY() + amountY, p.getZ());
	}

	public static Coordinates create(float x2, float y2, float z2) {
		return new Position(x2, y2, z2);
	}

	public String toString() {
		return new StringBuilder().append("XYZ[").append(getX()).append(',').append(getY()).append(',').append(getZ())
				.append(']').toString();
	}
}
