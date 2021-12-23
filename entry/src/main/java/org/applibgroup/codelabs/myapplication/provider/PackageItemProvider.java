package org.applibgroup.codelabs.myapplication.provider;

import github.nisrulz.packagehunter.packagehunterlib.PkgInfo;
import ohos.aafwk.content.Intent;
import ohos.aafwk.content.Operation;
import ohos.app.Context;
import org.applibgroup.codelabs.myapplication.MainAbility;
import org.applibgroup.codelabs.myapplication.ResourceTable;
import java.util.List;

/**
 * Item Provider for Package ListContainer.
 */
public class PackageItemProvider extends BaseItemProvider {

    private final Context context;
    private final List<PkgInfo> pkgInfoList;

    public PackageItemProvider(Context context, List<PkgInfo> pkgInfoList) {
        this.pkgInfoList = pkgInfoList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return pkgInfoList.size();
    }

    @Override
    public Object getItem(int position) {
        return pkgInfoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private Component getItemComponent(int position) {
        return getComponent(position);
    }

    @Override
    public Component getComponent(int position, Component component, ComponentContainer componentContainer) {
        return getItemComponent(position);
    }

    private Component getComponent(int position) {
        PkgInfo pkgInfo = pkgInfoList.get(position);
        Component container = LayoutScatter.getInstance(context).parse(ResourceTable.Layout_list_item, null, false);
        Text textAppName = (Text) container.findComponentById(ResourceTable.Id_name);
        textAppName.setText(pkgInfo.getAppName());
        Text textVersionCode = (Text) container.findComponentById(ResourceTable.Id_version_code);
        textVersionCode.setText(pkgInfo.getVersionCode() + "");
        Text textVersionName = (Text) container.findComponentById(ResourceTable.Id_version_code);
        textVersionName.setText(pkgInfo.getVersionName());
        Component btnDetail = container.findComponentById(ResourceTable.Id_btn_detail);
        btnDetail.setClickedListener(component -> showPackageDetail(position));
        return container;
    }

    private void showPackageDetail(int position) {
        Intent intent = new Intent();
        Operation operation = new Intent.OperationBuilder()
                .withDeviceId("")
                .withBundleName(context.getBundleName())
                .withAbilityName("org.applibgroup.codelabs.myapplication.DetailAbility")
                .build();
        intent.setOperation(operation);
        intent.setParam("data", pkgInfoList.get(position));
        ((MainAbility) context).startAbility(intent);
    }
}
