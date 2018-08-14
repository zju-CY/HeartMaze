import java.util.ArrayList;
import java.util.List;


public class Path {
	
	private Maze maze;
	
	eBLOCK[] directions = new eBLOCK[] {eBLOCK.UP, eBLOCK.DOWN, eBLOCK.LEFT, eBLOCK.RIGHT};
	
	public Path(Maze maze) {
		this.maze = maze;
	}
	
	public boolean find(eBLOCK[][] mazeBlock, Block in, Block out) throws Exception {
		Block current = in;
		if (current.equals(out)) {
			return true;
		}
		
		List<Block> list = new ArrayList<>();
		list.add(current.up());
		list.add(current.down());
		list.add(current.left());
		list.add(current.right());
		for (int i = 0; i < list.size(); i++) {
			Block block = list.get(i);
			
			if (block != null && mazeBlock[block.row][block.col] == eBLOCK.ROAD) {
				mazeBlock[current.row][current.col] = directions[i];
				maze.draw();
				Thread.sleep(50);
				if (find(mazeBlock, block, out))
					return true;
			}
		}
		mazeBlock[current.row][current.col] = eBLOCK.BACK;
		maze.draw();
		Thread.sleep(50);
		
		return false;
	}
}