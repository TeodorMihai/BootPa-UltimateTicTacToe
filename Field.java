// // Copyright 2016 theaigames.com (developers@theaigames.com)

//    Licensed under the Apache License, Version 2.0 (the "License");
//    you may not use this file except in compliance with the License.
//    You may obtain a copy of the License at

//        http://www.apache.org/licenses/LICENSE-2.0

//    Unless required by applicable law or agreed to in writing, software
//    distributed under the License is distributed on an "AS IS" BASIS,
//    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//    See the License for the specific language governing permissions and
//    limitations under the License.
//  
//    For the full copyright and license information, please view the LICENSE
//    file that was distributed with this source code.

package bot;

import java.util.ArrayList;

import bot.Board;
import bot.Parameters;
import bot.Square;

/**
 * Field class
 * 
 * Handles everything that has to do with the field, such as storing the current
 * state and performing calculations on the field.
 * 
 * @author Jim van Eeden <jim@starapple.nl>, Joost de Meij <joost@starapple.nl>
 */

public class Field {
	
	private int mRoundNr;
	private int mMoveNr;
	private int[][] mBoard;
	private int[][] mMacroboard;

	private final int COLS = 9, ROWS = 9;
	private String mLastError = "";
	
	public int getRound() {
		return mRoundNr;
	}
	public void setBoard(int x, int y, int value) {
		mBoard[x][y] = value;
	}

	public void setMacroBoard(int x, int y, int value) {
		mMacroboard[x][y] = value;
	}

	public void setMacroBoard(int[][] board) {

		for (int i = 0; i < 3; ++i)
			for (int j = 0; j < 3; ++j)
				mMacroboard[i][j] = board[i][j];
	}

	public int[][] getmBoard() {
		return mBoard;
	}

	public int[][] getmMacroBoard() {
		return mMacroboard;
	}

	public void setCoordMacroBoard(int x, int y, int value) {
		mMacroboard[x][y] = value;
	}

	public int getCoordsBoard(int i, int j) {
		return mBoard[i][j];
	}

	public int getCoordsMacroBoard(int i, int j) {
		return mMacroboard[i][j];
	}
	/**
	 * 
	 * @param x macro-line of square
	 * @param y macro-column of square
	 * @return a big square 3x3 specified by (x, y)
	 */
	public Square getSquareAt(int x, int y) {

		Square sq = new Square();

		for (int i = x * 3; i <  (x + 1) * 3; i++)
			for (int j = y * 3; j <  (y + 1) * 3; ++j)
				sq.setValue(i - x * 3, j - y * 3, mBoard[i][j]);

		return sq;
	}
	/**
	 * All fields in squares are changed to our representation.
	 * For instance if a square is -1(their representation of canMove), now is set to param.canMove
	 * If it is represented as 0, it will be changed at param.getEmpty and so on.
	 * @param param
	 * @return Board with available Moves already set.
	 */
	public Board convertToOurRepresentation(Parameters param) {
		
		Board myBoard = new Board();
		
		int myId = BotParser.getmBotId();
		int enemyId = BotParser.getEnemyId();
	
		/* Change to our representation */
		for (int i = 0; i < 9; ++i)
			for(int j = 0; j < 9 ; ++j) {
				if(mBoard[i][j] == 0)
					myBoard.setValueInBoard(i, j, param.getEmptySq());
				
				if(mBoard[i][j] == myId)
					myBoard.setValueInBoard(i, j, param.getMySq());
				
				if(mBoard[i][j] == enemyId)
					myBoard.setValueInBoard(i, j, param.getEnemySq());
			}
				
		/* I can use a method from Board class (checkIfDraw)because it is applied only to 3x3 square and i already set
		 * all of these king of squares.
		 */
		for (int i = 0; i < 3; i++)  
			for (int j = 0; j < 3; j++) { 
				
				if(mMacroboard[i][j] == 0) //even if it is draw we check later on
					myBoard.setValueInMacroBoard(i, j, param.getEmptySq());
				
				if(checkIfDraw(i, j))
					myBoard.setValueInMacroBoard(i, j, param.getBlockedSq());
				
				if(mMacroboard[i][j] == BotParser.getmBotId())
					myBoard.setValueInMacroBoard(i, j, param.getMySq());
				
				if(mMacroboard[i][j] == BotParser.getEnemyId())
					myBoard.setValueInMacroBoard(i, j, param.getEnemySq());
			
				if(mMacroboard[i][j] == -1)
					myBoard.setValueInMacroBoard(i, j, param.getCanMove());
			}
		
		myBoard.setParameters(param);
		myBoard.setAvailableMoves(myBoard.computeAvailableMoves());
		myBoard.setAvailablesMacroMoves(myBoard.computeAvailableMacroMoves());
		
		return myBoard;
	}

	/**
	 * Check if Draw for their convention of id's. There is another method checkIfDraw 
	 * in Border, but that one checks in our convention.
	 * @param x - macro parameter for line
	 * @param y	- macro parameter for column
	 * @return true if is a draw false otherwise
	 */
	public boolean checkIfDraw(int x, int y) {
		
		if(mMacroboard[x][y] == 1 || mMacroboard[x][y] == 2)
			return false;
		
		for(int i = x * 3 ; i < (x + 1) * 3; ++i)
			for(int j = y * 3 ; j < (y + 1) * 3; ++j)
				if(mBoard[i][j] == 0) //their convention for empty
					return false;
		//no empty than okay
		return true;
	}
	public Field() {
		mBoard = new int[COLS][ROWS];
		mMacroboard = new int[COLS / 3][ROWS / 3];
		clearBoard();
	}

	/**
	 * Parse data about the game given by the engine
	 * 
	 * @param key : type of data given
	 * @param value value
	 */
	public void parseGameData(String key, String value) {
		if (key.equals("round")) {
			mRoundNr = Integer.parseInt(value);
			BotStarter.roundNr = mRoundNr;
		} else if (key.equals("move")) {
			mMoveNr = Integer.parseInt(value);
		} else if (key.equals("field")) {
			parseFromString(value); /* Parse Field with data */
		} else if (key.equals("macroboard")) {
			parseMacroboardFromString(value); /* Parse macroboard with data */
		}
	}

	/**
	 * Initialise field from comma separated String
	 * 
	 * @param String
	 *            :
	 */
	public void parseFromString(String s) {
		System.err.println("Move " + mMoveNr);
		s = s.replace(";", ",");
		String[] r = s.split(",");
		int counter = 0;
		for (int y = 0; y < ROWS; y++) {
			for (int x = 0; x < COLS; x++) {
				mBoard[x][y] = Integer.parseInt(r[counter]);
				counter++;
			}
		}
	}

	/**
	 * Initialise macroboard from comma separated String
	 * 
	 * @param String
	 *            :
	 */
	public void parseMacroboardFromString(String s) {
		String[] r = s.split(",");
		int counter = 0;
		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 3; x++) {
				mMacroboard[x][y] = Integer.parseInt(r[counter]);
				counter++;
			}
		}
	}

	public void clearBoard() {
		for (int x = 0; x < COLS; x++) {
			for (int y = 0; y < ROWS; y++) {
				mBoard[x][y] = 0;
			}
		}
	}

	public ArrayList<Move> getAvailableMoves() {
		
		ArrayList<Move> moves = new ArrayList<Move>();

		for (int y = 0; y < ROWS; y++) {
			for (int x = 0; x < COLS; x++) {
				if (isInActiveMicroboard(x, y) && mBoard[x][y] == 0) {
					moves.add(new Move(x, y));
				}
			}
		}

		return moves;
	}

	public Boolean isInActiveMicroboard(int x, int y) {
		return mMacroboard[(int) x / 3][(int) y / 3] == -1;
	}

	/**
	 * Returns reason why addMove returns false
	 * 
	 * @param args
	 *            :
	 * @return : reason why addMove returns false
	 */
	public String getLastError() {
		return mLastError;
	}

	@Override
	/**
	 * Creates comma separated String with player ids for the microboards.
	 * 
	 * @param args
	 *            :
	 * @return : String with player names for every cell, or 'empty' when cell
	 *         is empty.
	 */
	public String toString() {
		String r = "";
		int counter = 0;
		for (int y = 0; y < ROWS; y++) {
			for (int x = 0; x < COLS; x++) {
				if (counter > 0) {
					r += ",";
				}
				r += mBoard[x][y];
				counter++;
			}
		}
		return r;
	}

	/**
	 * Checks whether the field is full
	 * 
	 * @param args
	 *            :
	 * @return : Returns true when field is full, otherwise returns false.
	 */
	public boolean isFull() {
		for (int x = 0; x < COLS; x++)
			for (int y = 0; y < ROWS; y++)
				if (mBoard[x][y] == 0)
					return false; // At least one cell is not filled
		// All cells are filled
		return true;
	}

	public int getNrColumns() {
		return COLS;
	}

	public int getNrRows() {
		return ROWS;
	}

	public boolean isEmpty() {
		for (int x = 0; x < COLS; x++) {
			for (int y = 0; y < ROWS; y++) {
				if (mBoard[x][y] > 0) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Returns the player id on given column and row
	 * 
	 * @param args
	 *            : int column, int row
	 * @return : int
	 */
	public int getPlayerId(int column, int row) {
		return mBoard[column][row];
	}

}