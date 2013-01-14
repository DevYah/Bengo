package bengo;
import java.util.HashMap;

public class CDB {
	HashMap<String, Integer> map;
	
	public CDB()
	{
		this.map = new HashMap<String,Integer>();
		for(int i = 0; i <= 7; i++)
			this.map.put("R" + i, 0);
	}
	
	public Integer getRegisterValue(String regName) {
		return map.get(regName);
	}
	
	public void writeRegister(String regName, Integer value) {
		if(regName != "R0")
			map.put(regName, value);
	}
	public void printRegisters()
	{
		for(int i = 0; i <= 7; i++)
			System.out.println(map.get("R"+i));
	}
}
