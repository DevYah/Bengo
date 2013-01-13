package bengo;

public class ROBEntry {
	 int val;
	 String dest, type;
	 boolean ready;
	 
	 public ROBEntry(String dest, String type)
	 {
		 this.dest = dest;
		 this.type = type;
	 }
}
