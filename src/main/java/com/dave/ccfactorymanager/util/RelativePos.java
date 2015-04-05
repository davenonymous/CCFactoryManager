package com.dave.ccfactorymanager.util;

public class RelativePos {
	public int x, y, z;

	public RelativePos(String xyz) {
		this(0, 0, 0);
		String parts[] = xyz.split(",");
		this.x = Integer.parseInt(parts[0]);
		this.y = Integer.parseInt(parts[1]);
		this.z = Integer.parseInt(parts[2]);
	}

	public RelativePos(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	public String toString() {
		return x + "," + y + "," + z;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof RelativePos) {
			RelativePos other = (RelativePos) obj;
			return (other.x == this.x && other.y == this.y && other.z == this.z);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return (this.y + this.z * 31) * 31 + this.x;
	}
}
