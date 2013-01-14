package bengo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Pattern;

public class Assembler {
	static final String[] commands = {"LW", "SW", "JMP", "BEQ", "JALR", "RET", "ADD", "ADDI", "NAND", "MUL", "DIV"};
	static final int[] commandArgsCount = {3, 3, 2, 3, 2, 1, 3, 3, 3, 3, 3};
	static final int[] commandRegCount = {};
	
	String FILE_NAME, error, args[];
	
	public Assembler(String file) {
		FILE_NAME = file;
		error = "";
	}
	
	public boolean assemble() throws IOException {
		BufferedReader std = new BufferedReader(new FileReader(new File(FILE_NAME)));
		Pattern p = Pattern.compile("[A-Z]+ (?:[a-zA-Z_0-9]+, )*(?:[a-zA-Z_0-9]+)");
		int lineNum = 1;
		while (true) {
			String inst = std.readLine();
			if (inst == null)
				break;
			if (!p.matcher(inst).matches())
				return setErrorMessage(lineNum, "Invalid syntax");
			
			args = inst.split(" ");
			for (int i = 0; i < args.length; ++i)
				args[i] = args[i].split(",")[0];
			
			int command_ind = -1;
			for (int i = 0; i < commands.length; ++i)
				if (args[0].equals(commands[i])) {
					command_ind = i;
					break;
				}
			if (command_ind == -1)
				return setErrorMessage(lineNum, "Invalid command");
			if (args.length - 1 != commandArgsCount[command_ind])
				return setErrorMessage(lineNum, String.format("Wrong number of arguments, %d instead of %d.", args.length - 1, commandArgsCount[command_ind]));
			
			for (int i = 0; i < commandRegCount[command_ind]; ++i)
				if (args[i + 1] == "0") // FIXME: Check valid registers
					return setErrorMessage(lineNum, String.format("Invalid register No. %d", i + 1));
		    for (int i = 0; i < commandArgsCount[command_ind] - commandRegCount[command_ind]; ++i) {
                if (args[args.length - 1 - i] == "0") // FIXME: Check valid address
                    return setErrorMessage(lineNum, String.format("Invalid address %d", args.length - 1 - i));
            }
			//TODO Create instruction with the arguments
		
			++lineNum;
		}
		return true;
	}
	
	public String getErrorMessage() {
		return error;
	}
	public boolean setErrorMessage(int lineNum, String e) {
		error = String.format("Error in Line No. %d : %s", lineNum, e);
		args = null;
		return false;
	}
	public String[] getArguments() {
		return args;
	}
}
