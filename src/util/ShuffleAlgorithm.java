package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import game.board.Board;
import game.board.Cellstate;
import jgolad.Main;

public abstract class ShuffleAlgorithm {
	private String name;

	public ShuffleAlgorithm(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}

	public abstract void shuffle(Board b, int w, int h);

	public static ShuffleAlgorithm[] shufflers = new ShuffleAlgorithm[] { new ShuffleAlgorithm("White Soup") {
		@Override
		public void shuffle(Board b, int w, int h) {
			int chance = WinUtil.getInputInt("Enter the density percentage (as an integer)", 50);
			for (int x = 0; x < b.getWidth(); x++) {
				for (int y = 0; y < b.getHeight(); y++) {
					if (MathUtil.randInt(0, 100) <= chance) {
						b.setAt(x, y, Cellstate.NEUTRAL.getID());
					}
				}
			}
		}
	}, new ShuffleAlgorithm("Hanss314 Rotational Sym.") {
		@Override
		public void shuffle(Board b, int w, int h) {
			int chance = WinUtil.getInputInt("Enter the density percentage (as an integer)", 50);
			for (int x = 0; x < (b.getWidth() / 2); x++) {
				for (int y = 0; y < b.getHeight(); y++) {
					int pairX = w - 1 - x;
					int pairY = h - 1 - y;
					if (MathUtil.randInt(0, 100) < chance) {
						b.setAt(x, y, Cellstate.PL1.getID());
						b.setAt(pairX, pairY, Cellstate.PL2.getID());
					}
				}
			}
		}
	}, new ShuffleAlgorithm("Kiza Diagonal/Inverse Sym.") {
		@Override
		public void shuffle(Board b, int w, int h) {
			int chance = WinUtil.getInputInt("Enter the density percentage (as an integer)", 50);
			for (int x = 0; x < b.getWidth(); x++) {
				for (int y = 0; y < b.getHeight(); y++) {
					int pairX = y;
					int pairY = x;
					if (MathUtil.randInt(0, 100) < chance) {
						if (pairX != x && pairY != y) {
							b.setAt(x, y, Cellstate.PL1.getID());
							b.setAt(pairX, pairY, Cellstate.PL2.getID());
						}
					}
				}
			}
		}
	}, new ShuffleAlgorithm("Hanss314 Rotational Sym. (4p)") {
		@Override
		public void shuffle(Board b, int w, int h) {
			int chance = WinUtil.getInputInt("Enter the density percentage (as an integer)", 50);
			for (int x = 0; x < b.getWidth()/ 2; x++) {
				for (int y = 0; y < b.getHeight()/2; y++) {
					if (MathUtil.randInt(0, 100) < chance) {
						b.setAt(y, x, Cellstate.PL1.getID());
						b.setAt(b.getWidth()-x-1, y, Cellstate.PL2.getID());
						b.setAt(x, b.getHeight()-y-1, Cellstate.PL3.getID());
						b.setAt(b.getWidth()-y-1, b.getHeight()-x-1, Cellstate.PL4.getID());
					}
				}
			}
		}
	}, new ShuffleAlgorithm("4 Player Checkerboard") {
		@Override
		public void shuffle(Board b, int w, int h) {
			int chance = WinUtil.getInputInt("Enter the density percentage (as an integer)", 50);
			int xMax = b.getWidth()/2;
			int yMax = b.getHeight()/2;
			for (int x = 0; x < xMax; x++) {
				for (int y = 0; y < yMax; y++) {
					if (MathUtil.randInt(0, 100) < chance) {
						b.setAt(x, y, Cellstate.PL1.getID());
						b.setAt(xMax*2-x-1, y, Cellstate.PL2.getID());
						b.setAt(x, yMax*2-y-1, Cellstate.PL3.getID());
						b.setAt(xMax*2-x-1, yMax*2-y-1, Cellstate.PL4.getID());
					}
				}
			}
		}
	}, new ShuffleAlgorithm("IQuick's Ant") {
		@Override
		public void shuffle(Board b, int w, int h) {
			boolean ruleValid = false;
			String rule = "";
			while (!ruleValid) {
				rule = WinUtil.getInput("Enter the rule (R/L max 6 in length)", "RL").toUpperCase();
				if (rule.matches("^[RL]+$")) {
					ruleValid = true;
				} else {
					WinUtil.display("Invalid charcters!");
				}
			}
			
			int dir = 0;
			//UP RIGHT DOWN LEFT
			int[][] directions = {{0,1},{1,0},{0,-1},{-1,0}};
			
			int[][] board = new int[w][h];
			
			int x = Math.round(w/2);
			int y = Math.round(h/2);
			
			byte[] cells = {Cellstate.DEAD.getID(),Cellstate.NEUTRAL.getID(),Cellstate.PL1.getID(),Cellstate.PL2.getID(),Cellstate.PL3.getID(),Cellstate.PL4.getID(),Cellstate.EATER.getID(),Cellstate.ANTIEATER.getID()};
			
			int ruleLength = rule.length();
			
			for (int i = 0; i < 1000000; i++) {//set maximum of 1000000 iterations
				//update board
				board[x][y] = (board[x][y] + 1) % ruleLength;
				//change direction according to board
				if (rule.charAt(board[x][y]) == 'R') {
					dir = (dir + 5) % 4;
				} else {
					dir = (dir + 3) % 4;
				}
				//move
				x += directions[dir][0];
				y += directions[dir][1];
				//check if we're out of board and if so end the loop
				if (x > w-1 || y > h-1 || x < 0 || y < 0) {
					break;
				}
			}
			for (int i = 0; i < board.length; i++) {
				for (int j = 0; j < board[i].length; j++) {
					b.setAt(i, j, cells[board[i][j] % cells.length]);
				}
			}
		}
	}, new ShuffleAlgorithm("IQuick's (FAIR) Ant") {
		@Override
		public void shuffle(Board b, int w, int h) {
			String rule = "RL";
			
			int dir = 0;
			//UP RIGHT DOWN LEFT
			int[][] directions = {{0,1},{1,0},{0,-1},{-1,0}};
			
			int[][] board = new int[w/2][h/2];
			
			int x = Math.round(w/4)-1;
			int y = Math.round(h/4)-1;
			
			int ruleLength = rule.length();
			
			for (int i = 0; i < 1000000; i++) {//set maximum of 1000000 iterations
				//update board
				board[x][y] = (board[x][y] + 1) % ruleLength;
				//change direction according to board
				if (rule.charAt(board[x][y]) == 'R') {
					dir = (dir + 5) % 4;
				} else {
					dir = (dir + 3) % 4;
				}
				//move
				x += directions[dir][0];
				y += directions[dir][1];
				//check if we're out of board and if so end the loop
				if (x > w-1 || y > h-1 || x < 0 || y < 0) {
					break;
				}
			}
			for (int i = 0; i < board.length; i++) {
				for (int j = 0; j < board[i].length; j++) {
					if(board[i][j] != 0){
						b.setAt(j, i, Cellstate.PL1.getID());
						b.setAt(b.getWidth()-i-1, j, Cellstate.PL2.getID());
						b.setAt(i, b.getHeight()-j-1, Cellstate.PL3.getID());
						b.setAt(b.getWidth()-j-1, b.getHeight()-i-1, Cellstate.PL4.getID());
					}
				}
			}
		}
	}, new ShuffleAlgorithm("Board Filler by IQuick") {
		@Override
		public void shuffle(Board bo, int w, int h) {
			int width = WinUtil.getInputInt("Width of your pattern:", 5);
			int height = WinUtil.getInputInt("Height of your pattern:", 5);
			Object[][] input = WinUtil.getInputTable("Specify your pattern:", width, height, "bool");
			
			for (int i = 0; i < w; i += width) {
				for (int j = 0; j < h; j += height) {
					for (int a = 0; a < width; a++) {
						for (int b = 0; b < height; b++) {
							if (i+a < w && j+b < h) {
								if ((Boolean) input[a][b]) {
									bo.setAt(i+a, j+b, Cellstate.NEUTRAL.getID());
								}
							}
						}
					}
				}
			}
		}
	}, new ShuffleAlgorithm("Befunge... cuz why not? -IQuick 2017") {
		@Override
		public void shuffle(Board b, int w, int h) {
			String[] choices = {"Load from file", "Load from user"};
			String choice = WinUtil.getChoice("Choose program:", choices);
			String[] code = {};
			String input = "";
			if (choice == choices[0]) {
				File codeFile = WinUtil.getUserFile(Main.getFrame());
				Scanner codeIn = null;
				try {
					codeIn = new Scanner(codeFile);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				List<String> temp = new ArrayList<String>();
				while (codeIn.hasNextLine()) {
					temp.add(codeIn.nextLine());
				}
				code = temp.toArray(new String[0]);
				codeIn.close();
			} else {
				code = WinUtil.getLargeInput("Type code:").split("\n");
			}
			String[] choices1 = {"Load from file", "Load from user", "No input"};
			choice = WinUtil.getChoice("Choose input:", choices1);
			
			if (choice == choices1[0]) {
				File inputFile = WinUtil.getUserFile(Main.getFrame());
				try {
					FileInputStream fis = new FileInputStream(inputFile);
					byte[] data = new byte[(int) inputFile.length()];
					fis.read(data);
					fis.close();
					input = new String(data, "UTF-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else if (choice == choices1[1]){
				input = WinUtil.getLargeInput("Write input for program:");
			} else {
				input = "";
			}
			
			if (WinUtil.getYN("Append board size (width, height) before start of input?")) {
				input = w +" "+ h +" "+ input;
			}
			
			BefungeInterpreter BFDI = new BefungeInterpreter();
			
			int maxTime = Math.max(Math.min(WinUtil.getInputInt("Set maximum amout of time for code to run in ms (between 100 - 60000)", 15000), 60000), 100);
			BFDI.setTime((long) maxTime);
			
			try {
				BFDI.Execute(code, input);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			String output = BFDI.getOutput();
			
			String[] choices2 = {"x, y", "x, y, v", "Non whitespace -> alive"};
			String OutputInterpretation = WinUtil.getChoice("Select program output interpretation:", choices2);
			
			if (OutputInterpretation == choices2[0]) {
				Scanner sc = new Scanner(output);
				while (sc.hasNextInt()) {
					int temp = sc.nextInt();
					if (sc.hasNextInt()) {
						b.setAt(temp, sc.nextInt(), Cellstate.NEUTRAL.getID());
					}
				}
				sc.close();
			} else if (OutputInterpretation == choices2[1]) {
				Scanner sc = new Scanner(output);
				Cellstate[] states = Cellstate.getAll().toArray(new Cellstate[0]);
				while (sc.hasNextInt()) {
					int temp = sc.nextInt();
					if (sc.hasNextInt()) {
						int temp2 = sc.nextInt();
						if (sc.hasNextInt()) {
							b.setAt(temp, temp2, states[sc.nextInt() % states.length].getID());
						}
					}
				}
				sc.close();
			} else if (OutputInterpretation == choices2[2]) {
				for (int i = 0; i < output.length(); i++) {
					if (output.charAt(i) != ' ') {
						if (Math.floorDiv(i, w) >= h) {
							break;
						}
						b.setAt(i % w, Math.floorDiv(i, w), Cellstate.NEUTRAL.getID());
					}
				}
			}
		}
	}};
	
	public void shuffle(Board board) {
		shuffle(board, board.getWidth(), board.getHeight());
	}
}
