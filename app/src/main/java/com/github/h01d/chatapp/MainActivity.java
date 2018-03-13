package com.github.h01d.chatapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.github.h01d.chatapp.activities.ProfileActivity;
import com.github.h01d.chatapp.activities.UsersActivity;
import com.github.h01d.chatapp.activities.WelcomeActivity;
import com.github.h01d.chatapp.fragments.ChatFragment;
import com.github.h01d.chatapp.fragments.FriendsFragment;
import com.github.h01d.chatapp.fragments.RequestsFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

/**
 * This is a part of ChatApp Project (https://github.com/h01d/ChatApp)
 * Licensed under Apache License 2.0
 *
 * @author  Raf (https://github.com/h01d)
 * @version 1.1
 * @since   27/02/2018
 */

public class MainActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Action bar related

        Toolbar toolbar = findViewById(R.id.main_app_bar);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("ChatApp");

        // Fragments handler using SmartTabLayout

        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                getSupportFragmentManager(), FragmentPagerItems.with(this)
                .add("Requests", RequestsFragment.class)
                .add("Chat", ChatFragment.class)
                .add("Friends", FriendsFragment.class)
                .create());

        ViewPager viewPager = findViewById(R.id.viewpager);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(1);

        SmartTabLayout viewPagerTab = findViewById(R.id.viewpagertab);
        viewPagerTab.setViewPager(viewPager);
    }

    @Override
    public void onStart()
    {
        super.onStart();

        if(FirebaseAuth.getInstance().getCurrentUser() == null)
        {
            // If no logged in user send them to login/register

            Intent welcomeIntent = new Intent(MainActivity.this, WelcomeActivity.class);
            startActivity(welcomeIntent);
            finish();
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if(currentUser != null)
        {
            FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser.getUid()).child("online").setValue("true");
        }
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if(currentUser != null)
        {
            FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser.getUid()).child("online").setValue(ServerValue.TIMESTAMP);
        }
    }

    @Override
    public void onBackPressed()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Exit");
        builder.setMessage("Are you sure you want to close the application?");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                finish();
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        super.onOptionsItemSelected(item);

        switch(item.getItemId())
        {
            case R.id.menuLogout:
                AlertDialog.Builder logoutBuilder = new AlertDialog.Builder(MainActivity.this);
                logoutBuilder.setTitle("Logout");
                logoutBuilder.setMessage("Are you sure you want to logout?");
                logoutBuilder.setPositiveButton("YES", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("online").setValue(ServerValue.TIMESTAMP);

                        FirebaseAuth.getInstance().signOut();

                        Intent welcomeIntent = new Intent(MainActivity.this, WelcomeActivity.class);
                        startActivity(welcomeIntent);
                        finish();
                    }
                });
                logoutBuilder.setNegativeButton("NO", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        dialog.dismiss();
                    }
                });
                AlertDialog logoutDialog = logoutBuilder.create();
                logoutDialog.show();
                return true;
            case R.id.menuChangelog:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/h01d/ChatApp/blob/master/CHANGELOG.md")));
                return true;
            case R.id.menuAbout:
                AlertDialog.Builder aboutBuilder = new AlertDialog.Builder(MainActivity.this);
                aboutBuilder.setTitle("ChatApp v1.1");
                aboutBuilder.setMessage("Project is open source and released under Apache License 2.0.\n\nMake sure you read all Legal content.\n\nRaf, 2018. All Rights Reserved.");
                aboutBuilder.setNegativeButton("Close", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        dialog.dismiss();
                    }
                });
                AlertDialog aboutDialog = aboutBuilder.create();
                aboutDialog.show();
                return true;
            case R.id.menuLegal:
                AlertDialog.Builder legalBuilder = new AlertDialog.Builder(this);
                legalBuilder.setTitle("Legal");
                legalBuilder.setItems(new CharSequence[]{"License", "Privacy Policy", "Terms and Conditions", "Third Party Notices"}, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int position)
                    {
                        switch(position)
                        {
                            case 0:
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/h01d/ChatApp/blob/master/LICENSE")));
                                break;
                            case 1:
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/h01d/ChatApp/blob/master/PRIVACY_POLICY.md")));
                                break;
                            case 2:
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/h01d/ChatApp/blob/master/TERMS_AND_CONDITIONS.md")));
                                break;
                            case 3:
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/h01d/ChatApp/blob/master/THIRD_PARTY_NOTICES.md")));
                                break;
                        }
                    }
                });

                AlertDialog legalDialog = legalBuilder.create();
                legalDialog.show();
                return true;
            case R.id.menuProfile:
                Intent profileIntent = new Intent(MainActivity.this, ProfileActivity.class);
                profileIntent.putExtra("userid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                startActivity(profileIntent);
                return true;
            case R.id.menuSearch:
                Intent usersIntent = new Intent(MainActivity.this, UsersActivity.class);
                startActivity(usersIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}