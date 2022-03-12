package edu.gmu.cs477.program1_battleship_ahwang4;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ClickAdapter extends RecyclerView.Adapter<ClickAdapter.MyViewHolder> {
    // if String @ Position =
    //      "0" = default, no interaction yet
    //      "1" = default, hidden ship
    //      "2" = player MISS SHIP
    //      "3" = player HIT SHIP
    private ArrayList<String> board;

    private Context context;
    private ItemClickListener mClickListener;

    public ClickAdapter(Context context, ArrayList<String> board) {
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
        //      "0" = default, no interaction yet
        //      "1" = default, hidden ship
        //      "2" = player MISS SHIP
        //      "3" = player HIT SHIP
        switch(this.board.get(position)) {
            case "2":
                holder.myText.setBackgroundResource(R.color.ship_miss);
                holder.myText.setText("X");
                break;
            case "3":
                holder.myText.setBackgroundResource(R.color.ship_hit);
                holder.myText.setText("O");
                break;
            default:
                holder.myText.setBackgroundResource(R.color.default_cell);
                break;
        }
        holder.setIsRecyclable(false);
    }

    @Override
    public int getItemCount() { return this.board.size(); }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView myText;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            myText = itemView.findViewById(R.id.grid_cell_value);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null)
                mClickListener.onItemClick(view, getBindingAdapterPosition());
        }
    }

    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
