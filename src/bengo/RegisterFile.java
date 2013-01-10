package bengo;
import java.util.HashMap;


public class RegisterFile {
	HashMap<String, Integer> map;
	public int PC;
	
	public Integer getRegisterValue(String regName) {
		return map.get(regName);
	}
	
	public void writeRegister(String regName, Integer value) {
		map.put(regName, value);
	}
}
