package com.example.markohudomal.santorini.algorithm;

import android.graphics.Color;
import android.util.Log;

import com.example.markohudomal.santorini.GameActivity;

import java.io.PrintWriter;

public class Game {
    public int figures[]={0,0};
    public Cell selectedCell=null;
    public boolean nextMove=false;
    public int winner = -1;
    public PrintWriter output;

    public Game(PrintWriter output)
    {
        this.output=output;
    }
    public Cell[][] setFigure(Cell [][] matrix, int click_x,int click_y,int player)
    {
        nextMove=false;
        //uncheck
        if (matrix[click_x][click_y].getPlayer()==player)
        {
            //Izbacen undo
//            figures[player]--;
//            matrix[click_x][click_y].setPlayer(-1);
//            return matrix;
              return null;
        }
        //two already checked
        if (figures[player]==2) return null;

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
        return matrix;
    }
    public Cell[][] move(Cell [][] matrix,int click_x,int click_y,int player)
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
                return matrix;
            }

            //Log.d("SANTORINI",""+not_on_player+","+not_4+","+next_to_last_position+","+correct_step+","+not_me);
        }
        return null;
    }
    public Cell[][] build(Cell [][] matrix,int click_x,int click_y,int player)
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
                matrix[click_x][click_y].incHeight();
                nextMove=true;
                selectedCell=null;

                //PLAYER WON
                if (!canMove(matrix,1-player))
                {
                    winner=player;
                }
                return matrix;
            }

            //Log.d("SANTORINI",""+not_on_player+","+not_4+","+next_to_last_position+","+not_me);
        }


        return null;
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
}
