package com.sellit.testdrawer;

import android.app.Fragment;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 * Created by Andrewa on 4/29/2017.
 */

//Intended to be able to make and display the user's goals.
public class GoalFragment extends Fragment {

    RecyclerView recView;
    String TAG = GoalFragment.class.getSimpleName();
    View myView;


    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState)
    {
        myView = inflater.inflate(R.layout.list_donations_fragment, container, false);
        return myView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        recView = (RecyclerView) view.findViewById(R.id.donationIncomingRecView);
        setupRec(view);
        super.onViewCreated(view, savedInstanceState);
    }

    //Sets up the recycler view for inflated elements to be inserted into it.
    private void setupRec(View view)
    {

        getItems();
        LinearLayoutManager manager = new LinearLayoutManager(view.getContext());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recView.setLayoutManager(manager);
        recView.setItemAnimator(new DefaultItemAnimator());

    }

    //Gets the elements to be inserted into the recycler view fromm our database.
    public void getItems() {
        final ArrayList<Item> listItems = new ArrayList<>();
        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
        mRef.child("items").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<String, Object> items = (HashMap<String, Object>) dataSnapshot.getValue();
                if (items == null) {
                    return;
                }
                Set keys = items.keySet();
                Object[] itemList = items.values().toArray();
                final ItemListAdapter adapter = new ItemListAdapter(listItems, getActivity());
                for (int i = 0; i < itemList.length; i++) {
                    Object item = itemList[i];
                    String Key = (String) keys.toArray()[i];
                    HashMap<String, Object> itemMap = (HashMap<String, Object>) item;

                    final Item itemTemp = new Item();
                    itemTemp.description = (String) itemMap.remove("description");
                    Log.d(Item.TAG, "description: " + itemTemp.description);
                    itemTemp.Key = Key;
                    itemTemp.name = (String) itemMap.remove("name");
                    itemTemp.price = (String) itemMap.remove("price");
                    itemTemp.rating = ((Long) itemMap.remove("rating")).intValue();
                    itemTemp.uid = (String) itemMap.remove("uid");
                    String path = "gs://nationals-master.appspot.com";
                    StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(path);
                    Log.d(TAG, path);
                    StorageReference sRef = storageReference.child("images/items/" + Key + ".png");
                    sRef.getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            Log.d(TAG, "Num Bytes: " + bytes.length);
                            itemTemp.image = new BitmapDrawable(getResources(), BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
                            listItems.add(itemTemp);
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.notifyDataSetChanged();
                                }
                            });


                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });

                }
                Log.d(TAG, "Num Items: " + listItems.size());

                recView.setAdapter(adapter);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return;
    }
}
