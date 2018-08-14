import java.util.ArrayList;
import java.util.List;

import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.ansi.UnixTerminal;



public class Maze {

	static int ROW = 39;
	static int COL = 49;
	
	Block IN = new Block(2, 8);
	Block OUT = new Block(28, 38);

	private boolean[][] heart = new boolean[ROW][COL];
	private eBLOCK[][] maze = new eBLOCK[ROW][COL];
	private boolean[][] unmask = new boolean[ROW][COL];
	
	private Terminal terminal;
	
	public void init() throws Exception {
		terminal = new UnixTerminal();
		initHeart();
		initMaze();
		updateMaze();
		draw();
	}
	
	private void initHeart() {
		int row = 1, col = 0;
		for (double y = 1.2f; y >= -1.0f; y -= 0.06f) {
			for (double x = -1.2f; x <= 1.2f; x += 0.05f) {
				double temp = Math.pow(x, 2) + Math.pow(y, 2) - 1;
				if (Math.pow(temp, 3) - Math.pow(x, 2) * Math.pow(y, 3) <= 0.0)
					heart[row][col] = true;
				else
					heart[row][col] = false;
				col++;
			}
			row++; col = 0;
		}
	}

	private void initMaze() {
		for (int i = 1; i < ROW - 1; i++) {
			for (int j = 1; j < COL - 1; j++) {
				if (heart[i][j] && !(heart[i - 1][j] && heart[i + 1][j] && heart[i][j - 1] && heart[i][j + 1])) {
					maze[i][j] = eBLOCK.OUTWALL;
				} 
				else if (heart[i][j] && i % 2 == 0 && j % 2 == 0) {
					maze[i][j] = eBLOCK.ROAD;
				}
				else if (heart[i][j]) {
					maze[i][j] = eBLOCK.INWALL;
				}
				else {
					maze[i][j] =  eBLOCK.EMPTYWALL;
				}
			}
		}
		for (int i = 0; i < COL; i++) {
			maze[0][i] = eBLOCK.EMPTYWALL;
			maze[ROW-1][i] = eBLOCK.EMPTYWALL;
			unmask[0][i] = true;
			unmask[ROW-1][i] = true;
		}
		for (int i = 0; i < ROW; i++) {
			maze[i][0] = eBLOCK.EMPTYWALL;
			maze[i][COL-1] = eBLOCK.EMPTYWALL;
			unmask[i][0] = true;
			unmask[i][COL-1] = true;
		}
		
		maze[IN.row][IN.col] = eBLOCK.ROAD;
		maze[OUT.row][OUT.col] = eBLOCK.ROAD;
	}
	
	private void updateMaze() throws Exception {
		List<Block> list = new ArrayList<Block>();
		boolean[][] visited = new boolean[ROW][COL];
		list.add(IN);
		visited[IN.row][IN.col] = true;
		removeMask(IN);
		randomBFS(list, visited);
		removeOutWallMask();
	}
	
	private void randomBFS(List<Block> list, boolean[][] visited) throws Exception {
		while (list.size() != 0) {
			Block block = list.remove((int) (Math.random()*list.size()));
			int up = block.row - 2;
			int down = block.row + 2;
			int left = block.col - 2;
			int right = block.col + 2;
			int curRow = block.row;
			int curCol = block.col;
			Block temp;
			
			if (up >= 0 && maze[up][curCol] == eBLOCK.ROAD && !visited[up][curCol]) {
				maze[block.row-1][curCol] = eBLOCK.ROAD;
				visited[up][curCol] = true;
				temp = new Block(up, curCol);
				removeMask(temp);
				list.add(temp);
			}
			if (down < ROW && maze[down][curCol] == eBLOCK.ROAD && !visited[down][curCol]) {
				maze[block.row+1][curCol] = eBLOCK.ROAD;
				visited[down][curCol] = true;
				temp = new Block(down, curCol);
				removeMask(temp);
				list.add(temp);
			}
			if (left >= 0 && maze[curRow][left] == eBLOCK.ROAD && !visited[curRow][left]) {
				maze[curRow][block.col-1] = eBLOCK.ROAD;
				visited[curRow][left] = true;
				temp = new Block(curRow, left);
				removeMask(temp);
				list.add(temp);
			}
			if (right < COL && maze[curRow][right] == eBLOCK.ROAD && !visited[curRow][right]) {
				maze[curRow][block.col+1] = eBLOCK.ROAD;
				visited[curRow][right] = true;
				temp = new Block(curRow, right);
				removeMask(temp);
				list.add(temp);
			}
			draw();
			Thread.sleep(40);
		}
	}
	
	public void draw() throws Exception {
		terminal.setCursorPosition(0, 0);
		for (int i = 0; i < ROW; i++) {
			for (int j = 0; j < COL; j++) {
				if (!unmask[i][j]) {
					System.out.print(Color.ANSI_BLACK_BACKGROUND + " " + Color.ANSI_RESET);
				}
				else {
					switch (maze[i][j]) {

					case EMPTYWALL:
					case ROAD:
						System.out.print(" ");
						break;

					case OUTWALL:
					case INWALL:
						System.out.print(Color.ANSI_CYAN + "▣"+ Color.ANSI_RESET);
						break;
						
					case BACK:
						System.out.print(Color.ANSI_RED + "□"+ Color.ANSI_RESET);
						break;
						
					case UP:
						System.out.print(Color.ANSI_GREEN + "↑"+ Color.ANSI_RESET);
						break;
					case DOWN:
						System.out.print(Color.ANSI_GREEN + "↓"+ Color.ANSI_RESET);
						break;
					case LEFT:
						System.out.print(Color.ANSI_GREEN + "←"+ Color.ANSI_RESET);
						break;
					case RIGHT:
						System.out.print(Color.ANSI_GREEN + "→"+ Color.ANSI_RESET);
						break;

					default:
						break;
					}
				}
			}
			System.out.println();
		}
	}
	
	private void removeMask(Block block) {
		for (int i = block.row-1; i <= block.row+1; i++)
			for (int j = block.col-1; j <= block.col+1; j++) {
				if (i >= 0 && i < ROW && j >= 0 && j <COL) {
					unmask[i][j] = true;
				}
			}
	}
	
	private void removeOutWallMask() throws Exception {
		for (int i = 1; i < ROW - 1; i++) {
			for (int j = 1; j < COL - 1; j++) {
				if (maze[i][j] == eBLOCK.OUTWALL) {
					unmask[i][j] = true;
					draw();
					Thread.sleep(20);
				}
			}
		}
	}

	public eBLOCK[][] getMaze() {
		return maze;
	}
	
}

enum eBLOCK {
	EMPTYWALL, OUTWALL, INWALL, 
	ROAD, BACK, 
	UP, DOWN, LEFT, RIGHT
}

class Block {
	int row;
	int col;
	
	public Block(int row, int col) {
		this.row = row;
		this.col = col;
	}
	
	public Block up() {
		if (this.row - 1 >= 0) {
			return new Block(this.row-1, this.col);
		}
		return null;
	}
	public Block down() {
		if (this.row + 1 < Maze.ROW) {
			return new Block(this.row+1, this.col);
		}
		return null;
	}
	public Block left() {
		if (this.col - 1 >= 0) {
			return new Block(this.row, this.col-1);
		}
		return null;
	}
	public Block right() {
		if (this.col + 1 < Maze.COL) {
			return new Block(this.row, this.col+1);
		}
		return null;
	}
	
	@Override
	public boolean equals(Object object) {
		if (object instanceof Block) {
			Block that = (Block)object;
			return (this.row==that.row && this.col==that.col);
		}
		return false;
	}
}
