package com.armjld.eb3tly.Chat;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.armjld.eb3tly.Home.HomeActivity;
import com.armjld.eb3tly.Home.HomeFragment;
import com.armjld.eb3tly.R;
import Model.UserInFormation;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

import Model.ChatsData;

public class ChatFragmet extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    DatabaseReference messageDatabase;
    String uId = UserInFormation.getId();

    private chatsAdapter _chatsAdapter;
    public static String cameFrom = "Profile";

    RecyclerView recyclerChat;
    ArrayList<ChatsData> mChat;
    ImageView btnBack;
    TextView txtEmpty;

    public ChatFragmet() { }
    
    public static ChatFragmet newInstance(String param1, String param2) {
        ChatFragmet fragment = new ChatFragmet();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        recyclerChat = view.findViewById(R.id.recyclerChat);
        recyclerChat.setHasFixedSize(true);
        LinearLayoutManager layoutManager= new LinearLayoutManager(getActivity());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerChat.setLayoutManager(layoutManager);
        TextView tbTitle = view.findViewById(R.id.toolbar_title);
        btnBack = view.findViewById(R.id.btnBack);
        txtEmpty = view.findViewById(R.id.txtEmpty);
        tbTitle.setText("المحادثات");

        btnBack.setOnClickListener(v-> {
            HomeActivity.whichFrag = "Home";
            assert getFragmentManager() != null;
            getFragmentManager().beginTransaction().replace(R.id.container, new HomeFragment(), HomeActivity.whichFrag).addToBackStack("Home").commit();
            HomeActivity.bottomNavigationView.setSelectedItemId(R.id.home);
        });

        messageDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("chatRooms");
        mChat = new ArrayList<>();
        final int[] count = {0};
        FirebaseDatabase.getInstance().getReference().child("Pickly").child("users").child(uId).child("chats").orderByChild("timestamp").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds:snapshot.getChildren()) {
                    if(ds.child("roomid").exists()) {
                        ChatsData cchatData = ds.getValue(ChatsData.class);

                        String talk = "true";
                        if(ds.child("talk").exists()) {
                            talk = Objects.requireNonNull(ds.child("talk").getValue()).toString();
                        }

                        if(ds.child("timestamp").exists() && talk.equals("true")) {
                            mChat.add(cchatData);
                            count[0] +=1;
                            _chatsAdapter = new chatsAdapter(getActivity(), mChat);
                            _chatsAdapter.addItem(count[0],cchatData);
                            recyclerChat.setAdapter(_chatsAdapter);
                        }
                    }
                }

                if(mChat.size() >= 1 ) {
                    txtEmpty.setVisibility(View.GONE);
                } else {
                    txtEmpty.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
        
        return view;
    }

}