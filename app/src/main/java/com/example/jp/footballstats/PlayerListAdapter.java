package com.example.jp.footballstats;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

class PlayerListAdapter extends BaseAdapter {

    private final List<Player> playerList; // = new ArrayList<>();
    private final LayoutInflater mInflater;

    PlayerListAdapter(Context context, ArrayList<Player> myList) {
        this.playerList = myList;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return playerList.size();
    }

    @Override
    public Object getItem(int position) {
        return playerList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MyViewHolder mViewHolder;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.search_result_item, parent, false);
            mViewHolder = new MyViewHolder(convertView);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (MyViewHolder) convertView.getTag();
        }

        Player player = (Player)getItem(position);

        mViewHolder.tvPlayer.setText(player.getName());

        return convertView;
    }

    private class MyViewHolder {
        final TextView tvPlayer;

        MyViewHolder(View item) {
            tvPlayer = (TextView) item.findViewById(R.id.tvPlayer);
        }
    }
}
