
public class Instruction {
	
	int fetchTime, issueTime, excuteTime, writeTime, commitTime; 
	String[] fields;
	String status = "fetch";
    ReservationStation station;
//>>>>>>> memory
	
	public Instruction(int binary) {
		// parser to get the type, rd, rs, rm
	}
	
	public Instruction(String []instructionStr) {
		fields = new String[]{instructionStr.length >= 0 ? instructionStr[0] : null ,
				instructionStr.length >= 1 ? instructionStr[1] : null,
				instructionStr.length >= 2 ? instructionStr[2] : null,
				instructionStr.length >= 3 ? instructionStr[3] : null};
		fetchTime 	= -1;
		issueTime 	= -1;
		excuteTime	= -1;
		writeTime  	= -1;
		commitTime 	= -1;
	}
// <<<<<<< master (Nada)
	public String toString()
	{
		return fields[0] + " " + fields[1] + " " + fields[2] + " " + fields[3];
	}
// =======

//    public void execute() {
//        if (fields[0].equals("LW")) {
//            station.setA(station.getVj() + station.getA());
//            int val = Bengo.memory.read(station.getA());
//            
//        }
//    }
// >>>>>>> memory
}
