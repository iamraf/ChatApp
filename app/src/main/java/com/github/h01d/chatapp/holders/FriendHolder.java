package com.github.h01d.chatapp.holders;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.h01d.chatapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * This is a part of ChatApp Project (https://github.com/h01d/ChatApp)
 * Licensed under Apache License 2.0
 *
 * @author  Raf (https://github.com/h01d)
 * @version 1.1
 * @since   27/02/2018
 */

public class FriendHolder extends RecyclerView.ViewHolder
{
    private final String TAG = "CA/FriendHolder";

    private Activity activity;
    private View view;
    private Context context;

    // Will handle user data

    private DatabaseReference userDatabase;
    private ValueEventListener userListener;

    public FriendHolder(Activity activity, View view, Context context)
    {
        super(view);

        this.activity = activity;
        this.view = view;
        this.context = context;
    }

    public View getView()
    {
        return view;
    }

    public void setHolder(String userid, long date)
    {
        final TextView userName = view.findViewById(R.id.user_name);
        final TextView userStatus = view.findViewById(R.id.user_status);
        final CircleImageView userImage = view.findViewById(R.id.user_image);
        final ImageView userOnline = view.findViewById(R.id.user_online);

        userStatus.setText("Friends Since: " + new SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(date));

        if(userDatabase != null & userListener != null)
        {
            userDatabase.removeEventListener(userListener);
        }

        // Initialize/Update user data

        userDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(userid);
        userListener = new ValueEventListener()
        {
            Timer timer; // Will be used to avoid flickering online status when changing activity

            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                try
                {
                    final String name = dataSnapshot.child("name").getValue().toString();
                    final String image = dataSnapshot.child("image").getValue().toString();

                    if(dataSnapshot.hasChild("online"))
                    {
                        String online = dataSnapshot.child("online").getValue().toString();

                        if(online.equals("true"))
                        {
                            if(timer != null)
                            {
                                timer.cancel();
                                timer = null;
                            }

                            userOnline.setVisibility(View.VISIBLE);
                        }
                        else
                        {
                            if(userName.getText().toString().equals(""))
                            {
                                userOnline.setVisibility(View.INVISIBLE);
                            }
                            else
                            {
                                timer = new Timer();
                                timer.schedule(new TimerTask()
                                {
                                    @Override
                                    public void run()
                                    {
                                        activity.runOnUiThread(new Runnable()
                                        {
                                            @Override
                                            public void run()
                                            {
                                                userOnline.setVisibility(View.INVISIBLE);
                                            }
                                        });
                                    }
                                }, 2000);
                            }
                        }
                    }

                    userName.setText(name);

                    if(!image.equals("default"))
                    {
                        Picasso.with(context)
                                .load(image)
                                .networkPolicy(NetworkPolicy.OFFLINE)
                                .resize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, context.getResources().getDisplayMetrics()), (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, context.getResources().getDisplayMetrics()))
                                .centerCrop()
                                .placeholder(R.drawable.user)
                                .into(userImage, new Callback()
                                {
                                    @Override
                                    public void onSuccess()
                                    {

                                    }

                                    @Override
                                    public void onError()
                                    {
                                        Picasso.with(context)
                                                .load(image)
                                                .resize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, context.getResources().getDisplayMetrics()), (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, context.getResources().getDisplayMetrics()))
                                                .centerCrop()
                                                .placeholder(R.drawable.user)
                                                .error(R.drawable.user)
                                                .into(userImage);
                                    }
                                });
                    }
                    else
                    {
                        userImage.setImageResource(R.drawable.user);
                    }
                }
                catch(Exception e)
                {
                    Log.d(TAG, "userListener exception: " + e.getMessage());
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {
                Log.d(TAG, "userListener failed: " + databaseError.getMessage());
            }
        };
        userDatabase.addValueEventListener(userListener);
    }
}