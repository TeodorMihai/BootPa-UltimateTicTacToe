package bot;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;


public class Board {
	
	private Square[][] board = new Square[3][3];
	private Square macroBoard = new Square();
	private List<Move> availableMoves = new ArrayList<Move>(90);
	private Parameters parameters = new Parameters();
	private List<Move> availableMacroMoves = new ArrayList<Move>(10);
	public Deque<Boolean> previous = new ArrayDeque<Boolean>(); 
	
	private void allocBoard() {
		for(int i = 0 ; i < 3; ++i)
			for(int j = 0 ; j < 3; ++j)
				board[i][j] = new Square();
	}
	
	public Board() { 
		
		allocBoard();
		this.previous.addLast(true);
	}
	/**
	 * Copy constructor, return a Board object.
	 * @param board
	 * @param macroBoard
	 */
	public Board(Square[][] board, Square macroBoard) {
		allocBoard();
		this.setBoard(board);
		this.setMacroBoard(macroBoard);
		this.availableMoves = computeAvailableMoves();
		this.availableMacroMoves = computeAvailableMacroMoves();
		this.previous.addLast(true);
	}
	public Board(Square[][] board, Square macroBoard, Parameters param) {
		allocBoard();
		this.setBoard(board);
		this.setMacroBoard(macroBoard);
		this.availableMoves = computeAvailableMoves();
		this.availableMacroMoves = computeAvailableMacroMoves();
		this.setParameters(param);
		previous.addLast(true);
	}
	public Board(Square[][] board, Square macroBoard, List<Move> avMoves) {
		allocBoard();
		this.setBoard(board);
		this.setMacroBoard(macroBoard);
		this.setAvailableMoves(avMoves);
	}
	public Board(Square[][] board, Square macroBoard, List<Move> avMoves, List<Move> macroMoves) {
		allocBoard();
		this.setBoard(board);
		this.setMacroBoard(macroBoard);
		this.setAvailableMoves(avMoves);
		this.setAvailablesMacroMoves(macroMoves);
	}
	public Board(Square[][] board, Square macroBoard, List<Move> avMoves, List<Move> macroMoves, Parameters param ) {
		allocBoard();
		this.setBoard(board);
		this.setMacroBoard(macroBoard);
		this.setAvailableMoves(avMoves);
		this.setAvailablesMacroMoves(macroMoves);
		this.setParameters(param);
	}
	public Parameters getParameters() {
		return parameters;
	}
	public void setParameters(Parameters param) {
		this.parameters.setBlockedSq(param.getBlockedSq());
		this.parameters.setCanMove(param.getCanMove());
		this.parameters.setEmptySq(param.getEmptySq());
		this.parameters.setEnemySq(param.getEnemySq());
		this.parameters.setMySq(param.getMySq());
	}
	public Square[][] getBoard() {
		return board;
	}
	public void setBoard(Square[][] board) {
		
		for(int i = 0 ; i < 3; ++i)
			for(int j = 0 ; j < 3; ++j)
				this.board[i][j].setSquare(board[i][j]);
	}
	public Square getMacroBoard() {
		return macroBoard;
	}
	public void setMacroBoard(Square macroBoard) {
		this.macroBoard.setSquare(macroBoard);
	}
	public int getValueInMacroBord(int x, int y) {
		return macroBoard.getValue(x, y);
	}
	public int getValueInMacroBoard(Move m) {
		return macroBoard.getValue(m);
	}
	public int getValueInBoard(int x, int y) {
		return board[x / 3][y / 3].getValue(x % 3, y % 3);
	}
	public int getValueInBoard(Move m) {
		return board[m.getX() / 3][m.getY() / 3].getValue(m.getX() % 3, m.getY() % 3);
	}
	public void setValueInBoard(int x, int y, int value) {
		board[x/3][y/3].setValue(x % 3, y % 3, value);
	}
	public void setValueInMacroBoard(int x, int y, int value) {
		macroBoard.setValue(x, y, value);
	}
	/**
	 * Get the square(3x3) which is specified with coordinates in the macroBoard.
	 * @param x coordinate in macroboard.
	 * @param y coordinate in macroboard.
	 */
	public Square getSubSquareAt(int x, int y) {
		
		Square subSq = new Square();
		
		for(int i = 0 ; i < 3; ++i)
			for(int j = 0 ;j < 3; ++j)
				subSq.setValue(i, j, getValueInBoard(x * 3 + i, y * 3 + j));
		 
		return subSq;
	}
	/**
	 * All moves the can be done in the current state.
	 * @return A list of Move objects.
	 */
	public List<Move> getAvailableMoves() {
		return availableMoves;
	}
	public List<Move> getAvailablesMacroMoves() {
		return this.availableMacroMoves;
	}
	public void setAvailablesMacroMoves(List<Move> avMacroMoves) {
		
		this.availableMacroMoves.clear();
		
		for(Move m : avMacroMoves)
			this.availableMacroMoves.add(m);
	}
	public List<Move> computeAvailableMoves() {
		
		
		List<Move> avMoves = new ArrayList<Move>();
		
		for(int i = 0 ; i < 3; ++i)
			for(int j = 0 ; j < 3; ++j) {
				
				if(getValueInMacroBord(i, j) == parameters.getCanMove() )  {
					
					List<Move> relative = new ArrayList<Move>();
					relative = board[i][j].getAllEmptySquares(this.parameters);
					
					for(Move m : relative) 
						avMoves.add( m.makeItAbsolute(new Move(i, j)));
					
				}
			}
		
		
		return avMoves;
	}
	public List<Move> computeAvailableMacroMoves() {
		
		List<Move> macroMoves = new ArrayList<Move>();
		
		for(int i = 0 ; i < 3; ++i)
			for(int j = 0 ; j < 3; ++j)
				if(macroBoard.getValue(i, j) == parameters.getCanMove())
					macroMoves.add(new Move(i, j));
		
		return macroMoves;
	}
	public void setAvailableMoves(List<Move> availableMoves) {
		
		this.availableMoves.clear();
		
		for(Move m : availableMoves)
			this.availableMoves.add(m);
	}
	/**
	 * Returns a list of Move objects representing the absolute positions
	 * of the next moves which can be done in order to win a 3x3 square (any 3x3 square).
	 * Often is just one move or none.
	 * @param param
	 * @return A list of Move objects.
	 */
	public List<Move> getAllWinningSquareMoves(Parameters param) {
		
		List<Move> winningMoves = new ArrayList<Move>();
		
		for(int i = 0 ; i < 3; ++i)
			for(int j = 0 ;j < 3; ++j)
				if(getValueInMacroBord(i, j) == param.getCanMove()) {
	
					List<Move> forSq =  getSubSquareAt(i, j).getNextWinningSquareMoves(param);
					
					//relative moves to (i, j) square, make it absolute
					for(Move m : forSq)
						winningMoves.add(m.makeItAbsolute(new Move(i, j)));
				}
					
		return winningMoves;
	}
	/**
	 * Get a list of all winning moves which can be done at this turn.
	 * @param param
	 * @return A list of Move objects.
	 */
	public List<Move> getAllWiningGameMoves(Parameters param) {
		
		/* if I win any of this bigSqaures I win the game */
		List<Move> winBigSqaures = macroBoard.getNextWinningSquareMoves(param.getMacroParam());
	
		/* if I win any of these small squares I win the game */
		List<Move> winSmallSquares = new ArrayList<Move>();
		
		for(Move m : winBigSqaures) {
			
			List<Move> relativeSq = board[m.getX()][m.getY()].getNextWinningSquareMoves(param);
			
			for(Move relMove : relativeSq)
				winSmallSquares.add(relMove.makeItAbsolute(m));
		}

		return winSmallSquares;
	}
	/**
	 * Get a list of Moves object which represent all possible moves to make an open-line
	 * @param param
	 * @return A list of Move object.
	 */
	public List<Move> getAllOpenLineMoves(Parameters param) {
		
		List<Move> openMoves = new ArrayList<Move>();
		
		for(int i = 0 ; i < 3; ++i)
			for(int j = 0 ; j < 3; ++j) {
				
				if(macroBoard.getValue(i, j) == param.getCanMove()) {
					
					List<Move> openRelative = board[i][j].getNextOpenMoves(param);
					
					for(Move m : openRelative)
						openMoves.add( m.makeItAbsolute(new Move(i, j)));
				}
			}
		
		return openMoves;			
	}
	/**
	 * Computes the next state based on the valid move send as argument with all parameters set properly.
	 * Param has to be set for the player who makes the move.
	 * @param move
	 * @param param
	 * @return Board
	 */
	public Board applyMove(Move move, Parameters param) {
		
		Board after = new Board(this.board, this.macroBoard);//be careful this construct the avMoves List
		after.previous.addAll(this.previous);
		
		after.setValueInBoard(move.getX(), move.getY(), param.getMySq()); //complete the square
		
		int macroX = move.getX() / 3;
		int macroY = move.getY() / 3;
		
		int nextX = move.getX() % 3;
		int nextY = move.getY() % 3;
		
		//Enter here if the current square on macroBoard was won.
		
		if(after.board[macroX][macroY].squareWonBy(param) == param.getMySq()) 
			after.macroBoard.setValue(macroX, macroY, param.getMySq());
		else 
			if(after.board[macroX][macroY].checkIfDraw(param)) 
				after.macroBoard.setValue(macroX, macroY, param.getBlockedSq());
		
		//First of all set all previous canMove Square to emptySqaures.
		
		for(int i = 0 ; i < 3; ++i)
			for(int j = 0 ; j < 3; ++j)
				if(after.macroBoard.getValue(i, j) == param.getCanMove())
					after.macroBoard.setValue(i, j, param.getEmptySq());
		
		//Enter here to set macroBoard for the future
		//If it indicates a further OK position, then it's OK.
		
		if( after.macroBoard.getValue(nextX, nextY) == param.getEmptySq() ) {
			after.macroBoard.setValue(nextX, nextY, param.getCanMove());
			after.previous.addLast(false);
		} else {
			after.previous.addLast(true);
			//It points me to move to an already occupied territory (won, lost or draw)
			for(int i = 0; i < 3; i++) 
				for(int j = 0; j < 3; j++) 
					if( after.macroBoard.getValue(i, j) == param.getEmptySq() ) 
						after.macroBoard.setValue(i, j, param.getCanMove());	
		}
		
		if(after.macroBoard.squareWonBy(param.getMacroParamPossible()) != parameters.getEmptySq()) {
			
			after.getAvailableMoves().clear();
			after.getAvailablesMacroMoves().clear();
			after.setParameters(param.inverse());
			return after;
		}

		//Here I check for the next possible available moves.
		after.setParameters(param.inverse());
		after.setAvailableMoves(after.computeAvailableMoves());
		after.setAvailablesMacroMoves(after.computeAvailableMacroMoves());
		
		return after;
	}
	
	public void restorePreviousBoard(Move move, Parameters param) {
		
		//clear all lists and set ME as current player
		this.getAvailableMoves().clear();
		this.getAvailablesMacroMoves().clear();
		this.setParameters(param.inverse());
		
		//clear the previously completed little square 
		this.setValueInBoard(move.getX(), move.getY(), param.getEmptySq()); //complete the square
		
		//now, because we could move in this square, it means that it was of type
		//Parameters.canMove
		int macroX = move.getX() / 3;
		int macroY = move.getY() / 3;
		this.setValueInMacroBoard(macroX, macroY, param.getCanMove());
		
		//int nextX = move.getX() % 3;
		//int nextY = move.getY() % 3;

		//Restore the previous state, based on openMoveMade indicator
		this.previous.removeLast();
		Boolean openMoveMade = this.previous.getLast();
		
		if(openMoveMade == true) {
			for(int i = 0 ; i < 3; ++i)
				for(int j = 0 ; j < 3; ++j)
					if(this.macroBoard.getValue(i, j) == param.getEmptySq())
						this.macroBoard.setValue(i, j, param.getCanMove());
		} else {
			//aici am facut miscarea
			this.macroBoard.setValue(macroX, macroY, param.getCanMove());
		}

		//Here I check for the next possible available moves.
		this.setAvailableMoves(this.computeAvailableMoves());
		this.setAvailablesMacroMoves(this.computeAvailableMacroMoves());
	}
	
	public void simulateApplyMove( Move move, Parameters param) {
		
		this.setValueInBoard(move.getX(), move.getY(), param.getMySq()); //complete the square
		
		int macroX = move.getX() / 3;
		int macroY = move.getY() / 3;
		
		int nextX = move.getX() % 3;
		int nextY = move.getY() % 3;
		
		//Enter here if the current square on macroBoard was won.
		
		if(this.board[macroX][macroY].squareWonBy(param) == param.getMySq()) 
			this.macroBoard.setValue(macroX, macroY, param.getMySq());
		else 
			if(this.board[macroX][macroY].checkIfDraw(param)) 
				this.macroBoard.setValue(macroX, macroY, param.getBlockedSq());
		
		//First of all set all previous canMove Square to emptySqaures.
		
		for(int i = 0 ; i < 3; ++i)
			for(int j = 0 ; j < 3; ++j)
				if(this.macroBoard.getValue(i, j) == param.getCanMove())
					this.macroBoard.setValue(i, j, param.getEmptySq());
		
		//Enter here to set macroBoard for the future
		//If it indicates a further OK position, then it's OK.
		
		if( this.macroBoard.getValue(nextX, nextY) == param.getEmptySq() ) {
			this.macroBoard.setValue(nextX, nextY, param.getCanMove());
			this.previous.addLast(false);
		} else {
			//It points me to move to an already occupied territory (won, lost or draw)
			this.previous.addLast(true);
			for(int i = 0; i < 3; i++) 
				for(int j = 0; j < 3; j++) 
					if( this.macroBoard.getValue(i, j) == param.getEmptySq() ) 
						this.macroBoard.setValue(i, j, param.getCanMove());		
		}
		
		if(this.macroBoard.squareWonBy(param.getMacroParamPossible()) != parameters.getEmptySq()) {
			
			this.getAvailableMoves().clear();
			this.getAvailablesMacroMoves().clear();
			this.setParameters(param.inverse());
			return;
		}

		//Here I check for the next possible available moves.
		this.setParameters(param.inverse());
		this.setAvailableMoves(this.computeAvailableMoves());
		this.setAvailablesMacroMoves(this.computeAvailableMacroMoves());

	}
	/**
	
	 * Very nice printing of a board.
	 */
	public void printCurentState() {

		for(int i = 0 ; i < 9 ; ++i) {
			for(int j = 0 ; j < 9 ; ++j) {
				if(getValueInBoard(i, j) == parameters.getMySq())
					System.out.print("x ");
				if(getValueInBoard(i, j) == parameters.getEnemySq())
					System.out.print("o ");
				if(getValueInBoard(i, j) == parameters.getEmptySq())
					System.out.print(". ");
				
				if( j % 3 == 2 )
					System.out.print(" ");
			} 
			
			System.out.println();
		
			if(i % 3 == 2)
				System.out.println();
		}
		
		//macroBoard.printSquare();
		System.out.println("Micro-available-moves: ");
		int cnt = 0;
		
		for(Move m : availableMoves) {
			System.out.print( "(" + m.getX() + "," + m.getY() + ") ");
			
			cnt++;
			if(cnt % 10 == 0)
				System.out.println();
		}
		
		System.out.println();
		
		System.out.println("Macro-moves: ");
		
		for(Move m : availableMacroMoves)
			System.out.print( "(" + m.getX() + "," + m.getY() + ") ");
		
		System.out.println();
		System.out.println("Parameters: " + this.parameters.getMySq() + " " + this.parameters.getEnemySq() +   " "  + this.parameters.getEmptySq()
				+ " " + this.parameters.getBlockedSq() + " " + this.parameters.getCanMove());
	}
	/**
	 * returns the id of the player who won the game, otherwise returns the id of emptySquare
	 * @param param
	 * @return
	 */
	public int gameIsWonBy(Parameters param) {
		return macroBoard.squareWonBy(param);	
	}
	/**
	 * Number of squares won by param current player.
	 * @param param
	 * @return an integer, 0 for no squres won.
	 */
	public int countSquaresWonBy(Parameters param) {
		
		int cnt = 0;
		
		for(int i = 0 ; i < 3; ++i)
			for(int j = 0 ; j < 3; ++j)
				if(macroBoard.getValue(i, j) == param.getMySq())
					cnt++;
		return cnt;
					
	}
	/**
	 * Evaluate function. Evaluate a state of the board from the perspective of the player set in params.
	 * 
	 * @param param
	 * @return bigger if player with id param.mySq has a good position.
	 */
	public boolean ended() {
		if(getAvailableMoves().size() == 0)
			return true;
		return false;
	}
	/**
	 * You can move wherever you want.
	 * @param param
	 * @return
	 */
	public Board getOpenBoard(Parameters param) {
		
		Board board = new Board(this.board, this.macroBoard, param);//clone
		
		for(int i = 0 ; i < 3; ++i)
			for(int j = 0 ; j < 3; ++j)
				if(board.getMacroBoard().getValue(i, j) == param.getEmptySq())
					board.getMacroBoard().setValue(i, j, param.getCanMove());
		
		board.computeAvailableMacroMoves();
		board.computeAvailableMoves();
		
		return board;
	}
	
	public int countEmpty(Parameters param) {
		
		int cnt = 0;
		for(int i = 0 ; i < 9 ; ++i)
			for(int j = 0 ;j < 9 ; ++j) 
				if(this.getValueInBoard(i, j) == param.getEmptySq() && (this.macroBoard.getValue(i / 3, j / 3) == param.getEmptySq() 
						|| this.macroBoard.getValue(i / 3, j / 3) == param.getCanMove()))
						cnt++;
		return cnt;
	}

	
	public Square getOpenMacroBoard(Parameters param) {
		
		Square sq = new Square(this.macroBoard);
		
		for(int i = 0 ; i < 3; ++i)
			for(int j = 0 ; j < 3; ++j)
				if(sq.getValue(i, j) == param.getEmptySq())
					sq.setValue(i, j, param.getCanMove());
		
		
		return sq;
	}
	
	public Square getOpenMacroBoard2(Parameters param) {
		
		Square sq = new Square(this.macroBoard);
		
		for(int i = 0 ; i < 3; ++i)
			for(int j = 0 ; j < 3; ++j)
				if(sq.getValue(i, j) == param.getCanMove())
					sq.setValue(i, j, param.getEmptySq());
		
		
		return sq;
	}
	/**
	 * Calculate a danger for specific move in a specific board
	 * @param move move for which I calculate the danger
	 * @param param
	 * @return An integer, if the move is good, the integer is smaller.
	 **/
	public int evaluateGlobalMove(Move move, Parameters param) {
		
		int danger = 0;
		int weightLoseGame = Integer.MAX_VALUE - 1;
		int weightBlockedSquare = 700;
		int weightKeySquareRang1 = 1200;
		int weightLoseSquare = 700;
		int weightOpenLine = 150;
		int weightKeyEnemySquareOpen = 600; 
		int weightLoseCenter = 1000;
		
		Board openBoardBefore = getOpenBoard(param);
		
		List<Move> keySquaresMyRang1Before = openBoardBefore.getMacroBoard().getNextWinningSquareMoves(param.getMacroParam());
		
		Board nextBoard = this.applyMove(move, param);
		
		Board openBoard = nextBoard.getOpenBoard(param);
		
		List<Move> keySquarersEnemyRang1 = openBoard.getMacroBoard().getNextWinningSquareMoves(param.inverse().getMacroParam());
		List<Move> keySquaresMyRang1 = openBoard.getMacroBoard().getNextWinningSquareMoves(param.getMacroParam());
		
		int macroX = move.getX() % 3;
		int macroY = move.getY() % 3;
		Move nextSq = new Move(macroX, macroY);
		
		//if next turn I loose the game.
		if(nextBoard.getAllWiningGameMoves(param.inverse()).isEmpty() == false)
			return weightLoseGame / 2;
		
		int difKeySq = keySquaresMyRang1.size() - keySquaresMyRang1Before.size(); 
		
		if(difKeySq > 0)
			return difKeySq * weightKeySquareRang1;
		
		//if I redirect in a blocked square
		if(macroBoard.getValue(macroX, macroY) == param.getBlockedSq() || macroBoard.getValue(macroX, macroY) == param.getMySq() 
				|| macroBoard.getValue(macroX, macroY) == param.getEnemySq() ) {
		
			danger = weightBlockedSquare;
			
			if(keySquarersEnemyRang1.size() > 0)
				danger += weightKeySquareRang1;
			
			if(keySquaresMyRang1.size() > 0)
				danger += weightKeySquareRang1;
			
			return danger;
		}
		
		if(keySquarersEnemyRang1.contains(nextSq) || keySquaresMyRang1.contains(nextSq))
			danger += weightKeySquareRang1;
		
		danger += weightKeyEnemySquareOpen * keySquarersEnemyRang1.size();
		//if the square where I redirected I have an open line
		int openLinesInNextSquares = nextBoard.getSubSquareAt(macroX, macroY).getNextWinningSquareMoves(param).size();
		int openLinesEnemy = nextBoard.getSubSquareAt(macroX, macroY).getNextWinningSquareMoves(param.inverse()).size();
		
		if(openLinesInNextSquares == 1)
			danger += weightOpenLine;
		if(openLinesInNextSquares == 2)
			danger -= weightOpenLine;
		if(openLinesInNextSquares >= 3)
			danger -= 2 * weightOpenLine;
		
		if(openLinesEnemy >= 1) {
			
			danger += weightLoseSquare;
		
			if(nextSq.equals(new Move(1, 1)))
				danger += weightLoseCenter;
		}
		
		if(openLinesEnemy >= 3)
			danger -= weightLoseSquare;

		
		for(int i = macroX * 3; i < (macroX + 1) * 3; ++i)
			for(int j = macroY * 3; j < (1 + macroY) * 3; ++j) {
				
				if(this.getValueInBoard(i, j) == param.getMySq())
					danger--;
				
				if(this.getValueInBoard(i, j) == param.getEnemySq())
					danger += 4;
			}
		
		return danger;
			
	}
	/**
	 * get a score for local move, pretty small compared to evaluateGlobalMove
	 * @param move
	 * @param param
	 * @return
	 */
	public int evaluateLocalMove(Move move, Parameters param) {
		
		int danger = 0;
		int weightCenterMacro = 700;
		int weightCenterMicro = 680;
		int weightWinSquareMove = 7;
		int weightOpenMove = 2;
		
		int macroX = move.getX() / 3;
		int macroY = move.getY() / 3;
		
		
		Board nextBoard = applyMove(move, param);
		Square sq = nextBoard.board[macroX][macroY];
		
		//daca miscare mea il lasa cu multe winning moves-uri e proasta.
		danger += sq.getNextWinningSquareMoves(param.inverse()).size() * weightWinSquareMove;
		//daca il lasa cu openmove-uri
		danger += sq.getNextOpenMoves(param.inverse()).size() * weightOpenMove;
		if(move.equals(new Move(4, 4)))
			danger -= 100;
		
		if(move.isACenter() == true && move.isInCenterSquare() == false) 	
			danger += weightCenterMicro;

		if(move.isInCenterSquare() == true)
			danger -= weightCenterMacro;
		return danger;		
	}
	class Comparator implements java.util.Comparator<Move> {

		@Override
		public int compare(Move o1, Move o2) {
			if(o1.getScore() < o2.getScore())
				return -1;
			else if(o1.getScore() > o2.getScore())
				return 1;
			return 0;
		}	
	}
	public void SortMoves(Parameters param) {
		
		List<Move> winGameMoves = getAllWiningGameMoves(param);
		List<Move> winSquareMoves = getAllWinningSquareMoves(param);
		List<Move> winEnemyGame = getAllWiningGameMoves(param.inverse());
		List<Move> openLineMoves = getAllOpenLineMoves(param);
		List<Move> blockSquares = getAllWinningSquareMoves(param.inverse());
		
		int weightWinGame = Integer.MIN_VALUE - 80000;
		int weightWinSquare = 1400 * 2;
		int weightOpenLine = 400 * 2;
		int weightBlockSquare = 200 * 2;
		int weightEnemyWins = 100 * 2;
		int weightKeySquare1 = 1300 * 2;
		
		Board openBoard = getOpenBoard(param);
		List<Move> myKeySq = openBoard.getMacroBoard().getNextWinningSquareMoves(param.getMacroParam());
		List<Move> enemyKeySq = openBoard.getMacroBoard().getNextWinningSquareMoves(param.inverse().getMacroParam());
		
		int danger = 0;
		
		for(int i = 0 ; i < getAvailableMoves().size() ; ++i) {
			
			Move m = getAvailableMoves().get(i);
			Move macro = new Move(m.getX() / 3, m.getY() / 3);
			
			int global = evaluateGlobalMove(m, param);
			int local = evaluateLocalMove(m, param);
			danger = local + global;
			
			if(winGameMoves.contains(m)) 
				danger -= weightWinGame;
			
			
			if(winSquareMoves.contains(m)) 
				danger -= weightWinSquare;
			
			
			if(openLineMoves.contains(m)) 
				danger -= weightOpenLine;
			
			
			if(blockSquares.contains(m)) 
				danger -= weightBlockSquare;
			
			
			if(winEnemyGame.contains(m)) 
				danger -= weightEnemyWins;
			
			
			if(myKeySq.contains(macro) || enemyKeySq.contains(macro)) 
				danger -= weightKeySquare1;
			
			
			getAvailableMoves().get(i).setScore(danger);
		}
		//cea mai mica e cea mai buna
		Collections.sort(getAvailableMoves(), new Comparator());
	}
	public void sortMovesFast(Parameters param) {
		
		Set<Move> winGameMoves = new HashSet<Move>();
		Set<Move> winSquareMoves = new HashSet<Move>();
		Set<Move> openLineMoves = new HashSet<Move>(100);
		Set<Move> blockSquares = new HashSet<Move>();
		
		winGameMoves.addAll(getAllWiningGameMoves(param));
		winSquareMoves.addAll(getAllWinningSquareMoves(param));
		openLineMoves.addAll(getAllOpenLineMoves(param));
		blockSquares.addAll(getAllWinningSquareMoves(param.inverse()));
		
		int weightWinGame = Integer.MIN_VALUE + 8000;
		int weightWinSquare = 1400 * 2;
		int weightOpenLine = 400 * 2;
		int weightBlockSquare = 200 * 2;
		int weightKeySquare1 = 1300 * 2;
		

		Board openBoard = getOpenBoard(param);
		HashSet<Move> myKeySq = new HashSet<Move>();
		HashSet<Move> enemyKeySq = new HashSet<Move>();
		
		myKeySq.addAll(openBoard.getMacroBoard().getNextWinningSquareMoves(param.getMacroParam()));
		enemyKeySq.addAll(openBoard.getMacroBoard().getNextWinningSquareMoves(param.inverse().getMacroParam()));
		
		int danger = 0;
		
		for(int i = 0 ; i < getAvailableMoves().size() ; ++i) {
			
			Move m = getAvailableMoves().get(i);

			Move macro = new Move(m.getX() / 3, m.getY() / 3);
			
			if(winGameMoves.contains(m)) 
				danger -= weightWinGame;
			
			
			if(winSquareMoves.contains(m)) 
				danger -= weightWinSquare;
			
			
			if(openLineMoves.contains(m)) 
				danger -= weightOpenLine;
			
			
			if(blockSquares.contains(m)) 
				danger -= weightBlockSquare;
			
			
			if(myKeySq.contains(macro) || enemyKeySq.contains(macro)) 
				danger -= weightKeySquare1;
			
			
			getAvailableMoves().get(i).setScore(danger);
		}
		
		Collections.sort(getAvailableMoves(), new Comparator());
		
	}
	
	double openSquaresFormula1(double x1, double x2, double weight, double scale, double maxPoints, double ratio) {
		
		double sum = x1 + x2;
		double mg = Math.sqrt(x1 * x2);
		double scaleFactor = 1.0;
		double dominate = 0.70;
		double scaleWin = 2.5;
		
		if(x1 > ratio) {
			if(x2 > ratio)
				return (sum * dominate  + mg) * weight / scaleWin;
			else 
				return ( (maxPoints + x2 * scaleFactor) / scale) * weight + x1 * weight * ( (maxPoints + x2 * scaleFactor) / scale) / ratio / scaleWin;
		} else {
			
			if(x2 > ratio) 
				return ( (maxPoints + x1 * scaleFactor) / scale) * weight + x2 * weight * ( (maxPoints + x1 * scaleFactor) / scale) / ratio / scaleWin;
			else {
				
				double points1 = (maxPoints + x1 * scaleFactor) / scale;
				double points2 = (maxPoints + x2 * scaleFactor) / scale;
				
				return weight * (points1 + points2);
			}
		}
	}
	
	
	double openSquaresFormula2(double x1, double x2, double weight, double scale, double maxPoints, double ratio) {
		
		double sum = x1 + x2;
		double mg = Math.sqrt(x1 * x2);
		double scaleFactor = 1.0;
		double dominate = 0.70;
		
		double scaleWin = 2.5;
		
		if(x1 < -ratio) {
			if(x2 < -ratio)
				return (sum * dominate  - mg) * weight / scaleWin;
			else 
				return -((maxPoints - x2 * scaleFactor) / scale) * weight + x1 * weight * ( (maxPoints - x2 * scaleFactor) / scale) / ratio / scaleWin;
		} else {
			
			if(x2 < -ratio) 
				return -( (maxPoints - x1 * scaleFactor) / scale) * weight + x2 * weight * ( (maxPoints - x1 * scaleFactor) / scale) / ratio /scaleWin;
			else {
				//ambele impotriva
				double points1 = (maxPoints - x1 * scaleFactor) / scale;
				double points2 = (maxPoints - x2 * scaleFactor) / scale;
				
				return -weight *( points1 + points2);
			}
		}
			
	}
	
	public double threeEmpty1(double x1, double x2, double x3, double maxPoints) {
		
		double ma = x1 + x2 + x3;
		return  ma;
	
	}
	
	public double threeEmpty(double x1, double x2, double x3, double maxPoints) {
		
		double ma = x1 + x2 + x3;
		return  ma;
	/*	
		double mg = Math.pow(Math.abs(x1 * x2 * x3), 1/3);
		double ma = x1 + x2 + x3;
	*/
			
			
		/*
		double bonus3 = 1;
		double scale = 40;
		double x11 =x1;
		double x22 = x2;
		double x33 = x3;
		
		if(x11 > 0 && x11 < 3)
			x11 = 3;
		if(x11 > -3 && x11 < 0)
			x11 = -3;
		
		if(x22 > 0 && x22 < 3)
			x2 = 3;
		if(x22 > -3 && x22 < 0)
			x22 = -3;
		
		if(x33 > 0 && x33 < 3)
			x33 = 3;
		if(x33 > -3 && x33 < 0)
			x33 = -3;
		double mg = Math.pow(Math.abs(x11 * x22 * x33), 1/3);
		double ma = x11 + x22 + x33;
		if(ma > -1 && ma < 1)
			ma = 1;
		
		List<Double> win = new ArrayList<Double>(3);
		List<Double> lose = new ArrayList<Double>(3);
		
		if(x11 < 0)
			lose.add(x11);
		else 
			win.add(x11);
		
		if(x22 < 0)
			lose.add(x22);
		else 
			win.add(x22);
		
		if(x33 < 0)
			lose.add(x33);
		else 
			win.add(x33);
		
		if(win.size() == 3) 
			return ma + mg * bonus3;
		else if(win.size() == 2) {
			/*
			if(ma > 0) {
				double factor = (maxPoints - lose.get(0)) / scale;
				return factor * ma;
			} else 
				return ma;
				
			return ma;
		} if(win.size() == 1) {
			return ma;
		} else 
			return ma - mg * bonus3;
			*/
	}
	public double eval1(Parameters param) {
		
		double score = 0;
		double weightKeySquare = 60;
		double scaleWinKeySq = 1;
		double weightOpenlineSquare = 11;
		double scaleFactor = 1;

		double maxPointSquare = 90;
		double scale = 20;
		double scaleKey = 20;
		double diag = 1;
		double diagKey = 1;
		double diagOpen = 1;
		double bonus = 1;
		
		double ratio = maxPointSquare / scale;

		if (this.ended()) {
			
			int idWon = this.gameIsWonBy(param);
			
			if (idWon == param.getMySq()) 
				return BotStarter.MaxVal;
			
			if (idWon == param.getEnemySq())
				return BotStarter.MinVal;
			
			return 0D;
		}
		Square openBoard = getOpenMacroBoard2(param);
		
		double[][] scoresFromSquares = new double[3][3];
		for (int i = 0; i < 3; i++)
			for (int j=0; j < 3; j++) {
				Square sq = getSubSquareAt(i, j);
				scoresFromSquares[i][j] = sq.evalMinSquare1(i, j, param, this.macroBoard.getValue(i, j), this.macroBoard);
			}
		
		int mycntKeyLines = 0;
		int mycntKeyBlockedLines = 0;
		int enemycntBlockedLines = 0;
		int enemycntKeyLines = 0;
		int myOpenLines = 0;
		int enemyOpenLines = 0;
		
		for(int i = 0 ; i < 3; ++i) {
			//line i
			
			PairEval p = openBoard.analyze3Macro(openBoard.getValue(i, 0), openBoard.getValue(i, 1),
					openBoard.getValue(i, 2), param);
			Move keyBig = new Move(i, p.getInd());
			
			double sc = scoresFromSquares[keyBig.getX()][keyBig.getY()]; 
			
			if(p.getScore() == 2) {
				if (sc > ratio)
				{
					score += (bonus + sc * weightKeySquare) / scaleWinKeySq;
					mycntKeyLines++;
				}
				else
				{
					score += weightKeySquare * (maxPointSquare + scaleFactor * sc + bonus) / scaleKey ;
					mycntKeyBlockedLines ++;
				}
			  } else if(p.getScore() == -2) {//al inamicului
				
				if (sc < -ratio) //el domina
				{
					score -= Math.abs(sc + bonus) * weightKeySquare/ scaleWinKeySq;
					mycntKeyLines++;
				}
				else
				{
					score -= weightKeySquare * (maxPointSquare - scaleFactor * sc + bonus) / scaleKey ;
					mycntKeyBlockedLines ++;
				}
				
			} else if (p.getScore() == 1) {
				
				Move c1 = null;
				Move c2 = null;
				
				if(p.getInd() == 0) {
					c1 = new Move(i, 1);
					c2 = new Move(i, 2);
				} else if(p.getInd() == 1) {
					c1 = new Move(i, 0);
					c2 = new Move(i, 2);
				} else {
					c1 = new Move(i, 0);
					c2 = new Move(i, 1);
				}
				double scoreOpen1 = scoresFromSquares[c1.getX()][c1.getY()];
				double scoreOpen2 = scoresFromSquares[c2.getX()][c2.getY()];

				score += openSquaresFormula1(scoreOpen1, scoreOpen2, weightOpenlineSquare, scale, maxPointSquare, ratio);
				//System.out.println(openSquaresFormula1(scoreOpen1, scoreOpen2, weightOpenlineSquare, scale, maxPointSquare, ratio));
				myOpenLines++;
				
			} else if (p.getScore() == -1) {
				
				Move c1 = null;
				Move c2 = null;
				
				if(p.getInd() == 0) {
					c1 = new Move(i, 1);
					c2 = new Move(i, 2);
				} else if(p.getInd() == 1) {
					c1 = new Move(i, 0);
					c2 = new Move(i, 2);
				} else {
					c1 = new Move(i, 0);
					c2 = new Move(i, 1);
				}
				
				double scoreOpen1 = scoresFromSquares[c1.getX()][c1.getY()];
				double scoreOpen2 = scoresFromSquares[c2.getX()][c2.getY()];

				score += openSquaresFormula2(scoreOpen1, scoreOpen2, weightOpenlineSquare, scale, maxPointSquare, ratio);
				myOpenLines++;
				
			} else if(p.getScore() == 4) {
			
				score += threeEmpty1(scoresFromSquares[i][0], scoresFromSquares[i][1],scoresFromSquares[i][2], maxPointSquare) ;
			}
		} 
		
		//coloane
		
		for(int j = 0 ; j < 3; ++j) {
			

			PairEval p = openBoard.analyze3Macro(openBoard.getValue(0, j), 
					openBoard.getValue(1, j), openBoard.getValue(2, j), param);
			Move keyBig = new Move(p.getInd() , j);
			double sc = scoresFromSquares[keyBig.getX()][keyBig.getY()];
			
			if(p.getScore() == 2) {
				if (sc > ratio)
				{
					score += (bonus +sc * weightKeySquare) / scaleWinKeySq;
					mycntKeyLines++;
				}
				else
				{
					score += weightKeySquare * (maxPointSquare + scaleFactor * sc + bonus) / scaleKey ;
					mycntKeyBlockedLines ++;
				}
			} else if(p.getScore() == -2) {
				
				if(sc < -ratio) {
					score -= (bonus + Math.abs(sc) * weightKeySquare) / scaleWinKeySq;
					mycntKeyLines++;
				} else {
					score -= weightKeySquare * (maxPointSquare - scaleFactor * sc + bonus) / scaleKey ;
					mycntKeyBlockedLines ++;
				}
				
			} else if (p.getScore() == 1) {
				
				Move c1 = null;
				Move c2 = null;
				
				if(p.getInd() == 0) {
					c1 = new Move(1, j);
					c2 = new Move(2, j);
				} else if(p.getInd() == 1) {
					c1 = new Move(0, j);
					c2 = new Move(2, j);
				} else {
					c1 = new Move(0, j);
					c2 = new Move(1, j);
				}
				
				double scoreOpen1 = scoresFromSquares[c1.getX()][c1.getY()];
				double scoreOpen2 = scoresFromSquares[c2.getX()][c2.getY()];

				score += openSquaresFormula1(scoreOpen1, scoreOpen2, weightOpenlineSquare, scale, maxPointSquare, ratio);
				myOpenLines++;
				
			} else if (p.getScore() == -1) {
				
				Move c1 = null;
				Move c2 = null;
				
				if(p.getInd() == 0) {
					c1 = new Move(1, j);
					c2 = new Move(2, j);
				} else if(p.getInd() == 1) {
					c1 = new Move(0, j);
					c2 = new Move(2, j);
				} else {
					c1 = new Move(0, j);
					c2 = new Move(1, j);
				}
				
				double scoreOpen1 = scoresFromSquares[c1.getX()][c1.getY()];
				double scoreOpen2 = scoresFromSquares[c2.getX()][c2.getY()];

				score += openSquaresFormula2(scoreOpen1, scoreOpen2, weightOpenlineSquare, scale, maxPointSquare, ratio);
				myOpenLines++;
				
			}else if(p.getScore() == 4) {
				
				score += threeEmpty1(scoresFromSquares[0][j], scoresFromSquares[1][j],scoresFromSquares[2][j], maxPointSquare) ;
				
				
			}
			
		}
		
		
		//diagonale
		
		PairEval p = openBoard.analyze3Macro(openBoard.getValue(0, 0), openBoard.getValue(1, 1), 
				openBoard.getValue(2, 2), param);
		Move keyBig = new Move(p.getInd() , p.getInd());
		double sc = scoresFromSquares[keyBig.getX()][keyBig.getY()];
		
		if(p.getScore() == 2) {
			if (sc > ratio)
			{
				score += (bonus +  sc * weightKeySquare) / scaleWinKeySq;
				mycntKeyLines++;
			}
			else
			{
				score += weightKeySquare * (maxPointSquare + scaleFactor * sc + bonus) / scaleKey * diagKey;
				mycntKeyBlockedLines ++;
			}
		} else if(p.getScore() == -2) {
			
			if (sc < -ratio)
			{
				score -= (bonus + Math.abs(sc) * weightKeySquare) / scaleWinKeySq;
				mycntKeyLines++;
			}
			else
			{
				score -= weightKeySquare * (maxPointSquare - scaleFactor * sc + bonus) / scaleKey  * diagKey;
				mycntKeyBlockedLines ++;
			}
			
		} else if (p.getScore() == 1) {
			
			Move c1 = null;
			Move c2 = null;
			
			if(p.getInd() == 0) {
				c1 = new Move(1, 1);
				c2 = new Move(2, 2);
			} else if(p.getInd() == 1) {
				c1 = new Move(0, 0);
				c2 = new Move(2, 2);
			} else {
				c1 = new Move(0, 0);
				c2 = new Move(1, 1);
			}
			
			double scoreOpen1 = scoresFromSquares[c1.getX()][c1.getY()];
			double scoreOpen2 = scoresFromSquares[c2.getX()][c2.getY()];

			score += openSquaresFormula1(scoreOpen1, scoreOpen2, weightOpenlineSquare, scale, maxPointSquare, ratio) * diagOpen;
			myOpenLines++;
			
		} else if (p.getScore() == -1) {
			
			Move c1 = null;
			Move c2 = null;
			
			if(p.getInd() == 0) {
				c1 = new Move(1, 1);
				c2 = new Move(2, 2);
			} else if(p.getInd() == 1) {
				c1 = new Move(0, 0);
				c2 = new Move(2, 2);
			} else {
				c1 = new Move(0, 0);
				c2 = new Move(1, 1);
			}
			
			double scoreOpen1 = scoresFromSquares[c1.getX()][c1.getY()];
			double scoreOpen2 = scoresFromSquares[c2.getX()][c2.getY()];
			

			score += openSquaresFormula2(scoreOpen1, scoreOpen2, weightOpenlineSquare, scale, maxPointSquare, ratio) * diagOpen;;
			myOpenLines++;
			
		} else if(p.getScore() == 4) {
			
			score += threeEmpty1(scoresFromSquares[0][0], scoresFromSquares[1][1],scoresFromSquares[2][2], maxPointSquare) * diag;
		
		}
		
		//digonala 2
		p = openBoard.analyze3Macro(openBoard.getValue(0, 2), openBoard.getValue(1, 1), 
				openBoard.getValue(2, 0), param);
		keyBig = new Move(p.getInd() , 2 - p.getInd());
		
		sc = scoresFromSquares[keyBig.getX()][keyBig.getY()];
		
		if(p.getScore() == 2) {
			if (sc > ratio)
			{
				score += (bonus + sc * weightKeySquare) / scaleWinKeySq;
				mycntKeyLines++;
			}
			else
			{
				score += weightKeySquare * (maxPointSquare + scaleFactor * sc + bonus) / scaleKey ;
				mycntKeyBlockedLines++;
			}
		} else if(p.getScore() == -2) {
			
			if (sc < -ratio)
			{
				score -= (bonus + Math.abs(sc) * weightKeySquare)/ scaleWinKeySq;
				mycntKeyLines++;
			}
			else
			{
				score -= weightKeySquare * (maxPointSquare - scaleFactor * sc + bonus) / scaleKey ;
				mycntKeyBlockedLines ++;
			} 
		} else if (p.getScore() == 1) {
				
				
				Move c1 = null;
				Move c2 = null;
				
				if(p.getInd() == 0) {
					c1 = new Move(1, 1);
					c2 = new Move(2, 0);
				} else if(p.getInd() == 1) {
					c1 = new Move(0, 2);
					c2 = new Move(2, 0);
				} else {
					c1 = new Move(0, 2);
					c2 = new Move(1, 1);
				}
				
				double scoreOpen1 = scoresFromSquares[c1.getX()][c1.getY()];
				double scoreOpen2 = scoresFromSquares[c2.getX()][c2.getY()];
	
				score += openSquaresFormula1(scoreOpen1, scoreOpen2, weightOpenlineSquare, scale, maxPointSquare, ratio) ;
				myOpenLines++;
		} else if (p.getScore() == -1) {
			
			Move c1 = null;
			Move c2 = null;
			
			if(p.getInd() == 0) {
				c1 = new Move(1, 1);
				c2 = new Move(2, 0);
			} else if(p.getInd() == 1) {
				c1 = new Move(0, 2);
				c2 = new Move(2, 0);
			} else {
				c1 = new Move(0, 2);
				c2 = new Move(1, 1);
			}
			
			double scoreOpen1 = scoresFromSquares[c1.getX()][c1.getY()];
			double scoreOpen2 = scoresFromSquares[c2.getX()][c2.getY()];

			score += openSquaresFormula2(scoreOpen1, scoreOpen2, weightOpenlineSquare, scale, maxPointSquare, ratio);
			myOpenLines++;
			
		} else if(p.getScore() == 4) {
			
			score += threeEmpty1(scoresFromSquares[0][2], scoresFromSquares[1][1],scoresFromSquares[2][0], maxPointSquare) * 1;
			
		}
		
		return score;
	}
	/*
	public double eval2(Parameters param) {
		Square openBoard = getOpenMacroBoard(param);
		
		Set<Move> keySquaresSet = new HashSet<Move>();
		Set<Move> openLineSquaresSet = new HashSet<Move>();
		Set<Move> enemyKeySquareSet = new HashSet<Move>();
		Set<Move> enemyOpenLineSquaresSet = new HashSet<Move>();
		
		keySquaresSet.addAll(openBoard.getNextWinningSquareMoves(param.getMacroParam()));
		openLineSquaresSet.addAll(openBoard.getNextOpenMoves(param.getMacroParam()));
		enemyKeySquareSet.addAll(openBoard.getNextWinningSquareMoves(param.inverse().getMacroParam()));
		enemyOpenLineSquaresSet.addAll(openBoard.getNextOpenMoves(param.inverse().getMacroParam()));
		
		double score = 0;
		double weightKeySquare = 130;
		double weightOpenlineSquare = 17;
		double center = 4;
		double corner = 3;
		double margin = 2;
		double maxPointSquare = 130;
		double scale = 20;
		
		if (this.ended()) {
			
			int idWon = this.gameIsWonBy(param);
			
			if (idWon == param.getMySq()) 
				return BotStarter.MaxVal;
			
			if (idWon == param.getEnemySq())
				return BotStarter.MinVal;
			
			return 0D;
		}

		for (int i = 0; i < 3 ;i++) {
			for (int j = 0; j < 3; j++) {
				
				Square sq = getSubSquareAt(i, j);
				Move square = new Move(i,j);
				double heuristicSquare = sq.evalMinSquare2(param, this.macroBoard.getValue(i, j), this.macroBoard);
				
				if (keySquaresSet.contains(square))
				{
					if (heuristicSquare > 0)
						score += weightKeySquare * heuristicSquare ;
					else if (heuristicSquare < 0)
						score += weightKeySquare * (maxPointSquare + heuristicSquare) / scale ;
				}
				
				if (enemyKeySquareSet.contains(square)) {
					
					
					if(heuristicSquare < 0)
						score += weightKeySquare * heuristicSquare ;
					else if (heuristicSquare > 0)
						score +=  weightKeySquare * (-maxPointSquare + heuristicSquare) / scale;
				}
				
				if (openLineSquaresSet.contains(square)) {
					
					
					if (heuristicSquare > 0)
						score += weightOpenlineSquare* heuristicSquare ;
					else if (heuristicSquare < 0)
						score +=   weightOpenlineSquare * (maxPointSquare + heuristicSquare) / scale;	
				}
				
				if (enemyOpenLineSquaresSet.contains(square)) {
					
					if (heuristicSquare < 0)
						score += weightOpenlineSquare * heuristicSquare;
					else if(heuristicSquare > 0)
						score += weightOpenlineSquare* (-maxPointSquare + heuristicSquare) / scale;
				}
				
				if (square.isACenter())
					score += heuristicSquare * center;
				else if (square.isCorner())
					score += corner * heuristicSquare;
			    else 
					score += margin * heuristicSquare;
			}
		}
		
		return score;
	}
	
	*/
	public double eval2 (Parameters param) {
		
		double score = 0;
		double weightKeySquare = 122;
		double scaleWinKeySq = 1;
		double weightOpenlineSquare = 22;
		double scaleFactor = 1;

		double maxPointSquare = 90;
		double scale = 18;
		double scaleKey = 18;
		double ratio = maxPointSquare / scale;

		if (this.ended()) {
			
			int idWon = this.gameIsWonBy(param);
			
			if (idWon == param.getMySq()) 
				return BotStarter.MaxVal;
			
			if (idWon == param.getEnemySq())
				return BotStarter.MinVal;
			
			return 0D;
		}
		Square openBoard = getOpenMacroBoard2(param);
		
		double[][] scoresFromSquares = new double[3][3];
		for (int i = 0; i < 3; i++)
			for (int j=0; j < 3; j++) {
				Square sq = getSubSquareAt(i, j);
				scoresFromSquares[i][j] = sq.evalMinSquare2(i, j, param, this.macroBoard.getValue(i, j), this.macroBoard);
			}
		
		int mycntKeyLines = 0;
		int mycntKeyBlockedLines = 0;
		int enemycntBlockedLines = 0;
		int enemycntKeyLines = 0;
		int myOpenLines = 0;
		int enemyOpenLines = 0;
		
		for(int i = 0 ; i < 3; ++i) {
			//line i
			
			PairEval p = openBoard.analyze3Macro(openBoard.getValue(i, 0), openBoard.getValue(i, 1),
					openBoard.getValue(i, 2), param);
			Move keyBig = new Move(i, p.getInd());
			
			double sc = scoresFromSquares[keyBig.getX()][keyBig.getY()]; 
			
			if(p.getScore() == 2) {
				if (sc > ratio)
				{
					score += sc * weightKeySquare / scaleWinKeySq;
					mycntKeyLines++;
				}
				else
				{
					score += weightKeySquare * (maxPointSquare + scaleFactor * sc) / scaleKey ;
					mycntKeyBlockedLines ++;
				}
			  } else if(p.getScore() == -2) {//al inamicului
				
				if (sc < -ratio) //el domina
				{
					score -= Math.abs(sc) * weightKeySquare/ scaleWinKeySq;
					mycntKeyLines++;
				}
				else
				{
					score -= weightKeySquare * (maxPointSquare - scaleFactor * sc) / scaleKey ;
					mycntKeyBlockedLines ++;
				}
				
			} else if (p.getScore() == 1) {
				
				Move c1 = null;
				Move c2 = null;
				
				if(p.getInd() == 0) {
					c1 = new Move(i, 1);
					c2 = new Move(i, 2);
				} else if(p.getInd() == 1) {
					c1 = new Move(i, 0);
					c2 = new Move(i, 2);
				} else {
					c1 = new Move(i, 0);
					c2 = new Move(i, 1);
				}
				double scoreOpen1 = scoresFromSquares[c1.getX()][c1.getY()];
				double scoreOpen2 = scoresFromSquares[c2.getX()][c2.getY()];

				score += openSquaresFormula1(scoreOpen1, scoreOpen2, weightOpenlineSquare, scale, maxPointSquare, ratio);
				//System.out.println(openSquaresFormula1(scoreOpen1, scoreOpen2, weightOpenlineSquare, scale, maxPointSquare, ratio));
				myOpenLines++;
				
			} else if (p.getScore() == -1) {
				
				Move c1 = null;
				Move c2 = null;
				
				if(p.getInd() == 0) {
					c1 = new Move(i, 1);
					c2 = new Move(i, 2);
				} else if(p.getInd() == 1) {
					c1 = new Move(i, 0);
					c2 = new Move(i, 2);
				} else {
					c1 = new Move(i, 0);
					c2 = new Move(i, 1);
				}
				
				double scoreOpen1 = scoresFromSquares[c1.getX()][c1.getY()];
				double scoreOpen2 = scoresFromSquares[c2.getX()][c2.getY()];

				score += openSquaresFormula2(scoreOpen1, scoreOpen2, weightOpenlineSquare, scale, maxPointSquare, ratio);
				myOpenLines++;
				
			} else if(p.getScore() == 4) {
			
				score += threeEmpty(scoresFromSquares[i][0], scoresFromSquares[i][1],scoresFromSquares[i][2], maxPointSquare) ;
			}
		} 
		
		//coloane
		
		for(int j = 0 ; j < 3; ++j) {
			

			PairEval p = openBoard.analyze3Macro(openBoard.getValue(0, j), 
					openBoard.getValue(1, j), openBoard.getValue(2, j), param);
			Move keyBig = new Move(p.getInd() , j);
			double sc = scoresFromSquares[keyBig.getX()][keyBig.getY()];
			
			if(p.getScore() == 2) {
				if (sc > ratio)
				{
					score += sc * weightKeySquare / scaleWinKeySq;
					mycntKeyLines++;
				}
				else
				{
					score += weightKeySquare * (maxPointSquare + scaleFactor * sc) / scaleKey ;
					mycntKeyBlockedLines ++;
				}
			} else if(p.getScore() == -2) {
				
				if(sc < -ratio) {
					score -= Math.abs(sc) * weightKeySquare / scaleWinKeySq;
					mycntKeyLines++;
				} else {
					score -= weightKeySquare * (maxPointSquare - scaleFactor * sc) / scaleKey ;
					mycntKeyBlockedLines ++;
				}
				
			} else if (p.getScore() == 1) {
				
				Move c1 = null;
				Move c2 = null;
				
				if(p.getInd() == 0) {
					c1 = new Move(1, j);
					c2 = new Move(2, j);
				} else if(p.getInd() == 1) {
					c1 = new Move(0, j);
					c2 = new Move(2, j);
				} else {
					c1 = new Move(0, j);
					c2 = new Move(1, j);
				}
				
				double scoreOpen1 = scoresFromSquares[c1.getX()][c1.getY()];
				double scoreOpen2 = scoresFromSquares[c2.getX()][c2.getY()];

				score += openSquaresFormula1(scoreOpen1, scoreOpen2, weightOpenlineSquare, scale, maxPointSquare, ratio);
				myOpenLines++;
				
			} else if (p.getScore() == -1) {
				
				Move c1 = null;
				Move c2 = null;
				
				if(p.getInd() == 0) {
					c1 = new Move(1, j);
					c2 = new Move(2, j);
				} else if(p.getInd() == 1) {
					c1 = new Move(0, j);
					c2 = new Move(2, j);
				} else {
					c1 = new Move(0, j);
					c2 = new Move(1, j);
				}
				
				double scoreOpen1 = scoresFromSquares[c1.getX()][c1.getY()];
				double scoreOpen2 = scoresFromSquares[c2.getX()][c2.getY()];

				score += openSquaresFormula2(scoreOpen1, scoreOpen2, weightOpenlineSquare, scale, maxPointSquare, ratio);
				myOpenLines++;
				
			}else if(p.getScore() == 4) {
				
				score += threeEmpty(scoresFromSquares[0][j], scoresFromSquares[1][j],scoresFromSquares[2][j], maxPointSquare) ;
				
			}
			
		}
		
		
		//diagonale
		
		PairEval p = openBoard.analyze3Macro(openBoard.getValue(0, 0), openBoard.getValue(1, 1), 
				openBoard.getValue(2, 2), param);
		Move keyBig = new Move(p.getInd() , p.getInd());
		double sc = scoresFromSquares[keyBig.getX()][keyBig.getY()];
		
		if(p.getScore() == 2) {
			if (sc > ratio)
			{
				score += sc * weightKeySquare / scaleWinKeySq;
				mycntKeyLines++;
			}
			else
			{
				score += weightKeySquare * (maxPointSquare + scaleFactor * sc) / scaleKey ;
				mycntKeyBlockedLines ++;
			}
		} else if(p.getScore() == -2) {
			
			if (sc < -ratio)
			{
				score -= Math.abs(sc) * weightKeySquare / scaleWinKeySq;
				mycntKeyLines++;
			}
			else
			{
				score -= weightKeySquare * (maxPointSquare - scaleFactor * sc) / scaleKey ;
				mycntKeyBlockedLines ++;
			}
			
		} else if (p.getScore() == 1) {
			
			Move c1 = null;
			Move c2 = null;
			
			if(p.getInd() == 0) {
				c1 = new Move(1, 1);
				c2 = new Move(2, 2);
			} else if(p.getInd() == 1) {
				c1 = new Move(0, 0);
				c2 = new Move(2, 2);
			} else {
				c1 = new Move(0, 0);
				c2 = new Move(1, 1);
			}
			
			double scoreOpen1 = scoresFromSquares[c1.getX()][c1.getY()];
			double scoreOpen2 = scoresFromSquares[c2.getX()][c2.getY()];

			score += openSquaresFormula1(scoreOpen1, scoreOpen2, weightOpenlineSquare, scale, maxPointSquare, ratio);
			myOpenLines++;
			
		} else if (p.getScore() == -1) {
			
			Move c1 = null;
			Move c2 = null;
			
			if(p.getInd() == 0) {
				c1 = new Move(1, 1);
				c2 = new Move(2, 2);
			} else if(p.getInd() == 1) {
				c1 = new Move(0, 0);
				c2 = new Move(2, 2);
			} else {
				c1 = new Move(0, 0);
				c2 = new Move(1, 1);
			}
			
			double scoreOpen1 = scoresFromSquares[c1.getX()][c1.getY()];
			double scoreOpen2 = scoresFromSquares[c2.getX()][c2.getY()];
			

			score += openSquaresFormula2(scoreOpen1, scoreOpen2, weightOpenlineSquare, scale, maxPointSquare, ratio);
			myOpenLines++;
			
		} else if(p.getScore() == 4) {
			
			
			score += threeEmpty(scoresFromSquares[0][0], scoresFromSquares[1][1],scoresFromSquares[2][2], maxPointSquare) ;
		}
		
		//digonala 2
		p = openBoard.analyze3Macro(openBoard.getValue(0, 2), openBoard.getValue(1, 1), 
				openBoard.getValue(2, 0), param);
		keyBig = new Move(p.getInd() , 2 - p.getInd());
		
		sc = scoresFromSquares[keyBig.getX()][keyBig.getY()];
		
		if(p.getScore() == 2) {
			if (sc > ratio)
			{
				score += sc * weightKeySquare / scaleWinKeySq;
				mycntKeyLines++;
			}
			else
			{
				score += weightKeySquare * (maxPointSquare + scaleFactor * sc) / scaleKey ;
				mycntKeyBlockedLines++;
			}
		} else if(p.getScore() == -2) {
			
			if (sc < -ratio)
			{
				score -= Math.abs(sc) * weightKeySquare/ scaleWinKeySq;
				mycntKeyLines++;
			}
			else
			{
				score -= weightKeySquare * (maxPointSquare - scaleFactor * sc) / scaleKey ;
				mycntKeyBlockedLines ++;
			} 
		} else if (p.getScore() == 1) {
				
				
				Move c1 = null;
				Move c2 = null;
				
				if(p.getInd() == 0) {
					c1 = new Move(1, 1);
					c2 = new Move(2, 0);
				} else if(p.getInd() == 1) {
					c1 = new Move(0, 2);
					c2 = new Move(2, 0);
				} else {
					c1 = new Move(0, 2);
					c2 = new Move(1, 1);
				}
				
				double scoreOpen1 = scoresFromSquares[c1.getX()][c1.getY()];
				double scoreOpen2 = scoresFromSquares[c2.getX()][c2.getY()];
	
				score += openSquaresFormula1(scoreOpen1, scoreOpen2, weightOpenlineSquare, scale, maxPointSquare, ratio);
				myOpenLines++;
		} else if (p.getScore() == -1) {
			
			Move c1 = null;
			Move c2 = null;
			
			if(p.getInd() == 0) {
				c1 = new Move(1, 1);
				c2 = new Move(2, 0);
			} else if(p.getInd() == 1) {
				c1 = new Move(0, 2);
				c2 = new Move(2, 0);
			} else {
				c1 = new Move(0, 2);
				c2 = new Move(1, 1);
			}
			
			double scoreOpen1 = scoresFromSquares[c1.getX()][c1.getY()];
			double scoreOpen2 = scoresFromSquares[c2.getX()][c2.getY()];

			score += openSquaresFormula2(scoreOpen1, scoreOpen2, weightOpenlineSquare, scale, maxPointSquare, ratio);
			myOpenLines++;
			
		} else if(p.getScore() == 4) {
			
			score += threeEmpty(scoresFromSquares[0][2], scoresFromSquares[1][1],scoresFromSquares[2][0], maxPointSquare) ;
			
		}
		
		return score;
	}
	@Override
	public int hashCode() {
		
		int[] serialize = new int[92];
		int cnt = 0;
		
		for(int x = 0 ; x < 9; ++x)
			for(int y = 0 ;y < 9; ++y) {
				
				serialize[cnt] = this.getValueInBoard(x, y);
				cnt++;
			}
		
		for(int i = 0 ; i < 3; ++i)
			for(int j = 0 ;j < 3; ++j) {
				serialize[cnt] = this.getValueInMacroBord(i, j);
				cnt++;
			}
		
		return Arrays.hashCode(serialize);
	}
	
	@Override
	public boolean equals(Object obj) {
		
		/*
		 * if number of available moves differs, then the boards cannot be the same
		 */
		if (!(obj instanceof Board))
            return false;
		
		if((this.availableMoves.size() != ((Board)obj).availableMoves.size())) 
			return false;
		
		if(obj == this)
			return true;
		
		/*
		 * comparing all 81 little squares, to check for equality
		 */
		Square sq1, sq2;
		Board b = (Board)(obj);
		
		for(int i = 0; i < 3; i++) {
			for(int j = 0; j < 3; j++) {
				
				sq1 = this.board[i][j];
				sq2 = b.board[i][j];
				
				if(b.macroBoard.getValue(i, j) != this.macroBoard.getValue(i, j))
					return false;
				
				for(int k = 0; k < 3; k++) 
					for(int l = 0; l < 3; l++) 
						if(sq1.getValue(k, l) != sq2.getValue(k, l)) 
							return false;
			}
		}
	
		return true;
	}
	public void clearBoard(Parameters param) {
		
		for(int i = 0 ; i < 3; ++i)
			for(int j = 0 ; j < 3; ++j) { 
				macroBoard.setValue(i, j, param.getCanMove());
				
				for(int x = 0 ; x < 3; ++x)
					for(int y = 0 ; y < 3; ++y)
						board[i][j].setValue(x, y, param.getEmptySq());
			}
		
		getAvailableMoves().clear();
		getAvailablesMacroMoves().clear();
		
		setAvailableMoves(computeAvailableMoves());
		setAvailablesMacroMoves(computeAvailableMacroMoves());
		setParameters(param);
				
	}
}
