package bengo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class Assembler {
	static final String[] commands = {"LW", "SW", "JMP", "BEQ", "JALR", "RET", "ADD", "ADDI", "NAND", "MUL", "DIV"};
	static final int[] commandArgsCount = {3, 3, 2, 3, 2, 1, 3, 3, 3, 3, 3};
	static final int[] commandRegCount = {2, 2, 1, 2, 2, 1, 3, 2, 3, 3, 3};
	static final String[] REGS = {"R0", "R1", "R2", "R3", "R4", "R5", "R6", "R7"};
	
	String FILE_NAME, error;
	Instruction[] program;
	
	public Assembler(String file) {
		FILE_NAME = file;
		error = "";
	}
	
	public String[] extractArguments(String line) {
		String[] args = line.split(",");
		String[] ret = new String[args.length + 1];
		ret[0] = args[0].split("[ ]+")[0];
		ret[1] = args[0].split("[ ]+")[1];
		for (int i = 1; i < args.length; ++i)
			ret[i + 1] = args[i].trim();
		return ret;
	}
	
	public boolean assemble() throws IOException {
		//TODO Test
		BufferedReader std = new BufferedReader(new FileReader(new File(FILE_NAME)));
		Pattern commandMatcher = Pattern.compile("[A-Z]+ +(?:[a-zA-Z_0-9]+, *)*(?:(?:[a-zA-Z_0-9]+)|(?:-?[0-9]+))");
		Pattern intMatcher = Pattern.compile("-?[0-9]+");
		ArrayList<Instruction> instructions = new ArrayList<Instruction>();
		program = null;
		int lineNum = 1;
		while (true) {
			String inst = std.readLine();
			if (inst == null)
				break;
			inst = inst.trim();
			if (!commandMatcher.matcher(inst).matches())
				return setErrorMessage(lineNum, "Invalid syntax");
			
			String[] args = extractArguments(inst);
			
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
			
			for (int i = 0; i < commandRegCount[command_ind]; ++i) {
				boolean valid = false;
				for (int j = 0; j < REGS.length; ++j)
					if (args[i + 1].equals(REGS[j])) {
						valid = true;
						break;
					}
				if (!valid)
					return setErrorMessage(lineNum, String.format("Invalid register name, argument #%d", i + 1));
			}
			for (int i = 0; i < commandArgsCount[command_ind] - commandRegCount[command_ind]; ++i) {
				if (!intMatcher.matcher(args[args.length - 1 - i]).matches())
					return setErrorMessage(lineNum, String.format("Invalid argument, argument #%d", args.length - 1 - i));
				else {
					int address = Integer.parseInt(args[args.length - 1 - i]);
					if (address > 63 || address < -64)
						return setErrorMessage(lineNum, String.format("Invalid argument, argument #%d", args.length - 1 - i));
					 
				}
			}
			instructions.add(new Instruction(args, lineNum - 1));
			++lineNum;
		}
		program = instructions.toArray(new Instruction[0]);
		return true;
	}
	
	public String getErrorMessage() {
		return error;
	}
	public boolean setErrorMessage(int lineNum, String e) {
		error = String.format("Error in Line No. %d : %s", lineNum, e);
		return false;
	}
	public Instruction[] getProgram() {
		return program;
	}
	
	public static void main(String[] args) throws IOException {
		Assembler q = new Assembler("AssmeblerTest");
		if (q.assemble()) {
			Instruction[] program = q.getProgram();
			for (int i = 0; i < program.length; ++i) {
				System.out.println(program[i]);
			}
		} else {
			System.out.println(q.getErrorMessage());
		}
	}
}
