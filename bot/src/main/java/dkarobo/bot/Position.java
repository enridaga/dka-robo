package dkarobo.bot;

import org.apache.commons.lang3.builder.HashCodeBuilder;

public class Position implements Coordinates {
	private int X, Y, Z;
	private int hashCode;

	private Position(int X, int Y, int Z) {
		this.X = X;
		this.Y = Y;
		this.Z = Z;
		hashCode = new HashCodeBuilder(7, 37).append(X).append(Y).append(Z).append(Position.class).toHashCode();
	}

	@Override
	public int getX() {
		return X;
	}

	@Override
	public int getY() {
		return Y;
	}

	@Override
	public int getZ() {
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

	public final static Position shift(Position p, int amountX, int amountY, int amountZ) {
		return new Position(p.getX() + amountX, p.getY() + amountY, p.getZ() + amountZ);
	}

	public final static Position shiftXY(Position p, int amountX, int amountY) {
		return new Position(p.getX() + amountX, p.getY() + amountY, p.getZ());
	}

	public static Coordinates create(int x2, int y2, int z2) {
		return new Position(x2, y2, z2);
	}

}
