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

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;
import github.nisrulz.packagehunter.PackageHunter;
import java.util.ArrayList;
import java.util.Locale;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

        String packageName = getIntent().getStringExtra("data");
        PackageHunter packageHunter = new PackageHunter(this);

        String version = packageHunter.getVersionForPkg(packageName);
        String versionCode = packageHunter.getVersionCodeForPkg(packageName);
        String appName = packageHunter.getAppNameForPkg(packageName);
        long firstInstallTime = packageHunter.getFirstInstallTimeForPkg(packageName);
        long lastUpdateTime = packageHunter.getLastUpdatedTimeForPkg(packageName);
        Drawable icon = packageHunter.getIconForPkg(packageName);

        String[] permissions = packageHunter.getPermissionForPkg(packageName);
        String[] activities = packageHunter.getActivitiesForPkg(packageName);
        String[] services = packageHunter.getServicesForPkg(packageName);
        String[] providers = packageHunter.getProvidersForPkg(packageName);
        String[] receivers = packageHunter.getReceiverForPkg(packageName);

        TextView txt_version = (TextView) findViewById(R.id.txtvw_vname);
        TextView txt_versioncode = (TextView) findViewById(R.id.txtvw_vc);
        TextView txt_pkg = (TextView) findViewById(R.id.txtvw_pkgname);
        ImageView img_icon = (ImageView) findViewById(R.id.imgvw_icn);

        TextView txt_firsttime = (TextView) findViewById(R.id.txtvw_firsttime);
        TextView txt_lastupdated = (TextView) findViewById(R.id.txtvw_lastupdated);

        img_icon.setImageDrawable(icon);
        txt_version.setText("Version : " + version);
        txt_versioncode.setText("Version Code : " + versionCode);
        txt_pkg.setText(packageName);
        txt_firsttime.setText("First Install Time : " + getFormattedUpTime(firstInstallTime));
        txt_lastupdated.setText("Last Update Time : " + getFormattedUpTime(lastUpdateTime));

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(appName);
        }

        RecyclerView rv = (RecyclerView) findViewById(R.id.rv_detaillist);
        ArrayList<ElementInfo> elementInfoArrayList = new ArrayList<>();
        elementInfoArrayList.add(new ElementInfo("Permissions", permissions));
        elementInfoArrayList.add(new ElementInfo("Services", services));
        elementInfoArrayList.add(new ElementInfo("Activities", activities));
        elementInfoArrayList.add(new ElementInfo("Providers", providers));
        elementInfoArrayList.add(new ElementInfo("Receivers", receivers));

        RVDetailsAdapter adapter = new RVDetailsAdapter(elementInfoArrayList);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        finish();
    }

    private String getFormattedUpTime(long millis) {
        int sec = (int) (millis / 1000) % 60;
        int min = (int) ((millis / (1000 * 60)) % 60);
        int hr = (int) ((millis / (1000 * 60 * 60)) % 24);

        return String.format(Locale.US, "%02d:%02d:%02d", hr, min, sec);
    }
}
