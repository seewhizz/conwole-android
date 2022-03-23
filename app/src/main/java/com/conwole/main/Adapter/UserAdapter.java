package com.conwole.main.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.conwole.main.Common.Common;
import com.conwole.main.Model.User;
import com.conwole.main.ProfileActivity;
import com.conwole.main.R;
import com.conwole.main.SearchActivity;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> implements Filterable {

    private Context context;
    private List<User> userList;
    private List<User> userListFull;
    private String filterPattern;

    public UserAdapter(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList;
        userListFull = new ArrayList<>(userList);
    }

    public class UserViewHolder extends RecyclerView.ViewHolder{

        private CircularImageView userImage;
        private TextView userName;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            userImage = itemView.findViewById(R.id.user_image);
            userName = itemView.findViewById(R.id.user_name);
        }

    }

    @NonNull
    @Override
    public UserAdapter.UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new UserAdapter.UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.UserViewHolder holder, int position) {
        User user = this.userList.get(position);

        if(user!=null){
            if(user.getProfile_pic_url()!= null){
                Glide.with(context).load(user.getProfile_pic_url()).into(holder.userImage);
            }
            if(user.getFull_name()!= null){
                SpannableStringBuilder sb = new SpannableStringBuilder(user.getFull_name());
                Pattern word = Pattern.compile(filterPattern.toLowerCase());
                Matcher match = word.matcher(user.getFull_name().toLowerCase());
                while (match.find()) {
                    ForegroundColorSpan fcs = new ForegroundColorSpan(
                            ContextCompat.getColor(context, R.color.colorAccent)); //specify color here
                    sb.setSpan(fcs, match.start(), match.end(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                }
                holder.userName.setText(sb);
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Common.currentProfile = user;
                    context.startActivity(new Intent(context,ProfileActivity.class));
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    @Override
    public Filter getFilter() {
        return searchFilter;
    }

    private Filter searchFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<User> filteredList = new ArrayList<>();

            if(constraint == null || constraint.length() == 0){
                filteredList.addAll(userListFull);
            } else {
                filterPattern = constraint.toString().toLowerCase().trim();
                for (User user : userListFull){
                    if(user.getFull_name().toLowerCase().contains(filterPattern)) {
                        filteredList.add(user);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            userList.clear();
            userList.addAll((List)filterResults.values);
            notifyDataSetChanged();
        }
    };

}
