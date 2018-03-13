package com.github.h01d.chatapp.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.flaviofaria.kenburnsview.KenBurnsView;
import com.github.h01d.chatapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import jahirfiquitiva.libs.fabsmenu.FABsMenu;
import jahirfiquitiva.libs.fabsmenu.TitleFAB;

/**
 * This is a part of ChatApp Project (https://github.com/h01d/ChatApp)
 * Licensed under Apache License 2.0
 *
 * @author  Raf (https://github.com/h01d)
 * @version 1.1
 * @since   27/02/2018
 */

public class ProfileActivity extends AppCompatActivity
{
    private final String TAG = "CA/ProfileActivity";

    // // Will handle all changes happening in database

    private DatabaseReference userDatabase, requestsDatabase, friendsDatabase;
    private ValueEventListener userListener, requestsListener, friendsListerner;

    // Users data

    private String currentUserId, otherUserId;

    // activity_profile views

    private TextView name, status;
    private CircleImageView image;
    private KenBurnsView cover;
    private FABsMenu menu;
    private TitleFAB button1, button2, button3;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        name = findViewById(R.id.profile_name);
        status = findViewById(R.id.profile_status);
        image = findViewById(R.id.profile_image);
        menu = findViewById(R.id.profile_fabs_menu);
        cover = findViewById(R.id.profile_cover);

        otherUserId = getIntent().getStringExtra("userid");
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        if(userDatabase != null && userListener != null)
        {
            userDatabase.removeEventListener(userListener);
        }

        // Initialize/Update realtime user data such as name, email, status, image

        userDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(otherUserId);
        userDatabase.keepSynced(true); // For offline use
        userListener = new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                try
                {
                    String layoutName = dataSnapshot.child("name").getValue().toString();
                    String layoutStatus = dataSnapshot.child("status").getValue().toString();
                    final String layoutImage = dataSnapshot.child("image").getValue().toString();
                    final String layoutCover = dataSnapshot.child("cover").getValue().toString();

                    name.setText(layoutName);
                    status.setText("\"" + layoutStatus + "\"");

                    if(!layoutImage.equals("default"))
                    {
                        Picasso.with(getApplicationContext())
                                .load(layoutImage)
                                .resize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 180, getResources().getDisplayMetrics()), (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 180, getResources().getDisplayMetrics()))
                                .centerCrop()
                                .networkPolicy(NetworkPolicy.OFFLINE)
                                .placeholder(R.drawable.user)
                                .error(R.drawable.user)
                                .into(image, new Callback()
                                {
                                    @Override
                                    public void onSuccess()
                                    {

                                    }

                                    @Override
                                    public void onError()
                                    {
                                        Picasso.with(getApplicationContext())
                                                .load(layoutImage)
                                                .resize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 180, getResources().getDisplayMetrics()), (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 180, getResources().getDisplayMetrics()))
                                                .centerCrop()
                                                .placeholder(R.drawable.user)
                                                .error(R.drawable.user)
                                                .into(image);
                                    }
                                });

                        image.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View view)
                            {
                                Intent intent = new Intent(ProfileActivity.this, FullScreenActivity.class);
                                intent.putExtra("imageUrl", layoutImage);
                                startActivity(intent);
                            }
                        });
                    }
                    else
                    {
                        image.setImageResource(R.drawable.user);
                    }

                    if(!layoutCover.equals("default"))
                    {
                        Picasso.with(getApplicationContext())
                                .load(layoutCover)
                                .resize(getResources().getDisplayMetrics().widthPixels, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 260, getResources().getDisplayMetrics()))
                                .centerCrop()
                                .networkPolicy(NetworkPolicy.OFFLINE)
                                .placeholder(R.drawable.logo_cover)
                                .error(R.drawable.logo_cover)
                                .into(cover, new Callback()
                                {
                                    @Override
                                    public void onSuccess()
                                    {

                                    }

                                    @Override
                                    public void onError()
                                    {
                                        Picasso.with(getApplicationContext())
                                                .load(layoutCover)
                                                .resize(getResources().getDisplayMetrics().widthPixels, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 260, getResources().getDisplayMetrics()))
                                                .centerCrop()
                                                .placeholder(R.drawable.logo_cover)
                                                .error(R.drawable.logo_cover)
                                                .into(cover);
                                    }
                                });
                    }
                    else
                    {
                        cover.setImageResource(R.drawable.logo_cover);
                    }
                }
                catch(Exception e)
                {
                    Log.d(TAG, "userDatabase listener exception: " + e.getMessage());
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {
                Log.d(TAG, "userDatabase listener failed: " + databaseError.getMessage());
            }
        };
        userDatabase.addValueEventListener(userListener);

        if(otherUserId.equals(currentUserId))
        {
            initMyProfile();
        }
        else
        {
            initOtherProfile();
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId).child("online").setValue("true");
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId).child("online").setValue(ServerValue.TIMESTAMP);
    }

    @Override
    protected void onStop()
    {
        super.onStop();

        removeListeners();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1 && resultCode == RESULT_OK)
        {
            Uri url = data.getData();

            //Uploading selected picture

            StorageReference file = FirebaseStorage.getInstance().getReference().child("profile_images").child(currentUserId + ".jpg");
            file.putFile(url).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>()
            {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task)
                {
                    if(task.isSuccessful())
                    {
                        String imageUrl = task.getResult().getDownloadUrl().toString();

                        // Updating image on user data

                        userDatabase.child("image").setValue(imageUrl).addOnCompleteListener(new OnCompleteListener<Void>()
                        {
                            @Override
                            public void onComplete(@NonNull Task<Void> task)
                            {
                                if(task.isSuccessful())
                                {
                                    Toast.makeText(ProfileActivity.this, "Picture updated", Toast.LENGTH_SHORT).show();
                                }
                                else
                                {
                                    Log.d(TAG, "updateImage listener failed: " + task.getException().getMessage());
                                }
                            }
                        });
                    }
                    else
                    {
                        Log.d(TAG, "uploadImage listener failed: " + task.getException().getMessage());
                    }
                }
            });
        }
        else if(requestCode == 2 && resultCode == RESULT_OK)
        {
            Uri url = data.getData();

            //Uploading selected cover picture

            StorageReference file = FirebaseStorage.getInstance().getReference().child("profile_covers").child(currentUserId + ".jpg");
            file.putFile(url).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>()
            {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task)
                {
                    if(task.isSuccessful())
                    {
                        String imageUrl = task.getResult().getDownloadUrl().toString();

                        // Updating image on user data

                        userDatabase.child("cover").setValue(imageUrl).addOnCompleteListener(new OnCompleteListener<Void>()
                        {
                            @Override
                            public void onComplete(@NonNull Task<Void> task)
                            {
                                if(task.isSuccessful())
                                {
                                    Toast.makeText(ProfileActivity.this, "Cover updated", Toast.LENGTH_SHORT).show();
                                }
                                else
                                {
                                    Log.d(TAG, "updateUserCover listener failed: " + task.getException().getMessage());
                                }
                            }
                        });
                    }
                    else
                    {
                        Log.d(TAG, "uploadCover listener failed: " + task.getException().getMessage());
                    }
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case android.R.id.home:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed()
    {
        if(menu.isExpanded())
        {
            menu.collapse();
        }
        else
        {
            super.onBackPressed();
        }
    }

    public void initMyProfile()
    {
        if(button1 != null)
        {
            menu.removeButton(button1);
        }

        if(button2 != null)
        {
            menu.removeButton(button2);
        }

        if(button3 != null)
        {
            menu.removeButton(button3);
        }

		button1 = new TitleFAB(ProfileActivity.this);
		button1.setTitle("Change Cover");
		button1.setBackgroundColor(getResources().getColor(R.color.colorPurple));
		button1.setRippleColor(getResources().getColor(R.color.colorPurpleDark));
		button1.setImageResource(R.drawable.ic_filter_hdr_white_24dp);
		button1.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent gallery = new Intent();
                gallery.setType("image/*");
                gallery.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(gallery, "Select Cover"), 2);

                menu.collapse();
            }
        });
		menu.addButton(button1);

        button2 = new TitleFAB(ProfileActivity.this);
        button2.setTitle("Change Image");
        button2.setBackgroundColor(getResources().getColor(R.color.colorGreen));
        button2.setRippleColor(getResources().getColor(R.color.colorGreenDark));
        button2.setImageResource(R.drawable.ic_image_white_24dp);
        button2.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent gallery = new Intent();
                gallery.setType("image/*");
                gallery.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(gallery, "Select Image"), 1);

                menu.collapse();
            }
        });
        menu.addButton(button2);

        button3 = new TitleFAB(ProfileActivity.this);
        button3.setTitle("Change Status");
        button3.setBackgroundColor(getResources().getColor(R.color.colorBlue));
        button3.setRippleColor(getResources().getColor(R.color.colorBlueDark));
        button3.setImageResource(R.drawable.ic_edit_white_24dp);
        button3.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                menu.collapse();

                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                builder.setTitle("Enter your new Status:");

                View mView = ProfileActivity.this.getLayoutInflater().inflate(R.layout.status_dialog, null);

                final EditText tmp = mView.findViewById(R.id.status_text);

                builder.setPositiveButton("Update", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        final String newStatus = tmp.getText().toString();

                        if(newStatus.length() < 1 || newStatus.length() > 24)
                        {
                            Toast.makeText(ProfileActivity.this, "Status must be between 1-24 characters.", Toast.LENGTH_LONG).show();
                            dialogInterface.dismiss();
                        }
                        else
                        {
                            // Updating on status on user data

                            userDatabase.child("status").setValue(newStatus).addOnCompleteListener(new OnCompleteListener<Void>()
                            {
                                @Override
                                public void onComplete(@NonNull Task<Void> task)
                                {
                                    if(task.isSuccessful())
                                    {
                                        Toast.makeText(ProfileActivity.this, "Status updated", Toast.LENGTH_SHORT).show();
                                    }
                                    else
                                    {
                                        Toast.makeText(ProfileActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        }
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        dialogInterface.dismiss();
                    }
                });

                builder.setView(mView);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
        menu.addButton(button3);
    }

    public void initOtherProfile()
    {
        if(requestsDatabase != null && requestsListener != null)
        {
            requestsDatabase.removeEventListener(requestsListener);
        }

        if(friendsDatabase != null && friendsListerner != null)
        {
            friendsDatabase.removeEventListener(friendsListerner);
        }

        // Checking if current user has sent or received a request from other user

        requestsDatabase = FirebaseDatabase.getInstance().getReference().child("Requests").child(otherUserId);
        requestsListener = new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.hasChild(currentUserId))
                {
                    String type = dataSnapshot.child(currentUserId).child("type").getValue().toString();

                    if(type.equals("sent"))
                    {
                        setFabMenu(2); //cancel
                    }
                    else if(type.equals("received"))
                    {
                        setFabMenu(1); //accept
                    }
                }
                else
                {
                    // Otherwise check if current user has other user as friend

                    friendsDatabase = FirebaseDatabase.getInstance().getReference().child("Friends").child(currentUserId);
                    friendsListerner = new ValueEventListener()
                    {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot)
                        {
                            if(dataSnapshot.hasChild(otherUserId))
                            {
                                setFabMenu(3); //remove
                            }
                            else
                            {
                                setFabMenu(0); //send
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError)
                        {

                        }
                    };
                    friendsDatabase.addValueEventListener(friendsListerner);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        };
        requestsDatabase.addValueEventListener(requestsListener);
    }

    public void setFabMenu(int index)
    {
        if(button1 != null)
        {
            menu.removeButton(button1);
        }

        if(button2 != null)
        {
            menu.removeButton(button2);
        }

        if(button3 != null)
        {
            menu.removeButton(button3);
        }

		/* TODO in next version

		button1 = new TitleFAB(ProfileActivity.this);
		button1.setTitle("Block User");
		button1.setBackgroundColor(getResources().getColor(R.color.colorPurple));
		button1.setRippleColor(getResources().getColor(R.color.colorPurpleDark));
		button1.setImageResource(R.drawable.ic_block_white_24dp);
		menu.addButton(button1);*/

        button2 = new TitleFAB(ProfileActivity.this);
        button2.setBackgroundColor(getResources().getColor(R.color.colorGreen));
        button2.setRippleColor(getResources().getColor(R.color.colorGreenDark));
        switch(index)
        {
            case 0:
                button2.setTitle("Send Friend Request");
                button2.setImageResource(R.drawable.ic_person_add_white_24dp);
                button2.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        menu.collapse();

                        // Pushing notification to get keyId

                        DatabaseReference notificationRef = FirebaseDatabase.getInstance().getReference().child("Notifications").child(otherUserId).push();
                        String notificationId = notificationRef.getKey();

                        // "Packing" request

                        HashMap<String, String> notificationData = new HashMap<>();
                        notificationData.put("from", currentUserId);
                        notificationData.put("type", "request");

                        HashMap map = new HashMap();
                        map.put("Requests/" + otherUserId + "/" + currentUserId + "/type", "received");
                        map.put("Requests/" + currentUserId + "/" + otherUserId + "/type", "sent");
                        map.put("Notifications/" + otherUserId + "/" + notificationId, notificationData);

                        // Updating data into database

                        FirebaseDatabase.getInstance().getReference().updateChildren(map, new DatabaseReference.CompletionListener()
                        {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference)
                            {
                                if(databaseError == null)
                                {
                                    Toast.makeText(ProfileActivity.this, "Friend Request sent!", Toast.LENGTH_SHORT).show();
                                }
                                else
                                {
                                    Log.d(TAG, "sendRequest failed: " + databaseError.getMessage());
                                }
                            }
                        });
                    }
                });
                break;
            case 1:
                button2.setTitle("Cancel Friend Request");
                button2.setImageResource(R.drawable.ic_cancel_white_24dp);
                button2.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        menu.collapse();

                        // "Packing" data

                        Map map = new HashMap<>();
                        map.put("Requests/" + otherUserId + "/" + currentUserId, null);
                        map.put("Requests/" + currentUserId + "/" + otherUserId, null);

                        // Updating data on database

                        FirebaseDatabase.getInstance().getReference().updateChildren(map, new DatabaseReference.CompletionListener()
                        {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference)
                            {
                                if(databaseError == null)
                                {
                                    Toast.makeText(ProfileActivity.this, "Friend Request Canceled", Toast.LENGTH_SHORT).show();
                                }
                                else
                                {
                                    Log.d(TAG, "cancelRequest failed: " + databaseError.getMessage());
                                }
                            }
                        });
                    }
                });
                break;
            case 2:
                button2.setTitle("Accept Friend Request");
                button2.setImageResource(R.drawable.ic_person_add_white_24dp);
                button2.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        menu.collapse();

                        // Pushing notification to get keyId

                        DatabaseReference acceptNotificationRef = FirebaseDatabase.getInstance().getReference().child("Notifications").child(otherUserId).push();
                        String acceptNotificationId = acceptNotificationRef.getKey();

                        // "Packing" request

                        HashMap<String, String> acceptNotificationData = new HashMap<>();
                        acceptNotificationData.put("from", currentUserId);
                        acceptNotificationData.put("type", "accept");

                        // "Packing" data

                        Map map = new HashMap<>();
                        map.put("Friends/" + otherUserId + "/" + currentUserId + "/date", ServerValue.TIMESTAMP);
                        map.put("Friends/" + currentUserId + "/" + otherUserId + "/date", ServerValue.TIMESTAMP);

                        map.put("Requests/" + otherUserId + "/" + currentUserId, null);
                        map.put("Requests/" + currentUserId + "/" + otherUserId, null);

                        map.put("Notifications/" + otherUserId + "/" + acceptNotificationId, acceptNotificationData);

                        // Updating data

                        FirebaseDatabase.getInstance().getReference().updateChildren(map, new DatabaseReference.CompletionListener()
                        {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference)
                            {
                                if(databaseError == null)
                                {
                                    Toast.makeText(ProfileActivity.this, "You are now friends!", Toast.LENGTH_SHORT).show();
                                }
                                else
                                {
                                    Log.d(TAG, "acceptRequest failed: " + databaseError.getMessage());
                                }
                            }
                        });
                    }
                });
                break;
            case 3:
                button2.setTitle("Remove friend");
                button2.setImageResource(R.drawable.ic_remove_circle_white_24dp);
                button2.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        menu.collapse();

                        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                        builder.setTitle("Remove Friend");
                        builder.setMessage("Are you sure you want to remove this person from your friend list?");
                        builder.setPositiveButton("YES", new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int id)
                            {
                                // "Packing" data

                                Map map = new HashMap<>();
                                map.put("Friends/" + otherUserId + "/" + currentUserId, null);
                                map.put("Friends/" + currentUserId + "/" + otherUserId, null);

                                // Updating data

                                FirebaseDatabase.getInstance().getReference().updateChildren(map, new DatabaseReference.CompletionListener()
                                {
                                    @Override
                                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference)
                                    {
                                        if(databaseError == null)
                                        {
                                            Toast.makeText(ProfileActivity.this, "You are not friends anymore", Toast.LENGTH_SHORT).show();
                                        }
                                        else
                                        {
                                            Log.d(TAG, "removeFriend failed: " + databaseError.getMessage());
                                        }
                                    }
                                });
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
                });
                break;
        }
        menu.addButton(button2);

        button3 = new TitleFAB(ProfileActivity.this);
        button3.setTitle("Send Message");
        button3.setBackgroundColor(getResources().getColor(R.color.colorBlue));
        button3.setRippleColor(getResources().getColor(R.color.colorBlueDark));
        button3.setImageResource(R.drawable.ic_message_white_24dp);
        button3.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent chatIntent = new Intent(ProfileActivity.this, ChatActivity.class);
                chatIntent.putExtra("userid", otherUserId);
                startActivity(chatIntent);
            }
        });
        menu.addButton(button3);
    }

    public void removeListeners()
    {
        if(userDatabase != null && userListener != null)
        {
            userDatabase.removeEventListener(userListener);
        }

        if(requestsDatabase != null && requestsListener != null)
        {
            requestsDatabase.removeEventListener(requestsListener);
        }

        if(friendsDatabase != null && friendsListerner != null)
        {
            friendsDatabase.removeEventListener(friendsListerner);
        }
    }
}