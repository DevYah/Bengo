package bengo.data_fetcher;

import bengo.Bengo;

class CacheBlock {

	int blockSize; // in words
	int tag;
	private short[] data;
	int lastUsed;
	boolean empty;
	boolean dirty; // for write back

	public CacheBlock(int blockSize) {
		tag  = -1;
		this.blockSize = blockSize;
		empty = true;
	}

	public boolean isEmpty()
	{
		return this.empty;
	}

	public short[] getData(){
		lastUsed = Bengo.CURRENT_CYCLE;
		dirty = false;
		return data;
	}
	public void write(short[] newData, int tag, boolean dirtyWrite){
		try {
	//		this.data = makeCompatible(newData);
			dirty = dirtyWrite;
			if (data == null) // first write is not dirty
				dirty = false;
			
			if (newData.length != this.blockSize)
				throw new Exception("Incompatible blockSize");
			else
				this.data = newData;

			this.tag = tag;
			lastUsed = Bengo.CURRENT_CYCLE;
			empty = false;
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}

	}

	public String toString() {
		String s = "<";
		if (!empty) {
			s += "tag:" +  tag + "  " ;
			for (int d : data)
				s += d + ",";
		}else {
			s += "empty";
		}
		s += dirty? "D" : "N";
		s += ">";
		return s;

	}
}
