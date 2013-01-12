package bengo.instruction_fetcher;
import java.util.ArrayList;

import bengo.Instruction;


public class InstructionFetcher
{
	int levels;
	InstructionCache[] caches;
	ArrayList<Instruction> instructs;
	int memTime;
	
	public InstructionFetcher(int levels, int[] assoc, int[] lines,
			int [] penalties, int[] instructionsPerLine,
			ArrayList<Instruction> instructs, int memTime)
	{
		this.instructs = instructs;
		this.memTime = memTime;
		this.levels = levels;
		this.caches = new InstructionCache[levels];
		for(int i = 0; i < caches.length; i++)
		{
			caches[i] = new InstructionCache(assoc[i],lines[i],
					instructionsPerLine[i],instructs,penalties[i]);
		}
	}
	
	public Object[] fetchInstruction(int address)
	{
		Instruction instr = null;
		int cycles = 0;
		for(int i = 0; i < caches.length; i++)
		{
			cycles += caches[i].hitTime;
			instr = caches[i].read(address);
			if(instr != null)
				break;
		}
		if(instr == null)
		{
			instr = this.instructs.get(address);
			cycles += memTime;
		}
		return new Object[] {new Integer(cycles),instr};
	}
	
}