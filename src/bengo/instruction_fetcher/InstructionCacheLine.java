package bengo.instruction_fetcher;

import bengo.Instruction;

class InstructionCacheLine {
	
	int instructionsPerLine;
	Instruction[] instructions;
	int[] tags;
	
	public InstructionCacheLine(int instructionsPerLine)
	{
		this.instructionsPerLine = instructionsPerLine;
		this.instructions = new Instruction[instructionsPerLine];
		this.tags = new int[instructionsPerLine];
	}
	
	public boolean isEmpty()
	{
		return instructions[0] == null;
	}
	
	public Instruction fetchInstruction(int address)
	{
		for(int i = 0; i < instructions.length; i++)	
			if(instructions[i] != null)
				if(instructions[i].getAddress() == address)
					return instructions[i];
		return null;
	}
	public void writeInstruction(Instruction[] instrs, int[]tags)
	{
		this.instructions = instrs;
		this.tags = tags;
	}
	

}
