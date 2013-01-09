
public class Instruction {
	
	int fetchTime, issueTime, excuteTime, writeTime, commitTime; 
	String[] fields;
	
	public Instruction(int binary) {
		// parser to get the type, rd, rs, rm
	}
	
	public Instruction(String type, String rd, String rs, String rm) {
		fields = new String[]{type, rd, rs, rm};
		fetchTime 	= -1;
		issueTime 	= -1;
		excuteTime	= -1;
		writeTime  	= -1;
		commitTime 	= -1;
	}
}