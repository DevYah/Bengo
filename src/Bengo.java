import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;


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
	
	public Bengo()
	{
		 this.reservationStations = new ReservationStation[loadStations + addStations + multStations];
		 for(int i = 0; i <= loadStations; i++)
		 {
			 reservationStations[i] = new ReservationStation("Load "+ (i + 1));
		 }
		 for(int i = 0; i <= addStations; i++)
		 {
			 reservationStations[i] = new ReservationStation("Add "+ (i + 1));
		 }
		 for(int i = 0; i <= multStations; i++)
		 {
			 reservationStations[i] = new ReservationStation("Mult "+ (i + 1));
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
			
			
		
	}
	public static void main(String[] abbas) {
		Bengo bengo = new Bengo();
		
	
	}
}
