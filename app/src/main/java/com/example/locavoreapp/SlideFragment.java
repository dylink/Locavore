package com.example.locavoreapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SlideFragment extends Fragment implements Filterable {
    ListView listView;
    int page;
    public ArrayList<Offreclass> offreListe = new ArrayList<>();
    public ArrayList<Offreclass> offreListeFull = new ArrayList<>();
    public CustomAdapter customAdapter;
    public boolean visible = false;
    public boolean created = false;
    public boolean isSearching = false;
    private DatabaseReference mPostReference;
    public SwipeRefreshLayout pullToRefresh;
    FirebaseUser user;

    public SlideFragment(){
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.listview, container, false);
        listView = (ListView) rootView.findViewById(R.id.listView);
        user = FirebaseAuth.getInstance().getCurrentUser();
        mPostReference = FirebaseDatabase.getInstance().getReference().child("offres");
        user = FirebaseAuth.getInstance().getCurrentUser();
        created = true;
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Offreclass offre = (Offreclass) parent.getAdapter().getItem(position);
                Intent intent = new Intent(getActivity(), Offre.class);
                intent.putExtra("offre", offre.titre);
                getActivity().startActivityForResult(intent,  1);

            }
        });
        customAdapter = new CustomAdapter(getActivity(),offreListe );
        listView.setAdapter(customAdapter);
        if(!isSearching){
            mPostReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                    offreListe.clear();
                    for (DataSnapshot child : snapshot.getChildren()) {
                        if(page != 2){
                            final Offreclass offre = child.getValue(Offreclass.class);
                            offreListe.add(offre);
                            if(page == 1) {
                                Collections.sort(offreListe);
                            }
                        }
                        else{
                            if(child.child("ListID").child(user.getUid()).exists()){
                                offreListe.add(child.getValue(Offreclass.class));
                            }
                        }
                    }
                    customAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                }
            });
        }

        pullToRefresh = (SwipeRefreshLayout) rootView.findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //getDataBase();
                listView.setAdapter(customAdapter);
                pullToRefresh.setRefreshing(false);
            }
        });
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (listView.getChildAt(0) != null) {
                    pullToRefresh.setEnabled(listView.getFirstVisiblePosition() == 0 && listView.getChildAt(0).getTop() == 0 && !isSearching);
                }
            }
        });
        return rootView;
    }

    @Override
    public void onStart(){
        super.onStart();

    }

    @Override
    public Filter getFilter() {

        Filter filter = new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                ArrayList<Offreclass> offreListe2 = new ArrayList<>();
                if ( (offreListe2.size()!=offreListeFull.size()) && (constraint == null || constraint.length() == 0)) {
                    offreListe2.addAll(offreListeFull);
                } else {
                    String pattern = constraint.toString().toLowerCase().trim();
                    for(Offreclass item : offreListeFull){
                        if(item.titre.toLowerCase().startsWith(pattern)) {
                            offreListe2.add(item);
                        }
                    }
                }
                FilterResults results = new FilterResults();
                results.values = offreListe2;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                offreListe.clear();
                offreListe.addAll((List)results.values);
                customAdapter.notifyDataSetChanged();
            }
        };
        return filter;
    }

    @Override
    public void setMenuVisibility(boolean isvisible) {
        super.setMenuVisibility(isvisible);
        if (isvisible){
            visible = true;
            if(created){
                if(!isSearching){
                    getDataBase(page);
                }

            }

        }
    }

    public void getDataBase(final int pageu){
        offreListe.clear();
        mPostReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                offreListe.clear();
                    for (DataSnapshot child : snapshot.getChildren()) {
                        if(pageu != 2){
                            final Offreclass offre = child.getValue(Offreclass.class);
                            offreListe.add(offre);
                            if(pageu == 1) {
                                Collections.sort(offreListe);
                            }
                        }
                        else{
                            if(child.child("ListID").child(user.getUid()).exists()){
                                offreListe.add(child.getValue(Offreclass.class));
                            }
                        }
                    }
                customAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
            }
        });
    }
}
