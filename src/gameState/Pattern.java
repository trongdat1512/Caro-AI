package gameState;

import java.util.HashMap;
import java.util.HashSet;

public class Pattern {

	public static HashMap<String, Integer> patternsCPU;
	public static HashMap<String, Integer> patternsHUMAN;

	// CPU pattern
	private String[] patternListX = { "XXXXX", "-XXXX-", "XXXX-", "XX-XX", "XXX-X", "X-XXX-", "-XXX-", "XXX--", "-XX-X",
			"XX-X-", "-XX-X-", "--XX-", "XX---", "X---X", "X----", "--X--", "-X---" };
	// Human pattern
	private String[] patternListO = { "OOOOO", "-OOOO-", "OOOO-", "OO-OO", "OOO-O", "O-OOO-", "-OOO-", "OOO--", "-OO-O",
			"OO-O-", "-OO-O-", "--OO-", "OO---", "O---O", "O----", "--O--", "-O---" };

	int[] profit = { 1000000000, 1000000, 50000, 1000, 1000, 1500, 500, 300, 100, 100, 100, 5, 5, 5, 2, 2, 2 };

	public Pattern() {
		patternsCPU = createList(true);
		patternsHUMAN = createList(false);
	}

	private HashMap<String, Integer> createList(boolean cpu) {
		HashMap<String, Integer> temp = new HashMap<String, Integer>();

		for (int i = 0; i < 17; i++) {
			String pat;
			int val;

			if (cpu) {
				pat = patternListX[i];
				val = profit[i];
			} else {

				pat = patternListO[i];
				val = (-1) * profit[i];
			}

			temp.put(pat, val);
		}

		return temp;
	}

	public boolean isValidPattern(String pattern) {
		HashSet<Character> set = new HashSet<>();
		for (int i = 0; i < pattern.length(); i++) {
			set.add(pattern.charAt(i));
		}
		if (set.size() > 2)
			return false;
		else
			return true;
	}

	public String patternType(String pattern) {
		HashSet<Character> set = new HashSet<Character>();
		for (int i = 0; i < pattern.length(); i++) {
			set.add(pattern.charAt(i));
		}
		if (set.size() > 2)
			return "mix";
		else {
			if (set.contains('X'))
				return "CPU";
			return "human";
		}
	}

	public int isPatternExist(String pat, boolean cpu) {
		for (int i = 0; i < 16; i++) {
			String s, sRev;
			if (cpu)
				s = patternListX[i];
			else
				s = patternListO[i];

			sRev = new StringBuilder(s).reverse().toString();

			if (pat.equals(s) || pat.equals(sRev)) {
				return i;
			}
		}

		return -1;
	}

	public int getEvaluateValuePatternWise(int index, boolean isCpuPatternType, boolean cpuTurn) {

		int value;

		if (isCpuPatternType) {
			value = patternsCPU.get(patternListX[index]);
			if (cpuTurn)
				value *= 5;
		} else {
			value = patternsHUMAN.get(patternListO[index]);
			if (!cpuTurn)
				value *= 5;
		}

		return value;
	}
}
