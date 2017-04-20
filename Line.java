package bot;

public class Line {
	
	private int[] line = new int[3];
	
	public Line() { }
	
	/**
	 * Construct a line as follows(first, second, third)
	 * @param first
	 * @param second
	 * @param third
	 */
	public Line(int first, int second, int third) {
		
		line[0] = first;
		line[1] = second;
		line[2] = third;
	}
	/**
	 * Returns a line identical to the argument given
	 * @param line
	 */
	public Line(int[] line) {
		
		
		for(int i = 0 ; i < 3; ++i)
			this.line[i] = line[i];
			
	}
	/**
	 * Construct a copy of the argument
	 * @param line lien must of type Line
	 */
	public Line(Line line) {
		
		for(int i = 0 ; i < 3; ++i)
			this.line[i] = line.getValue(i);
	}
	/**
	 * get the line[index]
	 * @param index
	 */
	public int getValue(int index) {
		return this.line[index];
	}
	public void setValue(int index, int value) {
		this.line[index] = value;
	}
	/**
	 * How many squares you have to conquer till you win the line.
	 * @param parametes for evaluating the line.
	 * @return 4 if you cannot win the the line
	 * else [0..3]
	 */
	public int squaresToWinLine(Parameters param) {
		
		int cnt = 0;
		
		for(int i = 0 ; i < 3; ++i) {
			
			if(line[i] == param.getMySq())
				cnt++;
			
			if(line[i] == param.getEnemySq())
				return 4;
			
			if(line[i] == param.getBlockedSq())
				return 4;
		}
		
		return cnt;
	}
	/**
	 * 
	 * @param param
	 * @return true if myBot owns the line.
	 */
	
	/**
	 * 0 daca linia nu mai poate fi castigata, sau daca nimeni nu a pus nimic pe ea, returneaza 0
	 * @param param
	 * @return
	 */
	public double analyzeLine(Parameters param) {
		
		double cntMy = 0;
		double cntEnemy = 0;
		int ind = 0;
		
		for(int i = 0 ; i < 3; ++i) {
			
			if(line[i] == param.getMySq())
				cntMy++;
			else if(line[i] == param.getEnemySq())
					cntEnemy++;
			else ind = i;
		}
		
		if(cntEnemy > 0) {
			if(cntMy > 0)//e blocata linia nu o poate lua nimeni
				return 0;
			else 
				return cntMy - cntEnemy;
		} else 
			return cntMy - cntEnemy;
		
	}
	public boolean isWon(Parameters param) {
		
		if(squaresToWinLine(param) == 3)
			return true;
		return false;
	}
	/**
	 * 
	 * @param param 
	 * @return return id of the players who owns the square, -1 otherwise 
	 */
	public int isWonBy(Parameters param) {
		
		if(isWon(param))
			return line[0];
		return -1;
	}
	/**
	 * Find the winning index, if one
	 * @param param 
	 * @return the index of the line which can be taken in order the win the line
	 */
	
	public int getNextWinLineIndex(Parameters param) {
			
		if(line[0] == param.getMySq() && line[1] == param.getMySq() && line[2] == param.getEmptySq())
			return 2;
		
		if(line[0] == param.getMySq() && line[1] == param.getEmptySq() && line[2] == param.getMySq())
			return 1;
		
		if(line[0] == param.getEmptySq() && line[1] == param.getMySq() && line[2] == param.getMySq())
			return 0;
			
		return -1;
	}
	
}
