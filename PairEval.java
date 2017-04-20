package bot;

public class PairEval {
	
	private double score;
	private int ind;
	public PairEval() {
		
	}
	public PairEval(double score, int ind) {
		this.score = score;
		this.ind = ind;
	}
	public double getScore() {
		return score;
	}
	public void setScore(double score) {
		this.score = score;
	}
	public int getInd() {
		return ind;
	}
	public void setInd(int ind) {
		this.ind = ind;
	}

}
