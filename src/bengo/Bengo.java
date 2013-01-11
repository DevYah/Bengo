package bengo;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import bengo.data_fetcher.InstructionFetcher;
import bengo.data_fetcher.Memory;


public class Bengo {
	
	public static int CURRENT_CYCLE;
	public int PC;
	public int fetchPC; //pointer to next instruction to be fetched
	public int issuePC; //pointer to next instruction to be issued
	public int execPC; // pointer to next instruction to be executed
	
	int loadStations;
	int loadTime;
	int addStations;
	int addTime;
	int multStations;
	int multTime;
	int divideTime;
	
	// 	ROB, Reservation Station, Instruction, Cache & Memory
	ArrayList<Instruction> instructs;
	ReservationStation[] reservationStations;
	
	ArrayList<Integer> fetchedInstructions;
	ArrayList<Integer> issuedInstructions;
	
	InstructionFetcher instructionFetcher;
	
	
	public Bengo()
	{
		 this.instructionFetcher = new InstructionFetcher();
		 PC = 0;
		 fetchPC = 0;
		 issuePC = -1;
		 execPC = -1;
		 this.fetchedInstructions = new ArrayList<Integer>();
		 this.issuedInstructions = new ArrayList<Integer>();
		 this.reservationStations = new ReservationStation[loadStations + addStations + multStations];
		 for(int i = 0; i <= loadStations; i++)
		 {
			 reservationStations[i] = new ReservationStation("LOAD "+ (i + 1));
		 }
		 for(int i = 0; i <= addStations; i++)
		 {
			 reservationStations[i] = new ReservationStation("ADD "+ (i + 1));
		 }
		 for(int i = 0; i <= multStations; i++)
		 {
			 reservationStations[i] = new ReservationStation("MULT "+ (i + 1));
		 }
		 this.instructs = new ArrayList<Instruction>();
		// read the instructions
		 try
		 {
			 BufferedReader instructionsReader = new BufferedReader(new FileReader("program.txt")); 
			 String instructionStr;
				try
				{
					while((instructionStr = instructionsReader.readLine()) != null)
					{
						Instruction instructionObj = new Instruction(instructionStr.split(" "));
						this.instructs.add(instructionObj);
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
		 
		 this.run();
		
	}
	public void run()
	{
		this.fetch();
		this.issue();
		this.execute();
		CURRENT_CYCLE++;
	}
	public void fetch()
	{
		this.instructionFetcher.fetch(fetchPC);
		fetchPC++;
	}
	public void issue()
	{
		if(issuePC >= 0)
		{
				for(int i = 0; i < reservationStations.length; i++)
				{
					if((!reservationStations[i].isBusy()) && (reservationStations[i].isCompatible(instructs.get(issuePC).getType())))
					{
						//TODO CHECK REGISTER STATUS
						reservationStations[i].assignInstruction(instructs.get(issuePC));
						this.issuedInstructions.add(issuePC);
					}
				}
			
		}
	}
	public void execute()
	{
		
	}
	public static void main(String[] abbas) {
		Bengo bengo = new Bengo();
		
	
	}
}

class CircularQueue <T> {
	int head, tail;
	T queue[];
	
	public CircularQueue(int size) {
		head = tail = 0;
		queue = (T[]) new Object[size];
	}
	public void enqueue(T val) {
		queue[tail++] = val;
		tail %= queue.length;
	}
	public T dequeue(int val) {
		T ret = queue[head++];
		head %= queue.length;
		return ret;
	}
}
