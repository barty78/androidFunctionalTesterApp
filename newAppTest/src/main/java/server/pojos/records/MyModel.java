package server.pojos.records;

import com.activeandroid.Model;

class MyModel extends Model {
	private int fufs;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + fufs;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (getClass() != obj.getClass())
			return false;
		MyModel other = (MyModel) obj;
		if (fufs != other.fufs)
			return false;
		return true;
	}
	
}
