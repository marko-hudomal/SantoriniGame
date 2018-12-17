package com.example.markohudomal.santorini;


import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.markohudomal.santorini.algorithm.Cell;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MojHolder> {

    private Cell[][] mCells;

    private GameActivity mContext;

    public MyAdapter(GameActivity mContext, Cell[][] mCells) {
        this.mContext = mContext;
        this.mCells = mCells;
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

            //CLICK
            //----------------------------------------------------------------------------------------------------------------
            itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    v.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.my_anim1));
                    //Toast.makeText(mContext, "cell:"+coordX+":"+coordY, Toast.LENGTH_SHORT).show();


                    switch(GameActivity.player_state){
                        case GameActivity.STATE_INIT:{

                            Cell[][] ret=mContext.myGame.setFigure(mCells,coordX,coordY,GameActivity.player_turn);
                            if (ret!=null) {
                                mCells=ret;
                            }else
                            {
                                Toast.makeText(mContext, "False init!", Toast.LENGTH_SHORT).show();
                            }
                            refreshBoard();

                            break;
                        }
                        case GameActivity.STATE_MOVE:{
                            Cell[][] ret = mContext.myGame.move(mCells,coordX,coordY,GameActivity.player_turn);
                                if (ret!=null) {
                                    mCells=ret;
                                }else
                                {
                                    Toast.makeText(mContext, "False move!", Toast.LENGTH_SHORT).show();
                                }

                            if (mContext.myGame.winner!=-1)
                            {
                                GameActivity.player_state=GameActivity.STATE_END;
                            }
                            break;
                        }
                        case GameActivity.STATE_BUILD:{
                            Cell[][] ret = mContext.myGame.build(mCells,coordX,coordY,GameActivity.player_turn);
                                if (ret!=null) {
                                    mCells=ret;
                                }else
                                {
                                    Toast.makeText(mContext, "False build!", Toast.LENGTH_SHORT).show();
                                }
                            if (mContext.myGame.winner!=-1)
                            {
                                GameActivity.player_state=GameActivity.STATE_END;
                            }
                            break;
                        }
                        case GameActivity.STATE_END:{
                                Toast.makeText(mContext, "Game is over", Toast.LENGTH_SHORT).show();

                            break;
                        }
                        default:{
                            Toast.makeText(mContext, "Error!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    mContext.nextMove();
               }
               });
            //----------------------------------------------------------------------------------------------------------------
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    v.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.my_anim2));
                    if (GameActivity.player_state!=GameActivity.STATE_MOVE) return true;

                    if (mCells[coordX][coordY].getPlayer()!=GameActivity.player_turn)
                    {
                        Toast.makeText(mContext, "No figure on that position!", Toast.LENGTH_SHORT).show();
                        return true;
                    }
                    mContext.myGame.selectedCell=mCells[coordX][coordY];
                    mContext.myGame.selectedCell.setColor(GameActivity.COLOR_SELECTED);
                    //Toast.makeText(mContext, "Selected ["+coordX+":"+coordY+"]", Toast.LENGTH_SHORT).show();
                    refreshBoard();
                    return true;
                }
            });
            //------------------------------------------------------------------------------------------------------------------
            mCard = itemView.findViewById(R.id.card_id);
            mCardInside = itemView.findViewById(R.id.card_inside_id);
            mHeight = itemView.findViewById(R.id.my_holder_text_view_height);
        }

        public void setValues(Cell cell) {
            //mCard.setBackgroundColor(cell.getColor());
            mCardInside.setBackgroundColor(cell.getColor());
            mHeight.setText(""+cell.getHeight());
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
                if (mCells[i][j]==mContext.myGame.selectedCell){
                    mCells[i][j].setColor(GameActivity.COLOR_SELECTED);
                }
            }
        }

        notifyDataSetChanged();
    }
}
