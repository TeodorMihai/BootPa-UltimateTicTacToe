package bot;

public class Pair {
	private Move move;
	private double score;
	
	public Pair() {
		
	}
	public Pair(double d, Move move) {
		this.score = d;
		this.move = move;
	}
	
	public Pair(Pair x) {
		this.score = x.score;
		this.move = new Move(x.move.getX(), x.move.getY());
	}
	
	public double first(){
		return score;
	}
	public Move second(){
		return move;
	}
}
