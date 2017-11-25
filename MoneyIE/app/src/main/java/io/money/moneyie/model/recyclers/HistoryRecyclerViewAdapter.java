package io.money.moneyie.model.recyclers;


import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import io.money.moneyie.R;
import io.money.moneyie.model.MoneyFlow;

//This is recycler view adapter which loads the users histroy data in the fragment Fragment_DataHistory
public class HistoryRecyclerViewAdapter extends RecyclerView.Adapter<HistoryRecyclerViewAdapter.MyHolder>{

    private Context context;
    private List<MoneyFlow> data;
    private LayoutInflater inflater;
    private String uid;

    public HistoryRecyclerViewAdapter(Context context, List<MoneyFlow> data) {
        inflater = LayoutInflater.from(context);
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        this.context = context;
        this.data = data;
    }

    class MyHolder extends RecyclerView.ViewHolder {
        ImageView imageIE, imageFriend;
        TextView date, type, comment, price;

        MyHolder(View row) {
            super(row);
            type = row.findViewById(R.id.row_history_type);
            date = row.findViewById(R.id.row_history_date);
            comment = row.findViewById(R.id.row_history_comment);
            price = row.findViewById(R.id.row_history_price);
            imageIE = row.findViewById(R.id.row_history_img);
            imageFriend = row.findViewById(R.id.row_history_img_friend);
        }

    }

    @Override
    public HistoryRecyclerViewAdapter.MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.recycler_histoy_row, parent, false);
        MyHolder viewHolder = new MyHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyHolder holder, int position) {
        MoneyFlow moneyFlow = data.get(position);

        holder.comment.setText(moneyFlow.getComment());
        holder.type.setText(moneyFlow.getType());
        holder.price.setText(String.format("%.2f", moneyFlow.getSum()) + " " + context.getString(R.string.leva));
        holder.date.setText(new SimpleDateFormat("d-MMM-yy' / 'HH:mm").format(new Date(moneyFlow.getCalendar())));

        if (moneyFlow.getExpense().equals("ex")) {
            holder.imageIE.setImageResource(R.drawable.outcome_icon);//outcome
            holder.price.setTextColor(Color.argb(255,255,85,85));
            holder.imageFriend.setImageResource(R.drawable.friend_expence);
        } else {
            holder.imageIE.setImageResource(R.drawable.income_icon);//income
            holder.price.setTextColor(Color.argb(255,0,255,64));
            holder.imageFriend.setImageResource(R.drawable.friend_income);
        }

        if(moneyFlow.getUid().equals(uid)){
            holder.imageFriend.setVisibility(View.INVISIBLE);
        } else {
            holder.imageFriend.setVisibility(View.VISIBLE);
        }

    }


    @Override
    public int getItemCount() {
        return this.data.size();
    }
}
