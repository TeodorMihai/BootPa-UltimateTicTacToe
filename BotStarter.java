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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;


/**
 * BotStarter class
 * 
 * Magic happens here. You should edit this file, or more specifically the
 * makeTurn() method to make your bot do more than random moves.
 * 
 * @author Jim van Eeden <jim@starapple.nl>
 */

public class BotStarter {
	
	public static int startMinimax = 0;
	public static int roundNr;
	public static double MaxVal = 10000000000000000000.0;
	public static double MinVal = -10000000000000000000.0;
	public static int hashMax = 10;
	public static int hashLimit = 100;
	public static HashMap<Board, PairNew> oldHashTable = new LinkedHashMap<Board, PairNew>(hashMax);
	public static HashMap<Board, PairNew> newHashTable = new LinkedHashMap<Board, PairNew>(hashMax);
	public static double[] lgg = new double[20];
	public static int timeRemain = 0;
	public static Chronometer ch3 = new Chronometer();
	public static int timeSpent = 0;
	
	public static int DEPTH = 7;
	public Move ret ;
	
	public int cntRight = 0;
	public int sumRight = 0;
	public int sumWrong = 0;
	int cntWrong = 0;
	
	class Comparator implements java.util.Comparator<Pair> {

		@Override
		public int compare(Pair o1, Pair o2) {
			if(o1.first() < o2.first())
				return 1;
			else if(o1.first() > o2.first())
				return 1;
			return 0;
		}	
	}
	class Comparator2 implements java.util.Comparator<State> {

		@Override
		public int compare(State o1, State o2) {
			if(o1.getScore() < o2.getScore())
				return -1;
			else if(o1.getScore() > o2.getScore())
				return 1;
			return 0;
		}	
	}
	
	class ComparatorInv2 implements java.util.Comparator<State> {

		@Override
		public int compare(State o1, State o2) {
			if(o1.getScore() > o2.getScore())
				return -1;
			else if(o1.getScore() < o2.getScore())
				return 1;
			return 0;
		}	
	}

	private void sortByHash(State[] nodes, Board board, Parameters param, int perspective) {
		
		List<Move> moves = board.getAvailableMoves();
		int sz = moves.size();
		
		for(int i = 0 ; i < sz; ++i) {
			
			nodes[i] = new State();
			Move move = moves.get(i);
			nodes[i].setBoard(board.applyMove(move, param));
			nodes[i].setMove(new Move(move.getX(), move.getY()));
		}
		
		for(int i = 0; i < sz; i++) {
			
			PairNew p = oldHashTable.get(nodes[i].getBoard());
			
			if(p == null)
				if(perspective == 1)
					nodes[i].setScore(MaxVal);
				else 
					nodes[i].setScore(MinVal);
			
			if(p != null) 
				nodes[i].setScore(p.getScore());	
		}
		
		
		if(perspective == 1)
			Arrays.sort(nodes, new ComparatorInv2());//descrescatoare in ordinea punctajului, pentru maxi
		else 
			Arrays.sort(nodes, new Comparator2());//crecatoare, de la cele mai proaste miscari pentru mine, pt mini

	}
	
	public Pair maxi1(Board board, Parameters param, int depth, double alpha, double beta, int maxMoves, int maxTime) {
	
		if(depth > 9) {//sortez cu hashuri si retin starile, trebuie sa creez boardurile

			if (depth == 0 || board.ended()) 
				return new Pair(board.eval1(param), new Move());
			
			List<Move> moves = new ArrayList<Move>();
		    moves.addAll(board.getAvailableMoves());
		    int sz = moves.size();
		    State[] nodes = new State[sz];
		    sortByHash(nodes, board, param, 2);
		
			Move bestMove = nodes[0].getMove();
			double bestScore = MinVal;
			
			
			for (int i = 0 ; i < maxMoves && i < sz ; ++i) {
				
				Move move = nodes[i].getMove();
				
				//board.simulateApplyMove(move, param);
				
				Pair pair = mini1(nodes[i].getBoard(), param.inverse(), depth - 1, alpha, beta, maxMoves, maxTime);
				
				//board.restorePreviousBoard(move, param);
				
				if(bestScore < pair.first()) {
					bestScore = pair.first();
					bestMove = move;
				}
				
				if(bestScore > alpha)  
					alpha = bestScore;
				
				if (alpha >= beta) 
					break;
			}
			
			newHashTable.put(board, new PairNew(depth, bestScore));
			return new Pair(bestScore, bestMove);
			
		} else { //nu sortez cu hashuri, nu retin board-urile, dau apply si reverse
			

			if (depth == 0 || board.ended()) 
				return new Pair(board.eval1(param), new Move());
			
			List<Move> moves = new ArrayList<Move>();
			moves.addAll(board.getAvailableMoves());
		    int sz = moves.size();
		
			Move bestMove = board.getAvailableMoves().get(0);
			double bestScore = MinVal;
			
			int newDepth = depth - 1;

			
			
			for (int i = 0 ; i < maxMoves && i < sz ; ++i) {
				
				Move move = moves.get(i);
				
				board.simulateApplyMove(move, param);
				
				Pair pair = mini1(board, param.inverse(), newDepth , alpha, beta, maxMoves, maxTime);
				
				board.restorePreviousBoard(move, param);
				
				if(bestScore < pair.first()) {
					bestScore = pair.first();
					bestMove = move;
				}
				
				if(bestScore > alpha)  
					alpha = bestScore;
				
				if (alpha >= beta) 
					break;
			}
			
			return new Pair(bestScore, bestMove);
		}
		
	}
	
	public Pair mini1(Board board, Parameters param, int depth, double alpha, double beta, int maxMoves, int maxTime) {
		
		//evaluez tot timpul din perspectiva mea
		
		if(depth > 9) {
			
			if (depth == 0 || board.ended())
				return new Pair(board.eval1(param.inverse()), new Move());
			
			List<Move> moves = new ArrayList<Move>();
			moves.addAll(board.getAvailableMoves());
			int sz = board.getAvailableMoves().size();
			
			State[] nodes = new State[sz];
			sortByHash(nodes, board, param, 2);
			double minScore = MaxVal;
			Move bestMove = board.getAvailableMoves().get(0);
		
			
			
			for (int i = 0 ; i < maxMoves && i < sz ; ++i) {
				
				/*
				Move move = moves.get(i);
				board.simulateApplyMove(move, param);
				
				Pair pair = maxi1(board, param.inverse(), depth - 1 , alpha, beta, maxMoves, maxTime);
				board.restorePreviousBoard(move, param);
				*/
				Move move = nodes[i].getMove();
				Pair pair = maxi1(nodes[i].getBoard(), param.inverse(), depth - 1 , alpha, beta, maxMoves, maxTime);
				
				if(minScore > pair.first()) {
					minScore = pair.first();
					bestMove = move;
				}
				
				if(minScore < beta)
					beta = minScore;
				
				if (beta <= alpha)
					break;	
			}
			newHashTable.put(board, new PairNew(depth, minScore));
			return new Pair(minScore, bestMove);
			
		} else {
			
			if (depth == 0 || board.ended())
				return new Pair(board.eval1(param.inverse()), new Move());
			
			List<Move> moves = new ArrayList<Move>();
			moves.addAll(board.getAvailableMoves());
			int sz = board.getAvailableMoves().size();
			
			double minScore = MaxVal;
			Move bestMove = board.getAvailableMoves().get(0);
			
			
			
			for (int i = 0 ; i < maxMoves && i < sz ; ++i) {
				
				
				Move move = moves.get(i);
				board.simulateApplyMove(move, param);
				
				Pair pair = maxi1(board, param.inverse(), depth - 1 , alpha, beta, maxMoves, maxTime);
				board.restorePreviousBoard(move, param);
				
				if(minScore > pair.first()) {
					minScore = pair.first();
					bestMove = move;
				}
				
				if(minScore < beta)
					beta = minScore;
				
				if (beta <= alpha)
					break;	
			}
			
			return new Pair(minScore, bestMove);
			
		}
	}
	
	public Pair maxi2(Board board, Parameters param, int depth, double alpha, double beta, int maxMoves, int maxTime) {
		
		if (depth == 0 || board.ended()) 
			return new Pair(board.eval2(param), new Move());
		
		List<Move> moves = new ArrayList<Move>();
		moves.addAll(board.getAvailableMoves());
	    int sz = moves.size();
	
		Move bestMove = board.getAvailableMoves().get(0);
		double bestScore = MinVal;
		
		for (int i = 0 ; i < maxMoves && i < sz ; ++i) {
			
			Move move = moves.get(i);
			
			board.simulateApplyMove(move, param);
			
			Pair pair = mini2(board, param.inverse(), depth - 1, alpha, beta, maxMoves, maxTime);
			
			board.restorePreviousBoard(move, param);
			
			if(bestScore < pair.first()) {
				bestScore = pair.first();
				bestMove = move;
			}
			
			if(bestScore > alpha)  
				alpha = bestScore;
			
			if (alpha >= beta) 
				break;
		}
		
		return new Pair(bestScore, bestMove);
	}
	
	public Pair mini2(Board board, Parameters param, int depth, double alpha, double beta, int maxMoves, int maxTime) {
		//din perspective adversarului
		if (depth == 0 || board.ended())
			return new Pair(board.eval2(param.inverse()), new Move());
		
		List<Move> moves = new ArrayList<Move>();
		moves.addAll(board.getAvailableMoves());
		int sz = board.getAvailableMoves().size();
		
		double minScore = MaxVal;
		Move bestMove = board.getAvailableMoves().get(0);
		
		for (int i = 0 ; i < maxMoves && i < sz ; ++i) {
			
			
			Move move = moves.get(i);
			board.simulateApplyMove(move, param);
			
			Pair pair = maxi2(board, param.inverse(), depth - 1, alpha, beta, maxMoves, maxTime);
			board.restorePreviousBoard(move, param);
			
			if(minScore > pair.first()) { //se alege minimul
				minScore = pair.first();
				bestMove = move;
			}
			
			if(minScore < beta) //se alege minimul pentru beta
				beta = minScore;
			
			if (beta <= alpha)//minimul mai prost decat cea mai buna miscare curenta(alpha) => se taie
				break;	
		}
		
		return new Pair(minScore, bestMove);
	}
	
	
	private void cleanMiniMax1(Parameters params) {
		
	  // System.out.print("\n" + oldHashTable.size() +  "   ");
	   oldHashTable.clear();
	   Iterator it = newHashTable.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry<Board, PairNew> pair = (Map.Entry)it.next();
	        oldHashTable.put((Board)pair.getKey(), (PairNew)pair.getValue());
	        it.remove(); 
	    }
	}

	public Move makeTurn(Field field) {

		/* You don't have to know ant convention for param, just give 5 random numbers different one from each other and it's okay
		 * We convert their field into our Board with these convention.*/
	
		Parameters param = new Parameters( 8 ,9 , 3 , 4, 5);
		Board board = field.convertToOurRepresentation(param);
		int depth1 = 6;
		int depth2 = 6;
		int playGames = 100;
		
		int maxMovesAnalysed = Integer.MAX_VALUE;
		/*
		int maxMovesAnalysed2 = Integer.MAX_VALUE;
	
		int win1 = 0;
		int win2 = 0;
		int draws = 0;
		int movesMade = 0;
	
		Move m = null;
		Random r = new Random();
		
		Chronometer ch1 = new Chronometer();
		Chronometer ch2 = new Chronometer();
		
		long time1 = 0;
		long time2 = 0;
		
		
		for(int i = 0 ; i < playGames; ++i) {	
			int cnt = 0; 
			while(board.ended() == false ) {
				if(cnt % 2 == 0) {
					
					ch1.start();
					ch3.start();
					
					if(cnt == 0) 
						m = board.getAvailableMoves().get(r.nextInt(board.getAvailableMoves().size() - 1));
					else {
						Pair m2;
						
						if(cnt < 15){
							m2 = maxi2(board, param, depth1 - 2, MinVal, MaxVal, maxMovesAnalysed, 200);//zero
							cleanMiniMax1(param);
						}
						
						else if(cnt < 16) {
							m2 = maxi2(board, param, depth1 - 1  , MinVal, MaxVal, maxMovesAnalysed, 400);//zero
							//cleanMiniMax1(param);
						}
						
						else {
							
							 if(timeSpent >100000) {
								 if(timeSpent > 9200) {
									 if(timeSpent > 9500) {
										 m2 = maxi2(board, param, 4 , MinVal, MaxVal, maxMovesAnalysed, 500);
										System.out.println("4");
									 }
									 else {
										 m2 = maxi2(board, param, 5 , MinVal, MaxVal, maxMovesAnalysed, 500);
										System.out.println("5");
									 }
								 } else {
									m2 = maxi2(board, param, 6 , MinVal, MaxVal, maxMovesAnalysed, 1000);
									System.out.println("6");
								 }
							 } else if(cnt / 2 * 500 - timeSpent < 1500) {
								m2 = maxi2(board, param, 7 , MinVal, MaxVal, maxMovesAnalysed, 1000);
								
							}
							 
							 else {
								if(board.getAvailableMoves().size() > 9) 
								m2 = maxi2(board, param, depth1 , MinVal, MaxVal, maxMovesAnalysed, 9000);
							else 
								m2 = maxi2(board, param, depth1  , MinVal, MaxVal, maxMovesAnalysed, 9000);
								//System.out.println("normal");
							}
							//cleanMiniMax1(param);
						
						}
						
						m = m2.second();
					}
					
					ch3.stop();
					timeSpent += ch3.getTime();
					//System.out.println(ch3.getTime());
					ch1.stop();
					
					
					time1 += ch1.getTime();
					
				} else {
					ch2.start();
					ch3.start();
					
					if(cnt == 1)
						m = board.getAvailableMoves().get(r.nextInt(board.getAvailableMoves().size() - 1));
					else {
						
						Pair m2 = null;
						
						
						if(cnt < 16){
							m2 = maxi1(board, param, depth2 - 2 , MinVal, MaxVal, maxMovesAnalysed, 333);//zero
							cleanMiniMax1(param);
						}
						
						else {
							
							if(board.getAvailableMoves().size() > 9)
								m2 = maxi1(board, param, depth2 , MinVal, MaxVal, maxMovesAnalysed, 500);
							else 
								m2 = maxi1(board, param, depth2 , MinVal, MaxVal, maxMovesAnalysed, 500);
							//cleanMiniMax1(param);
						}
						
						m = m2.second();	
						
					//	System.out.println("\n\n\n" + m2.first() +  " \n\n\n\n");
					}
					ch2.stop();
					
					ch3.stop();
					
					time2 += ch2.getTime();
				}
				board = board.applyMove(m, param);
				param = param.inverse();
				
				cnt++;
				/*
				System.out.println("Move: " + m.getX() + " " + m.getY());
				if(cnt % 2 == 1)
					board.printCurentState();
				else  {
					
					board.setParameters(param.inverse());
					board.printCurentState();
					board.setParameters(param.inverse());
				}
				
				
				
			}
			movesMade += cnt;
			
			if(cnt % 2 == 1)
				param = param.inverse();
			
			if(board.gameIsWonBy(param) == param.getMySq())
				win1++;
			if(board.gameIsWonBy(param) == param.getEnemySq())
				win2++;
			if(board.gameIsWonBy(param) == param.getEmptySq())
				draws++;
			
			System.out.println("Won by: " +  board.gameIsWonBy(param));
			System.out.println("Time: player " + time1 + " " + time2 );
		
			param = new Parameters( 8 , 9 , 3 , 4, 5);
			board.clearBoard(param);
			timeSpent = 0;
			
	
		}
			
		
		System.out.println("Results:\n" + "Player1: " + win1 + "\nPlayer2: " + win2 + "\nDraws: " + draws );
		System.out.println("Tiem total player1 " + time1);
		System.out.println("Time total player2 " + time2);
		System.out.print("Time total :  ");

		System.out.println(time1 + time2);
		System.out.println("Total wrong / right : " + sumWrong + " " + sumRight);
	
		
		Square x = new Square();
		x.setValue(0, 0, param.getEmptySq());
		x.setValue(0, 1, param.getMySq());
		x.setValue(0, 2, param.getMySq());
		

		x.setValue(1, 0, param.getEnemySq());
		x.setValue(1, 1, param.getEmptySq());
		x.setValue(1, 2, param.getEmptySq());
		

		x.setValue(2, 0, param.getEmptySq());
		x.setValue(2, 1, param.getEmptySq());
		x.setValue(2, 2, param.getEmptySq());
		
		//PairEval pp = x.analyze3Macro( param.getMySq(), param.getEmptySq(), param.getEmptySq(), param);
		board.clearBoard(param);
		System.out.println(x.evalMinSquare1(0, 2, param, 0, board.getMacroBoard()));
		//System.out.println("score : "  + pp.getScore() + "  index " +  pp.getInd() );
		
		*/
		
		int nr = board.getAvailableMoves().size();
		ch3.start();
		
		Pair m3 = null;
		Random r =  new Random();
		if(BotStarter.roundNr == 1) {
			Move m4 =  board.getAvailableMoves().get(r.nextInt(nr));
			while(m4.isACenter() == true) {
				 m4 =  board.getAvailableMoves().get(r.nextInt(nr));
			}
			return m4;
		} else if(BotStarter.roundNr == 2) {
			Move m4 =  board.getAvailableMoves().get(r.nextInt(nr));
			while(m4.isACenter() == true) {
				 m4 =  board.getAvailableMoves().get(r.nextInt(nr));
			}
			return m4;
		} else if(BotStarter.roundNr < 16){
			m3 = maxi2(board, param, depth1 - 3, MinVal, MaxVal, maxMovesAnalysed, 200);//zero
			//cleanMiniMax1(param);
		}
		else if(BotStarter.roundNr < 26) {
			m3 = maxi2(board, param, depth1 - 1, MinVal, MaxVal, maxMovesAnalysed, 500);//zero
	
		} else  {
			
			 if(timeSpent > 7600) {
				 if(timeSpent > 9200) {
					 if(timeSpent > 9500) {
						 m3 = maxi2(board, param, 4 , MinVal, MaxVal, maxMovesAnalysed, 420);
					 }
					 else 
						 m3 = maxi2(board, param, 5 , MinVal, MaxVal, maxMovesAnalysed, 420);
				 } else 
					m3 = maxi2(board, param, 6 , MinVal, MaxVal, maxMovesAnalysed, 420);
			 } else if(BotStarter.roundNr / 2 * 500 - timeSpent < 1400) {
				m3 = maxi2(board, param, 7 , MinVal, MaxVal, maxMovesAnalysed, 420);
			}  else if(board.getAvailableMoves().size() > 9)
				m3 = maxi2(board, param, depth1 , MinVal, MaxVal, maxMovesAnalysed, 500);
			else 
				m3 = maxi2(board, param, depth1 , MinVal, MaxVal, maxMovesAnalysed, 500);
			//cleanMiniMax1(param);
		}
		
		ch3.stop();
		timeSpent += ch3.getTime();
		
		return m3.second();
		
	}

	public static void main(String[] args) {
		
		for(int i = 1; i < 19; ++i)
			lgg[i] = Math.log(i);
		BotParser parser = new BotParser(new BotStarter());
		parser.run();
	}
}
