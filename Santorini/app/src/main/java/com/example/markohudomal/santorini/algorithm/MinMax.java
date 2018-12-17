package com.example.markohudomal.santorini.algorithm;


import android.util.Log;

import com.example.markohudomal.santorini.GameActivity;
import com.example.markohudomal.santorini.struct.Cell;

import java.util.ArrayList;

public class MinMax {

    public static class BoardState{
        public Cell[][] board;
        public Point pointFrom;
        public Point pointMove;
        public Point pointBuild;
        public int value=-1;

        public BoardState(Cell[][] board, Point movePoint, Point buildPoint, Point fromPoint) {
            this.board = board;
            this.pointMove = movePoint;
            this.pointBuild = buildPoint;
            this.pointFrom = fromPoint;
        }

    }

    public static ArrayList<BoardState> posible_moves_from(BoardState boardState,int player)
    {
        ArrayList<BoardState> ret_array = new ArrayList<>();

        ArrayList<Point> figures = new ArrayList<>();
        Cell[][] board = boardState.board;

        //Find figures position------------------------------------
        for(int i = 0; i<GameActivity.BOARD_WIDTH;i++)
        {
            for(int j = 0; j<GameActivity.BOARD_WIDTH;j++)
            {
                if (board[i][j].getPlayer()==player){
                    figures.add(new Point(i,j));
                }
            }
        }
        if (figures.size()<2) return null;
        //---------------------------------------------------------
        //TWO FIGURES ON TABLE [n=2]
        for(int figure=0;figure<2;figure++)
        {
            int fig_x = figures.get(figure).x;
            int fig_y = figures.get(figure).y;

            //Move Figure X [n=9]
            for (int i = fig_x - 1; i <= fig_x + 1; i++) {
                for (int j = fig_y - 1; j <= fig_y + 1; j++) {

                    //Skip bad move
                    if ((i >= GameActivity.BOARD_WIDTH) || (i < 0)) continue;
                    if ((j >= GameActivity.BOARD_WIDTH) || (j < 0)) continue;
                    if (board[i][j].getPlayer() != -1) continue;
                    if (board[i][j].getHeight() > (board[fig_x][fig_y].getHeight() + 1)) continue;
                    if (board[i][j].getHeight() == 4) continue;

                    //Good move
                    Cell[][] move_board = copyBoard(board);
                    //Last move delete and set new position
                    move_board[fig_x][fig_y].setPlayer(-1);
                    move_board[i][j].setPlayer(player);//---------------------------------------------------------------------<<<<<PLAYER

                    //Build around Figure X [n=9]
                    for (int ii = i - 1; ii <= i + 1; ii++) {
                        for (int jj = j - 1; jj <= j + 1; jj++) {

                            if ((ii >= GameActivity.BOARD_WIDTH) || (ii < 0)) continue;
                            if ((jj >= GameActivity.BOARD_WIDTH) || (jj < 0)) continue;
                            if (move_board[ii][jj].getPlayer() != -1) continue;
                            if (move_board[ii][jj].getHeight() == 4) continue;

                            //Good build
                            Cell[][] build_board = copyBoard(move_board);
                            //Update field
                            build_board[ii][jj].incHeight();

                            //Add to list!
                            ret_array.add(new BoardState(build_board,new Point(i,j),new Point(ii,jj),new Point(fig_x,fig_y)));
                        }
                    }

                }//for(j)
            }//for(i)

        }//for (figure)

        return ret_array;
    }

    public static Cell[][] copyBoard(Cell[][] board)
    {
        Cell[][] ret = new Cell[GameActivity.BOARD_WIDTH][GameActivity.BOARD_WIDTH];
        for(int i=0;i<GameActivity.BOARD_WIDTH;i++)
        {
            for(int j=0;j<GameActivity.BOARD_WIDTH;j++)
            {
                ret[i][j]=new Cell(board[i][j].getHeight(),board[i][j].getPlayer(), board[i][j].getColor(),i,j);
            }
        }

        return ret;
    }


    public static BoardState minmaxDecision(Cell[][] current_board,int depth,int player)
    {
        boolean maxPlayer;

        BoardState boardState = new BoardState(current_board,new Point(0,0),new Point(0,0),new Point(0,0));
        BoardState bestBoard = minmax(boardState,depth,true,player);

        return bestBoard;
    }
    public static BoardState minmax(BoardState boardState,int depth,boolean maxPlayer,int player){


        //End
        if (depth<=0)
        {
            boardState.value=heuristicFunction(boardState,player);
            return boardState;
        }

        //Get childer
        ArrayList<BoardState> boards = posible_moves_from(boardState,player);
        if (boards==null)
        {
            Log.d("SANTORINI_LOG","Posible moves in one moment null");
            return null;
        }

        //MAX Player
        if (maxPlayer){
            int bestVal= Integer.MIN_VALUE;
            BoardState bestBoard=null;

            for(int i=0;i<boards.size();i++)
            {
                BoardState currBoard = minmax(boards.get(i),depth-1,false,1-player);
                if (currBoard!=null && currBoard.value>bestVal)
                {
                    bestVal=currBoard.value;
                    bestBoard=boards.get(i);
                }

            }
            return bestBoard;
        }else
        {
            //MIN Player
            int bestVal= Integer.MAX_VALUE;
            BoardState bestBoard=null;

            for(int i=0;i<boards.size();i++)
            {
                BoardState currBoard = minmax(boards.get(i),depth-1,true,1-player);
                if (currBoard!=null && currBoard.value<bestVal)
                {
                    bestVal=currBoard.value;
                    bestBoard=boards.get(i);
                }

            }
            return bestBoard;
        }
    }

    public static int heuristicFunction(BoardState boardState,int player)
    {
        if (boardState==null) return 0;

        Cell[][] board = boardState.board;
        ArrayList<Point> figuresMy = new ArrayList<>();
        ArrayList<Point> figuresOther = new ArrayList<>();

        for(int i = 0; i<GameActivity.BOARD_WIDTH;i++)
        {
            for(int j = 0; j<GameActivity.BOARD_WIDTH;j++)
            {
                if (board[i][j].getPlayer()==player){
                    figuresMy.add(new Point(i,j));
                }else
                    figuresOther.add(new Point(i,j));
            }
        }

        int m=board[boardState.pointMove.x][boardState.pointMove.y].getHeight();
        int n=board[boardState.pointBuild.x][boardState.pointBuild.y].getHeight()-1;
        int ud1=Math.abs(figuresMy.get(0).x-boardState.pointBuild.x) + Math.abs(figuresMy.get(0).y-boardState.pointBuild.y);
        ud1+=Math.abs(figuresMy.get(1).x-boardState.pointBuild.x) + Math.abs(figuresMy.get(1).y-boardState.pointBuild.y);
        int ud2=Math.abs(figuresOther.get(0).x-boardState.pointBuild.x) + Math.abs(figuresOther.get(0).y-boardState.pointBuild.y);
        ud2+=Math.abs(figuresOther.get(1).x-boardState.pointBuild.x) + Math.abs(figuresOther.get(1).y-boardState.pointBuild.y);

        int l = m+n*(ud1-ud2);

        Log.d("SANTORINI_LOG",player+". heuristic func: "+l);
        return l;
    }
}
