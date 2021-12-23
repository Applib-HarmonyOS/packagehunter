package org.applibgroup.codelabs.myapplication.slice;

import github.nisrulz.packagehunter.packagehunterlib.PackageHunter;
import github.nisrulz.packagehunter.packagehunterlib.PkgInfo;
import github.nisrulz.packagehunter.packagehunterlib.utils.LogUtils;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.agp.components.ListContainer;
import ohos.agp.window.dialog.ToastDialog;
import ohos.app.Context;
import ohos.bundle.IBundleManager;
import ohos.security.SystemPermission;
import org.applibgroup.codelabs.myapplication.MainAbility;
import org.applibgroup.codelabs.myapplication.ResourceTable;
import org.applibgroup.codelabs.myapplication.provider.PackageItemProvider;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;


/**
 * Main Ability for the app.
 */
public class MainAbilitySlice extends AbilitySlice {

    private final static String TAG = MainAbilitySlice.class.getSimpleName();
    private PackageHunter packageHunter;
    private List<PkgInfo> pkgInfoList = new ArrayList<>();
    private ListContainer listContainer;
    private PackageItemProvider packageItemProvider;
    private Random randomInt;

    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_main);
        try {
            randomInt = SecureRandom.getInstanceStrong();
        } catch (NoSuchAlgorithmException e) {
            LogUtils.e(TAG, e.getMessage());
        }
    }

    @Override
    public void onActive() {
        super.onActive();

        packageHunter = new PackageHunter(this);
        initUiComponents();
        initProvider();
    }

    private void initUiComponents() {
        listContainer = (ListContainer) findComponentById(ResourceTable.Id_list_package);
        findComponentById(ResourceTable.Id_text_pkg_hunter)
                .setClickedListener(component -> populatePackageList()); // findInstalledPackages());
    }

    private void initProvider() {
        packageItemProvider = new PackageItemProvider(getAbility(), pkgInfoList);
        listContainer.setItemProvider(packageItemProvider);
        listContainer.setReboundEffect(true);
    }

    private void populatePackageList() {
        preparePackageList();
        packageItemProvider.notifyDataChanged();
    }

    /**
     * Create list of dummy packages.
     */
    private void preparePackageList() {
        for (int count = 0; count < 8; count++) {
            PkgInfo pkgInfo = new PkgInfo();
            pkgInfo.setAppName("Demo App " + count);
            pkgInfo.setPackageName("org.applibgroup.demo.sampleapp" + count);
            pkgInfo.setVersionCode(this.randomInt.nextInt());
            pkgInfo.setVersionName("1.0." + count);
            pkgInfo.setFirstInstallTime(TimeUnit.MILLISECONDS.toSeconds(
                    System.currentTimeMillis() - this.randomInt.nextInt(100000)));
            pkgInfo.setLastUpdateTime(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));
            pkgInfoList.add(pkgInfo);
        }
        showTips(this, "Size = " + pkgInfoList.size() + "");
        // shuffle the list
        Collections.shuffle(pkgInfoList);
    }

    /**
     * method to find list of installed packages.
     */
    private void findInstalledPackages() {
        pkgInfoList = packageHunter.getInstalledPackages();
        for (int count = 0; count < pkgInfoList.size(); count++) {
            LogUtils.info(TAG, "PkgInfo Details [ " + count + " ] = "
                    + pkgInfoList.get(count).toString());
        }
    }

    /**
     * method to request permissions.
     * @param requestCode request code to be passed
     */
    private void requestPermission(int requestCode) {
        if (verifySelfPermission(SystemPermission.GET_BUNDLE_INFO) == IBundleManager.PERMISSION_GRANTED) {
            showTips(this, "Permission already obtained");
            return;
        }
        if (!canRequestPermission(SystemPermission.GET_BUNDLE_INFO)) {
            showTips(this, "Cannot request Permission");
            LogUtils.e(TAG, "Cannot request Permission");
            //return;
        }
        requestPermissionsFromUser(new String[]{SystemPermission.GET_BUNDLE_INFO}, requestCode);
    }

    private void showTips(Context context, String message) {
        new ToastDialog(context).setText(message).show();
    }

    @Override
    public void onForeground(Intent intent) {
        super.onForeground(intent);
    }
}
