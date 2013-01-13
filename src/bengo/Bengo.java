package bengo;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import bengo.data_fetcher.Memory;
import bengo.instruction_fetcher.InstructionFetcher;


public class Bengo {
	
	public static int CURRENT_CYCLE;
	public int PC;
	public int fetchPC; //pointer to next instruction to be fetched
	public int issuePC; //pointer to next instruction to be issued
	public int execPC; // pointer to next instruction to be executed
	
	int loadStations = 2;
	int loadTime = 4;
	int addStations = 3;
	int addTime = 4;
	int multStations = 1;
	int multTime = 3;
	int divideTime = 13;
	int remainingFetchDelay = 0;
	Instruction lastFetched;
	Instruction lastIssued;
	CircularQueue<ROBEntry> ROB;
	int ROBSize = 100;
	RegisterStatus registerStatus = new RegisterStatus();
	ArrayList<ReservationStation> writeBack;
	CDB dataBus;
	Instruction lastInstr;
	boolean done = false;
	
	static Memory memory; 
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
		 //this.instructionFetcher = new InstructionFetcher();
		 PC = 0;
		 fetchPC = 0;
		 issuePC = 0;
		 execPC = -1;
		 this.ROB = new CircularQueue<ROBEntry>(this.ROBSize);
		 this.fetchedInstructions = new ArrayList<Instruction>();
		 this.issuedInstructions = new ArrayList<Instruction>();
		 this.reservationStations = new ReservationStation[loadStations + addStations + multStations];
		 for(int i = 0; i < loadStations; i++)
		 {
			// System.out.println("CREATING LOAD STATION");
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
		 int memTime = 10;
		 this.instructionFetcher = new InstructionFetcher(levels, assoc, lines, penalties, instructionsPerLine,instructs,memTime);
		 this.remainingFetchDelay = (int) this.instructionFetcher.fetchInstruction(fetchPC)[0];
		 this.lastFetched = (Instruction) this.instructionFetcher.fetchInstruction(fetchPC)[1];
		 fetchPC++;
		 this.run();
		
	}
	public void run()
	{
		lastFetched.setFetchTime(CURRENT_CYCLE);
		this.writeBack();
		this.execute();
		this.issue();
		this.fetch();
		
		
		CURRENT_CYCLE++;
		boolean busy = false;
		for(int i = 0; i < this.reservationStations.length; i++)
		{
			if(reservationStations[i].isBusy())
			{
				busy = true;
				break;
			}
		}
		//if((issuePC < instructs.size()) || (this.remainingFetchDelay > 0))
		if(!done)
			run();
		
	}
	public void fetch()
	{
	//	System.out.println("TRYING TO FETCH AT CYCLE " + CURRENT_CYCLE);
		// TODO this.instructionFetcher.fetch(fetchPC);
		if(this.remainingFetchDelay == 0)
		{
		//	System.out.println("Fetching");
			// can fetch instruction
			lastFetched.setFetchTime(CURRENT_CYCLE);
			this.fetchedInstructions.add(lastFetched);
			if(fetchPC < instructs.size())
			{
				this.remainingFetchDelay = (int) this.instructionFetcher.fetchInstruction(fetchPC)[0];
				 this.lastFetched = (Instruction) this.instructionFetcher.fetchInstruction(fetchPC)[1];
				 
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
		//System.out.println("At cycle " + CURRENT_CYCLE + " and trying to issue " + issuePC);
		
		if(this.fetchedInstructions.size() > issuePC)
		{
			if(this.ROB.hasSpace())
			{
			//	System.out.println("ROB HAS SPACE");
				int instrDelay = 0;
				for(int i = 0; i < reservationStations.length; i++)
				{
					if(this.fetchedInstructions.get(issuePC).getType() == "LOAD")
					{
						instrDelay = this.loadTime;
					}
					else
					{
					if((!reservationStations[i].isBusy()) && (reservationStations[i].isCompatible(this.fetchedInstructions.get(issuePC).getType())))
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
					//	System.out.println("FOUND A STATION");
						Instruction instr = this.fetchedInstructions.get(issuePC);
						lastIssued = instr;
						instr.setIssueTime(CURRENT_CYCLE);
						reservationStations[i].assignInstruction(this.fetchedInstructions.get(issuePC), instrDelay);
						// create ROBEntry
						ROBEntry rob = new ROBEntry(instr.fields[1],instr.fields[0]);
						int index = this.ROB.enqueue(rob);
						reservationStations[i].setROBIndex(index);
						reservationStations[i].setOperation(instr.getType());
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
		//System.out.println("EXECUTING");
		//loop on all reservation Stations
		for(int i = 0; i < this.reservationStations.length; i++)
		{
			if((reservationStations[i].isBusy()) && ((reservationStations[i].qj == "") || (reservationStations[i].qj == null)) && ((reservationStations[i].qk == "") || (reservationStations[i].qk == null) ))
			{
				//System.err.println("found a reservation station");
				// Step a cycle
				System.out.println("STATION + " + i);
				if(reservationStations[i].isFinished())
				{
					reservationStations[i].instruction.setExecuteTime(CURRENT_CYCLE);
					//System.out.println("Reservation station finished at " + CURRENT_CYCLE);
					// Instruction done executing.
					// compute the actual result
					int answer = 0;
					//System.out.println("STATION NUMBER" + i);
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
					reservationStations[i].setAnswer(answer);
					this.writeBack.add(reservationStations[i]);
					
				}
			}
		}
	}
	public void writeBack()
	{
		System.out.println("writing back at cycle " + CURRENT_CYCLE + " size = " + this.writeBack.size());
		while(!this.writeBack.isEmpty())
		{
			String rd = this.writeBack.get(0).instruction.fields[1];
			System.err.println("RD = " + rd);
			this.writeBack.get(0).instruction.setWrittenTime(CURRENT_CYCLE);
			if(this.writeBack.get(0).instruction == this.lastInstr)
				this.done = true;
			
			this.dataBus.writeRegister(this.writeBack.get(0).instruction.fields[1], this.writeBack.get(0).answer);
			this.writeBack.get(0).reset();
			this.writeBack.remove(0);
			this.registerStatus.assignRegister("",rd);
			for(int i = 0; i < this.reservationStations.length; i++)
			{
				System.out.println("I + " + i);
				System.out.println("RD = " + rd);
				//if(this.reservationStations[i].isBusy())
				//{
					System.out.println(i + " IS BUSY");
					try
					{
						if(this.ROB.get(Integer.parseInt(this.reservationStations[i].qj)).dest == rd)
						{
							System.err.println(this.reservationStations[i]);
							this.reservationStations[i].setQj("");
							this.reservationStations[i].setVj(rd);
						}	

						if(this.ROB.get(Integer.parseInt(this.reservationStations[i].qk)).dest == rd)
						{
							System.out.println("HIIIIIT");
							this.reservationStations[i].setQk("");
							this.reservationStations[i].setVk(rd);
						}
					
					}
					catch(Exception e)
					{
						// not a number;
					}
				//}
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
	//	bengo.printReservationStations();
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
	public T dequeue(int val) {
		T ret = queue[head++];
		head %= queue.length;
		isFull = false;
		counter--;
		return ret;
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
}
