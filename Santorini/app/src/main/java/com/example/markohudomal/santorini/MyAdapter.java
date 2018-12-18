package com.example.markohudomal.santorini;


import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.markohudomal.santorini.struct.Cell;
import com.example.markohudomal.santorini.algorithm.Game;

import static com.example.markohudomal.santorini.GameActivity.*;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MojHolder> {

    public static int[][] pictures = {{R.drawable.t00,R.drawable.t01,R.drawable.t0},{R.drawable.t10,R.drawable.t11,R.drawable.t1},{R.drawable.t20,R.drawable.t21,R.drawable.t2},{R.drawable.t30,R.drawable.t31,R.drawable.t3},{R.drawable.t4,R.drawable.t4,R.drawable.t4}};


    private Cell[][] mCells;
    private Game myGame;

    private GameActivity mContext;

    public MyAdapter(GameActivity mContext, Cell[][] mCells, Game myGame) {
        this.mContext = mContext;
        this.mCells = mCells;
        this.myGame=myGame;
    }

    @NonNull
    @Override
    public MojHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View view = layoutInflater.inflate(R.layout.my_holder, viewGroup, false);
        return new MojHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MojHolder mojHolder, int position) {
        Cell cell = mCells[position / GameActivity.BOARD_WIDTH][position % GameActivity.BOARD_WIDTH];
        mojHolder.setValues(cell);
    }

    @Override
    public int getItemCount() {
        if (mCells!=null)
            return mCells.length*mCells.length;
        else
            return 0;
    }

    class MojHolder extends RecyclerView.ViewHolder {

        private TextView mHeight;
        private CardView mCard;
        private LinearLayout mCardInside;

        //private TextView mDatum;
        private int coordX;
        private int coordY;

        public MojHolder(@NonNull View itemView) {
            super(itemView);

            //CLICK LISTENERS
            //----------------------------------------------------------------------------------------------------------------
            itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    //Toast.makeText(mContext, "cell:"+coordX+":"+coordY, Toast.LENGTH_SHORT).show();
                    if (bots[myGame.player_turn])
                    {
                        Toast.makeText(mContext, "bot is playing!", Toast.LENGTH_SHORT).show();
                        Log.d("SANTORINI_LOG","short click: bot is playing!");
                        return;
                    }

                    //TRY NEXT MOVE
                    String ret_message = myGame.playNextMove(coordX,coordY);

                    if (ret_message!=null)
                        Toast.makeText(mContext, ret_message, Toast.LENGTH_SHORT).show();

                    v.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.my_anim1));
               }
               });
            //----------------------------------------------------------------------------------------------------------------
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    //Toast.makeText(mContext, "Selected ["+coordX+":"+coordY+"]", Toast.LENGTH_SHORT).show();
                    if (bots[myGame.player_turn])
                    {
                        Toast.makeText(mContext, "bot is playing!", Toast.LENGTH_SHORT).show();
                        Log.d("SANTORINI_LOG","long click: bot is playing!");
                        return true;
                    }

                    if (myGame.player_state!=Game.STATE_MOVE) return true;
                    if (mCells[coordX][coordY].getPlayer()!=myGame.player_turn)
                    {
                        Toast.makeText(mContext, "No figure on that position!", Toast.LENGTH_SHORT).show();
                        v.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.my_anim2));
                        return true;
                    }
                    mContext.myGame.selectedCell=mCells[coordX][coordY];
                    refreshBoard();

                    v.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.my_anim2));
                    return true;
                }
            });
            //------------------------------------------------------------------------------------------------------------------
            mCard = itemView.findViewById(R.id.card_id);
            mCardInside = itemView.findViewById(R.id.card_inside_id);
            mHeight = itemView.findViewById(R.id.my_holder_text_view_height);
        }

        public void setValues(Cell cell) {
                            int p;
                if (cell.getPlayer()==-1) p=2;else p=cell.getPlayer();
                mCard.setBackground(mContext.getResources().getDrawable(pictures[cell.getHeight()][p]));
                if (cell.getColor()==GameActivity.COLOR_SELECTED)
                {
                    mCardInside.setBackgroundColor(GameActivity.COLOR_SELECTED);
                    mCardInside.setAlpha((float)0.45);
                }
                //mCardInside.setBackgroundColor(cell.getColor());
                //mHeight.setText(""+cell.getHeight());
                coordX = cell.getX();
                coordY = cell.getY();


        }
    }

    public void refreshBoard()
    {

        for(int i=0;i<GameActivity.BOARD_WIDTH;i++)
        {
            for(int j=0;j<GameActivity.BOARD_WIDTH;j++)
            {
                switch(mCells[i][j].getPlayer()){

                    case 0: {
                        mCells[i][j].setColor(GameActivity.COLOR_PLAYER_0);
                        break;
                    }
                    case 1: {
                        mCells[i][j].setColor(GameActivity.COLOR_PLAYER_1);
                        break;
                    }
                    default:{
                        mCells[i][j].setColor(Color.WHITE);
                        break;
                    }
                }
                if (mCells[i][j]==mContext.myGame.selectedCell)
                {
                    mCells[i][j].setColor(GameActivity.COLOR_SELECTED);
                }
                if (mCells[i][j].getHeight()==4)
                {
                    mCells[i][j].setColor(GameActivity.COLOR_BUILD_TOP);
                }
            }
        }

        notifyDataSetChanged();

    }
}
