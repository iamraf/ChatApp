package com.github.h01d.chatapp.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.github.h01d.chatapp.R;
import com.github.h01d.chatapp.activities.ProfileActivity;
import com.github.h01d.chatapp.holders.RequestHolder;
import com.github.h01d.chatapp.models.Request;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;
import java.util.Map;

/**
 * This is a part of ChatApp Project (https://github.com/h01d/ChatApp)
 * Licensed under Apache License 2.0
 *
 * @author  Raf (https://github.com/h01d)
 * @version 1.1
 * @since   27/02/2018
 */

public class RequestsFragment extends Fragment
{
    private final String TAG = "CA/RequestsFragment";

    private FirebaseRecyclerAdapter adapter;

    public RequestsFragment()
    {

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        final View view = inflater.inflate(R.layout.fragment_request, container, false);

        final String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Initializing Request database

        DatabaseReference requestsDatabase = FirebaseDatabase.getInstance().getReference().child("Requests").child(currentUserId);
        requestsDatabase.keepSynced(true); // For offline use

        // RecyclerView related

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

        RecyclerView recyclerView = view.findViewById(R.id.f_request_recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), linearLayoutManager.getOrientation());
        recyclerView.addItemDecoration(mDividerItemDecoration);

        // Initializing adapter

        FirebaseRecyclerOptions<Request> options = new FirebaseRecyclerOptions.Builder<Request>().setQuery(requestsDatabase.orderByChild("type"), Request.class).build();

        adapter = new FirebaseRecyclerAdapter<Request, RequestHolder>(options)
        {
            @Override
            protected void onBindViewHolder(final RequestHolder holder, int position, final Request model)
            {
                if(model.getType().equals("sent"))
                {
                    holder.getView().setVisibility(View.GONE);
                }
                else
                {
                    final String userid = getRef(position).getKey();

                    holder.setHolder(userid);
                    holder.getView().setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View view)
                        {
                            PopupMenu popup = new PopupMenu(getContext(), view);

                            popup.getMenu().add(Menu.NONE, 1, 1, "View Profile");
                            popup.getMenu().add(Menu.NONE, 2, 2, "Accept Request");

                            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
                            {
                                @Override
                                public boolean onMenuItemClick(MenuItem menuItem)
                                {
                                    switch(menuItem.getItemId())
                                    {
                                        case 1:
                                            Intent userProfileIntent = new Intent(getContext(), ProfileActivity.class);
                                            userProfileIntent.putExtra("userid", userid);
                                            startActivity(userProfileIntent);
                                            return true;
                                        case 2:
                                            // Pushing notification to get keyId

                                            DatabaseReference acceptNotificationRef = FirebaseDatabase.getInstance().getReference().child("Notifications").child(userid).push();
                                            String acceptNotificationId = acceptNotificationRef.getKey();

                                            // "Packing" request

                                            HashMap<String, String> acceptNotificationData = new HashMap<>();
                                            acceptNotificationData.put("from", currentUserId);
                                            acceptNotificationData.put("type", "accept");

                                            // "Packing" data

                                            Map map = new HashMap<>();
                                            map.put("Friends/" + userid + "/" + currentUserId + "/date", ServerValue.TIMESTAMP);
                                            map.put("Friends/" + currentUserId + "/" + userid + "/date", ServerValue.TIMESTAMP);

                                            map.put("Requests/" + userid + "/" + currentUserId, null);
                                            map.put("Requests/" + currentUserId + "/" + userid, null);

                                            map.put("Notifications/" + userid + "/" + acceptNotificationId, acceptNotificationData);

                                            // Updating data

                                            FirebaseDatabase.getInstance().getReference().updateChildren(map, new DatabaseReference.CompletionListener()
                                            {
                                                @Override
                                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference)
                                                {
                                                    if(databaseError == null)
                                                    {
                                                        Toast.makeText(getContext(), "You are now friends!", Toast.LENGTH_SHORT).show();
                                                    }
                                                    else
                                                    {
                                                        Log.d(TAG, "acceptRequest failed: " + databaseError.getMessage());
                                                    }
                                                }
                                            });
                                            return true;
                                        default:
                                            return false;
                                    }
                                }
                            });
                            popup.show();
                        }
                    });
                }
            }

            @Override
            public RequestHolder onCreateViewHolder(ViewGroup parent, int viewType)
            {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user, parent, false);

                return new RequestHolder(getActivity(), view, getContext());
            }

            @Override
            public void onDataChanged()
            {
                super.onDataChanged();

                int counter = 0;

                for(int i = 0; i < adapter.getItemCount(); i++)
                {
                    Request tmp = (Request) adapter.getItem(i);

                    if(tmp != null && tmp.getType().equals("received"))
                    {
                        counter++;
                    }
                }

                TextView text = view.findViewById(R.id.f_request_text);

                if(counter == 0)
                {
                    text.setVisibility(View.VISIBLE);
                }
                else
                {
                    text.setVisibility(View.GONE);
                }
            }
        };

        recyclerView.setAdapter(adapter);
        return view;
    }

    public void onStart()
    {
        super.onStart();

        adapter.startListening();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onStop()
    {
        super.onStop();

        adapter.stopListening();
    }
}