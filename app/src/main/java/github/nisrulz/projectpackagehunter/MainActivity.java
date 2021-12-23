/*
 * Copyright (C) 2016 Nishant Srivastava
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package github.nisrulz.projectpackagehunter;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import github.nisrulz.packagehunter.PackageHunter;
import github.nisrulz.packagehunter.PkgInfo;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private RVMainAdapter adapter;

    private PackageHunter packageHunter;

    private ArrayList<PkgInfo> pkgInfoArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        packageHunter = new PackageHunter(this);

        RecyclerView rv = (RecyclerView) findViewById(R.id.rv_pkglist);
        pkgInfoArrayList = packageHunter.getInstalledPackages();

        adapter = new RVMainAdapter(this, pkgInfoArrayList);
        rv.hasFixedSize();
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);

        // Set On Click
        rv.addOnItemTouchListener(
                new RVItemClickListener(this, new RVItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Intent i = new Intent(MainActivity.this, DetailActivity.class);
                        i.putExtra("data", pkgInfoArrayList.get(position).getPackageName());
                        startActivity(i);
                        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                    }
                }));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        MenuItem searchViewItem = menu.findItem(R.id.action_search);
        final SearchView searchViewAndroidActionBar =
                (SearchView) MenuItemCompat.getActionView(searchViewItem);
        searchViewAndroidActionBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchViewAndroidActionBar.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String query) {

                pkgInfoArrayList = packageHunter.searchInList(query, PackageHunter.PACKAGES);
                adapter.updateWithNewListData(pkgInfoArrayList);

                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh: {
                pkgInfoArrayList = packageHunter.getInstalledPackages();
                adapter.updateWithNewListData(pkgInfoArrayList);
                break;
            }
            case R.id.action_about: {
                startActivity(new Intent(MainActivity.this, AboutActivity.class));
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                break;
            }
            case R.id.action_privacy: {
                Uri uri = Uri.parse(
                        "https://cdn.rawgit.com/nisrulz/f142e91b83497ae254499d1d44b4afad/raw/1c46f779a80db0bd4946273acdf8109874984eac/PackageHunterPrivacyPolicy.html");
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(browserIntent);
            }
        }

        return super.onOptionsItemSelected(item);
    }
}
