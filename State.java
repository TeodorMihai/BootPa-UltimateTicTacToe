package bot;

public class State {
	
	private Board board;
	private Move move;
	private double score;
	
	public Move getMove() {
		return move;
	}
	public void setMove(Move move) {
		this.move = move;
	}
	public Board getBoard() {
		return board;
	}
	public void setBoard(Board board) {
		this.board = board;
	}
	public double getScore() {
		return score;
	}
	public void setScore(double d) {
		this.score = d;
	}

}
