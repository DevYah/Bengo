package bengo.instruction_fetcher;

import java.util.ArrayList;

import bengo.Instruction;

public class InstructionCache {
	
	int levels;
	
	InstructionCacheLine[][] cache;
	int[] tags;
	int cycles;
	int assoc;
	int lines;
	int instructionsPerLine;
	ArrayList<Instruction> instructs;
	int hitTime;
	public InstructionCache(int assoc, int lines, int instructionsPerLine, ArrayList<Instruction> instructs, int hitTime)
	{
		this.hitTime = hitTime;
		this.instructs = instructs; 
		this.assoc = assoc;
		this.lines = lines;
		this.instructionsPerLine = instructionsPerLine;
		this.cache = new InstructionCacheLine[lines/assoc][assoc];
		for(int i = 0; i < cache.length;i++)
		{
			for(int j = 0; j < cache[i].length; j++)
			{
				this.cache[i][j] = new InstructionCacheLine(this.instructionsPerLine);
			}
		}
		
	}
	
	public Instruction read(int address)
	{
		System.out.println("reading " + address);
		int[] TIO = map(address);
		Instruction instr;
		for(int i = 0; i < cache[TIO[1]].length; i++)
		{
			if((instr = cache[TIO[1]][i].fetchInstruction(address)) != null)
				return instr;
		}
		
		// Cache miss , update cache.
		
		this.updateCache(address);
		return null;
		
	}
	
	public void updateCache(int address)
	{
		// search for an empty cache line
		int[] TIO = map(address);
		int[] tags = new int[this.instructionsPerLine];
		Instruction[] newBlock = new Instruction[this.instructionsPerLine];
		address = this.getAddressBase(address);
		for(int i = 0; i < newBlock.length; i++)
		{
			newBlock[i] = this.instructs.get(address);
			tags[i] = map(address)[0];
			address++;
		}
		
		boolean written = false;
		for(int i = 0; i < cache[TIO[1]].length; i++)
		{
			//System.out.println("LOOKING FOR CACHELINE" + i);
			if(cache[TIO[1]][i].isEmpty())
			{
				
				// found empty space, update cache
				System.out.println("found empty space for address" + (address - 2));
				written = true;
				cache[TIO[1]][i].writeInstruction(newBlock, tags);
				break;
			}
		}
		if(!written)
		{
			// remove random block.
			System.out.println("REMOVING RANDOM BLOCK TO MAKE SPACE FOR" + address);
			cache[TIO[1]][0].writeInstruction(newBlock, tags);
		}
	}
	
	private static int makeNOnes(int n) {
		int res = 0;
		for (int i = 0; i < n; i++) {
			res += (1 << i);
		}
		//System.out.println("MAKING " + n + " ONES");
		return res;
	}
	

	
	private  int getAddressBase(int address)
	{
		return address - (address % this.instructionsPerLine);
	}
	
	private static int log2(int n) {
		return (int) Math.ceil((Math.log10(n)/Math.log10(2)));
	}
	
	
	
	// returns array = {tag, index, offset}
	public int[] map(int address) {
		// TESTED AND  WORKING (all sheet examples were tests)
		
		int offset = address & makeNOnes(log2(instructionsPerLine));
		int index 	= address &
				(makeNOnes(log2(lines/assoc)) << log2(instructionsPerLine));
		index = index >> log2(instructionsPerLine);
		
		int tag = address >> ((log2(instructionsPerLine) + log2(lines/assoc)));
		
	//	System.out.println("tag = " + tag + " index =  " + index + " offset = " + offset);
		
		return new int[] {tag, index, offset};
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
		
		
		InstructionCache ic = new InstructionCache(2,8,2,instr,0);
	//	System.out.println(ic.read(2));
	//	System.out.println(ic.read(3));
	//	System.out.println(ic.read(2));
	//	System.out.println(ic.read(4));
	//	System.out.println(ic.read(7));
//		System.out.println(ic.read(5));
		/*System.out.println(ic.read(0));
		System.out.println(ic.read(0));
		System.out.println(ic.read(8));
		System.out.println(ic.read(9));
		System.out.println(ic.read(0));
		System.out.println(ic.read(24));
		System.out.println(ic.read(24));
		System.out.println(ic.read(0));
		System.out.println(ic.read(8));*/
	//	System.out.println(ic.map(0));
	//	System.out.println(ic.map(8));
	//	System.out.println(ic.map(24));
	//	
		System.out.println(ic.read(9));
		System.out.println(ic.read(8));
		System.out.println(ic.read(9));
		//System.out.println(ic.getAddressBase(11));
	}
	
	
	
	

}
