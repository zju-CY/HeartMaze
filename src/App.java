
public class App {
	static Maze maze = new Maze();
	static Path path = new Path(maze);
	public static void main(String[] args) throws Exception {
		 maze.init();
		 path.find(maze.getMaze(), maze.IN, maze.OUT);
	}
}
