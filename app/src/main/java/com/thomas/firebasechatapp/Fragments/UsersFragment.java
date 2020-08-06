package com.thomas.firebasechatapp.Fragments;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.thomas.firebasechatapp.Adapters.UserAdapter;

import Base.BaseFragment;

import com.thomas.firebasechatapp.Database.MyDataBase;
import com.thomas.firebasechatapp.Models.User;
import com.thomas.firebasechatapp.R;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

/**
 * A simple {@link Fragment} subclass.
 */
public class UsersFragment extends BaseFragment {

    private RecyclerView recyclerView;

    private UserAdapter adapter;
    private List<User> userList = new ArrayList<>();
    EditText search;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_users, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        search = view.findViewById(R.id.search_view);
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                searchUsers(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        userList = new ArrayList<>();

        retrieveUsers();
        return view;
    }

    private void searchUsers(String s) {
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Query query = MyDataBase.getInstance().getUsersCollections().orderBy("username").startAt(s).endAt(s + "\uf8ff");
        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                userList.clear();
                for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()) {
                    User user = snapshot.toObject(User.class);
                    if (!user.getId().equals(firebaseUser.getUid())) {
                        userList.add(user);
                    }
                }
                adapter = new UserAdapter(activity, userList, false);
                recyclerView.setAdapter(adapter);

            }
        });
    }

    private void retrieveUsers() {

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        MyDataBase.getInstance().getUsersCollections().addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (search.getText().toString().isEmpty()) {
                    if (e != null) {
                        showMessage(getString(R.string.error), e.getLocalizedMessage(), getString(R.string.ok));
                        return;
                    }
                    userList.clear();

                    assert queryDocumentSnapshots != null;
                    for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {

                        User user = dc.getDocument().toObject(User.class);
                        assert firebaseUser != null;
                        if (!user.getId().equals(firebaseUser.getUid()))
                            userList.add(user);
                    }

                    adapter = new UserAdapter(getContext(), userList, false);
                    recyclerView.setAdapter(adapter);
                }
            }
        });
    }

}
