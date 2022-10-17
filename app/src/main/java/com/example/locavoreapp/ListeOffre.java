package com.example.locavoreapp;

import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toolbar;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ListeOffre extends AppCompatActivity {

    SlideFragment SlideFragment1;
    SlideFragment SlideFragment2;
    SlideFragment SlideFragment3;
    FirebaseUser user;
    public ArrayList<Offreclass> offreListeFull = new ArrayList<>();
    private DatabaseReference mPostReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste_offre);
        SlideFragment1 = new SlideFragment();
        SlideFragment2 = new SlideFragment();
        SlideFragment3 = new SlideFragment();
        SlideFragment1.page = 0;
        SlideFragment2.page = 1;
        SlideFragment3.page = 2;
        ViewPager mPager;
        TabLayout tabLayout;
        ScreenSlidePagerAdapter adapter;
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);

        mPager = (ViewPager) findViewById(R.id.pager);
        adapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        adapter.addFragment( SlideFragment1, getString(R.string.all));
        adapter.addFragment( SlideFragment2, getString(R.string.top));
        adapter.addFragment( SlideFragment3, getString(R.string.favorite));
        mPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(mPager);
        user = FirebaseAuth.getInstance().getCurrentUser();
        mPostReference = FirebaseDatabase.getInstance().getReference().child("offres");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        MenuItem searchItem = menu.findItem(R.id.searchView);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(SlideFragment1.created) SlideFragment1.isSearching = true;
                if(SlideFragment2.created) SlideFragment2.isSearching = true;
                if(SlideFragment3.created) SlideFragment3.isSearching = true;
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                if(SlideFragment1.created) SlideFragment1.isSearching = false;
                if(SlideFragment2.created) SlideFragment2.isSearching = false;
                if(SlideFragment3.created) SlideFragment3.isSearching = false;
                return false;
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                getAllOffers(newText);
                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item){
        if(item.getItemId() == R.id.settings){
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(ListeOffre.this, SignIn.class));
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void addOffre (View view){
        Intent intent = new Intent(ListeOffre.this, AddOffre.class);
        startActivity(intent);
    }

    public void showMaps(View view){
        Intent intent = new Intent(ListeOffre.this, OffreMaps.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_up,  R.anim.no_animation);
    }

    private static class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @NotNull
        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

    }

    public void getAllOffers(final String newText){

        mPostReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                offreListeFull.clear();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    final Offreclass offre = child.getValue(Offreclass.class);
                    offreListeFull.add(offre);

                }
                if(SlideFragment1.created) {

                    SlideFragment1.offreListeFull = offreListeFull;
                    SlideFragment1.getFilter().filter(newText);
                }
                if(SlideFragment2.created) {
                    SlideFragment2.offreListeFull = offreListeFull;
                    SlideFragment2.getFilter().filter(newText);
                }
                if(SlideFragment3.created) {
                    SlideFragment3.offreListeFull = offreListeFull;
                    SlideFragment3.getFilter().filter(newText);
                }
            }

            @Override
            public void onCancelled(@NotNull DatabaseError databaseError) {
            }
        });
    }

}