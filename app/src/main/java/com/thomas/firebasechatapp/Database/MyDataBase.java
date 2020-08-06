package com.thomas.firebasechatapp.Database;


import android.annotation.SuppressLint;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.thomas.firebasechatapp.Models.Chat;
import com.thomas.firebasechatapp.Models.User;

public class MyDataBase {
    private static MyDataBase dataBase;
    @SuppressLint("StaticFieldLeak")
    private static FirebaseFirestore firebasDB;
    private static final String USERS_REF = "users";
    private static final String MESSAGES_REF = "messages";
    private FirebaseAuth auth = FirebaseAuth.getInstance();

    FirebaseUser firebaseUser = auth.getCurrentUser();

    public static MyDataBase getInstance() {
        if (dataBase == null) {
            dataBase = new MyDataBase();
            firebasDB = FirebaseFirestore.getInstance();
        }
        return dataBase;
    }

    public CollectionReference getUsersCollections() {
        return firebasDB.collection(USERS_REF);
    }

    public CollectionReference getMessagesCollections() {
        return firebasDB.collection(MESSAGES_REF);
    }

    public void createNewUser(User user, String userId, OnCompleteListener listener, OnFailureListener failure) {
        DocumentReference reference = getUsersCollections().document(userId);
        user.setId(userId);
        reference.set(user).addOnCompleteListener(listener).addOnFailureListener(failure);
    }

    public void addMessage(Chat chat, OnCompleteListener listener, OnFailureListener onFailureListener) {
        DocumentReference reference = getMessagesCollections().document();
        reference.set(chat).addOnCompleteListener(listener).addOnFailureListener(onFailureListener);

    }

    public void getUserRefOnce(String user_Id, OnCompleteListener<DocumentSnapshot> onCompleteListener) {
        DocumentReference reference = getUsersCollections().document(user_Id);
        reference.get().addOnCompleteListener(onCompleteListener);

    }


}

