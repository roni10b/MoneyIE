package io.money.moneyie.model.recyclers;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import io.money.moneyie.R;
import io.money.moneyie.model.Type;

//This is recycler view adapter which loads the user's custom created types of Income and Outcome money. This data is loaded in the fragment Fragment_Profile
public class ShowCustomTypesRecyclerViewAdapter extends RecyclerView.Adapter<ShowCustomTypesRecyclerViewAdapter.MyViewHolder>{

    private Context context;
    private ArrayList<Type> types;
    private LayoutInflater inflater;
    private ItemClickListener mClickListener;


    public ShowCustomTypesRecyclerViewAdapter(Context context, ArrayList<Type> types) {
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.types = types;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View row;
        final ShowCustomTypesRecyclerViewAdapter.MyViewHolder viewHolder;
        row = inflater.inflate(R.layout.recycler_show_custom_types, parent, false);
        viewHolder = new ShowCustomTypesRecyclerViewAdapter.MyViewHolder(row,
                (ImageView)row.findViewById(R.id.custom_types_image),
                (TextView)row.findViewById(R.id.custom_types_kind),
                (ImageView)row.findViewById(R.id.custom_types_remove));
        row.setTag(viewHolder);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final Type type = types.get(position);
        holder.image.setImageResource(R.drawable.custom_type);
        holder.kind.setText(type.getType() + " - " + ((type.getExpense().equalsIgnoreCase("true")) ? "Expense" : "Income"));
    }

    @Override
    public int getItemCount() {
        return types.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView image;
        TextView kind;
        ImageView delete;

        public MyViewHolder(View itemView, ImageView image, TextView kind, ImageView delete) {
            super(itemView);
            this.image = image;
            this.kind = kind;
            this.delete = delete;
            delete.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) {
                mClickListener.onItemClick(view, getAdapterPosition());
            }
        }
    }
    public Type getItem(int id) {
        return types.get(id);
    }

    public void setClickListener(ShowCustomTypesRecyclerViewAdapter.ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
