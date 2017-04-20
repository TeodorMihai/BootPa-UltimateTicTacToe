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

/**
 * Move class
 * 
 * Stores a move.
 * 
 * @author Jim van Eeden <jim@starapple.nl>, Joost de Meij <joost@starapple.nl>
 */

public class Move {
	
	private int mX, mY;
	private int score;
	private int index;
	
	public Move() {
	}
	public Move(int x, int y) {
		mX = x;
		mY = y;
	}

	
	public Move(int x, int y, int score) {
		mX = x;
		mY = y;
		this.setScore(score);
	}
	
	@Override
	public int hashCode() {
		return mX * 10 + mY;
	}
	@Override
	public boolean equals(Object obj) {
		Move m = (Move)obj;
		return (mX == m.getX()) && (m.getY() == mY);
	}
	public int getX() { return mX; }
	public int getY() { return mY; }
	/**
	 * (x, y) is a micro-square, 
	 * @return true if (x, y) is a center in a any small square, else false
	 */
	public boolean isACenter() {

		if (mX % 3 == 1 && mY % 3 == 1)
			return true;
		
		return false;
	}
	
	public boolean isCorner()
	{
		int x = mX % 3;
		int y = mY % 3;
		if ( (x == 0 || x == 2) && (y == 0 || y == 2) )
			return true;
		return false;
	}
	
	public boolean isMargin() {
		int x = mX % 3;
		int y = mY % 3;
		
		if(isACenter())
			return false;
		
		if( x == 1 || y == 1)
			return true;
		
		return false;
		
	}
	/**
	 * (x, y) must be a micro-square
	 * @return true if (x, y) is in the center square. (macro)
	 * 
	 */
	public boolean isInCenterSquare() {

		if( (mX >= 3 && mX < 6) && (mY >= 3 && mY < 6) )
			return true;

		return false;
	}
	/**
	 * Make an absolute move from two relative ones. Needs to have the macroSquare. 
	 * @param move The macroSquare in which the relative positions is.
	 * @return The absolute position.
	 */
	public Move makeItAbsolute(Move move) {
		return new Move(move.getX() * 3 + mX, move.getY() * 3 + mY); 
	}
	public int getScore() {
		return score;
	}
	public void setScore(int score) {
		this.score = score;
	}
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
}
