package bengo;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;


import bengo.instruction_fetcher.InstructionFetcher;


public class Bengo {
	
	public static int CURRENT_CYCLE;
	public int PC;
	public int fetchPC; //pointer to next instruction to be fetched
	public int issuePC; //pointer to next instruction to be issued
	public int execPC; // pointer to next instruction to be executed
	
	int loadStations = 2;
	int loadTime = 4;
	int addStations = 2;
	int addTime = 4;
	int multStations = 1;
	int multTime = 3;
	int divideTime = 13;
	int remainingFetchDelay = 0;
	Instruction lastFetched;
	Instruction lastIssued;
	CircularQueue<ROBEntry> ROB;
	int ROBSize = 4;
	RegisterStatus registerStatus = new RegisterStatus();
	ArrayList<ReservationStation> writeBack;
	CDB dataBus;
	Instruction lastInstr;
	boolean done = false;
	RegisterFile registerFile;
	

	// 	ROB, Reservation Station, Instruction, Cache & Memory
	ArrayList<Instruction> instructs;
	ReservationStation[] reservationStations;
	ArrayList<Instruction> fetchedInstructions;
	ArrayList<Instruction> issuedInstructions;
	InstructionFetcher instructionFetcher;
	
	
	public Bengo()
	{
		this.writeBack = new ArrayList<ReservationStation>();
		this.dataBus = new CDB();
		 PC = 0;
		 fetchPC = 0;
		 issuePC = 0;
		 execPC = -1;
		 this.ROB = new CircularQueue<ROBEntry>(this.ROBSize);
		 this.registerFile = new RegisterFile();
		 this.fetchedInstructions = new ArrayList<Instruction>();
		 this.issuedInstructions = new ArrayList<Instruction>();
		 this.reservationStations = new ReservationStation[loadStations + addStations + multStations];
		 for(int i = 0; i < loadStations; i++)
		 {
			 reservationStations[i] = new ReservationStation("LOAD "+ (i + 1));
		 }
			reservationStations[0].isBusy();
		 for(int i = loadStations; i < addStations + loadStations; i++)
		 {
			 reservationStations[i] = new ReservationStation("ADD "+ (i + 1));
		 }
		 for(int i = loadStations + addStations; i < multStations + loadStations + addStations; i++)
		 {
			 reservationStations[i] = new ReservationStation("MULT "+ (i + 1));
		 }
		 this.instructs = new ArrayList<Instruction>();
		// read the instructions
		 try
		 {
			 BufferedReader instructionsReader = new BufferedReader(new FileReader("program.txt")); 
			 String instructionStr;
			 int instructionAddress = 0;
				try
				{
					while((instructionStr = instructionsReader.readLine()) != null)
					{
						Instruction instructionObj = new Instruction(instructionStr.split(" "), instructionAddress);
						this.instructs.add(instructionObj);
						instructionAddress++;
					}
				}
				catch(IOException e)
				{
					e.printStackTrace();
				}
		 }
		 catch(FileNotFoundException e)
		 {
			 e.printStackTrace();
		 }
		 this.lastInstr = this.instructs.get(this.instructs.size() - 1);
		 int levels = 3;
		 int assoc[] = new int[]{2,4,4};
		 int lines[] = new int[]{12,16,20};
		 int penalties[] = new int[]{2,4,6};
		 int instructionsPerLine[] = new int[]{2,4,8};
		 int memTime = 0;
		 this.instructionFetcher = new InstructionFetcher(levels, assoc, lines, penalties, instructionsPerLine,instructs,memTime);
		 this.remainingFetchDelay = ((Integer) this.instructionFetcher.fetchInstruction(fetchPC)[0]);
		 this.lastFetched = (Instruction) this.instructionFetcher.fetchInstruction(fetchPC)[1];
		 fetchPC++;
		 this.run();
		
	}
	public void clearWritten()
	{
		for(int i = 0; i < this.reservationStations.length;i++)
			reservationStations[i].setWritten(false);
	}
	public void run()
	{
		this.printReservationStations();
		if(lastFetched.fetchTime == -1 && fetchPC != 0)
			lastFetched.setFetchTime(CURRENT_CYCLE);
		this.commit();
		this.writeBack();
		this.execute();
		this.issue();
		if(this.fetchedInstructions.size() < this.instructs.size())
			this.fetch();
		CURRENT_CYCLE++;
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		try {
		
			br.readLine();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		this.clearWritten();
		this.printFetchTime();
		if(!done)
		{
			System.err.println("CAN RUN");
			run();
		}
	}
	public void fetch()
	{
		if(this.remainingFetchDelay == 0)
		{
			// can fetch instruction
			if(lastFetched != null)
			{
				lastFetched.setFetchTime(CURRENT_CYCLE);
				this.fetchedInstructions.add(lastFetched);
			}
	
			if(fetchPC < instructs.size())
			{
				 this.remainingFetchDelay = (int) this.instructionFetcher.fetchInstruction(fetchPC)[0];
				 this.lastFetched = (Instruction) this.instructionFetcher.fetchInstruction(fetchPC)[1];
				 System.err.print("FETCH DELAY SET TO " + this.remainingFetchDelay);
				 System.err.println(lastFetched);
			}
			 fetchPC++;
		}
		else
		{
			//System.out.println("CANT FETCH");
			this.remainingFetchDelay--;
		}
	}
	public void issue()
	{
		if(lastIssued != null)
			this.issuedInstructions.add(lastIssued);
		System.out.println("At cycle " + CURRENT_CYCLE + " and trying to issue " + issuePC);
		System.out.println("FETCHED INSTRUCTIONS SIZE = " + this.fetchedInstructions.size());
		if(this.fetchedInstructions.size() > issuePC && issuePC >= 0)
		{
			if(this.ROB.hasSpace())
			{
				System.out.println("ROB HAS SPACE");
				int instrDelay = 0;
				for(int i = 0; i < reservationStations.length; i++)
				{
					if(this.fetchedInstructions.get(issuePC).getType() == "LOAD")
					{
						// LOAD INSTRUCTION ISSUE LOGIC HERE.
						instrDelay = this.loadTime;
						
					}
					else
					{
					if((!reservationStations[i].isBusy()) && (!this.reservationStations[i].isWritten) && (reservationStations[i].isCompatible(this.fetchedInstructions.get(issuePC).getType())))
					{
						if(this.fetchedInstructions.get(issuePC).getType() == "ADD")
						{
							instrDelay = this.addTime;
						}
						if(this.fetchedInstructions.get(issuePC).fields[0].equalsIgnoreCase("MUL"))
						{
							instrDelay = this.multTime;
						}
						if(this.fetchedInstructions.get(issuePC).fields[0].equalsIgnoreCase("DIV"))
						{
							instrDelay = this.divideTime;
						}
						Instruction instr = this.fetchedInstructions.get(issuePC);
						lastIssued = instr;
						instr.setIssueTime(CURRENT_CYCLE);
						reservationStations[i].assignInstruction(this.fetchedInstructions.get(issuePC), instrDelay);
						// create ROBEntry
						ROBEntry rob = new ROBEntry(instr.fields[1],instr.fields[0],this.fetchedInstructions.get(issuePC));
						int index = this.ROB.enqueue(rob);
						reservationStations[i].setROBIndex(index);
						reservationStations[i].setOperation(instr.getType());
						if(!instr.isBranch())
						{
							if(instr.fields[2] != null)
							{
								if(registerStatus.getRegisterStation(instr.fields[2]) == "")
								{
									// no RAW
									reservationStations[i].setVj(instr.fields[2]);
								}
								else
								{
									reservationStations[i].setQj(registerStatus.getRegisterStation(instr.fields[2]));
								}
							}
							if(instr.fields[3] != null)
							{
								if(registerStatus.getRegisterStation(instr.fields[3]) == "")
								{
									// no RAW
									reservationStations[i].setVk(instr.fields[3]);
								}
								else
								{
									reservationStations[i].setQk(registerStatus.getRegisterStation(instr.fields[3]));
								}
							}
							
							if(registerStatus.getRegisterStation(instr.fields[1]) == "")
								//NO WAW
								registerStatus.assignRegister(index + "", instr.fields[1]);
						}
						else
						{
							if(instr.fields[0].equalsIgnoreCase("JMP") || instr.fields[0].equalsIgnoreCase("RET"))
							{
								System.err.println("------------- found dep for JMP=============");
								if(registerStatus.getRegisterStation(instr.fields[1]) == "")
								{
									// no RAW
									reservationStations[i].setVj(instr.fields[1]);
								}
								else
								{
									reservationStations[i].setQj(registerStatus.getRegisterStation(instr.fields[1]));
								}
							}
							if(instr.fields[0].equalsIgnoreCase("BEQ"))
							{
								System.err.println("------------- found dep for BEQ=============");
								if(registerStatus.getRegisterStation(instr.fields[1]) == "")
								{
									// no RAW
									reservationStations[i].setVj(instr.fields[1]);
								}
								else
								{
									reservationStations[i].setQj(registerStatus.getRegisterStation(instr.fields[1]));
								}
								if(registerStatus.getRegisterStation(instr.fields[2]) == "")
								{
									// no RAW
									reservationStations[i].setVk(instr.fields[2]);
								}
								else
								{
									reservationStations[i].setQk(registerStatus.getRegisterStation(instr.fields[2]));
								}
							}
							if(instr.fields[0].equalsIgnoreCase("JALR"))
							{
								System.err.println("------------- found dep for JALR=============");
								if(registerStatus.getRegisterStation(instr.fields[2]) == "")
								{
									// no RAW
									reservationStations[i].setVj(instr.fields[2]);
								}
								else
								{
									reservationStations[i].setQj(registerStatus.getRegisterStation(instr.fields[2]));
								}
								if(registerStatus.getRegisterStation(instr.fields[1]) == "")
									//NO WAW
									registerStatus.assignRegister(index + "", instr.fields[1]);
							}
						}
						issuePC++;
						break;
					}
					}
				}
			}
		
		}
	}
	public void execute()
	{
		//loop on all reservation Stations
		for(int i = 0; i < this.reservationStations.length; i++)
		{
			if((reservationStations[i].isBusy()) && ((reservationStations[i].qj == "") || (reservationStations[i].qj == null)) && ((reservationStations[i].qk == "") || (reservationStations[i].qk == null) ))
			{
				// Step a cycle
				if(reservationStations[i].isFinished())
				{
					reservationStations[i].instruction.setExecuteTime(CURRENT_CYCLE);
					// Instruction done executing.
					// compute the actual result
					int answer = 0;
					if(reservationStations[i].instruction.fields[0].equalsIgnoreCase("add"))
						answer = dataBus.getRegisterValue(reservationStations[i].instruction.fields[2]) + 
								 dataBus.getRegisterValue(reservationStations[i].instruction.fields[3]);
					if(reservationStations[i].instruction.fields[0].equalsIgnoreCase("addi"))
						answer = dataBus.getRegisterValue(reservationStations[i].instruction.fields[2]) + 
								 Integer.parseInt(reservationStations[i].instruction.fields[3]);
					if(reservationStations[i].instruction.fields[0].equalsIgnoreCase("mul"))
						answer = dataBus.getRegisterValue(reservationStations[i].instruction.fields[2]) * 
								 dataBus.getRegisterValue(reservationStations[i].instruction.fields[3]);
					if(reservationStations[i].instruction.fields[0].equalsIgnoreCase("div"))
						answer = dataBus.getRegisterValue(reservationStations[i].instruction.fields[2]) / 
								 dataBus.getRegisterValue(reservationStations[i].instruction.fields[3]);
					if(reservationStations[i].instruction.fields[0].equalsIgnoreCase("nand"))
						answer = ~(dataBus.getRegisterValue(reservationStations[i].instruction.fields[2]) & 
								 dataBus.getRegisterValue(reservationStations[i].instruction.fields[3]));
					// for branch instruction address will be -effective address if not taken
					// and +effective address if taken
					if(reservationStations[i].instruction.fields[0].equalsIgnoreCase("beq"))
					{
						if(this.dataBus.getRegisterValue(reservationStations[i].instruction.fields[1]) == this.dataBus.getRegisterValue(reservationStations[i].instruction.fields[2]))
						{
							answer = Integer.parseInt(reservationStations[i].instruction.fields[3]);
						}
						else
						{
							answer = -1;
						}
					}
					if(reservationStations[i].instruction.fields[0].equalsIgnoreCase("jmp"))
					{
						answer = Integer.parseInt(reservationStations[i].instruction.fields[2]) + this.getPC(this.reservationStations[i].instruction)
								+ 1 + 
							this.dataBus.getRegisterValue(reservationStations[i].instruction.fields[1]) ;
					}
					if(reservationStations[i].instruction.fields[0].equalsIgnoreCase("ret"))
					{
						answer = this.dataBus.getRegisterValue(reservationStations[i].instruction.fields[1]) ;
					}
					if(reservationStations[i].instruction.fields[0].equalsIgnoreCase("jalr"))
					{
						answer = this.dataBus.getRegisterValue(reservationStations[i].instruction.fields[2]) ;
					}
					reservationStations[i].setAnswer(answer);
					this.ROB.get(reservationStations[i].ROBEntry).val = reservationStations[i].answer;
					this.writeBack.add(reservationStations[i]);
					
				}
			}
		}
	}
	public int getPC(Instruction instruction)
	{
		for(int i = 0; i < this.instructs.size(); i++)
		{
			if(this.instructs.get(i) == instruction)
				return i;
		}
		return -1;
	}
	public void writeBack()
	{
		while(!this.writeBack.isEmpty())
		{
			System.err.println("WRITING BACK");
			String rd = this.writeBack.get(0).instruction.fields[1];
			this.writeBack.get(0).setWritten(true);
			this.writeBack.get(0).instruction.setWrittenTime(CURRENT_CYCLE);
			if(!this.writeBack.get(0).instruction.isBranch())
				this.dataBus.writeRegister(this.writeBack.get(0).instruction.fields[1], this.writeBack.get(0).answer);
			if(this.writeBack.get(0).instruction.fields[0].equalsIgnoreCase("JALR"))
				this.dataBus.writeRegister(this.writeBack.get(0).instruction.fields[1], this.writeBack.get(0).answer);
			this.ROB.get(this.writeBack.get(0).ROBEntry).ready = true;
			this.writeBack.get(0).reset();
			this.writeBack.remove(0);
			this.registerStatus.assignRegister("",rd);
			for(int i = 0; i < this.reservationStations.length; i++)
			{
					try
					{
						if(this.ROB.get(Integer.parseInt(this.reservationStations[i].qj)).dest == rd)
						{
							this.reservationStations[i].setQj("");
							this.reservationStations[i].setVj(rd);
						}	

						if(this.ROB.get(Integer.parseInt(this.reservationStations[i].qk)).dest == rd)
						{
							this.reservationStations[i].setQk("");
							this.reservationStations[i].setVk(rd);
						}
					}
					catch(Exception e)
					{
						// not a number;
					}
				
			}
		}
		
	}
	public void flush()
	{
		System.err.println("SHED EL SEEEFOOON");
		while(!this.fetchedInstructions.isEmpty())
			this.fetchedInstructions.remove(0);
		while(!this.issuedInstructions.isEmpty())
			this.issuedInstructions.remove(0);
		for(int i = 0; i < this.ROB.size; i++)
			this.ROB.dequeue();
		while(!this.writeBack.isEmpty())
			this.writeBack.remove(0);
		this.remainingFetchDelay = 0;
		this.lastFetched = null;
		this.lastIssued = null;
		this.registerStatus.clear();
		for(int i = 0; i < this.reservationStations.length; i++)
			this.reservationStations[i].reset();
		this.issuePC = 0;
		System.out.println("NEW FETCH PC IS " + fetchPC);
		System.err.println("FETCH SIZE AFTER FLUSH = " + this.fetchedInstructions.size());
	}
	public void commit()
	{
		System.out.println("ROB SIZE IS " + ROB.getSize());
		if(this.ROB.getSize() != 0)
			
			if(this.ROB.peak().ready)
			{
				if(this.ROB.peak().instr.isBranch())
				{
					System.err.println("THIS IS A BRANCH");
					if(this.ROB.peak().val >= 0)
					{
						// branch is taken
						if(this.ROB.peak().instr.fields[0].equalsIgnoreCase("JALR"))
							this.registerFile.writeRegister(this.ROB.peak().dest,this.ROB.peak().val);
						this.ROB.peak().instr.setCommit(CURRENT_CYCLE);
						if(ROB.peak().instr == this.lastInstr)
							this.done = true;
						fetchPC = this.ROB.peak().val;
						this.ROB.dequeue();
						this.flush();
					}
					else
					{
						this.ROB.peak().instr.setCommit(CURRENT_CYCLE);
						if(ROB.peak().instr == this.lastInstr)
							this.done = true;
						this.ROB.dequeue();
					}
				}
				else
				{
					this.ROB.peak().instr.setCommit(CURRENT_CYCLE);
					if(ROB.peak().instr == this.lastInstr)
						this.done = true;
					this.registerFile.writeRegister(this.ROB.peak().dest,this.ROB.peak().val);
					this.ROB.dequeue();
				}
		
			}
	}
	public void printFetchTime()
	{
		for(int i = 0; i < this.instructs.size(); i++)
			System.out.println(instructs.get(i));
	}
	public void printReservationStations()
	{
		for(int i = 0; i < this.reservationStations.length; i++)
			System.out.println(reservationStations[i]);
	}
	
	public static void main(String[] abbas) {
		Bengo bengo = new Bengo();
		bengo.printFetchTime();
		bengo.printReservationStations();
		System.out.println(Bengo.CURRENT_CYCLE);
		bengo.dataBus.printRegisters();
		
	
	}
}

class CircularQueue <T> {
	int head, tail;
	T queue[];
	boolean isFull = false;
	int size;
	int counter = 0;
	
	public CircularQueue(int size) {
		head = tail = 0;
		queue = (T[]) new Object[size];
		this.size = size;
		
	}
	public int enqueue(T val) {
		int t = tail;
		if(!isFull)
		{
			queue[tail++] = val;
			tail %= queue.length;
			
		}
		counter++;
		isFull = counter == size;
		return t;
	}
	public T dequeue() {
		if(counter > 0)
		{
			T ret = queue[head++];
			head %= queue.length;
			isFull = false;
			counter--;
			return ret;
		}
		return null;
	
	}
	public boolean hasSpace()
	{
		return !isFull;
	}
	public int getSize()
	{
		return counter;
	}
	public T get(int index)
	{
		return queue[index];
	}
	public T peak()
	{
		return queue[head];
	}
}
