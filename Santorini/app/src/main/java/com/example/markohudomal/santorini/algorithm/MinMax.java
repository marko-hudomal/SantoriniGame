package com.example.markohudomal.santorini.algorithm;


import android.support.v4.app.NotificationCompatSideChannelService;
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
        public int winner=-1;

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
                    int potential_winner = Game.winnerWho(move_board);
                    if (potential_winner!=-1)
                    {
                        BoardState bs_temp = new BoardState(move_board,new Point(i,j),new Point(0,0),new Point(fig_x,fig_y));
                        bs_temp.winner=potential_winner;
                        Log.d("SANTORINI_LOG","Found winner "+potential_winner);
                        //Add to list!
                        ret_array.add(bs_temp);
                    }else
                    {
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
                                BoardState bs_temp=new BoardState(build_board,new Point(i,j),new Point(ii,jj),new Point(fig_x,fig_y));
                                if (!Game.canMove(build_board,1-player)){
                                    bs_temp.winner=player;
                                    Log.d("SANTORINI_LOG","Found looser "+(1-player));
                                }
                                ret_array.add(bs_temp);
                            }
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
    //Funkcije vracaju max i min respektivno za date argumente
    private static int max(int a,int b)
    {
        if (a>=b) return a; else return b;
    }
    private static int min(int a,int b)
    {
        if (a<=b) return a; else return b;
    }

    //Ninja player===============================================================================================================================================
    //Wrapper funkcija za minmaxNinja(...)
    public static BoardState minmaxNinjaDecision(Cell[][] current_board,int depth,int player) {
        boolean maxPlayer;
        if (player==0) maxPlayer=true; else maxPlayer=false;

        BoardState boardState = new BoardState(current_board,new Point(0,0),new Point(0,0),new Point(0,0));
        BoardState bestBoard = minmaxNinja(boardState,depth,Integer.MIN_VALUE,Integer.MAX_VALUE,maxPlayer,player);

        if (bestBoard==null)
        {
            Log.d("SANTORINI_LOG","BEST BOARD IS NULL, NO!");
        }else{
            //Log.d("SANTORINI_LOG","a_From: "+ bestBoard.pointFrom.x+","+bestBoard.pointFrom.y);
            //Log.d("SANTORINI_LOG","a_Move: "+ bestBoard.pointMove.x+","+bestBoard.pointMove.y);
            //Log.d("SANTORINI_LOG","a_Build: "+ bestBoard.pointBuild.x+","+bestBoard.pointBuild.y);
            Log.d("SANTORINI_LOG","[Ninja] Final minmax value: "+ bestBoard.value);
        }

        return bestBoard;
    }
    //Rekurzivna funkcija odabiranja sledeceg poteza tezine 3
    public static BoardState minmaxNinja(BoardState boardState,int depth,int a, int b, boolean maxPlayer,int player){
        //Modification
        if (player==0) maxPlayer=true;else if (player==1) maxPlayer=false;

        //End
        if ((depth<=0) || (boardState.winner!=-1))
        {
            if (boardState.winner!=-1)
            {
                Log.d("SANTORINI_LOG","Game winner "+boardState.winner+", on depth" + depth);
            }
            boardState.value=heuristicFunctionNinja(boardState,player);
            Log.d("SANTORINI_LOG",depth+".bestVal_"+maxPlayer+": "+boardState.value);
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
                BoardState currBoard = minmaxNinja(boards.get(i),depth-1,a,b,false,1-player);
                if (currBoard!=null)
                {
                    if (currBoard.value>bestVal)
                    {
                        bestVal=currBoard.value;
                        bestBoard=boards.get(i);
                        bestBoard.value=bestVal;
                    }
                    a=max(a,bestVal);
                    if (a>=b) {
                        //Log.d("SANTORINI_LOG","max cut");
                    break;
                    }
                }

            }
            Log.d("SANTORINI_LOG",depth+".bestVal_MAX: "+bestBoard.value);
            return bestBoard;
        }else
        {
            //MIN Player
            int bestVal= Integer.MAX_VALUE;
            BoardState bestBoard=null;

            for(int i=0;i<boards.size();i++)
            {
                BoardState currBoard = minmaxNinja(boards.get(i),depth-1,a,b,true,1-player);
                if (currBoard!=null)
                {
                    if (currBoard.value<bestVal)
                    {
                        bestVal=currBoard.value;
                        bestBoard=boards.get(i);
                        bestBoard.value=bestVal;
                    }
                    b=min(b,bestVal);
                    if (a>=b) {
                        //Log.d("SANTORINI_LOG","min cut");
                        break;
                    }
                }
            }
            Log.d("SANTORINI_LOG",depth+".bestVal_MIN: "+bestBoard.value);
            return bestBoard;
        }
    }


    //Stupid player===============================================================================================================================================
    //Wrapper funkcija za minmaxAlphaBeta(...)
    public static BoardState minmaxAlphaBetaDecision(Cell[][] current_board,int depth,int player) {
        boolean maxPlayer;
        if (player==0) maxPlayer=true; else maxPlayer=false;

        BoardState boardState = new BoardState(current_board,new Point(0,0),new Point(0,0),new Point(0,0));
        BoardState bestBoard = minmaxAlphaBeta(boardState,depth,Integer.MIN_VALUE,Integer.MAX_VALUE,maxPlayer,player);

        if (bestBoard==null)
        {
            Log.d("SANTORINI_LOG","BEST BOARD IS NULL, NO!");
        }else{
            //Log.d("SANTORINI_LOG","a_From: "+ bestBoard.pointFrom.x+","+bestBoard.pointFrom.y);
            //Log.d("SANTORINI_LOG","a_Move: "+ bestBoard.pointMove.x+","+bestBoard.pointMove.y);
            //Log.d("SANTORINI_LOG","a_Build: "+ bestBoard.pointBuild.x+","+bestBoard.pointBuild.y);
            Log.d("SANTORINI_LOG","[AlphaBeta] Final minmax value: "+ bestBoard.value);
        }

        return bestBoard;
    }
    //Rekurzivna funkcija odabiranja sledeceg poteza tezine 2
    public static BoardState minmaxAlphaBeta(BoardState boardState,int depth,int a, int b, boolean maxPlayer,int player){
        //Modification
        if (player==0) maxPlayer=true;else if (player==1) maxPlayer=false;

        //End
        if ((depth<=0) || (boardState.winner!=-1))
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
                BoardState currBoard = minmaxAlphaBeta(boards.get(i),depth-1,a,b,false,1-player);
                if (currBoard!=null)
                {
                    if (currBoard.value>bestVal)
                    {
                        bestVal=currBoard.value;
                        bestBoard=boards.get(i);
                        bestBoard.value=bestVal;
                    }
                    a=max(a,bestVal);
                    if (a>=b) break;
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
                BoardState currBoard = minmaxAlphaBeta(boards.get(i),depth-1,a,b,true,1-player);
                if (currBoard!=null)
                {
                    if (currBoard.value<bestVal)
                    {
                        bestVal=currBoard.value;
                        bestBoard=boards.get(i);
                        bestBoard.value=bestVal;
                    }
                    b=min(b,bestVal);
                    if (a>=b) break;
                }
            }
            return bestBoard;
        }
    }


    //Bolid player================================================================================================================================================
    //Wrapper funkcija za minmax(...)
    public static BoardState minmaxDecision(Cell[][] current_board,int depth,int player) {
        boolean maxPlayer;
        if (player==0) maxPlayer=true; else maxPlayer=false;

        BoardState boardState = new BoardState(current_board,new Point(0,0),new Point(0,0),new Point(0,0));
        BoardState bestBoard = minmax(boardState,depth,maxPlayer,player);

        if (bestBoard==null)
        {
            Log.d("SANTORINI_LOG","BEST BOARD IS NULL, NO!");
        }else{
            //Log.d("SANTORINI_LOG","From: "+ bestBoard.pointFrom.x+","+bestBoard.pointFrom.y);
            //Log.d("SANTORINI_LOG","Move: "+ bestBoard.pointMove.x+","+bestBoard.pointMove.y);
            //Log.d("SANTORINI_LOG","Build: "+ bestBoard.pointBuild.x+","+bestBoard.pointBuild.y);
            Log.d("SANTORINI_LOG","[MinMax] Final minmax value: "+ bestBoard.value);
        }

        return bestBoard;
    }
    //Rekurzivna funkcija odabiranja sledeceg poteza tezine 1
    public static BoardState minmax(BoardState boardState,int depth,boolean maxPlayer,int player){
        //Modification
        if (player==0) maxPlayer=true;else if (player==1) maxPlayer=false;

        //End
        if ((depth<=0) || (boardState.winner!=-1))
        {
            boardState.value=heuristicFunction(boardState,player);
            //Log.d("SANTORINI_LOG","heuristics calc for player: "+player+";val="+boardState.value);
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
                if (currBoard!=null && (currBoard.value>bestVal))
                {
                    bestVal=currBoard.value;
                    //Log.d("SANTORINI_LOG",depth+".bestVal_MAX: "+bestVal);
                    bestBoard=boards.get(i);
                    bestBoard.value=bestVal;
                }

            }
            Log.d("SANTORINI_LOG",depth+".bestVal_MAX: "+bestBoard.value);
            return bestBoard;
        }else
        {
            //MIN Player
            int bestVal= Integer.MAX_VALUE;
            BoardState bestBoard=null;

            for(int i=0;i<boards.size();i++)
            {
                BoardState currBoard = minmax(boards.get(i),depth-1,true,1-player);
                if (currBoard!=null && (currBoard.value<bestVal))
                {
                    bestVal=currBoard.value;
                    //Log.d("SANTORINI_LOG",depth+". bestVal_MIN: "+bestVal);
                    bestBoard=boards.get(i);
                    bestBoard.value=bestVal;
                }

            }
            Log.d("SANTORINI_LOG",depth+".bestVal_MIN: "+bestBoard.value);
            return bestBoard;
        }
    }
    //============================================================================================================================================================
    //Heuristicka funkcija za sledeci potez za tezine 1 i 2
    public static int heuristicFunction(BoardState boardState,int player) {
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
                }else if (board[i][j].getPlayer()==1-player)
                    figuresOther.add(new Point(i,j));
            }
        }
        int t;
        if (player==0) t=1; else  t=-1;

        int m=board[boardState.pointMove.x][boardState.pointMove.y].getHeight();
        int n=board[boardState.pointBuild.x][boardState.pointBuild.y].getHeight()-1;
        int ud1=Math.abs(figuresMy.get(0).x-boardState.pointBuild.x) + Math.abs(figuresMy.get(0).y-boardState.pointBuild.y);
        ud1+=Math.abs(figuresMy.get(1).x-boardState.pointBuild.x) + Math.abs(figuresMy.get(1).y-boardState.pointBuild.y);
        int ud2=Math.abs(figuresOther.get(0).x-boardState.pointBuild.x) + Math.abs(figuresOther.get(0).y-boardState.pointBuild.y);
        ud2+=Math.abs(figuresOther.get(1).x-boardState.pointBuild.x) + Math.abs(figuresOther.get(1).y-boardState.pointBuild.y);

        if (boardState.winner!=-1)
        {
            return -1*t*(100);//-1 because it is detected on level below
        }

        int func = t*(m+n*(ud1-ud2));

        //Log.d("SANTORINI_LOG","----------------------------------------------");
        //Log.d("SANTORINI_LOG","["+t+"]. heuristic func: "+func);
        //Log.d("SANTORINI_LOG","m: "+m+",n: "+n+",ud1: "+ud1+",ud2: "+ud2);
        //Log.d("SANTORINI_LOG","MY=> f1: "+figuresMy.get(0).x+","+figuresMy.get(0).y+"; f2: "+figuresMy.get(1).x+","+figuresMy.get(1).y);
        //Log.d("SANTORINI_LOG","HIS=> f1: "+figuresOther.get(0).x+","+figuresOther.get(0).y+"; f2: "+figuresOther.get(1).x+","+figuresOther.get(1).y);

        //Log.d("SANTORINI_LOG","POINT BUILD=> f1: "+boardState.pointBuild.x+","+boardState.pointBuild.y);
        //Log.d("SANTORINI_LOG","POINT MOVE=> f1: "+boardState.pointMove.x+","+boardState.pointMove.y);
        //Log.d("SANTORINI_LOG","POINT FROM=> f1: "+boardState.pointFrom.x+","+boardState.pointFrom.y);
        return func;
    }
    //Heuristicka funkcija za sledeci potez za tezinu 3
    public static int heuristicFunctionNinja(BoardState boardState,int player) {
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
                }else if (board[i][j].getPlayer()==1-player)
                    figuresOther.add(new Point(i,j));
            }
        }
        int t;
        if (player==0) t=1; else  t=-1;

        int m=board[boardState.pointMove.x][boardState.pointMove.y].getHeight();
        int n=board[boardState.pointBuild.x][boardState.pointBuild.y].getHeight()-1;
        int ud1=Math.abs(figuresMy.get(0).x-boardState.pointBuild.x) + Math.abs(figuresMy.get(0).y-boardState.pointBuild.y);
        int ud1_1=Math.abs(figuresMy.get(1).x-boardState.pointBuild.x) + Math.abs(figuresMy.get(1).y-boardState.pointBuild.y);
        int ud2=Math.abs(figuresOther.get(0).x-boardState.pointBuild.x) + Math.abs(figuresOther.get(0).y-boardState.pointBuild.y);
        int ud2_2=Math.abs(figuresOther.get(1).x-boardState.pointBuild.x) + Math.abs(figuresOther.get(1).y-boardState.pointBuild.y);

        int y=0;
        switch(m)
        {
            case 3: y=20;
                    break;
            case 2: y=15;
                    break;
            case 1: y=4;
                    break;
            case 0: y=2;
                    break;
        }


        int x;
        switch(n)
        {
            case 3: if (min(ud2,ud2_2)==1) x=10; else x=0;
                    break;
            case 2: if (min(ud2,ud2_2)==1) x=0; else x=10;
                    break;
            default: x=4;
        }

        if (boardState.winner!=-1)
        {
            return -1*t*(100);//-1 because it is detected on level below
        }

        return t*(y+x);

        //Log.d("SANTORINI_LOG","----------------------------------------------");
        //Log.d("SANTORINI_LOG","["+t+"]. heuristic func: "+func);
        //Log.d("SANTORINI_LOG","m: "+m+",n: "+n+",ud1: "+ud1+",ud2: "+ud2);
        //Log.d("SANTORINI_LOG","MY=> f1: "+figuresMy.get(0).x+","+figuresMy.get(0).y+"; f2: "+figuresMy.get(1).x+","+figuresMy.get(1).y);
        //Log.d("SANTORINI_LOG","HIS=> f1: "+figuresOther.get(0).x+","+figuresOther.get(0).y+"; f2: "+figuresOther.get(1).x+","+figuresOther.get(1).y);

        //Log.d("SANTORINI_LOG","POINT BUILD=> f1: "+boardState.pointBuild.x+","+boardState.pointBuild.y);
        //Log.d("SANTORINI_LOG","POINT MOVE=> f1: "+boardState.pointMove.x+","+boardState.pointMove.y);
        //Log.d("SANTORINI_LOG","POINT FROM=> f1: "+boardState.pointFrom.x+","+boardState.pointFrom.y);

    }
}
