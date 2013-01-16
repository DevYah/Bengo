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
    int address;
//>>>>>>> memory
	
	public Instruction(int binary) {
		// parser to get the type, rd, rs, rm
	}
	
	public Instruction(String []instructionStr, int address) {
		//System.out.println(instructionStr.length);
		fields = new String[]{instructionStr.length >= 0 ? instructionStr[0] : null ,
				instructionStr.length > 1 ? instructionStr[1] : null,
				instructionStr.length > 2 ? instructionStr[2] : null,
				instructionStr.length > 3 ? instructionStr[3] : null};
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
		this.address = address;
	}
	public void setFetchTime(int fetchTime)
	{
		this.fetchTime = fetchTime;
	}
	public void setCommit(int time)
	{
		this.commitTime = time;
	}
	public void setIssueTime(int issueTime)
	{
		this.issueTime = issueTime;
	}
	public int getAddress()
	{
		return this.address;
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
	public boolean isBranch()
	{
		if(this.fields[0].equalsIgnoreCase("JMP") || this.fields[0].equalsIgnoreCase("BEQ") || this.fields[0].equalsIgnoreCase("RET") || this.fields[0].equalsIgnoreCase("JALR") )
			return true;
		return false;
	}
	public void fillMul()
// <<<<<<< master (Nada)
	{
		this.mulInstructions.add("MUL");
		this.mulInstructions.add("DIV");
	}
	public void setStatus(String status)
	{
		this.status = status;
	}
	public String getStatus()
	{
		return this.status;
	}
	public void setExecuteTime(int exec)
	{
		this.excuteTime = exec;
	}
	public void setWrittenTime(int time)
	{
		
		this.writeTime = time;
	}
	public String toString()
	{
		return fields[0] + " " + fields[1] + " " + fields[2] + " " + fields[3] + " WAS FETCHED AT " + this.fetchTime + " ISSUED AT " + this.issueTime + " Executed AT " + this.excuteTime 
				+ " WRITTEN AT " + this.writeTime + " COMMITTED AT " + this.commitTime;
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
