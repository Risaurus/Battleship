package edu.gmu.cs477.program1_battleship_ahwang4;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class DisplayAdapter extends RecyclerView.Adapter<DisplayAdapter.MyViewHolder> {
    // if String @ Position =
    //      { "1", "2", "3", "4", "5" } = SHIP LOCATION
    //      "0" = default EMPTY LOCATION
    //      "-1" = computer HIT SHIP
    //      "-2" = computer MISSED
    private ArrayList<String> board;
    private Context context;

    public DisplayAdapter(Context context, ArrayList<String> board) {
        this.context = context; this.board = board;
    }

    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(this.context);
        View view = inflater.inflate(R.layout.grid_cell, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        // if String @ Position =
        //      { "1", "2", "3", "4", "5" } = SHIP LOCATION
        //      "0" = default EMPTY LOCATION
        //      "-1" = computer HIT SHIP
        //      "-2" = computer MISSED
        switch(this.board.get(position)) {
            case "0":
                holder.myText.setBackgroundResource(R.color.default_cell);
                break;
            case "-1":
                holder.myText.setBackgroundResource(R.color.ship_hit);
                break;
            case "-2":
                holder.myText.setBackgroundResource(R.color.ship_miss);
                break;
            default:
                holder.myText.setText(this.board.get(position));
                holder.myText.setBackgroundResource(R.color.ship_space);
        }
        holder.setIsRecyclable(false);
    }

    @Override
    public int getItemCount() { return this.board.size(); }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView myText;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            myText = itemView.findViewById(R.id.grid_cell_value);
        }
    }
}
