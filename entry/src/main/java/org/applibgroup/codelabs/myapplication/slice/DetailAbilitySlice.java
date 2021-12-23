package org.applibgroup.codelabs.myapplication.slice;

import github.nisrulz.packagehunter.packagehunterlib.PackageHunter;
import github.nisrulz.packagehunter.packagehunterlib.PkgInfo;
import github.nisrulz.packagehunter.packagehunterlib.utils.StringUtils;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.agp.components.Image;
import ohos.agp.components.Text;
import ohos.media.image.PixelMap;
import org.applibgroup.codelabs.myapplication.ResourceTable;
import java.util.Locale;

/**
 * Detail Ability for showing package details.
 */
public class DetailAbilitySlice extends AbilitySlice {

    private PkgInfo pkgInfo;
    private Text txtAppName;
    private Text txtVersionCode;
    private Text txtVersion;
    private Text txtPkgName;
    private Text txtFirstInstall;
    private Text txtLastUpdated;
    private Image imgIcon;

    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_detail);

        initUiComponents();

        String packageName = intent.getStringParam("data");
        if (!StringUtils.isEmpty(packageName)) {
            populateDataUsingPackageName(packageName);
        }
        pkgInfo = intent.getSequenceableParam("data");
        if (null != pkgInfo) {
            populateDataFromIntentObject();
        }
    }

    private void populateDataUsingPackageName(String packageName) {
        PackageHunter packageHunter = new PackageHunter(this);
        final String version = packageHunter.getVersionForPkg(packageName);
        final String versionCode = packageHunter.getVersionCodeForPkg(packageName);
        final String appName = packageHunter.getAppNameForPkg(packageName);
        final long firstInstallTime = packageHunter.getFirstInstallTimeForPkg(packageName);
        final long lastUpdateTime = packageHunter.getLastUpdatedTimeForPkg(packageName);
        PixelMap icon = packageHunter.getIconForPkg(packageName);
        if (null != icon) {
            imgIcon.setPixelMap(icon);
        }
        txtAppName.setName(appName);
        txtVersion.setText("Version : " + version);
        txtVersionCode.setText("Version Code : " + versionCode);
        txtPkgName.setText(pkgInfo.getPackageName());
        txtFirstInstall.setText("First Install Time : " + getFormattedUpTime(firstInstallTime));
        txtLastUpdated.setText("Last Update Time : " + getFormattedUpTime(lastUpdateTime));
        // String[] permissions = packageHunter.getPermissionForPkg(packageName);
        // String[] activities = packageHunter.getAbilitiesForPkg(packageName);
        // String[] services = packageHunter.getServicesForPkg(packageName);
    }

    private void populateDataFromIntentObject() {
        txtAppName.setName(pkgInfo.getAppName());
        txtVersion.setText("Version : " + pkgInfo.getVersionName());
        txtVersionCode.setText("Version Code : " + pkgInfo.getVersionCode());
        txtPkgName.setText(pkgInfo.getPackageName());
        txtFirstInstall.setText("First Install Time : " + getFormattedUpTime(pkgInfo.getFirstInstallTime()));
        txtLastUpdated.setText("Last Update Time : " + getFormattedUpTime(pkgInfo.getLastUpdateTime()));
    }

    private void initUiComponents() {
        txtAppName = (Text) findComponentById(ResourceTable.Id_txtvw_appname);
        txtVersion = (Text) findComponentById(ResourceTable.Id_txtvw_vname);
        txtVersionCode = (Text) findComponentById(ResourceTable.Id_txtvw_vc);
        txtPkgName = (Text) findComponentById(ResourceTable.Id_txtvw_pkgname);
        imgIcon = (Image) findComponentById(ResourceTable.Id_imgvw_icn);
        txtFirstInstall = (Text) findComponentById(ResourceTable.Id_txtvw_firsttime);
        txtLastUpdated = (Text) findComponentById(ResourceTable.Id_txtvw_lastupdated);
    }

    private String getFormattedUpTime(long millis) {
        int sec = (int) (millis / 1000) % 60;
        int min = (int) ((millis / (1000 * 60)) % 60);
        int hr = (int) ((millis / (1000 * 60 * 60)) % 24);

        return String.format(Locale.US, "%02d:%02d:%02d", hr, min, sec);
    }

    @Override
    public void onActive() {
        super.onActive();
    }

    @Override
    public void onForeground(Intent intent) {
        super.onForeground(intent);
    }
}
