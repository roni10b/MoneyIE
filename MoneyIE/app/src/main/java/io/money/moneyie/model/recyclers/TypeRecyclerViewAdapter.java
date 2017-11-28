package io.money.moneyie.model.recyclers;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import io.money.moneyie.R;
import io.money.moneyie.model.Type;
import io.money.moneyie.model.utilities.Utilities;

//This is recycler view adapter which loads the types of Income/Expense in the fragments Fragment_Income and Fragment_Expense
public class TypeRecyclerViewAdapter extends RecyclerView.Adapter<TypeRecyclerViewAdapter.MyViewHolder>{

    private Context context;
    private ArrayList<Type> types;
    private LayoutInflater inflater;
    private ItemClickListener mClickListener;
    private String[] typeNames;

    public TypeRecyclerViewAdapter(Context context, ArrayList<Type> types) {
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.types = types;
        typeNames = Utilities.getTypeNames(context);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View row;
        final TypeRecyclerViewAdapter.MyViewHolder viewHolder;
        row = inflater.inflate(R.layout.recycler_expense_income_row, parent, false);
        viewHolder = new TypeRecyclerViewAdapter.MyViewHolder(row,
                (ImageView)row.findViewById(R.id.row_image),
                (TextView)row.findViewById(R.id.row_text));
        row.setTag(viewHolder);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final Type type = types.get(position);
        holder.image.setImageResource(type.getPictureId());
        if (TextUtils.isDigitsOnly(type.getType()) && (Integer.parseInt(type.getType()) < typeNames.length)){
            holder.text.setText(typeNames[Integer.parseInt(type.getType())]);
        } else {
            holder.text.setText(type.getType());
        }
    }

    @Override
    public int getItemCount() {
        return types.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView image;
        TextView text;

        public MyViewHolder(View row, ImageView image, TextView text) {
            super(row);
            this.image = image;
            this.text = text;
            image.setOnClickListener(this);
            text.setOnClickListener(this);
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

    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    //interface which is implemented in the view holder. The method is used when an item in the row is clicked
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
