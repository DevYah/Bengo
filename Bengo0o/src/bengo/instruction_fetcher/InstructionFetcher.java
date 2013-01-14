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
	public InstructionCache getCache(int i)
	{
		return this.caches[i];
	}
	
	public Object[] fetchInstruction(int address)
	{
		Instruction instr = null;
		int cycles = 0;
		for(int i = 0; i < caches.length; i++)
		{
			//System.out.println("CHECKING CACHE + " + i);
			cycles += caches[i].hitTime;
			instr = caches[i].read(address);
			if(instr != null)
			{
				//System.out.println("INSTRUCTION FOUND IN CACHE " + i);
				break;
			}
			
		}
		if(instr == null)
		{
			instr = this.instructs.get(address);
			cycles += memTime;
		}
		//System.out.println("INSTRUCTION = " + instr);
		//System.out.println("used cycles" + cycles);
		return new Object[] {new Integer(cycles),instr};
	}
	public double[] getHitRatio()
	{
		double[] ret = new double[this.levels];
		for(int i = 0; i < this.caches.length; i++)
			ret[i] = this.caches[i].getHitRatio();
		return ret;
	}
	
	public static void main(String[]args)
	{
		Instruction i1 = new Instruction(new String[]{"ADD1","R0","R1","R2"},0);
		Instruction i2 = new Instruction(new String[]{"SUB","R0","R1","R2"},1);
		Instruction i3 = new Instruction(new String[]{"ADD3","R0","R1","R2"},2);
		Instruction i4 = new Instruction(new String[]{"ADD4","R0","R1","R2"},3);
		Instruction i5 = new Instruction(new String[]{"ADD5","R0","R1","R2"},4);
		Instruction i6 = new Instruction(new String[]{"ADD6","R0","R1","R2"},5);
		Instruction i7 = new Instruction(new String[]{"ADD7","R0","R1","R2"},6);
		Instruction i8 = new Instruction(new String[]{"ADD8","R0","R1","R2"},7);
		Instruction i9 = new Instruction(new String[]{"ADD9","R0","R1","R2"},8);
		Instruction i10 = new Instruction(new String[]{"ADD10","R0","R1","R2"},9);
		Instruction i11 = new Instruction(new String[]{"ADD11","R0","R1","R2"},10);
		Instruction i12 = new Instruction(new String[]{"ADD12","R0","R1","R2"},11);
		Instruction i13 = new Instruction(new String[]{"ADD13","R0","R1","R2"},12);
		Instruction i14 = new Instruction(new String[]{"ADD14","R0","R1","R2"},13);
		Instruction i15 = new Instruction(new String[]{"ADD15","R0","R1","R2"},14);
		Instruction i16 = new Instruction(new String[]{"ADD16","R0","R1","R2"},15);
		Instruction i17 = new Instruction(new String[]{"ADD17","R0","R1","R2"},16);
		Instruction i18 = new Instruction(new String[]{"ADD18","R0","R1","R2"},17);
		Instruction i19 = new Instruction(new String[]{"ADD19","R0","R1","R2"},18);
		Instruction i20 = new Instruction(new String[]{"ADD20","R0","R1","R2"},19);
		Instruction i21 = new Instruction(new String[]{"ADD21","R0","R1","R2"},20);
		Instruction i22 = new Instruction(new String[]{"ADD22","R0","R1","R2"},21);
		Instruction i23 = new Instruction(new String[]{"ADD23","R0","R1","R2"},22);
		Instruction i24 = new Instruction(new String[]{"ADD24","R0","R1","R2"},23);
		Instruction i25 = new Instruction(new String[]{"ADD25","R0","R1","R2"},24);
		ArrayList<Instruction> instr = new ArrayList<Instruction>();
		instr.add(i1);
		instr.add(i2);
		instr.add(i3);
		instr.add(i4);
		instr.add(i5);
		instr.add(i6);
		instr.add(i7);
		instr.add(i8);
		instr.add(i9);
		instr.add(i10);
		instr.add(i11);
		instr.add(i12);
		instr.add(i13);
		instr.add(i14);
		instr.add(i15);
		instr.add(i16);
		instr.add(i17);
		instr.add(i18);
		instr.add(i19);
		instr.add(i20);
		instr.add(i21);
		instr.add(i22);
		instr.add(i23);
		instr.add(i24);
		instr.add(i25);
		instr.add(i25);

		InstructionFetcher iFetcher = new InstructionFetcher(3,new int[] {2,2,4},new int[] {12,16,24},new int[]{4,10,20},new int[]{2,4,8},instr,2);
		iFetcher.fetchInstruction(0);
		iFetcher.fetchInstruction(2);
		iFetcher.fetchInstruction(8);
		iFetcher.fetchInstruction(15);
		
	}
	
}