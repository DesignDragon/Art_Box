package com.example.artbox;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class side_menu extends AppCompatActivity {
    BottomNavigationView bottomappbar;
    NavigationView nav;
    Fragment f=null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_side_menu);

        /*viewPager = (ViewPager) findViewById(R.id.view_pager_for_bottom);*/

        nav=(NavigationView)findViewById(R.id.left_menu);
        nav.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId())
                {
                    case R.id.user_profile:
                        f=new user_profile_fragment();
                        break;
                    case R.id.logout:
                        logout();
                }
                return loadFragment(f);
            }
        });


        bottomappbar = (BottomNavigationView) findViewById(R.id.btm_view);
        bottomappbar.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.home:
                                f = new home_screen_fragment();
                                /*viewPager.setCurrentItem(0);*/
                                break;
                            case R.id.search:
                                f = new search_tab_fragment();
                                /*viewPager.setCurrentItem(1);*/
                                break;
                            case R.id.camera:
                                f= new camera();
                                break;
                            case R.id.add_picture:
                                startActivity(new Intent(side_menu.this, upload_picture.class));
                                finish();
                        }
                        return loadFragment(f);
                    }
                }
        );

        /*viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }
            @Override
            public void onPageSelected(int position) {
                if(m!=null)
                {
                    m.setChecked(false);
                }
                else
                {
                    bottomappbar.getMenu().getItem(0).setChecked(false);
                }
                bottomappbar.getMenu().getItem(position).setChecked(true);
                m=bottomappbar.getMenu().getItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        setupViewPager(viewPager);
    }*/

   /* private void setupViewPager(ViewPager viewPager)
    {
        ViewPagerAdapter adapter=new ViewPagerAdapter(getSupportFragmentManager());
        f1=new home_screen_fragment();
        f2=new search_tab_fragment();
        f11=new user_profile_fragment();

        adapter.addFragment(f1);
        adapter.addFragment(f2);
        adapter.addFragment(f11);

        viewPager.setAdapter(adapter);
    }
*/
    }
    private boolean loadFragment(Fragment fragment)
    {
        if(fragment!=null)
        {
            getSupportFragmentManager().beginTransaction().replace(R.id.frag_container,fragment).commit();
            return true;
        }
        return false;
    }
    private void logout ()
    {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(side_menu.this, sign_in.class));
        finish();
    }
}
