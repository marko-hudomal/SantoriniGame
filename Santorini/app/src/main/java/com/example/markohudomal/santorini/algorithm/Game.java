package com.example.markohudomal.santorini.algorithm;

import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;

import com.example.markohudomal.santorini.GameActivity;
import com.example.markohudomal.santorini.MainActivity;
import com.example.markohudomal.santorini.struct.Cell;

import java.io.PrintWriter;

public class Game {

    //GAME STATE
    public final static int STATE_INIT=0;
    public final static int STATE_MOVE=1;
    public final static int STATE_BUILD=2;
    public final static int STATE_END=3;

    //Current game turn and state
    public Cell[][] mCells;
    public int player_turn=-1;
    public int player_state=-1;
    public int winner = -1;
    //Current extras
    private int figures[]={0,0};
    public Cell selectedCell=null;
    public boolean nextMove=false;

    private GameActivity myActivity;
    private PrintWriter output;


    public MinMax.BoardState lastBoard=null;
    public Point p_from=null;
    public Point p_move=null;
    public Point p_build=null;

    public Game(GameActivity myActivity,Cell[][] mCells, PrintWriter output)
    {
        this.output=output;
        this.mCells=mCells;
        this.myActivity=myActivity;
    }

    public String playNextMove(int coordX,int coordY){

        String ret_message=null;
        switch(player_state){
            case Game.STATE_INIT:{

                boolean success = setFigure(mCells,coordX,coordY,player_turn);
                if (success) {
                    //:)
                    myActivity.refreshBoard();//could not be next move
                }else
                {
                    ret_message = "False init!";
                }
                break;
            }
            case Game.STATE_MOVE:{
                boolean success = move(mCells,coordX,coordY,player_turn);
                if (success) {
                    //:)
                }else
                {
                    ret_message="False move!";
                }

                if (winner!=-1)
                {
                    player_state=Game.STATE_END;
                }
                break;
            }
            case Game.STATE_BUILD:{
                boolean success = build(mCells,coordX,coordY,player_turn);
                if (success) {
                    //:)
                }else
                {
                    ret_message="False build!";
                }
                if (winner!=-1)
                {
                    player_state=Game.STATE_END;
                }
                break;
            }
            case Game.STATE_END:{
                ret_message="Game is over.";
                return ret_message;
            }
            default:{
                ret_message="Error";
            }
        }

        nextState();
        return ret_message;
    }
    public void nextState()
    {
            if (nextMove)
            {
                //Log.d("SANTORINI_LOG","nextState (nextMove=true)");
                nextMove=false;
                switch (player_state)
                {
                    case Game.STATE_INIT:{
                        if (player_turn==1)
                        {
                            player_state=Game.STATE_MOVE;
                        }
                        player_turn=(1-player_turn);
                        break;
                    }
                    case Game.STATE_MOVE:{
                        player_state=Game.STATE_BUILD;
                        break;
                    }
                    case Game.STATE_BUILD:{
                        player_turn=(1-player_turn);
                        player_state=Game.STATE_MOVE;
                        myActivity.refreshBoard();

                        break;
                    }
                    case Game.STATE_END:{
                        //mAdapter.refreshBoard();
                        player_turn=winner;
                        myActivity.setTitleWon();
                        break;
                    }
                    default:{
                        //Toast.makeText(this, "Error!", Toast.LENGTH_SHORT).show();
                        break;
                    }

                }

                myActivity.nextMoveRefresh();

            }else
            {
                //Log.d("SANTORINI_LOG","nextState (nextMove=false)");
                //Toast.makeText(this, "can't proceed", Toast.LENGTH_SHORT).show();
            }

            //Automatic Next move goes
            if ((GameActivity.bot_view==1) && (GameActivity.bots[player_turn]))
            {
                Log.d("SANTORINI_LOG","Bots now plays");
                botPlayNextMove(GameActivity.difficulty);
            }
    }
    public boolean setFigure(Cell [][] matrix, int click_x,int click_y,int player)
    {
        nextMove=false;

        //uncheck
        if (matrix[click_x][click_y].getPlayer()==player)
        {
            //Izbacen undo
            //figures[player]--;
            //matrix[click_x][click_y].setPlayer(-1);
            //return matrix;
            return false;
        }
        //two already checked
        if (figures[player]==2) return false;

        //check
        figures[player]++;
        matrix[click_x][click_y].setPlayer(player);
        //Print init--------------------------------------
            printPosOutputFile(click_x,click_y);
        //------------------------------------------------

        if (figures[player]==2) {
            //Print line--------------------------------------
                output.append(System.getProperty("line.separator"));
                output.flush();
            //------------------------------------------------
            nextMove=true;
        }
        return true;
    }
    public boolean move(Cell [][] matrix,int click_x,int click_y,int player)
    {
        nextMove=false;
        if (selectedCell!=null)
        {
            boolean not_on_player = matrix[click_x][click_y].getPlayer()==-1;
            boolean not_4 = matrix[click_x][click_y].getHeight()<4;
            boolean next_to_last_position =  (Math.abs(selectedCell.getX()-click_x)<=1) && (Math.abs(selectedCell.getY()-click_y)<=1);
            boolean correct_step = selectedCell.getHeight()+1 >= matrix[click_x][click_y].getHeight();
            boolean not_me = !((selectedCell.getX()==click_x) && (selectedCell.getY()==click_y));
            if (not_on_player && next_to_last_position && not_4 && correct_step && not_me){
                //Print move--------------------------------------
                printPosOutputFile(selectedCell.x,selectedCell.y);
                printPosOutputFile(click_x,click_y);
                //------------------------------------------------
                p_from=new Point(selectedCell.x,selectedCell.y);//For BoardState
                p_move=new Point(click_x,click_y);//For BoardState

                selectedCell.setPlayer(-1);
                selectedCell.setColor(Color.WHITE);
                matrix[click_x][click_y].setPlayer(player);

                selectedCell=matrix[click_x][click_y];

                nextMove=true;

                //PLAYER WON
                if (selectedCell.getHeight()==3)
                {
                    winner=player;
                }
                return true;
            }

            //Log.d("SANTORINI",""+not_on_player+","+not_4+","+next_to_last_position+","+correct_step+","+not_me);
        }
        return false;
    }
    public boolean build(Cell [][] matrix,int click_x,int click_y,int player)
    {
        nextMove=false;
        if (selectedCell!=null)
        {
            boolean not_on_player = matrix[click_x][click_y].getPlayer()==-1;
            boolean not_4 = matrix[click_x][click_y].getHeight()<4;
            boolean next_to_last_position =  (Math.abs(selectedCell.getX()-click_x)<=1) && (Math.abs(selectedCell.getY()-click_y)<=1);
            boolean not_me = !((selectedCell.getX()==click_x) && (selectedCell.getY()==click_y));
            if (not_on_player && next_to_last_position && not_4 && not_me){
                //Print build-------------------------------------
                printLinePosOutputFile(click_x,click_y);
                //------------------------------------------------
                p_build=new Point(click_x,click_y);//For BoardState
                matrix[click_x][click_y].incHeight();
                nextMove=true;
                selectedCell=null;

                //PLAYER WON
                if (!canMove(matrix,1-player))
                {
                    winner=player;
                }

                lastBoard = new MinMax.BoardState(mCells,p_move,p_build,p_from);
                MinMax.heuristicFunction(lastBoard,player_turn);
                return true;
            }

            //Log.d("SANTORINI",""+not_on_player+","+not_4+","+next_to_last_position+","+not_me);
        }

        return false;
    }
    public static boolean canMove(Cell[][] matrix,int player)
    {
        for(int i = 0; i<GameActivity.BOARD_WIDTH; i++)
        {
            for(int j=0;j<GameActivity.BOARD_WIDTH;j++)
            {
                if (matrix[i][j].getPlayer()==player)
                {
                      //OLD
//                    boolean up = (i!=0) && (matrix[i-1][j].getPlayer()==-1) && (matrix[i-1][j].getHeight()<=matrix[i][j].getHeight()+1);
//                    boolean down = (i!=(GameActivity.BOARD_WIDTH-1)) && (matrix[i+1][j].getPlayer()==-1) && (matrix[i+1][j].getHeight()<=(matrix[i][j].getHeight()+1));
//                    boolean left = (j!=0) && (matrix[i][j-1].getPlayer()==-1) && (matrix[i][j-1].getHeight()<=matrix[i][j].getHeight()+1);
//                    boolean right = (j!=(GameActivity.BOARD_WIDTH-1)) && (matrix[i][j+1].getPlayer()==-1) && (matrix[i][j+1].getHeight()<=(matrix[i][j].getHeight()+1));
//
                    for(int k=i-1;k<=i+1;k++)
                    {
                        for(int g=j-1;g<=j+1;g++)
                        {
                            if ((g>=GameActivity.BOARD_WIDTH) || (g<0)) continue;
                            if ((k>=GameActivity.BOARD_WIDTH) || (k<0)) continue;
                            if (matrix[k][g].getPlayer()!=-1) continue;
                            if (matrix[k][g].getHeight()>(matrix[i][j].getHeight()+1)) continue;
                            if (matrix[k][g].getHeight()==4) continue;
                            return true;
                        }
                    }

                }
            }
        }
        return false;
    }


    public void printPosOutputFile(int i, int j)
    {
        output.append((char)((int)'A' + i)+""+(j+1)+" ");
        output.flush();
    }
    public void printLinePosOutputFile(int i,int j)
    {
        output.append((char)((int)'A' + i)+""+(j+1)+System.getProperty("line.separator"));
        output.flush();
    }
    public static int decodeX(String data)
    {
        char c = data.charAt(0);
        return ((int)c-(int)'A');
    }
    public static int decodeY(String data)
    {
        int x = Integer.parseInt(data.charAt(1)+"");
        return x-1;
    }

    //Bot

    public Point randCell() {
        int x;
        int y;
        do{
            int num = (int)(Math.random() * GameActivity.BOARD_WIDTH*GameActivity.BOARD_WIDTH);
            x= num/GameActivity.BOARD_WIDTH;
            y= num%GameActivity.BOARD_WIDTH;
        }while(mCells[x][y].getPlayer()!=-1);

        return new Point(x,y);
    }
    public void botPlayNextMove(int difficulty)
    {
        int game_depth=GameActivity.game_depth;

        switch(player_state){
            case Game.STATE_INIT:{
                Point point = randCell();
                boolean success = setFigure(mCells,point.x,point.y,player_turn);
                if (success) {
                    myActivity.refreshBoard();//not necessary next move
                }
                break;
            }
            case Game.STATE_MOVE:{

                //Choose difficulty
                MinMax.BoardState bs;
                switch(difficulty)
                {
                    case 1:bs = MinMax.minmaxAlphaBetaDecision(mCells,game_depth,player_turn);break;
                    case 2:bs = MinMax.minmaxAlphaBetaDecision(mCells,game_depth,player_turn);break;
                    default:bs= MinMax.minmaxDecision(mCells,game_depth,player_turn);break;
                }


                if (bs!=null) {
                    selectedCell=mCells[bs.pointFrom.x][bs.pointFrom.y];
                    move(mCells,bs.pointMove.x,bs.pointMove.y,player_turn);
                    if (winner!=-1) {
                        player_state=Game.STATE_END;
                        Log.d("SANTORINI_LOG","Game ended with move!");
                        break;
                    }
                    build(mCells,bs.pointBuild.x,bs.pointBuild.y,player_turn);
                    if (winner!=-1) {
                        player_state=Game.STATE_END;
                        Log.d("SANTORINI_LOG","Game ended with build!");
                        break;
                    }
                    player_state=Game.STATE_BUILD;//Build and move in one move
                }
                break;
            }
            case Game.STATE_BUILD:{
                //No use
                break;
            }
            case Game.STATE_END:{
                return;
            }
            default:{

            }
        }

        nextState();
        return ;
    }



}
