package bot;

class PairNew {
	
	public int depth;
	public double score;
	
	PairNew(int d, double s) {
		depth = d;
		score = s;
	}
	
	public int getDepth() {
		return depth;
	}
	
	public double getScore() {
		return score;
	}
	
	public void setDepth(int d) {
		depth = d;
	}
	
	public void setScore(double s) {
		score = s;
	}
	
}