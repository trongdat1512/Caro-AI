package gameState;

import gameSettings.GameSettings;

public class Game {

	public int boardSize;

	// For direction
	private int fx[] = { 0 , 0, 1, -1, -1, 1, -1,  1 };
	private int fy[] = { -1, 1, 0,  0,  1, 1, -1, -1 };

	private Evaluation evaluation;
	private int depth = 1;

	int startI = 0;
	int startJ = 0;

	int endI = boardSize;
	int endJ = boardSize;

	public Game() {
		this.boardSize = GameSettings.boardSize;
		evaluation = new Evaluation();
	}

//	CPU first move
	public Move getFirstMove() {
		return new Move(boardSize/2, boardSize/2);
	}

//	Initialized the board
	public String[][] initialiseBoard() {
		String[][] board = new String[boardSize][boardSize];
		for (int i = 0; i < boardSize; i++) {
			for (int j = 0; j < boardSize; j++) {
				board[i][j] = "-";
			}
		}
		return board;
	}

//	Move validation
	private boolean isValid(int tx, int ty, String board[][], String player) {

		if (tx >= boardSize || tx < 0)
			return false;
		if (ty >= boardSize || ty < 0)
			return false;

		if (!board[tx][ty].equals(player))
			return false;

		return true;
	}

//	Direction validation
	private boolean isValidDir(int tx, int ty) {
		if (tx >= boardSize || tx < 0)
			return false;
		if (ty >= boardSize || ty < 0)
			return false;
		return true;
	}

	
	private boolean isWinner(int tx, int ty, int dx, int dy, String board[][]) {
		String player = board[tx][ty];
		int count = 0;
		while (true) {
			if (player.equals(board[tx][ty])) {
				count++;
			} else
				return false;

			if (count == 5)
				return true;

			tx += dx;
			ty += dy;

			if (!isValid(tx, ty, board, player))
				return false;
		}

	}

	public int checkWin(String board[][]) {
		for (int i = 0; i < boardSize; i++) {
			for (int j = 0; j < boardSize; j++) {
				String player = board[i][j];
				if (player.equals("X") || player.equals("O")) {
					for (int k = 0; k < 8; k++) {
						int dirX = fx[k];
						int dirY = fy[k];

						if (isWinner(i, j, dirX, dirY, board)) {
							if (player.equals("X"))
								return 10;
							else if (player.equals("O"))
								return -10;
						}
					}
				}
			}
		}
		return 0;
	}

	public boolean isMovesLeft(String board[][]) {
		for (int i = 0; i < boardSize; i++)
			for (int j = 0; j < boardSize; j++)
				if (board[i][j].equals("-"))
					return true;
		return false;
	}

	// Minimax algorithm
	private int minimax(String board[][], boolean turn, int step, int computerMoves) {

		if (step == depth) {
			return evaluation.evaluate(board, turn);
		}
		
		if (turn) {
			int mx = -Integer.MAX_VALUE;
			for (int i = startI; i < endI; i++) {	
				for (int j = startJ; j < endJ; j++) {
					if (!hasAdjacent(i, j, board, computerMoves))
						continue;

					if (board[i][j].equals("-")) {
						board[i][j] = "X";

						int minimaxValue = minimax(board, !turn, step + 1, computerMoves);
						board[i][j] = "-";
						mx = Math.max(mx, minimaxValue);
					}
				}
			}
			return mx;

		} else {
			int mn = Integer.MAX_VALUE;
			for (int i = startI; i < endI; i++) {
				for (int j = startJ; j < endJ; j++) {
					if (!hasAdjacent(i, j, board, computerMoves))
						continue;

					if (board[i][j].equals("-")) {
						board[i][j] = "O";

						int minimaxValue = minimax(board, turn, step + 1, computerMoves);
						board[i][j] = "-";
						mn = Math.min(mn, minimaxValue);
					}
				}
			}
			return mn;
		}
	}
	
	// Minimax algorithm with alpha beta pruning
	private int minimaxAB(String board[][], boolean turn, int step, int alpha, int beta, int computerMoves) {

		if (step == depth) {
			return evaluation.evaluate(board, turn);
		}
		
		if (turn) {
			for (int i = startI; i < endI; i++) {
				if (alpha >= beta) {
					return alpha;
				}
				for (int j = startJ; j < endJ; j++) {
					if (!hasAdjacent(i, j, board, computerMoves))
						continue;

					if (board[i][j].equals("-")) {
						board[i][j] = "X";

						int minimaxValue = minimaxAB(board, !turn, step + 1, alpha, beta, computerMoves);
						board[i][j] = "-";
						alpha = Math.max(alpha, minimaxValue);

						if (alpha >= beta) {
							return alpha;
						}
					}
				}
			}
			return alpha;

		} else {
			for (int i = startI; i < endI; i++) {
				if (alpha >= beta) {
					return beta;
				}
				for (int j = startJ; j < endJ; j++) {
					if (!hasAdjacent(i, j, board, computerMoves))
						continue;

					if (board[i][j].equals("-")) {
						board[i][j] = "O";

						int minimaxValue = minimaxAB(board, turn, step + 1, alpha, beta, computerMoves);
						board[i][j] = "-";
						beta = Math.min(beta, minimaxValue);

						if (alpha >= beta) {
							return beta;
						}
					}
				}
			}

			return beta;
		}
	}

	
	private boolean hasAdjacent(int i, int j, String board[][], int computerMoves) {
		
		int adjCount = 1;
		
		for (int ii = 0; ii < 8; ii++) {
			int x = i;
			int y = j;
			for (int jj = 0; jj < adjCount; jj++) {

				x += fx[ii];
				y += fy[ii];

				if (!isValidDir(x, y))
					continue;

				if (board[x][y] == "X" || board[x][y] == "O")
					return true;
			}
		}

		return false;

	}

	// Build smaller board to reduce searching space
	private String[][] buildSmallBoard(String board[][]) {
		int maxI = 0;
		int maxJ = 0;

		int minI = boardSize - 1;
		int minJ = boardSize - 1;

		for (int i = 0; i < boardSize; i++) {
			for (int j = 0; j < boardSize; j++) {
				if (board[i][j] == "X" || board[i][j] == "O") {
					if (minI > i) {
						minI = i;
					}
					if (minJ > j) {
						minJ = j;
					}

					if (maxI < i) {
						maxI = i;
					}
					if (maxJ < j) {
						maxJ = j;
					}
				}
			}
		}

		String[][] smallBoard = new String[boardSize][boardSize];

		int x = 1;
		if (minI >= x)
			minI -= x;
		else
			minI = 0;
		if (minJ >= x)
			minJ -= x;
		else
			minJ = 0;

		if (maxI < boardSize - 1)
			maxI += x;
		else
			maxI = boardSize - 1;
		if (maxJ < boardSize - 1)
			maxJ += x;
		else
			maxJ = boardSize - 1;

		startI = minI;
		startJ = minJ;

		endI = maxI + 1;
		endJ = maxJ + 1;

		for (int i = minI; i <= maxI; i++) {
			for (int j = minJ; j <= maxJ; j++) {
				smallBoard[i][j] = board[i][j];
			}
		}

		return smallBoard;

	}

	// Calls the minimax function to find optimal move
	public Move findOptimalMove(String board[][], int computerMoves, boolean isPruning) {
		int bestVal = -Integer.MAX_VALUE;

		int moveI = -9;
		int moveJ = -9;

		int step = 0;
		int alpha = -Integer.MAX_VALUE;
		int beta = Integer.MAX_VALUE;

		board = buildSmallBoard(board);

		for (int i = startI; i < endI; i++) {

			for (int j = startJ; j < endJ; j++) {

				if (!hasAdjacent(i, j, board, computerMoves))
					continue;

				if (board[i][j].equals("-")) {

					board[i][j] = "X";

					int moveVal = isPruning ? minimaxAB(board, false, step, alpha, beta, computerMoves)
							: minimax(board, false, step, computerMoves);

					board[i][j] = "-";
		
					if (moveVal > bestVal) {
						moveI = i;
						moveJ = j;
						bestVal = moveVal;
					}
				}
			}
		}
		return new Move(moveI, moveJ);
	}

}
