package org.applibgroup.codelabs.myapplication;

import github.nisrulz.packagehunter.packagehunterlib.utils.LogUtils;
import ohos.aafwk.ability.Ability;
import ohos.aafwk.content.Intent;
import ohos.agp.window.dialog.ToastDialog;
import ohos.app.Context;
import ohos.bundle.IBundleManager;
import org.applibgroup.codelabs.myapplication.slice.MainAbilitySlice;

import java.util.Arrays;

import static ohos.data.search.schema.PhotoItem.TAG;

public class MainAbility extends Ability {
    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setMainRoute(MainAbilitySlice.class.getName());
    }

    @Override
    public void onRequestPermissionsFromUserResult(int requestCode, java.lang.String[] permissions,
                                                   int[] grantResults) {
        if (permissions == null || permissions.length == 0 || grantResults == null || grantResults.length == 0) {
            return;
        }
        LogUtils.debug(TAG,
                "requestCode: " + requestCode + ", permissions:" + Arrays.toString(permissions) + ", grantResults: "
                        + Arrays.toString(grantResults));

        if (grantResults[0] == IBundleManager.PERMISSION_GRANTED) {
            showTips(this, "Permission granted");
        } else {
            showTips(this, "Permission denied");
        }
    }

    private void showTips(Context context, String message) {
        new ToastDialog(context).setText(message).show();
    }

}
