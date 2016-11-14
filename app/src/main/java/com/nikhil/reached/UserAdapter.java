package com.nikhil.reached;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.nikhil.reached.beans.User;
import com.nikhil.reached.utils.SendNotification;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nikhil on 07/11/16.
 */

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private Activity mActivity;
    private DatabaseReference mDatabaseReference;
    private ChildEventListener mChildEventListener;
    private String selectedLocation;
    private List<String> mUserIds = new ArrayList<>();
    private List<User> mUsers = new ArrayList<>();

    private static final int JOURNEY_STARTED = 1;


    public UserAdapter(final Activity mActivity, DatabaseReference ref, String selectedLocation) {
        this.mActivity = mActivity;
        this.mDatabaseReference = ref;
        this.selectedLocation = selectedLocation;


        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {

                // A new myUser has been added, add it to the displayed list
                User user = dataSnapshot.getValue(User.class);

                if (!user.getUserEmail().equalsIgnoreCase(User.getMyUser().getUserEmail())) {
                    mUserIds.add(dataSnapshot.getKey());
                    mUsers.add(user);
                    notifyItemInserted(mUsers.size() - 1);
                }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {

                // A myUser has changed, use the key to determine if we are displaying this
                // myUser and if so displayed the changed myUser.
                User newUser = dataSnapshot.getValue(User.class);
                String userKey = dataSnapshot.getKey();


                int userIndex = mUserIds.indexOf(userKey);
                if (userIndex > -1) {
                    // Replace with the new data
                    mUsers.set(userIndex, newUser);

                    // Update the RecyclerView
                    notifyItemChanged(userIndex);
                }

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

                // A myUser has changed, use the key to determine if we are displaying this
                // myUser and if so remove it.
                String userKey = dataSnapshot.getKey();

                // [START_EXCLUDE]
                int userIndex = mUserIds.indexOf(userKey);
                if (userIndex > -1) {
                    // Remove data from the list
                    mUserIds.remove(userIndex);
                    mUsers.remove(userIndex);

                    // Update the RecyclerView
                    notifyItemRemoved(userIndex);
                } else {
                }
                // [END_EXCLUDE]
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {

                // A myUser has changed position, use the key to determine if we are
                // displaying this myUser and if so move it.
                User movedUser = dataSnapshot.getValue(User.class);
                String userKey = dataSnapshot.getKey();

                // ...
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(mActivity, R.string.failed_to_load_users,
                        Toast.LENGTH_SHORT).show();
            }
        };
        ref.addChildEventListener(childEventListener);

        mChildEventListener = childEventListener;
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mActivity);
        View view = inflater.inflate(R.layout.user_list_item, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(UserViewHolder holder, int position) {
        User user = mUsers.get(position);
        holder.itemView.setTag(user);
        holder.name.setText(user.getUserName());
        holder.firstLetter.setText(user.getUserName().substring(0, 1));
        holder.email.setText(user.getUserEmail());
        holder.phone.setText(user.getMobileNo());


    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public void cleanupListener() {
        if (mChildEventListener != null) {
            mDatabaseReference.removeEventListener(mChildEventListener);
        }
    }


    public class UserViewHolder extends RecyclerView.ViewHolder {

        public TextView name;
        public TextView firstLetter;
        public TextView phone;
        public TextView email;
        public View itemView;

        public UserViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    User user = (User) view.getTag();
                    new SendNotification(user.getFirebaseRegid(), user.getUserName(), JOURNEY_STARTED, selectedLocation, mActivity).execute();


                }
            });
            name = (TextView) itemView.findViewById(R.id.name);
            firstLetter = (TextView) itemView.findViewById(R.id.firstLetter);
            phone = (TextView) itemView.findViewById(R.id.phone);
            email = (TextView) itemView.findViewById(R.id.email);


        }
    }


}