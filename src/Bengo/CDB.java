package Bengo;
import java.util.HashMap;

public class CDB {
	HashMap<String, Integer> map;
	
	public Integer getRegisterValue(String regName) {
		return map.get(regName);
	}
	
	public void writeRegister(String regName, Integer value) {
		map.put(regName, value);
	}
}
