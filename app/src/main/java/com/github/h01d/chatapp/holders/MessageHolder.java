package com.github.h01d.chatapp.holders;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.h01d.chatapp.R;
import com.github.h01d.chatapp.activities.FullScreenActivity;
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

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * This is a part of ChatApp Project (https://github.com/h01d/ChatApp)
 * Licensed under Apache License 2.0
 *
 * @author  Raf (https://github.com/h01d)
 * @version 1.1
 * @since   27/02/2018
 */

public class MessageHolder extends RecyclerView.ViewHolder
{
    private final String TAG = "CA/MessageHolder";

    private View view;
    private Context context;

    // Will handle User, Chat and Chat Typing data

    private DatabaseReference userDatabase, chatSeenDatabase, chatTypingDatabase;
    private ValueEventListener userListener, chatSeenListener, chatTypingListener;

    public MessageHolder(View view, Context context)
    {
        super(view);

        this.view = view;
        this.context = context;
    }

    public void hideBottom()
    {
        final RelativeLayout messageBottom = view.findViewById(R.id.message_relative_bottom);

        messageBottom.setVisibility(View.GONE);
    }

    public void setLastMessage(final String currentUserId, final String from, final String to)
    {
        // If the message is the last message in the list

        final TextView messageSeen = view.findViewById(R.id.message_seen);
        final TextView messageTyping = view.findViewById(R.id.message_typing);

        final RelativeLayout messageBottom = view.findViewById(R.id.message_relative_bottom);

        messageBottom.setVisibility(View.VISIBLE);

        String otherUserId = from;

        if(from.equals(currentUserId))
        {
            otherUserId = to;

            if(chatSeenDatabase != null && chatSeenListener != null)
            {
                chatSeenDatabase.removeEventListener(chatSeenListener);
            }

            // Initialize/Update seen message on the bottom of the message

            chatSeenDatabase = FirebaseDatabase.getInstance().getReference().child("Chat").child(to).child(currentUserId);
            chatSeenListener = new ValueEventListener()
            {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot)
                {
                    try
                    {
                        if(from.equals(currentUserId) && dataSnapshot.hasChild("seen"))
                        {
                            messageSeen.setVisibility(View.VISIBLE);

                            long seen = (long) dataSnapshot.child("seen").getValue();

                            if(seen == 0)
                            {
                                messageSeen.setText("Sent");
                            }
                            else
                            {
                                messageSeen.setText("Seen at " + new SimpleDateFormat("MMM d, HH:mm", Locale.getDefault()).format(seen));
                            }
                        }
                        else
                        {
                            messageSeen.setVisibility(View.INVISIBLE);
                        }
                    }
                    catch(Exception e)
                    {
                        Log.d(TAG, "chatSeenListerner exception: " + e.getMessage());
                        e.printStackTrace();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError)
                {
                    Log.d(TAG, "chatSeenListerner failed: " + databaseError.getMessage());
                }
            };
            chatSeenDatabase.addValueEventListener(chatSeenListener);
        }
        else
        {
            messageSeen.setVisibility(View.INVISIBLE);
        }

        if(chatTypingDatabase != null && chatTypingListener != null)
        {
            chatTypingDatabase.removeEventListener(chatTypingListener);
        }

        // Initialize/Update typing status on the bottom

        chatTypingDatabase = FirebaseDatabase.getInstance().getReference().child("Chat").child(otherUserId).child(currentUserId);
        chatTypingListener = new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                try
                {
                    if(dataSnapshot.hasChild("typing"))
                    {
                        int typing = Integer.parseInt(dataSnapshot.child("typing").getValue().toString());

                        messageTyping.setVisibility(View.VISIBLE);

                        if(typing == 1)
                        {
                            messageTyping.setText("Typing...");
                        }
                        else if(typing == 2)
                        {
                            messageTyping.setText("Deleting...");
                        }
                        else if(typing == 3)
                        {
                            messageTyping.setText("Thinking...");
                        }
                        else
                        {
                            messageTyping.setVisibility(View.INVISIBLE);
                        }
                    }
                    else
                    {
                        messageTyping.setVisibility(View.INVISIBLE);
                    }
                }
                catch(Exception e)
                {
                    Log.d(TAG, "chatTypingListener exception: " + e.getMessage());
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {
                Log.d(TAG, "chatTypingListener failed: " + databaseError.getMessage());
            }
        };
        chatTypingDatabase.addValueEventListener(chatTypingListener);
    }

    public void setRightMessage(String userid, final String message, long time, String type)
    {
        // If this an upcoming message

        final RelativeLayout messageLayoutLeft = view.findViewById(R.id.message_relative_left);

        final RelativeLayout messageLayoutRight = view.findViewById(R.id.message_relative_right);
        final TextView messageTextRight = view.findViewById(R.id.message_text_right);
        final TextView messageTimeRight = view.findViewById(R.id.message_time_right);
        final CircleImageView messageImageRight = view.findViewById(R.id.message_image_right);
        final ImageView messageTextPictureRight = view.findViewById(R.id.message_imagetext_right);
        final TextView messageLoadingRight = view.findViewById(R.id.message_loading_right);

        messageLayoutLeft.setVisibility(View.GONE);

        messageLayoutRight.setVisibility(View.VISIBLE);

        if(type.equals("text"))
        {
            messageTextPictureRight.setVisibility(View.GONE);
            messageLoadingRight.setVisibility(View.GONE);

            messageTextRight.setVisibility(View.VISIBLE);
            messageTextRight.setText(message);
        }
        else
        {
            messageTextRight.setVisibility(View.GONE);

            messageTextPictureRight.setVisibility(View.VISIBLE);
            messageLoadingRight.setVisibility(View.VISIBLE);
            messageLoadingRight.setText("Loading picture...");

            Picasso.with(context)
                    .load(message)
                    .fit()
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .into(messageTextPictureRight, new Callback()
                    {
                        @Override
                        public void onSuccess()
                        {
                            messageLoadingRight.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError()
                        {
                            Picasso.with(context)
                                    .load(message)
                                    .fit()
                                    .into(messageTextPictureRight, new Callback()
                                    {
                                        @Override
                                        public void onSuccess()
                                        {
                                            messageLoadingRight.setVisibility(View.GONE);
                                        }

                                        @Override
                                        public void onError()
                                        {
                                            messageLoadingRight.setText("Error: could not load picture.");
                                        }
                                    });
                        }
                    });

            messageTextPictureRight.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    Intent intent = new Intent(context, FullScreenActivity.class);
                    intent.putExtra("imageUrl", message);
                    context.startActivity(intent);
                }
            });
        }

        messageTimeRight.setText(DateUtils.isToday(time) ? new SimpleDateFormat("HH:mm", Locale.getDefault()).format(time) : new SimpleDateFormat("MMM d, HH:mm", Locale.getDefault()).format(time));

        if(userDatabase != null && userListener != null)
        {
            userDatabase.removeEventListener(userListener);
        }

        // Initialize/Update user image

        userDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(userid);
        userListener = new ValueEventListener()
        {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot)
            {
                try
                {
                    final String image = dataSnapshot.child("image").getValue().toString();

                    if(!image.equals("default"))
                    {
                        Picasso.with(context)
                                .load(image)
                                .resize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, context.getResources().getDisplayMetrics()), (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, context.getResources().getDisplayMetrics()))
                                .centerCrop()
                                .networkPolicy(NetworkPolicy.OFFLINE)
                                .placeholder(R.drawable.user)
                                .into(messageImageRight, new Callback()
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
                                                .into(messageImageRight);
                                    }
                                });
                    }
                    else
                    {
                        messageImageRight.setImageResource(R.drawable.user);
                    }
                }
                catch(Exception e)
                {
                    Log.d(TAG, "userDatabase exception: " + e.getMessage());
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {
                Log.d(TAG, "userDatabase failed: " + databaseError.getMessage());
            }
        };
        userDatabase.addValueEventListener(userListener);
    }

    public void setLeftMessage(String userid, final String message, long time, String type)
    {
        // If this is a sent message

        final RelativeLayout messageLayoutRight = view.findViewById(R.id.message_relative_right);

        final RelativeLayout messageLayoutLeft = view.findViewById(R.id.message_relative_left);
        final TextView messageTextLeft = view.findViewById(R.id.message_text_left);
        final TextView messageTimeLeft = view.findViewById(R.id.message_time_left);
        final CircleImageView messageImageLeft = view.findViewById(R.id.message_image_left);
        final ImageView messageTextPictureLeft = view.findViewById(R.id.message_imagetext_left);
        final TextView messageLoadingLeft = view.findViewById(R.id.message_loading_left);

        messageLayoutRight.setVisibility(View.GONE);

        messageLayoutLeft.setVisibility(View.VISIBLE);

        if(type.equals("text"))
        {
            messageTextPictureLeft.setVisibility(View.GONE);
            messageLoadingLeft.setVisibility(View.GONE);

            messageTextLeft.setVisibility(View.VISIBLE);
            messageTextLeft.setText(message);
        }
        else
        {
            messageTextLeft.setVisibility(View.GONE);

            messageTextPictureLeft.setVisibility(View.VISIBLE);
            messageLoadingLeft.setVisibility(View.VISIBLE);
            messageLoadingLeft.setText("Loading picture...");

            Picasso.with(context)
                    .load(message)
                    .fit()
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .into(messageTextPictureLeft, new Callback()
                    {
                        @Override
                        public void onSuccess()
                        {
                            messageLoadingLeft.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError()
                        {
                            Picasso.with(context)
                                    .load(message)
                                    .fit()
                                    .into(messageTextPictureLeft, new Callback()
                                    {
                                        @Override
                                        public void onSuccess()
                                        {
                                            messageLoadingLeft.setVisibility(View.GONE);
                                        }

                                        @Override
                                        public void onError()
                                        {
                                            messageLoadingLeft.setText("Error: could not load picture.");
                                        }
                                    });
                        }
                    });

            messageTextPictureLeft.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    Intent intent = new Intent(context, FullScreenActivity.class);
                    intent.putExtra("imageUrl", message);
                    context.startActivity(intent);
                }
            });
        }
        messageTimeLeft.setText(DateUtils.isToday(time) ? new SimpleDateFormat("HH:mm", Locale.getDefault()).format(time) : new SimpleDateFormat("MMM d, HH:mm", Locale.getDefault()).format(time));

        if(userDatabase != null && userListener != null)
        {
            userDatabase.removeEventListener(userListener);
        }

        // Initilize/Update user image

        userDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(userid);
        userListener = new ValueEventListener()
        {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot)
            {
                try
                {
                    final String image = dataSnapshot.child("image").getValue().toString();

                    if(!image.equals("default"))
                    {
                        Picasso.with(context)
                                .load(image)
                                .resize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, context.getResources().getDisplayMetrics()), (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, context.getResources().getDisplayMetrics()))
                                .centerCrop()
                                .networkPolicy(NetworkPolicy.OFFLINE)
                                .placeholder(R.drawable.user)
                                .into(messageImageLeft, new Callback()
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
                                                .into(messageImageLeft);
                                    }
                                });
                    }
                    else
                    {
                        messageImageLeft.setImageResource(R.drawable.user);
                    }
                }
                catch(Exception e)
                {
                    Log.d(TAG, "userDatabase exception: " + e.getMessage());
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {
                Log.d(TAG, "userDatabase failed: " + databaseError.getMessage());
            }
        };
        userDatabase.addValueEventListener(userListener);
    }
}
