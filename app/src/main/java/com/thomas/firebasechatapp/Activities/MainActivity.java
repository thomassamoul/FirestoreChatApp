package com.thomas.firebasechatapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.thomas.firebasechatapp.Adapters.ViewPagerAdapter;

import Base.BaseActivity;

import com.thomas.firebasechatapp.Database.MyDataBase;
import com.thomas.firebasechatapp.Fragments.ChatsFragment;
import com.thomas.firebasechatapp.Fragments.ProfileFragment;
import com.thomas.firebasechatapp.Fragments.UsersFragment;
import com.thomas.firebasechatapp.Models.User;
import com.thomas.firebasechatapp.R;

import de.hdodenhof.circleimageview.CircleImageView;


public class MainActivity extends BaseActivity {

    CircleImageView profileImage;
    TextView username;

    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        profileImage = findViewById(R.id.profile_image);
        username = findViewById(R.id.username);


        MyDataBase.getInstance().getUserRefOnce(firebaseUser.getUid(), new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {

                    User user = task.getResult().toObject(User.class);

                    username.setText(user.getUsername());
                    if (user.getImageUrl().equals("default")) {
                        profileImage.setImageResource(R.mipmap.ic_launcher_round);
                    } else {
                        Glide.with(activity).load(user.getImageUrl()).into(profileImage);
                    }
                }
            }
        });

        TabLayout tabLayout = findViewById(R.id.tab_layout);
        ViewPager viewPager = findViewById(R.id.viewpager);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new ChatsFragment(), "Chats");
        adapter.addFragment(new UsersFragment(), "Users");
        adapter.addFragment(new ProfileFragment(), "Profile");

        viewPager.setAdapter(adapter);

        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(activity, StartActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
                return true;
        }
        return false;
    }

    private void status(final String status) {
        DocumentReference reference = FirebaseFirestore.getInstance().collection("users").document(firebaseUser.getUid());
        reference.update("status", status);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        status("offline");
    }

    @Override
    protected void onStop() {
        super.onStop();
        status("offline");
    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
    }

    @Override
    protected void onStart() {
        super.onStart();
        status("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        status("offline");
    }
}
