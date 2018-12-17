package com.example.markohudomal.santorini;

import android.content.Intent;
import android.graphics.Color;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.example.markohudomal.santorini.algorithm.Cell;
import com.example.markohudomal.santorini.algorithm.Game;
import com.example.markohudomal.santorini.algorithm.MinMax;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GameActivity extends AppCompatActivity {

    public final static int BOARD_WIDTH = 5;

    public final static int COLOR_PLAYER_0 = (Color.parseColor("#52a8b8"));
    public final static int COLOR_PLAYER_1 = (Color.parseColor("#bb4055"));
    public final static int COLOR_SELECTED = Color.parseColor("#eee8aa");
    public final static int COLOR_WON = Color.parseColor("#FFD700");

    public final static int STATE_INIT=0;
    public final static int STATE_MOVE=1;
    public final static int STATE_BUILD=2;
    public final static int STATE_END=3;

    public final static String gamePath="/storage/emulated/0/SantoriniGame";

    public final String STATES[] = {"set figures","move","build","won"};

    private RecyclerView recyclerView;
    private MyAdapter mAdapter;
    private TextView textViewPlayer;
    private TextView textViewState;

    private static Cell[][] mCells = new Cell[BOARD_WIDTH][BOARD_WIDTH];
    public Game myGame;
    public int game_mode;
    public boolean load_file;
    public int game_depth;
    public int bot_view;

    public static PrintWriter output;
    //STATES
    public static int player_turn=-1;
    public static int player_state=-1;


    //CREATE========================================================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        Log.d("SANTORINI_LOG","Game started..");

        //Read Intent
        Intent intent = getIntent();
        game_mode=intent.getIntExtra("game_mode",-1);
        load_file=intent.getBooleanExtra("load_file",false);
        game_depth=intent.getIntExtra("depth",-1);
        bot_view=intent.getIntExtra("bot_view",-1);
        Log.d("SANTORINI_LOG","game_mode:"+game_mode+", load_file:"+load_file+", game_depth:"+game_depth+", bot_view:"+bot_view);


        //TextViews
        textViewPlayer = findViewById(R.id.text_left);
        textViewState = findViewById(R.id.text_right);

        //RecyclerView
        recyclerView=findViewById(R.id.my_recycler_view);
        mAdapter = new MyAdapter(this,mCells);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this,BOARD_WIDTH));

        //New Game
        initalizeBoard();
        output = initalizeOutputFile();
        myGame=new Game(output);


        //Load from file
        if (load_file)
        {
            readGameStateInputFile("Input_Santorini.txt");
        }else
        {
            Log.d("SANTORINI_LOG","no loading from file");
        }
    }
    //==============================================================================================
    private void initalizeBoard()
    {
        for(int i=0;i<BOARD_WIDTH;i++)
        {
            for(int j=0;j<BOARD_WIDTH;j++)
            {
                mCells[i][j] = new Cell(0,-1,getResources().getColor(R.color.white),i,j);
            }
        }
        player_turn=0;
        player_state=STATE_INIT;
        setTitle();
    }
    private PrintWriter initalizeOutputFile(){

        //Write external text file
        PrintWriter out=null;
        //String timeStamp = new SimpleDateFormat("yyyMMdd_HHmmss").format(new Date());
        String fileName = "Output_Santorini_"+ "main" + ".txt";
        try {
//          File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
            File storageDir = new File(gamePath);
            File file = new File(storageDir,fileName);
            Log.d("FILE",storageDir.toString());
            out = new PrintWriter(file);
//            String result = "start>" + System.getProperty("line.separator");
//            out.append(result);
//            out.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return out;
    }
    private void readGameStateInputFile(String fileName)
    {
        BufferedReader in;
        int line_num=-1;
        int player=0;
        try {
            in = new BufferedReader(new FileReader(new File(gamePath,fileName)));
            String line;
            String temp;
            while((temp=in.readLine())!= null)
            {
                line=temp;
                line_num++;
                String[] data = line.split(" ");

                player=line_num%2;
                if (line_num<2) {

                    //setFigures------------------------------------------------
                    int i=Game.decodeX(data[0]);
                    int j=Game.decodeY(data[0]);
                    myGame.setFigure(mCells,i,j,player);
                        i=Game.decodeX(data[1]);
                        j=Game.decodeY(data[1]);
                    myGame.setFigure(mCells,i,j,player);
                    //setFigures------------------------------------------------

                    //turn and board state
                    player_turn=1-player;
                    if (line_num==0) player_state=STATE_INIT;
                        else player_state=STATE_MOVE;
                    //--------------------
                    Log.d("SANTORINI_LOG","["+(player)+"] initalize: "+data[0]+" and "+data[1]);

                }
                else {
                    //move, build------------------------------------------------
                    //Select cell
                    int i=Game.decodeX(data[0]);
                    int j=Game.decodeY(data[0]);
                    myGame.selectedCell=mCells[i][j];

                    //Move to
                    i=Game.decodeX(data[1]);
                    j=Game.decodeY(data[1]);
                    myGame.move(mCells,i,j,player);

                    //Build on
                    i=Game.decodeX(data[2]);
                    j=Game.decodeY(data[2]);
                    myGame.build(mCells,i,j,player);
                    //move, build------------------------------------------------

                    //turn and board state
                    player_turn=1-player;
                    player_state=STATE_MOVE;
                    //--------------------
                    Log.d("SANTORINI_LOG","["+(player)+"] move,build: "+data[0]+" -> "+data[1]+", "+data[2]);

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }



        if (myGame.winner!=-1)
        {
            player_turn=myGame.winner;
            player_state=STATE_END;
        }

        setTitle();
        mAdapter.refreshBoard();
        Toast.makeText(this, "Moves loaded from file..", Toast.LENGTH_SHORT).show();
        Log.d("SANTORINI_LOG","Loading from file ended..");
    }
    public static void writeOutputFile(PrintWriter pw, String text)
    {
        //Log.d("SANTORINI",text);
        pw.append(text);
        pw.flush();
    }
    private void setTitle()
    {
        textViewPlayer.setText("Player "+Integer.toString(player_turn+1));
        switch (player_turn)
        {
            case 0:{textViewPlayer.setTextColor(COLOR_PLAYER_0);break;}
            case 1:{textViewPlayer.setTextColor(COLOR_PLAYER_1);break;}
        }
        textViewState.setText(STATES[player_state]);
        mAdapter.notifyDataSetChanged();
    }

    public void nextMove()
    {
        if (myGame.nextMove)
        {
            myGame.nextMove=false;
            switch (player_state)
            {
                case GameActivity.STATE_INIT:{
                    if (player_turn==1)
                    {
                        player_state=GameActivity.STATE_MOVE;
                    }
                    player_turn=(1-player_turn);

                    break;
                }
                case GameActivity.STATE_MOVE:{
                    player_state=GameActivity.STATE_BUILD;
                    break;
                }
                case GameActivity.STATE_BUILD:{
                    player_turn=(1-player_turn);
                    player_state=GameActivity.STATE_MOVE;
                    mAdapter.refreshBoard();

                    break;
                }
                case GameActivity.STATE_END:{
                    //mAdapter.refreshBoard();
                    player_turn=myGame.winner;
                    textViewPlayer.setTextSize((float)35.0);
                    textViewState.setTextColor(GameActivity.COLOR_WON);
                    textViewState.setTextSize((float)35.0);

                    recyclerView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.game_end_anim));
                    recyclerView.setAlpha((float)0.6);
                    break;
                }
                default:{
                    //Toast.makeText(this, "Error!", Toast.LENGTH_SHORT).show();
                    break;
                }

            }
            textViewPlayer.startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.slide_out_right));
            textViewState.startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.slide_out_right));
            setTitle();
            mAdapter.refreshBoard();
        }else
        {
            //Toast.makeText(this, "can't proceed", Toast.LENGTH_SHORT).show();
        }
    }


    public void onTipsClick(View view)
    {
        if(game_mode!=MainActivity.GAME_MODE_HUMAN_BOT)
        {
            ((TextView)view).setText("no");
        }else
        {
            ((TextView)view).setText("calc...");
        }
        MinMax.BoardState bs = MinMax.minmaxDecision(mCells,game_depth,player_turn);
        if (bs!=null)
        {
//            for(int i=0;i<GameActivity.BOARD_WIDTH;i++)
//            {
//                    Log.d("SANTORINI_LOG",bs.board[i][0].player+" "+bs.board[i][1].getPlayer()+" "+bs.board[i][2].getPlayer()+" "+bs.board[i][3].getPlayer()+" "+bs.board[i][4].getPlayer());
//            }
            ((TextView)view).setText("From:"+ bs.pointFrom.x+","+bs.pointFrom.y +";Move: "+ bs.pointMove.x+","+bs.pointMove.y+";Build: "+ bs.pointBuild.x+","+bs.pointBuild.y);
            Log.d("SANTORINI_LOG","From: "+ bs.pointFrom.x+","+bs.pointFrom.y);
            Log.d("SANTORINI_LOG","Move: "+ bs.pointMove.x+","+bs.pointMove.y);
            Log.d("SANTORINI_LOG","Build: "+ bs.pointBuild.x+","+bs.pointBuild.y);
        }
    }
    public void onBackClick(View view)
    {
        //Intent res = new Intent();
        setResult(RESULT_OK);
        finish();
    }
}
