package bengo;
import java.util.HashMap;


public class RegisterFile {
	HashMap<String, Integer> map;
	public int PC;
	
	public RegisterFile()
	{
		this.map = new HashMap<String,Integer>();
		for(int i = 0; i <= 7; i++)
			this.map.put("R" + i, 0);
	}
	
	public Integer getRegisterValue(String regName) {
		return map.get(regName);
	}
	
	public void writeRegister(String regName, Integer value) {
		map.put(regName, value);
	}
}
