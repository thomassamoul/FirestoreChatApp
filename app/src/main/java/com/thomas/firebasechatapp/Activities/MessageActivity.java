package com.thomas.firebasechatapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;

import Base.BaseActivity;

import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.thomas.firebasechatapp.Adapters.MessageAdapter;
import com.thomas.firebasechatapp.Database.MyDataBase;
import com.thomas.firebasechatapp.Models.Chat;
import com.thomas.firebasechatapp.Models.User;
import com.thomas.firebasechatapp.R;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageActivity extends BaseActivity {

    CircleImageView profileImage;
    TextView username;

    FirebaseUser firebaseUser;

    ImageButton btnSend;
    EditText txtSend;

    RecyclerView recyclerView;

    MessageAdapter adapter;
    List<Chat> mChat = new ArrayList<>();


    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(activity,MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
            }
        });

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.hasFixedSize();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);


        profileImage = findViewById(R.id.profile_image);
        username = findViewById(R.id.username);
        btnSend = findViewById(R.id.btn_send);
        txtSend = findViewById(R.id.txt_send);

        intent = getIntent();
        final String userid = intent.getStringExtra("userid");

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String txtMessage = txtSend.getText().toString();
                Chat msg = new Chat();
                msg.setSender(firebaseUser.getUid());
                msg.setReceiver(userid);
                msg.setMessage(txtMessage);

                if (!txtMessage.isEmpty())
                    sendMessage(msg);
            }
        });

        MyDataBase.getInstance().getUserRefOnce(userid, new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    User user = task.getResult().toObject(User.class);

                    username.setText(user.getUsername());

                    if (user.getImageUrl().equals("default"))
                        profileImage.setImageResource(R.mipmap.ic_launcher_round);
                    else
                        Glide.with(activity).load(user.getImageUrl()).into(profileImage);

                    readMessage(firebaseUser.getUid(), userid, user.getImageUrl());
                }

            }
        });
    }

    private void sendMessage(Chat msg) {

        MyDataBase.getInstance().addMessage(msg, new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                MessageActivity.this.txtSend.setText(null);
            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }

    private void readMessage(final String myId, final String userID, final String imageUrl) {
        mChat = new ArrayList<>();

        MyDataBase.getInstance().getMessagesCollections().addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
//                mChat.clear();

                if (e != null) {
                    showMessage(getString(R.string.error), e.getLocalizedMessage()
                            , getString(R.string.ok));
                    return;
                }

                for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
                    switch (dc.getType()) {
                        case ADDED: {
                            Chat chat = dc.getDocument().toObject(Chat.class);

                            if ((chat.getReceiver().equals(myId) && chat.getSender().equals(userID)) ||
                                    (chat.getReceiver().equals(userID) && chat.getSender().equals(myId))) {
                                mChat.add(chat);
                            }

                            adapter = new MessageAdapter(activity, mChat, imageUrl);
                            recyclerView.setAdapter(adapter);

                        }
                    }
                }
            }
        });
    }

    private void status(final String status) {

        MyDataBase.getInstance().getUserRefOnce(firebaseUser.getUid(), new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                User user = task.getResult().toObject(User.class);
                user.setStatus(status);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        status("offline");
    }

}
