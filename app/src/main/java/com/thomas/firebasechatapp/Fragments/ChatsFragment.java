package com.thomas.firebasechatapp.Fragments;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.thomas.firebasechatapp.Adapters.UserAdapter;
import com.thomas.firebasechatapp.Database.MyDataBase;
import com.thomas.firebasechatapp.Models.Chat;
import com.thomas.firebasechatapp.Models.User;
import com.thomas.firebasechatapp.R;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import Base.BaseFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatsFragment extends BaseFragment {

    private RecyclerView recyclerView;
    private UserAdapter adapter;
    private List<User> mUsers;

    FirebaseUser firebaseUser;
    private List<String> userList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chats, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        userList = new ArrayList<>();


        MyDataBase.getInstance().getMessagesCollections().addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                userList.clear();
                if (e != null) {
                    showMessage(getString(R.string.error), e.getLocalizedMessage(), getString(R.string.ok));
                    return;
                }

                assert queryDocumentSnapshots != null;
                for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
                    Chat chat = dc.getDocument().toObject(Chat.class);

                    if (chat.getSender().equals(firebaseUser.getUid())) {
                        userList.add(chat.getReceiver());
                    }

                    if (chat.getReceiver().equals(firebaseUser.getUid())) {
                        userList.add(chat.getSender());
                    }

                }
                readChats();
            }
        });

        return view;
    }

    private void readChats() {
        mUsers = new ArrayList<>();

        MyDataBase.getInstance().getUsersCollections().addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {


                if (e != null) {
                    showMessage(getString(R.string.error), e.getLocalizedMessage(), getString(R.string.ok));
                    return;
                }

                assert queryDocumentSnapshots != null;
                for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
                    User user = dc.getDocument().toObject(User.class);

                    for (String id : userList) {
                        if (user.getId().equals(id)) {
                            if (mUsers.size() != 0) {
                                for (User user1 : mUsers) {
                                    if (!user.getId().equals(user1.getId())) {
                                        mUsers.add(user);
                                    }
                                }
                            } else {
                                mUsers.add(user);
                            }
                        }
                    }

                }

                adapter = new UserAdapter(activity, mUsers, true);
                recyclerView.setAdapter(adapter);


            }
        });
    }

}
