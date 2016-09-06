package com.example.jp.footballstats;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.jp.footballstats.resources.Preferences;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

class GameListAdapter extends BaseAdapter{

    private final ArrayList<Game> gameList;
    private final LayoutInflater mInflater;
    private final Context context;

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
            mViewHolder = new MyViewHolder(convertView, context);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (MyViewHolder) convertView.getTag();
        }

        final Game game = (Game)getItem(position);

        mViewHolder.setDate(game.getDate());
        mViewHolder.setElo(game.getElo());
        mViewHolder.setResult(game.getResult());
        mViewHolder.setNote(game.getNote());

        return convertView;
    }

    private static class MyViewHolder {
        final TextView gameListItemDate;
        final TextView gameListItemElo;
        final TextView gameListItemResult;
        final LinearLayout gameListNoteLayout;
        final TextView gameListItemNote;
        final Context context;

        MyViewHolder(View convertView, Context context){
            this.gameListItemDate = (TextView) convertView.findViewById(R.id.game_list_item_date);
            this.gameListItemElo = (TextView) convertView.findViewById(R.id.game_list_item_elo);
            this.gameListItemResult = (TextView) convertView.findViewById(R.id.game_list_item_result);
            this.gameListItemNote = (TextView) convertView.findViewById(R.id.game_list_item_note);
            this.gameListNoteLayout = (LinearLayout) convertView.findViewById(R.id.game_list_note_layout);
            this.context = context;
        }

        void setDate(String gameDate){
            String dateString;
            DateFormat dateFormatIn  = new SimpleDateFormat(StatsDataAccessObject.DATE_FORMAT, Locale.US);
            try {
                if (gameDate == null) throw (new ParseException("", 0));
                Date date = dateFormatIn.parse(gameDate);
                dateString = Preferences.getFormattedDate(date);
            } catch (ParseException e) {
                dateString = context.getString(R.string.game_list_date_error);
            }
            this.gameListItemDate.setText(dateString);
        }

        void setResult(int result) {
            String resultText;
            switch(result) {
                case 2:
                    resultText = context.getString(R.string.game_list_item_result_won);
                    break;
                case 1:
                    resultText = context.getString(R.string.game_list_item_result_drawn);
                    break;
                case 0:
                    resultText = context.getString(R.string.game_list_item_result_lost);
                    break;
                default:
                    resultText = "";
            }
            this.gameListItemResult.setText(resultText);
        }

        void setNote(String note){
            if (note.isEmpty()){
                this.gameListNoteLayout.setVisibility(View.GONE);
            }else {
                this.gameListNoteLayout.setVisibility(View.VISIBLE);
                this.gameListItemNote.setText(note);
            }
        }

        void setElo(int elo){
            this.gameListItemElo.setText(String.valueOf(elo));
        }
    }
}


