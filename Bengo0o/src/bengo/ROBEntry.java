package bengo;

public class ROBEntry {
	 int val;
	 String dest, type;
	 boolean ready;
	 Instruction instr;
	 
	 public ROBEntry(String dest, String type,Instruction instr)
	 {
		 this.dest = dest;
		 this.type = type;
		 this.instr = instr;
	 }
}
