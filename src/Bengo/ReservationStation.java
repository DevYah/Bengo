package Bengo;

public class ReservationStation {
	
	Instruction instruction;
	String name;
	boolean busy = false;
	String operation;
	String vj;
	String vk;
	String qj;
	String qk;
	String answer;
	String status;
	
	public ReservationStation(String name)
	{
		this.name = name;
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
	}
	public void setQk(String qk)
	{
		this.qk = qk;
	}
	public void use()
	{
		this.busy = true;
	}
	public void finish()
	{
		this.busy = false;
	}
	public void assignInstruction(Instruction instruction)
	{
		this.instruction = instruction;
	}

	public String getVk() {
		return vk;
	}
	
	public String getVj() {
		return vj;
	}
}
