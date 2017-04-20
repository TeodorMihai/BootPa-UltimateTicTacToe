package bot;

import java.util.ArrayList;
import java.util.List;

public class Square {

	private int[][] square = new int[3][3];
	

	public Square() { 
		square = new int[3][3];
	}
	
	public Square(int[][] sq) {

		for (int i = 0; i < 3; ++i)
			for (int j = 0; j < 3; ++j)
				this.square[i][j] = sq[i][j];
	}
	public Square(Square sq) {
		
		for(int i = 0 ; i < 3; ++i)
			for(int j = 0 ; j < 3; ++j)
				square[i][j] = sq.getValue(i, j);
	}
	
	public void setSquare(Square sq) {
		
		for(int i = 0 ; i < 3; ++i)
			for(int j = 0 ; j < 3; ++j)
				this.square[i][j] = sq.getValue(i, j);
	}
	/**
	 * Put value on field (x, y)
	 * 
	 * @param x
	 *            line
	 * @param y
	 *            coloumn
	 * @param value
	 */
	public void setValue(int x, int y, int value) {
		square[x][y] = value;
	}
	public int getValue(int x, int y) {
		return square[x][y];
	}
	public int getValue(Move m) {
		return square[m.getX()][m.getY()];
	}
	/**
	 * Put at move (pair of coordinates in [0, 2] ) the value.
	 * 
	 * @param move
	 *            pair of coordinates [0,2]
	 * @param value
	 */
	public void setValue(Move move, int value) {
		square[move.getX()][move.getY()] = value;
	}
	public List<Move> getAllEmptySquares(Parameters param) {
		
		List<Move> allEmpty = new ArrayList<Move>();
		
		for(int i = 0 ; i < 3; ++i)
			for(int j = 0 ; j < 3; ++j)
				if(square[i][j] == param.getEmptySq())
					allEmpty.add(new Move(i, j));
		
		return allEmpty;	
	}
	/**
	 * index line
	 * 
	 * @param index
	 * @return Line object
	 */
	public Line getLine(int index) {

		Line line = new Line();

		for (int j = 0; j < 3; ++j)
			line.setValue(j, square[index][j]);

		return line;
	}
	/**
	 * Get column number index
	 * 
	 * @param index
	 * @return Line object
	 */
	public Line getColumn(int index) {

		Line line = new Line();

		for (int i = 0; i < 3; ++i)
			line.setValue(i, square[i][index]);

		return line;
	}
	/**
	 * Return one of the 2 diagonals
	 * 
	 * @param diagonalType
	 *            1 for first diagonal (0,0) -> (2, 2) and -1 for the othe (0,
	 *            2) -> (2, 0)
	 * @return Line object
	 */
	public Line getDiagonal(int diagonalType) {

		Line line = new Line();

		if (diagonalType == 1) {

			for (int i = 0; i < 3; ++i)
				line.setValue(i, square[i][i]);

		} else {

			for (int i = 0; i < 3; ++i)
				line.setValue(i, square[i][2 - i]);
		}

		return line;
	}
	/**
	 * Get list of moves of squares which have to be conquered in order to win
	 * the big square.
	 * 
	 * @param param
	 *            -id's of players.
	 * @return List of moves in order to conquer.
	 */
	public List<Move> getNextWinningSquareMoves(Parameters param) {

		List<Move> winningMoves = new ArrayList<Move>(3);

		for (int i = 0; i < 3; i++) {

			Line curentLine = getLine(i);
			int index = curentLine.getNextWinLineIndex(param);

			if (index != -1)
				winningMoves.add(new Move(i, index));
		}

		for (int j = 0; j < 3; j++) {

			Line column = getColumn(j);
			int index = column.getNextWinLineIndex(param);

			if (index != -1)
				winningMoves.add(new Move(index, j));
		}

		// first diagonal
		Line diag = getDiagonal(1);
		int index = diag.getNextWinLineIndex(param);

		if (index != -1)
			winningMoves.add(new Move(index, index));

		// second diagonal
		diag = getDiagonal(-1);
		index = diag.getNextWinLineIndex(param);
		if (index != -1)
			winningMoves.add(new Move(index, 2 - index));

		return winningMoves;
	}
	/**
	 * @return A list with all possible lines.
	 */
	public List<Line> getAllthreeInARow() {

		List<Line> allLines = new ArrayList<Line>(8);

		// lines
		for (int i = 0; i < 3; ++i)
			allLines.add(getLine(i));

		// columns
		for (int i = 0; i < 3; ++i)
			allLines.add(getColumn(i));

		// diagonals
		allLines.add(getDiagonal(-1));
		allLines.add(getDiagonal(1));

		return allLines;
	}
	/**
	 * 
	 * @return id of the player who won the square, param.getEmpty() otherwise
	 */
	public int squareWonBy(Parameters param) {

		List<Line> allLines = getAllthreeInARow();

		for (Line line : allLines) {

			if (line.isWon(param))
				return line.isWonBy(param);

			if (line.isWon(param.inverse()))
				return line.isWonBy(param.inverse());
		}

		return param.getEmptySq();
	}
	/**
	 * 
	 * @param param
	 * @return true if square is draw
	 */
	public boolean checkIfDraw(Parameters param) {
	
		for (int i = 0; i <= 2; i++) 
			for (int j = 0; j <= 2; j++) 
				if (square[i][j] == param.getEmptySq())
					return false;
		return true;
		
	}
	/**
	 * Try a move in order to see if forms an open line.
	 * @param param me and the enemy
	 * @param move Move to be tried., has to be a valid move. (the square has to be empty.)
	 * @return 0 if the move doesn't form an open move, the number of open lines formed otherwise.
	 */
	public int tryMoveforOpenLine(Parameters param, Move move) {
		
		square[move.getX()][move.getY()] = param.getMySq();//let's say that we already have the small square
		List<Move> winMoves = getNextWinningSquareMoves(param);
		
		square[move.getX()][move.getY()] = param.getEmptySq();//restore the small square
		
		return winMoves.size();
	}
	/**
	 * Get a list of objects of type Move which represents the moves that can be
	 * done inside the square in order to obtain a "open line".
	 * open line = if you have an open line next turn you can win the square.
	 * @param param me or enemy turn
	 * @return List of objects of type Move.
	 */
	public List<Move> getNextOpenMoves(Parameters param) {
		
		List<Move> openMoves = new ArrayList<Move>();
		
		for(int i = 0 ; i < 3; ++i)
			for(int j = 0; j < 3; ++j)
				if(square[i][j] == param.getEmptySq()) {
					
					int oportunities = tryMoveforOpenLine(param, new Move(i, j) );
					
					if(oportunities > 0)
						openMoves.add(new Move(i, j));
				}
		
		return openMoves;
	}
	
	public void printSquare(Parameters param) {
		
		for(int i = 0 ; i < 3; ++i) {
			for(int j = 0 ; j < 3; ++j)
				if(square[i][j] == param.getMySq())
					System.out.print("x ");
				else if(square[i][j] == param.getEnemySq())
					System.out.print("o ");
				else System.out.print(". " );
				
			System.out.println();
		}
	}
	
	public int countInARow(Move move, Parameters p, int row) {
		
		int x = move.getX();
		int y = move.getY();
		
		int count = 0;
		
		//is the same if its blocked or enemy
		
		if(move.isCorner()) {
			
			int cnt = 0;
			
			for(int i = 0; i < 3; ++i)
				if(square[x][i] == p.getMySq())
					cnt++;
				else if(square[x][i] == p.getEnemySq() || square[x][i] == p.getBlockedSq())
					cnt -= 3;
			
			if(cnt == row)
				count++;
			
			cnt = 0;
			
			for(int i = 0; i < 3; ++i)
				if(square[i][y] == p.getMySq())
					cnt++;
				else if(square[i][y] == p.getEnemySq() || square[i][y] == p.getBlockedSq())
					cnt -= 3;
			
			if(cnt == row)
				count++;
			
			if((x == 0 && y == 0) || (x == 2 && y == 2)) {
				
				cnt = 0;
				
				for(int i = 0 ; i < 3; ++i)
					if(square[i][i] == p.getMySq())
						cnt++;
					else if(square[i][i] == p.getEnemySq() || square[i][i] == p.getBlockedSq())
						cnt -= 3;
				
				if(cnt == row)
					count++;
			} else {
				
				cnt = 0;
				
				for(int i = 0 ; i < 3; ++i)
					if(square[2 - i][i] == p.getMySq())
						cnt++;
					else if(square[2 - i][i] == p.getEnemySq() || square[2 - i][i] == p.getBlockedSq())
						cnt -= 3;
				
				if(cnt == row)
					count++;
			}
			
			return count;	
			
		} else if(move.isMargin()) {
			
			int cnt = 0;
			
			for(int i = 0 ; i < 3; ++i)
				if(square[x][i] == p.getMySq())
					cnt++;
				else if(square[x][i] == p.getEnemySq() || square[x][i] == p.getBlockedSq())
					cnt -= 3;
			
			if(cnt == row)
				count++;
			
			cnt = 0;

			for(int i = 0 ; i < 3; ++i)
				if(square[i][y] == p.getMySq())
					cnt++;
				else if(square[i][y] == p.getEnemySq() || square[i][y] == p.getBlockedSq())
					cnt -= 3;
			
			if(cnt == row)
				count++;
			
			return count;
		} else  {
			
			int cnt = 0;
			
			for(int i = 0; i < 3; ++i)
				if(square[i][y] == p.getMySq())
					cnt++;
				else if(square[i][y] == p.getEnemySq() || square[i][y] == p.getBlockedSq())
					cnt -= 3;
			
			if(cnt == row)
				count++;
			
			cnt = 0;
			
			for(int i = 0 ; i < 3; ++i)
				if(square[x][i] == p.getMySq())
					cnt++;
				else if(square[x][i] == p.getEnemySq() || square[x][i] == p.getBlockedSq())
					cnt -= 3;
			
			if(cnt == row)
				count++;
			
			cnt = 0;
			
			for(int i = 0 ; i < 3; ++i)
				if(square[i][i] == p.getMySq())
					cnt++;
				else if(square[i][i] == p.getEnemySq() || square[i][i] == p.getBlockedSq())
					cnt -= 3;
			
			if(cnt == row)
				count++;
			
			cnt = 0;
			
			for(int i = 0 ; i < 3; ++i)
				if(square[i][2 - i] == p.getMySq())
					cnt++;
				else if(square[i][2 - i] == p.getEnemySq() || square[i][2 - i] == p.getBlockedSq())
					cnt -= 3;
			
			if(cnt == row)
				count++;
			
			return count;
		}
		
	}
	

	public double evalMinSquare2(int x, int y, Parameters param, int wonBy, Square macroBoard) {
		
		double score = 0;
		double maxPointsSquare = 90;
		double keyLine = 11;
		double openLine = 3.6;
		double multiply = 3.1;
		double notSoKeySq = 0.35; 
		
		int mycntKeyLines = 0;
		int mycntKeyBlockedLines = 0;
		int enemycntKeyLines = 0;
		int enemycntBlockedLines = 0;
		
		int myOpenLines = 0;
		int enemyOpenLines = 0;
		
	
		int myKeySq = 0;
		int enemyKeySq = 0;
		
		if (wonBy == param.getMySq())
			return maxPointsSquare;
		else if(wonBy == param.getEnemySq())
			return -maxPointsSquare;
		else if(wonBy == param.getBlockedSq())
			return 0;
		else {
			
			
			//linii
			for(int i = 0 ; i < 3; ++i) {
				//line i
				
				PairEval p = analyze3(square[i][0], square[i][1], square[i][2], param);
				Move keyBig = new Move(i, p.getInd());
				
				if(p.getScore() == 2) {
					if( (macroBoard.getValue(keyBig) == param.getEmptySq() || macroBoard.getValue(keyBig) == param.getCanMove()) && ( x != keyBig.getX() || y != keyBig.getY()) ) 
						mycntKeyLines++;
					else 
						mycntKeyBlockedLines++;
					} else if(p.getScore() == -2) {
							if( (macroBoard.getValue(keyBig) == param.getEmptySq() || macroBoard.getValue(keyBig) == param.getCanMove()) && ( x != keyBig.getX() || y != keyBig.getY() ) ) 	
								enemycntKeyLines++;
							else 
								enemycntBlockedLines++;
					} else if(p.getScore() > 0)
						myOpenLines++;
					else if(p.getScore() < 0)
						enemyOpenLines++;
			} 
			
			//coloane
			
			for(int j = 0 ; j < 3; ++j) {
				

				PairEval p = analyze3(square[0][j], square[1][j], square[2][j], param);
				Move keyBig = new Move(p.getInd() , j);
				
				if(p.getScore() == 2) {
					if( (macroBoard.getValue(keyBig) == param.getEmptySq() || macroBoard.getValue(keyBig) == param.getCanMove()) && ( x != keyBig.getX() || y != keyBig.getY()) ) 
						mycntKeyLines++;
					else 
						mycntKeyBlockedLines++;
					} else if(p.getScore() == -2) {
							if( (macroBoard.getValue(keyBig) == param.getEmptySq() || macroBoard.getValue(keyBig) == param.getCanMove())  && ( x != keyBig.getX() || y != keyBig.getY() ) ) 	
								enemycntKeyLines++;
							else 
								enemycntBlockedLines++;
					} else if(p.getScore() > 0)
						myOpenLines++;
					else if(p.getScore() < 0)
						enemyOpenLines++;
				
			}
			
			
			//diagonale
			
			PairEval p = analyze3(square[0][0], square[1][1], square[2][2], param);
			Move keyBig = new Move(p.getInd() , p.getInd());
			
			if(p.getScore() == 2) {
				if( (macroBoard.getValue(keyBig) == param.getEmptySq() || macroBoard.getValue(keyBig) == param.getCanMove()) && ( x != keyBig.getX() || y != keyBig.getY()) ) 
					mycntKeyLines++;
				else 
					mycntKeyBlockedLines++;
				} else if(p.getScore() == -2) {
						if( (macroBoard.getValue(keyBig) == param.getEmptySq() || macroBoard.getValue(keyBig) == param.getCanMove())  && ( x != keyBig.getX() || y != keyBig.getY() ) ) 	
							enemycntKeyLines++;
						else 
							enemycntBlockedLines++;
				} else if(p.getScore() > 0)
						myOpenLines++;
					else if(p.getScore() < 0)
						enemyOpenLines++;
			
			//digonala 2
			p = analyze3(square[0][2], square[1][1], square[2][0], param);
			keyBig = new Move(p.getInd() , 2 - p.getInd());
			
			if(p.getScore() == 2) {
				if( (macroBoard.getValue(keyBig) == param.getEmptySq() || macroBoard.getValue(keyBig) == param.getCanMove()) && ( x != keyBig.getX() || y != keyBig.getY()) ) 
					mycntKeyLines++;
				else 
					mycntKeyBlockedLines++;
				} else if(p.getScore() == -2) {
						if( (macroBoard.getValue(keyBig) == param.getEmptySq() || macroBoard.getValue(keyBig) == param.getCanMove()) && ( x != keyBig.getX() || y != keyBig.getY() ) ) 	
							enemycntKeyLines++;
						else 
							enemycntBlockedLines++;
				} else if(p.getScore() > 0)
					myOpenLines++;
				else if(p.getScore() < 0)
					enemyOpenLines++;
		}
					
		//log(1) = 0
		//log(2) = 0.69
		//log(3) = 1.09
		score += Math.log(mycntKeyLines * (1 + notSoKeySq)  + mycntKeyBlockedLines * notSoKeySq + 1) * keyLine;
		score -= Math.log(enemycntKeyLines * (1 + notSoKeySq) + enemycntBlockedLines * notSoKeySq  + 1) * keyLine;
		
		//score += Math.log(myOpenLines + 1) * openLine;
		//score -= Math.log(enemyOpenLines + 1) * openLine;
		
		//score += BotStarter.lgg[mycntKeyLines  + mycntKeyBlockedLines + 1] * keyLine;
		//score -= BotStarter.lgg[enemycntKeyLines + enemycntBlockedLines  + 1] * keyLine;
		
		score += BotStarter.lgg[myOpenLines + 1] * openLine;
		score -= BotStarter.lgg[enemyOpenLines + 1] * openLine;
		
		return score * multiply;
	}
	
	public PairEval analyze3(int x1, int x2, int x3, Parameters param) {
		
		int cntMy = 0;
		int  cntEnemy = 0;
		int ind = 0;
		
		if(x1 == param.getMySq()) {
			cntMy++;
		} else if(x1 == param.getEnemySq()) {
			cntEnemy++;
		} else ind = 0;
		
		if(x2 == param.getMySq()) {
			cntMy++;
		} else if(x2 == param.getEnemySq()) {
			cntEnemy++;
		} else  ind = 1;
		
		if(x3 == param.getMySq()) {
			cntMy++;
		} else if(x3 == param.getEnemySq()) {
			cntEnemy++;
		} else ind = 2;
		
		
		
		if(cntEnemy == 0 && cntMy == 0)
			return new PairEval(0, -1);
		else  {
			
			if(cntEnemy > 0) {
				if(cntMy > 0)//e blocata linia nu o poate lua nimeni
					return new PairEval(0, -1);
				else if(cntEnemy == 2)
					return new PairEval(-2, ind);
				else 
					return new PairEval(-1, -1);
			} else if(cntMy == 2)
					return new PairEval(2, ind);
			else return new PairEval(1, -1);
		}
	}
	
	
	public PairEval analyze3Macro(int x1, int x2, int x3, Parameters param) {
		
		int cntMy = 0;
		int cntEnemy = 0;
		int ind = 0;
		int full = 0;
		
		if(x1 == param.getMySq()) {
			full = 0;
			cntMy++;
		} else if(x1 == param.getEnemySq()) {
			cntEnemy++;
			full = 0;
		} else if(x1 == param.getBlockedSq()) {
			return new PairEval(5, 0);
		} else ind = 0;
		
		if(x2 == param.getMySq()) {
			cntMy++;
			full = 1;
		} else if(x2 == param.getEnemySq()) {
			cntEnemy++;
			full = 1;
		} else if(x2 == param.getBlockedSq())
			return new PairEval(5, 0);
		else 
			ind = 1;
		
		if(x3 == param.getMySq()) {
			cntMy++;
			full = 2;
		} else if(x3 == param.getEnemySq()) {
			full = 2;
			cntEnemy++;
		} if(x3 == param.getBlockedSq())
			return new PairEval(5, 0);
		else ind = 2;
		
		
		
		if(cntEnemy == 0 && cntMy == 0)//e libera
			return new PairEval(4, 0);
		else  {
			
			if(cntEnemy > 0) {
				if(cntMy > 0)//e blocata linia nu o poate lua nimeni
					return new PairEval(0, 0);
				else if(cntEnemy == 2)
					return new PairEval(-2, ind);
				else 
					return new PairEval(-1, full);
			} else if(cntMy == 2)
					return new PairEval(2, ind);
			else return new PairEval(1, full);
		}
	}

	public double evalMinSquare1(int x, int y, Parameters param, int wonBy, Square macroBoard) {
		
		double score = 0;
		double maxPointsSquare = 90;
		double keyLine = 11;
		double openLine = 3.6;
		double multiply = 3.1;
		double notSoKeySq = 0.35; 
		
		
		int myOpenLines = 0;
		int enemyOpenLines = 0;
		
		int mycntKeyLines = 0;
		int mycntKeyBlockedLines = 0;
		int enemycntKeyLines = 0;
		int enemycntBlockedLines = 0;
	
		
	
		int myKeySq = 0;
		int enemyKeySq = 0;
		
		if (wonBy == param.getMySq())
			return maxPointsSquare;
		else if(wonBy == param.getEnemySq())
			return -maxPointsSquare;
		else if(wonBy == param.getBlockedSq())
			return 0;
		else {
			
			
			//linii
			for(int i = 0 ; i < 3; ++i) {
				//line i
				
				PairEval p = analyze3(square[i][0], square[i][1], square[i][2], param);
				Move keyBig = new Move(i, p.getInd());
				
				if(p.getScore() == 2) {
					if( (macroBoard.getValue(keyBig) == param.getEmptySq() || macroBoard.getValue(keyBig) == param.getCanMove()) && ( x != keyBig.getX() || y != keyBig.getY()) ) 
						mycntKeyLines++;
					else 
						mycntKeyBlockedLines++;
					} else if(p.getScore() == -2) {
							if( (macroBoard.getValue(keyBig) == param.getEmptySq() || macroBoard.getValue(keyBig) == param.getCanMove()) && ( x != keyBig.getX() || y != keyBig.getY() ) ) 	
								enemycntKeyLines++;
							else 
								enemycntBlockedLines++;
					} else if(p.getScore() > 0)
						myOpenLines++;
					else if(p.getScore() < 0)
						enemyOpenLines++;
			} 
			
			//coloane
			
			for(int j = 0 ; j < 3; ++j) {
				

				PairEval p = analyze3(square[0][j], square[1][j], square[2][j], param);
				Move keyBig = new Move(p.getInd() , j);
				
				if(p.getScore() == 2) {
					if( (macroBoard.getValue(keyBig) == param.getEmptySq() || macroBoard.getValue(keyBig) == param.getCanMove()) && ( x != keyBig.getX() || y != keyBig.getY()) ) 
						mycntKeyLines++;
					else 
						mycntKeyBlockedLines++;
					} else if(p.getScore() == -2) {
							if( (macroBoard.getValue(keyBig) == param.getEmptySq() || macroBoard.getValue(keyBig) == param.getCanMove())  && ( x != keyBig.getX() || y != keyBig.getY() ) ) 	
								enemycntKeyLines++;
							else 
								enemycntBlockedLines++;
					} else if(p.getScore() > 0)
						myOpenLines++;
					else if(p.getScore() < 0)
						enemyOpenLines++;
				
			}
			
			
			//diagonale
			
			PairEval p = analyze3(square[0][0], square[1][1], square[2][2], param);
			Move keyBig = new Move(p.getInd() , p.getInd());
			
			if(p.getScore() == 2) {
				if( (macroBoard.getValue(keyBig) == param.getEmptySq() || macroBoard.getValue(keyBig) == param.getCanMove()) && ( x != keyBig.getX() || y != keyBig.getY()) ) 
					mycntKeyLines++;
				else 
					mycntKeyBlockedLines++;
				} else if(p.getScore() == -2) {
						if( (macroBoard.getValue(keyBig) == param.getEmptySq() || macroBoard.getValue(keyBig) == param.getCanMove())  && ( x != keyBig.getX() || y != keyBig.getY() ) ) 	
							enemycntKeyLines++;
						else 
							enemycntBlockedLines++;
				} else if(p.getScore() > 0)
						myOpenLines++;
					else if(p.getScore() < 0)
						enemyOpenLines++;
			
			//digonala 2
			p = analyze3(square[0][2], square[1][1], square[2][0], param);
			keyBig = new Move(p.getInd() , 2 - p.getInd());
			
			if(p.getScore() == 2) {
				if( (macroBoard.getValue(keyBig) == param.getEmptySq() || macroBoard.getValue(keyBig) == param.getCanMove()) && ( x != keyBig.getX() || y != keyBig.getY()) ) 
					mycntKeyLines++;
				else 
					mycntKeyBlockedLines++;
				} else if(p.getScore() == -2) {
						if( (macroBoard.getValue(keyBig) == param.getEmptySq() || macroBoard.getValue(keyBig) == param.getCanMove()) && ( x != keyBig.getX() || y != keyBig.getY() ) ) 	
							enemycntKeyLines++;
						else 
							enemycntBlockedLines++;
				} else if(p.getScore() > 0)
					myOpenLines++;
				else if(p.getScore() < 0)
					enemyOpenLines++;
		}
					
		//log(1) = 0
		//log(2) = 0.69
		//log(3) = 1.09
		score += Math.log(mycntKeyLines * (1 + notSoKeySq)  + mycntKeyBlockedLines * notSoKeySq + 1) * keyLine;
		score -= Math.log(enemycntKeyLines * (1 + notSoKeySq) + enemycntBlockedLines * notSoKeySq  + 1) * keyLine;
		
		//score += Math.log(myOpenLines + 1) * openLine;
		//score -= Math.log(enemyOpenLines + 1) * openLine;
		
		//score += BotStarter.lgg[mycntKeyLines  + mycntKeyBlockedLines + 1] * keyLine;
		//score -= BotStarter.lgg[enemycntKeyLines + enemycntBlockedLines  + 1] * keyLine;
		
		score += BotStarter.lgg[myOpenLines + 1] * openLine;
		score -= BotStarter.lgg[enemyOpenLines + 1] * openLine;
		
		return score * multiply;
	}
	
}
	