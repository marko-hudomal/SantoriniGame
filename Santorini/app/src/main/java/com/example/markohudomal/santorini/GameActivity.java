package com.example.markohudomal.santorini;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.example.markohudomal.santorini.struct.Cell;
import com.example.markohudomal.santorini.algorithm.Game;
import com.example.markohudomal.santorini.algorithm.MinMax;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

public class GameActivity extends AppCompatActivity {

    public final static int BOARD_WIDTH = 5;

    public final static int COLOR_PLAYER_0 = (Color.parseColor("#52a8b8"));
    public final static int COLOR_PLAYER_1 = (Color.parseColor("#bb4055"));
    public final static int COLOR_SELECTED = Color.parseColor("#eee8aa");
    public final static int COLOR_BUILD_TOP = Color.parseColor("#150000");
    public final static int COLOR_WON = Color.parseColor("#FFD700");


    public final static String gamePath="/storage/emulated/0/SantoriniGame";

    public final String STATES[] = {"set figures","move","build","won"};

    private RecyclerView recyclerView;
    private MyAdapter mAdapter;
    private TextView textViewPlayer;
    private TextView textViewState;

    private static Cell[][] mCells = new Cell[BOARD_WIDTH][BOARD_WIDTH];
    public Game myGame;

    //From intent
    public int game_mode;
    public boolean load_file;
    public int game_depth;
    public int bot_view;

    public static PrintWriter output;

    //CREATE========================================================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        Log.d("SANTORINI_LOG","Game started..");

        //Output and Game.class
        output = initalizeOutputFile();
        myGame=new Game(this,mCells,output);



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
        mAdapter = new MyAdapter(this,mCells,myGame);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this,BOARD_WIDTH));


        initalizeBoard(myGame);

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
    private void initalizeBoard(Game myGame)
    {
        for(int i=0;i<BOARD_WIDTH;i++)
        {
            for(int j=0;j<BOARD_WIDTH;j++)
            {
                mCells[i][j] = new Cell(0,-1,getResources().getColor(R.color.white),i,j);
            }
        }
        myGame.player_turn=0;
        myGame.player_state=Game.STATE_INIT;
        setTitle();
    }

    //Output----------------------------------------------------------------------------------------
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
    //Input-----------------------------------------------------------------------------------------
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
                    myGame.player_turn=1-player;
                    if (line_num==0) myGame.player_state=Game.STATE_INIT;
                        else myGame.player_state=Game.STATE_MOVE;
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
                    myGame.player_turn=1-player;
                    myGame.player_state=Game.STATE_MOVE;
                    //--------------------
                    Log.d("SANTORINI_LOG","["+(player)+"] move,build: "+data[0]+" -> "+data[1]+", "+data[2]);

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }



        if (myGame.winner!=-1)
        {
            myGame.player_turn=myGame.winner;
            myGame.player_state=Game.STATE_END;
            setTitleWon();
        }
        setTitle();
        mAdapter.refreshBoard();
        Toast.makeText(this, "Moves loaded from file..", Toast.LENGTH_SHORT).show();
        Log.d("SANTORINI_LOG","Loading from file ended..");
    }
//    public static void writeOutputFile(PrintWriter pw, String text)
//    {
//        //Log.d("SANTORINI",text);
//        pw.append(text);
//        pw.flush();
//    }


    @SuppressLint("SetTextI18n")
    private void setTitle()
    {
        textViewPlayer.setText("Player "+Integer.toString(myGame.player_turn+1));
        switch (myGame.player_turn)
        {
            case 0:{textViewPlayer.setTextColor(COLOR_PLAYER_0);break;}
            case 1:{textViewPlayer.setTextColor(COLOR_PLAYER_1);break;}
        }
        textViewState.setText(STATES[myGame.player_state]);
        mAdapter.notifyDataSetChanged();
    }
    public void setTitleWon()
    {
        textViewPlayer.setTextSize((float)35.0);
        textViewState.setTextColor(GameActivity.COLOR_WON);
        textViewState.setTextSize((float)35.0);
        recyclerView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.game_end_anim));
        recyclerView.setAlpha((float)0.6);
    }
    public void refreshBoard()
    {
        mAdapter.refreshBoard();
    }
    public void nextMoveRefresh()
    {
        textViewPlayer.startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.slide_out_right));
        textViewState.startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.slide_out_right));
        setTitle();
        mAdapter.refreshBoard();
    }


    @SuppressLint("SetTextI18n")
    public void onTipsClick(View view)
    {
        if(game_mode!=MainActivity.GAME_MODE_HUMAN_BOT)
        {
            ((TextView)view).setText("no");
            return;
        }else
        {
            ((TextView)view).setText("calc...");
        }
        MinMax.BoardState bs = MinMax.minmaxDecision(mCells,game_depth,myGame.player_turn);
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
