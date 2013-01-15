package bengo;
import java.util.HashMap;

public class CDB {
	HashMap<String, Short> map;
	
	public CDB()
	{
		this.map = new HashMap<String,Short>();
		for(int i = 0; i <= 7; i++)
			this.map.put("R" + i,(short) 0);
		this.map.put("R1",(short)12);
	}
	
	public Short getRegisterValue(String regName) {
		return map.get(regName);
	}
	
	public void writeRegister(String regName, Short value) {
		if(regName != "R0")
			map.put(regName, value);
	}
	public void printRegisters()
	{
		for(int i = 0; i <= 7; i++)
			System.out.println(map.get("R"+i));
	}
}
