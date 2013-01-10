import java.util.HashMap;
import java.util.Map;


public class RegisterStatus {
	
	Map<String,String> registerStations;
	
	public RegisterStatus()
	{
		this.registerStations = new HashMap<String,String>();
		for(int i = 0; i <= 7; i++)
			this.registerStations.put("R" + i, "");
		
	}
	
	public String getRegisterStation(String reg)
	{
		return this.registerStations.get(reg);
	}
	public void assignRegister(String stationName, String reg)
	{
		this.registerStations.put(reg,stationName);
	}
	

}
