package gameStatePacakge;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

import asset.ImageLoader;
import displayPackage.Display;
import gameSettings.GameSettings;

public class GameGraphicsManagement implements Runnable, KeyListener, MouseListener, MouseMotionListener {

	private Display display;
	private int width;
	private int height;
	private String title;

	private Thread thread;

	private int BOX_width = 50;
	private int BOX_height = 50;

	private int paddingX = 100;
	private int paddingY = 100;

	private int mousePointAtX = -1;
	private int mousePointAtY = -1;

	private BufferStrategy buffer;
	private Graphics g;

	private int rowColom;

	private Game game;

	private BufferedImage black;
	private BufferedImage white;
	private BufferedImage mouseIndicator;
	private BufferedImage background;
	private BufferedImage logo;

	private boolean isGameEnd = false;
	private boolean isGameStarted = false;
	private String winStatement = "";
	private String lastMoveComputer = "";
	private String lastMoveYou = "";

	private boolean firstMove = true;

	private String[][] board;
	private String player;
	private String playerCpu = "X";
	private String playerHuman = "O";
	private String playerEmpty = "-";

	private int computerMoves = 0;

	public GameGraphicsManagement() {

		this.width = GameSettings.width;
		this.height = GameSettings.height;
		this.title = GameSettings.title;
		this.display = new Display(width, height, title);

		this.rowColom = GameSettings.rowColom;
	}
// initialized all images, created game instance and added all sorts of listeners.
	private void init() {

		player = playerCpu;

		game = new Game();

		black = ImageLoader.loadImage("/Images/bl1.png", BOX_width, BOX_height);
		white = ImageLoader.loadImage("/Images/w1.png", BOX_width, BOX_height);
		mouseIndicator = ImageLoader.loadImage("/Images/w1.png", BOX_width, BOX_height);
		background = ImageLoader.loadImage("/Images/background.jpg", width, height);
		logo = ImageLoader.loadImage("/Images/logo.png");

		display.canvas.addKeyListener(this);
		display.canvas.addMouseMotionListener(this);
		display.canvas.addMouseListener(this);
	}

//	start
	public synchronized void start() {
		thread = new Thread(this);
		thread.start();

	}
//	stop
	public synchronized void stop() {
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

//	functionality of Drawing board 
	private void drawBoard(Graphics g) {

		g.setColor(Color.black);

		int lineWeight = 1; // thickness of lines of the board 

		int lineNumber = 1;
		char lineCharacter = 'A';

		for (int i = paddingX; i <= (rowColom * BOX_width) + paddingX; i += BOX_width) {
			g.setFont(new Font("arial", Font.CENTER_BASELINE, 14));
			if(i < (rowColom * BOX_width) + paddingX) {
				g.drawString(Character.toString(lineCharacter++), i - 50, paddingY - 45 + rowColom*50);
			}				
			for (int k = i - lineWeight; k < i + lineWeight; k++)
				g.drawLine(k-75, paddingY-75, k-75, BOX_height * rowColom+25);

		}
		lineNumber = 1;

		for (int i = paddingY; i <= (rowColom * BOX_height) + paddingY; i += BOX_height) {
			if(i < (rowColom * BOX_height) + paddingY) {
				g.drawString(Integer.toString(lineNumber++), paddingX - 55 + rowColom*50, i - 48);
			}						
			for (int k = i - lineWeight; k < i + lineWeight; k++)
				g.drawLine(paddingX-75, k-75, BOX_width * rowColom+25, k-75);
		}

	}

//	Returns Alphabet corresponding to the line number
	private String colomToString(int x) {
		char c = 'A';
		c += x;
		return Character.toString(c);

	}

//	Functionality of managing mouse pointer
	private void mousePointAt(Graphics g) {

		g.setColor(Color.blue);
		g.setFont(new Font("Agency fb", Font.CENTER_BASELINE, 40));

		String s = colomToString(mousePointAtY - 1) + ", " + mousePointAtX;

		if (!(mousePointAtX == -1 || mousePointAtY == -1)
				&& board[mousePointAtX - 1][mousePointAtY - 1].equals(playerEmpty)) {
			g.drawString(s, mousePointAtY * 50 + 30, mousePointAtX * 50 + 30);
			mousePointIndicatior(g);
		}

	}

//	Functionality of drawing the loaction of mouse pointing at
	private void mousePointIndicatior(Graphics g) {

		int x = mousePointAtY * BOX_width - (BOX_width / 2);
		int y = mousePointAtX * BOX_height - (BOX_height / 2);

		/*
		 * g.setColor(Color.GRAY); g.fillOval(x, y, BOX_width, BOX_height);
		 */

		g.drawImage(mouseIndicator, x, y, null);

	}

//	Functionality drawing player according to the board
	private void drawPlayer(Graphics g) {

		BufferedImage playerImage = null;
		for (int i = 0; i < rowColom; i++) {
			for (int j = 0; j < rowColom; j++) {

				if (board[i][j].equals(playerCpu)) {
					playerImage = black;
				} else if (board[i][j].equals(playerHuman)) {
					playerImage = white;
				}
				if (!board[i][j].equals(playerEmpty)) {

					int x = (j + 1) * BOX_width - (BOX_width / 2);
					int y = (i + 1) * BOX_height - (BOX_height / 2);
					g.drawImage(playerImage, x, y, null);

				}
			}
		}

	}

//	Check board state [win/lose/tie]
	private void checkWin() {
		int x = game.checkWin(board);

		isGameEnd = true;

		if (x == 10) {
			winStatement = "Computer Won";
		}

		else if (x == -10) {
			winStatement = "You Won";
		}

		else if (game.isMovesLeft(board) == false) {
			winStatement = "Tie";

		} else
			isGameEnd = false;

	}

	private void drawBackground(Graphics g) {
		g.drawImage(background, 0, 0, null);
	}

	private void drawLogo(Graphics g) {
		g.drawImage(logo, width - 340, 20, null);
	}

	private void drawWinStatus(Graphics g) {
		if (isGameEnd) {
			g.setColor(Color.blue);
			g.setFont(new Font("Century Gothic", Font.CENTER_BASELINE, 30));

			g.drawString(winStatement, width - 340, 360);

		}
	}

	private void drawStartPlayString(Graphics g) {
		if (!isGameStarted) {
			g.setColor(Color.black);
			g.setFont(new Font("Century Gothic", Font.BOLD, 20));

			g.drawString("Press Enter to start the game", width - 340, 450);

		}
	}

	private void drawLastMove(Graphics g) {
		if (isGameStarted) {
			g.setFont(new Font("Century Gothic", Font.CENTER_BASELINE, 40));

			g.setColor(Color.black);
			g.drawString("Last Move:", width - 340, 220);

			g.setFont(new Font("Century Gothic", Font.CENTER_BASELINE, 20));

			g.drawString(lastMoveComputer, width - 340, 250);

			g.setColor(Color.black);
			g.drawString(lastMoveYou, width - 340, 280);

		}
	}

	private void drawPlayerSign(Graphics g) {
		if (isGameStarted) {
			g.drawImage(black, width - 340, 400, null);
			g.setFont(new Font("Century Gothic", Font.CENTER_BASELINE, 20));
			g.setColor(Color.black);
			g.drawString("Computer", width - 280, 430);
			g.drawImage(white, width - 340, 450, null);
			g.drawString("You", width - 280, 480);

		}
	}

	private void draw(Graphics g) {

		drawBackground(g);
		drawLogo(g);
		drawStartPlayString(g);
		drawPlayerSign(g);
		drawLastMove(g);
		drawWinStatus(g);
		drawBoard(g);
		drawPlayer(g);
		mousePointAt(g);

	}

//	renders all graphical elements
	private void render() {
		buffer = display.canvas.getBufferStrategy();
		if (buffer == null) {
			display.canvas.createBufferStrategy(3);
			return;
		}

		g = buffer.getDrawGraphics();

		g.clearRect(0, 0, width, height);

		draw(g);

		buffer.show();
		g.dispose();
	}

//	Where AI calls the optimal move
	private void play() {
		Move move;
		if (player.equals(playerCpu)) {

			if (firstMove) {
				move = game.getFirstMove();
				firstMove = false;
			} else {
				System.out.println("Thinking. . . !!");
				move = game.findOptimalMove(board, computerMoves);
			}

			board[move.row][move.col] = playerCpu;
			
			computerMoves++;

			lastMoveComputer = "Computer, " + colomToString(move.col) + (move.row + 1);

			checkWin();
			
			player = playerHuman;
		}

	}

//	Reset functionality
	public void resetGame() {
		board = game.initialiseBoard();
		isGameEnd = false;
		player = playerCpu;
		isGameStarted = false;
		firstMove = true;
	}

	@Override
	public void run() {
		init();
		board = game.initialiseBoard();

		while (true) {
			render();
			if (isGameStarted && !isGameEnd)
				play();
		}
	}

//	Assigning move to the board array according to the mouse click
	@Override
	public void mouseClicked(MouseEvent e) {

		if (!isGameEnd && player.equals(playerHuman)) {

			if (mousePointAtX < 1 || mousePointAtX > rowColom || mousePointAtY < 1 || mousePointAtY > rowColom) {
				mousePointAtX = -1;
				mousePointAtY = -1;
			}

			else {

				int row = mousePointAtX - 1;
				int col = mousePointAtY - 1;

				if (board[row][col].equals(playerEmpty)) {

					board[row][col] = "O";

					lastMoveYou = "You, " + colomToString(col) + (row + 1);

					checkWin();
					player = playerCpu;

				}
			}
		} else {
			mousePointAtX = -1;
			mousePointAtY = -1;
		}

	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mouseDragged(MouseEvent e) {
	}

	@Override
	public void mouseMoved(MouseEvent e) {

		int x = e.getX();
		int y = e.getY();

		if (!isGameEnd && player.equals(playerHuman)) {

			mousePointAtX = y / BOX_height;
			mousePointAtY = x / BOX_width;

			if (y % 50 >= 25)
				mousePointAtX++;

			if (x % 50 >= 25)
				mousePointAtY++;

			if (mousePointAtX < 1 || mousePointAtX > rowColom || mousePointAtY < 1 || mousePointAtY > rowColom) {
				mousePointAtX = -1;
				mousePointAtY = -1;
			}
		} else {
			mousePointAtX = -1;
			mousePointAtY = -1;
		}

	}

	@Override
	public void keyTyped(KeyEvent e) {

	}

	@Override
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {

		case KeyEvent.VK_SPACE:
			resetGame();
			break;
		case KeyEvent.VK_ENTER:
			isGameStarted = true;
			break;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {

	}

}
