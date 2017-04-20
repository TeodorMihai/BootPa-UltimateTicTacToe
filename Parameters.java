package bot;

public class Parameters {
	
	private int enemySq;
	private int mySq;
	private int emptySq;
	private int blockedSq;
	private int canMove;
	
	public Parameters() { }
	public Parameters(int mySq, int enemySq, int emptySq, int blockedSq, int canMove) {
		this.mySq = mySq;
		this.enemySq = enemySq;
		this.emptySq = emptySq;
		this.blockedSq = blockedSq;
		this.canMove = canMove;
	}
	public int getEnemySq() {
		return enemySq;
	}
	public void setEnemySq(int enemySq) {
		this.enemySq = enemySq;
	}
	public int getMySq() {
		return mySq;
	}
	public void setMySq(int mySq) {
		this.mySq = mySq;
	}
	public int getEmptySq() {
		return emptySq;
	}
	public void setEmptySq(int emptySq) {
		this.emptySq = emptySq;
	}
	public int getBlockedSq() {
		return blockedSq;
	}
	public void setBlockedSq(int blockedSq) {
		this.blockedSq = blockedSq;
	}
	/**
	 * inverse enemy with me
	 */
	public Parameters inverse() {
		return new Parameters(enemySq, mySq, emptySq, blockedSq, canMove);
	}
	public int getCanMove() {
		return canMove;
	}
	public void setCanMove(int canMove) {
		this.canMove = canMove;
	}
	/**
	 * In order to apply algorithms written for small squares (3x3) on macroboard.
	 * Just apply this function on param and send it to the function.
	 * @return Paramteres obj.
	 */
	public Parameters getMacroParam() {
		//eu compar cu emptySq=> emptySq trebuie sa fie canMove
		return new Parameters(mySq, enemySq, canMove, blockedSq, blockedSq);
	}
	public Parameters getMacroParamPossible() {
		return new Parameters(mySq, enemySq, emptySq ,blockedSq ,emptySq );
	}
	
}
