package bengo;

import java.util.ArrayList;

public class Instruction {
	
	int fetchTime, issueTime, excuteTime, writeTime, commitTime; 
	String[] fields;
	String status = "fetch";
    ReservationStation station;
    ArrayList<String> loadInstructions;
    ArrayList<String> addInstructions;
    ArrayList<String> mulInstructions;
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
		this.loadInstructions = new ArrayList<String>();
		this.fillLoad();
		this.addInstructions = new ArrayList<String>();
		this.fillAdd();
		this.mulInstructions = new ArrayList<String>();
		this.fillMul();
	}
	public void fillLoad()
	{
		loadInstructions.add("LW");
		loadInstructions.add("SW");
	}
	public void fillAdd()
	{
		this.addInstructions.add("JMP");
		this.addInstructions.add("BEQ");
		this.addInstructions.add("JALR");
		this.addInstructions.add("RET");
		this.addInstructions.add("ADD");
		this.addInstructions.add("ADDI");
		this.addInstructions.add("NAND");

		
	}
	public String getType()
	{
		if(loadInstructions.contains(fields[0]))
			return "LOAD";
		if(addInstructions.contains(fields[0]))
			return "ADD";
		if(mulInstructions.contains(fields[0]))
			return "MULT";
		return "INVALID COMMAND";
	}
	public void fillMul()
// <<<<<<< master (Nada)
	{
		this.addInstructions.add("MUL");
		this.addInstructions.add("DIV");
	}
	public void setStatus(String status)
	{
		this.status = status;
	}
	public String getStatus()
	{
		return this.status;
	}
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
