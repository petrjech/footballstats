package com.example.jp.footballstats;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

class GameListAdapter extends BaseAdapter{

    private ArrayList<Game> gameList;
    private LayoutInflater mInflater;

    GameListAdapter(Context context, ArrayList<Game> myList) {
        this.gameList = myList;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return gameList.size();
    }

    @Override
    public Object getItem(int position) {
        return gameList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MyViewHolder mViewHolder;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.game_list_item, parent, false);
            mViewHolder = new MyViewHolder();
            mViewHolder.gameListItemDate = (TextView) convertView.findViewById(R.id.game_list_item_date);
            mViewHolder.gameListItemElo = (TextView) convertView.findViewById(R.id.game_list_item_elo);
            mViewHolder.gameListItemResult = (TextView) convertView.findViewById(R.id.game_list_item_result);
            mViewHolder.gameListItemNote = (TextView) convertView.findViewById(R.id.game_list_item_note);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (MyViewHolder) convertView.getTag();
        }

        final Game game = (Game)getItem(position);
        //todo use formatted date
        mViewHolder.gameListItemDate.setText(game.getDate());
        mViewHolder.gameListItemElo.setText(String.valueOf(game.getElo()));
        //todo change result to verbal type
        mViewHolder.gameListItemResult.setText(String.valueOf(game.getResult()));
        //todo hide note if empty
        mViewHolder.gameListItemNote.setText(game.getNote());

        return convertView;
    }

    private static class MyViewHolder {
        TextView gameListItemDate;
        TextView gameListItemElo;
        TextView gameListItemResult;
        TextView gameListItemNote;
    }
}


