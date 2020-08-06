package com.thomas.firebasechatapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.thomas.firebasechatapp.Activities.MessageActivity;
import com.thomas.firebasechatapp.Models.User;

import java.util.List;

import com.thomas.firebasechatapp.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private Context mContext;
    private List<User> mUsers;
    boolean isOnline;

    public UserAdapter(Context mContext, List<User> mUsers, boolean isOnline) {
        this.mContext = mContext;
        this.mUsers = mUsers;
        this.isOnline = isOnline;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_item, parent, false);
        return new ViewHolder(view);
    }

    public void changeData(List<User> items){
        mUsers=items;
        notifyDataSetChanged();

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final User user = mUsers.get(position);

        holder.username.setText(user.getUsername());
        if (user.getImageUrl().equals("default"))
            holder.profileImage.setImageResource(R.mipmap.ic_launcher_round);
        else
            Glide.with(mContext).load(user.getImageUrl()).into(holder.profileImage);

        if (isOnline) {
            if (user.getStatus().equals("online")) {
                holder.imgOn.setVisibility(View.VISIBLE);
                holder.imgOff.setVisibility(View.GONE);
            } else {
                holder.imgOn.setVisibility(View.GONE);
                holder.imgOff.setVisibility(View.VISIBLE);
            }
        }else{
            holder.imgOn.setVisibility(View.GONE);
            holder.imgOff.setVisibility(View.GONE);

        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, MessageActivity.class);
                intent.putExtra("userid", user.getId());
                mContext.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView username;
        public CircleImageView profileImage, imgOn, imgOff;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.username);
            profileImage = itemView.findViewById(R.id.profile_image);
            imgOn = itemView.findViewById(R.id.img_on);
            imgOff = itemView.findViewById(R.id.img_off);
        }
    }
}
