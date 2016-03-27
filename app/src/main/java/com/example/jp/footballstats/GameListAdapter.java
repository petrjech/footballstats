package com.example.jp.footballstats;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

class GameListAdapter extends BaseAdapter{

    private ArrayList<Game> gameList;
    private LayoutInflater mInflater;
    private Context context;

    GameListAdapter(Context context, ArrayList<Game> myList) {
        this.gameList = myList;
        this.context = context;
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
            mViewHolder.gameListNoteLayout = (LinearLayout) convertView.findViewById(R.id.game_list_note_layout);

            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (MyViewHolder) convertView.getTag();
        }

        final Game game = (Game)getItem(position);

        String date = formatDate(game);
        mViewHolder.gameListItemDate.setText(date);

        mViewHolder.gameListItemElo.setText(String.valueOf(game.getElo()));

        //todo change result to verbal type
        mViewHolder.gameListItemResult.setText(String.valueOf(game.getResult()));

        String note = game.getNote();
        if (note.isEmpty()){
            mViewHolder.gameListNoteLayout.setVisibility(View.GONE);
        }else {
            mViewHolder.gameListNoteLayout.setVisibility(View.VISIBLE);
            mViewHolder.gameListItemNote.setText(game.getNote());
        }

        return convertView;
    }

    @NonNull
    private String formatDate(Game game) {
        String dateString;
        DateFormat dateFormatIn  = new SimpleDateFormat(StatsDataAccessObject.DATE_FORMAT, Locale.US);
        DateFormat dateFormatOut = new SimpleDateFormat(MainActivity.displayDateFormat, Locale.US);
        try {
            String gameDate = game.getDate();
            if (gameDate == null) throw (new ParseException("", 0));
            Date date = dateFormatIn.parse(gameDate);
            dateString = dateFormatOut.format(date);
        } catch (ParseException e) {
            dateString = context.getString(R.string.game_list_date_error);
        }
        return dateString;
    }

    private static class MyViewHolder {
        TextView gameListItemDate;
        TextView gameListItemElo;
        TextView gameListItemResult;
        LinearLayout gameListNoteLayout;
        TextView gameListItemNote;
    }
}


