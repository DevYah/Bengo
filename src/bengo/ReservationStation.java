package bengo;

import bengo.data_fetcher.DataAction;


public class ReservationStation {
	
	Instruction instruction;
	String name;
	boolean busy = false;
	String operation;
	String vj ="";
	String vk ="";
	String qj ="";
	String qk ="";
	short answer;
	String status;
	int ROBEntry;
	int remainingTime;
	boolean execReady = false;
	boolean isWritten = false;
	boolean raw = false;
	DataAction dataAction;
	
	public ReservationStation(String name)
	{
		this.name = name;
	}
	public void update()
	{
		if(this.dataAction != null)
			this.dataAction.update();
	}
	public void setDataAction(DataAction dataAction)
	{
		this.dataAction = dataAction;
		System.err.println(this.dataAction.address);
	}
	public void setWritten(boolean written)
	{
		this.isWritten = written;
	}
	public void setROBIndex(int ROBEntry)
	{
		this.ROBEntry = ROBEntry;
	}
	public void setAnswer(short answer)
	{
		this.answer = answer;
	}
	public boolean isCompatible(String instructionType)
	{
		return this.name.contains(instructionType);
	}
	
	public void setOperation(String operation)
	{
		this.operation = operation;
	}
	public void setVj(String vj)
	{
		this.vj = vj;
	}
	public void setVk(String vk)
	{
		this.vk = vk;
	}
	public void setQj(String qj)
	{
		this.qj = qj;
		this.raw = true;
	}
	public void setQk(String qk)
	{
		this.qk = qk;
		this.raw = true;
	}
	public short getAnswer()
	{
		return answer;
	}
	public void use()
	{
		this.busy = true;
	}
	public void finish()
	{
		this.busy = false;
	}
	public void assignInstruction(Instruction instruction, int delay)
	{
		this.instruction = instruction;
		this.remainingTime = delay;
		this.use();
		
	}
	public boolean isFinished()
	{		
		if(raw)
			remainingTime++;
		if(this.dataAction != null)
		{
			if(this.dataAction.isReady())
			{
				this.execReady = true;
				this.setAnswer(this.dataAction.getData());
				return true;
			}
			
		}
		else
		{
			remainingTime--;
			if(remainingTime == 0)
			{
				//this.busy = false;
				execReady = true;
				return true;
			}
		}
		raw = false;
		
		return false;
	}
	public void reset()
	{
		// called when reservation station is done
		vj = qj = qk = vk = "";
		busy = execReady = isWritten = false;
		remainingTime = ROBEntry = 0;
		answer = 0;
		this.instruction = null;
		this.dataAction = null;
		
	}

	public String getVk() {
		return vk;
	}
	
	public String getVj() {
		return vj;
	}
	public boolean isBusy() {
		return this.busy;
	}
	public String toString()
	{
		if(this.instruction != null)
		return "Instruction : " + this.instruction.toString() + " QJ " + this.qj + " QK " + this.qk
				+ " VJ " + this.vj + " VK " + this.vk + " DEST " + this.ROBEntry + " Operation " + this.operation
				+ " name " + this.name + " BUSY " + this.busy + " ANSWER = " + this.answer; 
		return  "Instruction : null " + " QJ " + this.qj + " QK " + this.qk
				+ " VJ " + this.vj + " VK " + this.vk + " DEST " + this.ROBEntry + " Operation " + this.operation
				+ " name " + this.name + " BUSY " + this.busy; 
	}
}
