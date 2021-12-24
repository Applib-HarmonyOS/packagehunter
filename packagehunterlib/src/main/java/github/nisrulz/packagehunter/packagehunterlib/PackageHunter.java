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

package github.nisrulz.packagehunter.packagehunterlib;


import github.nisrulz.packagehunter.packagehunterlib.utils.LogUtils;
import github.nisrulz.packagehunter.packagehunterlib.utils.StringUtils;
import ohos.app.Context;
import ohos.bundle.AbilityInfo;
import ohos.bundle.BundleInfo;
import ohos.bundle.IBundleManager;
import ohos.global.resource.*;
import ohos.media.image.ImageSource;
import ohos.media.image.PixelMap;
import ohos.media.image.common.PixelFormat;
import ohos.media.image.common.Rect;
import ohos.media.image.common.Size;
import ohos.rpc.RemoteException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Class for finding all information about any or all packages for HarmonyOS.
 */
public class PackageHunter {

    // Flags
    public static final int APPLICATIONS = 0;

    public static final int PACKAGES = 1;

    public static final int PERMISSIONS = 2;

    public static final int SERVICES = 3;

    public static final int ACTIVITIES = 5;

    private static final String TAG = "PackageHunter";
    private static final int IO_END_LEN = -1;
    private static final int CACHE_SIZE = 256 * 1024;
    private final Context context;
    private final IBundleManager packageManager;

    public PackageHunter(Context context) {
        packageManager = context.getBundleManager();
        this.context = context;
    }

    /**
     * get the pixel map.
     *
     * @param context the context
     * @param id      the id
     * @return the pixel map
     */
    public static Optional<PixelMap> getPixelMap(Context context, int id) {
        String path = getPathById(context, id);
        if (StringUtils.isEmpty(path)) {
            LogUtils.e(TAG, "getPixelMap -> get empty path");
            return Optional.empty();
        }
        RawFileEntry assetManager = context.getResourceManager().getRawFileEntry(path);
        ImageSource.SourceOptions options = new ImageSource.SourceOptions();
        options.formatHint = "image/png";
        ImageSource.DecodingOptions decodingOptions = new ImageSource.DecodingOptions();
        try {
            Resource asset = assetManager.openRawFile();
            ImageSource source = ImageSource.create(asset, options);
            return Optional.ofNullable(source.createPixelmap(decodingOptions));
        } catch (IOException e) {
            LogUtils.e(TAG, "getPixelMap -> IOException");
        }
        return Optional.empty();
    }

    /**
     * get the path from id.
     *
     * @param context the context
     * @param id      the id
     * @return the path from id
     */
    public static String getPathById(Context context, int id) {
        LogUtils.info(TAG, "Entering getPathById");
        String path = "";
        if (context == null) {
            LogUtils.e(TAG, "getPathById -> get null context");
            return path;
        }
        ResourceManager manager = context.getResourceManager();
        if (manager == null) {
            LogUtils.e(TAG, "getPathById -> get null ResourceManager");
            return path;
        }
        try {
            path = manager.getMediaPath(id);
        } catch (IOException e) {
            LogUtils.e(TAG, "getPathById -> IOException");
        } catch (NotExistException e) {
            LogUtils.e(TAG, "getPathById -> NotExistException");
        } catch (WrongTypeException e) {
            LogUtils.e(TAG, "getPathById -> WrongTypeException");
        }
        return path;
    }

    /**
     * Returns Abilities for the specified package.
     *
     * @param packageName package name
     * @return Abilities name in String array
     */
    public String[] getAbilitiesForPkg(String packageName) {
        BundleInfo bundleInfo = getPkgInfo(packageName, AbilityInfo.AbilityType.PAGE.ordinal());
        if (bundleInfo.getAbilityInfos() != null) {
            ArrayList<String> data = new ArrayList<>(bundleInfo.abilityInfos.size());
            for (int i = 0; i < bundleInfo.abilityInfos.size(); i++) {
                data.add(bundleInfo.abilityInfos.get(i).getClassName());
            }
            return data.toArray(new String[0]);
        } else {
            return new String[0];
        }
    }

    public String getAppNameForPkg(String packageName) {
        BundleInfo bundleInfo = getPkgInfo(packageName, 0);
        return bundleInfo != null ? bundleInfo.appInfo.getName() : null;
    }

    public long getFirstInstallTimeForPkg(String packageName) {
        BundleInfo bundleInfo = getPkgInfo(packageName, 0);
        return bundleInfo != null ? bundleInfo.getInstallTime() : 0;
    }

    /**
     * Returns icon for specified package.
     *
     * @param packageName String packagename
     * @return icon in PixelMap format
     */
    public PixelMap getIconForPkg(String packageName) {
        BundleInfo info = getPkgInfo(packageName, 0x00000000);
        String iconPath = info.getAppInfo().getIcon();
        byte[] iconByteArray = null;
        try {
            if (!StringUtils.isEmpty(iconPath)) {
                iconByteArray = readByteFromFile(iconPath);
            }
        } catch (IOException e) {
            LogUtils.e(TAG, "getIconForPkg # exception during # readByteFromFile = " + e.getMessage());
        }
        if (null != iconByteArray) {
            ImageSource.SourceOptions srcOpts = new ImageSource.SourceOptions();
            srcOpts.formatHint = "image/png";
            ImageSource imageSource = ImageSource.create(iconByteArray, srcOpts);
            if (imageSource == null) {
                return null;
            }
            ImageSource.DecodingOptions decodingOpts = new ImageSource.DecodingOptions();
            decodingOpts.desiredSize = new Size(0, 0);
            decodingOpts.desiredRegion = new Rect(0, 0, 0, 0);
            decodingOpts.desiredPixelFormat = PixelFormat.ARGB_8888;
            return imageSource.createPixelmap(decodingOpts);
        }
        return null;
    }

    /**
     * Read File from string path.
     *
     * @param filePath filePath
     * @return byte array
     * @throws IOException for reading data from file
     */
    public byte[] readByteFromFile(String filePath) throws IOException {
        FileInputStream fileInputStream = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] bytes = new byte[0];
        byte[] cacheBytes = new byte[CACHE_SIZE];
        int len;

        try {
            fileInputStream = new FileInputStream(new File(filePath));
            len = fileInputStream.read(cacheBytes);
            while (len != IO_END_LEN) {
                baos.write(cacheBytes, 0, len);
                len = fileInputStream.read(cacheBytes);
            }
            bytes = baos.toByteArray();
        } catch (IOException e) {
            LogUtils.e(TAG, "obtain data file stream failed");
        } finally {
            if (fileInputStream != null) {
                fileInputStream.close();
            }
            try {
                baos.close();
            } catch (IOException e) {
                LogUtils.e(TAG, "close stream failed");
            }
        }
        return bytes;
    }

    /**
     * Returns list of all installed packages.
     *
     * @return array of PkgInfo type
     */
    public List<PkgInfo> getInstalledPackages() {
        return getAllPackagesInfo(PACKAGES);
    }

    /**
     * Returns last updated time for the specified package.
     *
     * @param packageName package name string
     * @return last updated time in long
     */
    public long getLastUpdatedTimeForPkg(String packageName) {
        BundleInfo bundleInfo = getPkgInfo(packageName, 0);
        return bundleInfo != null ? bundleInfo.getUpdateTime() : 0;
    }

    /**
     * Returns list of all permissions for specified package.
     *
     * @param packageName package string
     * @return String array
     */
    public String[] getPermissionForPkg(String packageName) {
        BundleInfo bundleInfo = getPkgInfo(packageName, IBundleManager.GET_APPLICATION_INFO_WITH_PERMISSION);
        if (bundleInfo.reqPermissions != null) {
            return bundleInfo.reqPermissions.toArray(new String[0]);
        } else {
            return new String[0];
        }
    }

    /**
     * Returns an array of Services in specified package.
     *
     * @param packageName package name string
     * @return String array of service type packages
     */
    public String[] getServicesForPkg(String packageName) {
        BundleInfo bundleInfo = getPkgInfo(packageName, AbilityInfo.AbilityType.SERVICE.ordinal());
        if (bundleInfo.getAbilityInfos() != null) {
            List<AbilityInfo> abilityInfoList = bundleInfo.getAbilityInfos();
            List<String> data = new ArrayList<>();
            for (AbilityInfo abilityInfo : abilityInfoList) {
                data.add(abilityInfo.getClassName());
            }
            return data.toArray(new String[0]);
        } else {
            return new String[0];
        }
    }

    /**
     * Returns version code for the specified package.
     *
     * @param packageName package name string
     * @return version code in string
     */
    public String getVersionCodeForPkg(String packageName) {
        BundleInfo bundleInfo = getPkgInfo(packageName, 0);
        return String.valueOf(bundleInfo.getVersionCode());
    }

    /**
     * Returns version name for the specified package.
     *
     * @param packageName package name string
     * @return version name in string
     */
    public String getVersionForPkg(String packageName) {
        BundleInfo bundleInfo = getPkgInfo(packageName, 0);
        return bundleInfo != null ? bundleInfo.getVersionName() : null;
    }

    /**
     * Returns List of PkgInfor as per search query.
     *
     * @param query search text
     * @param flag  type of package
     */
    public List<PkgInfo> searchInList(String query, int flag) {
        String queryLowercase = query.toLowerCase();
        ArrayList<PkgInfo> pkgInfoArrayList = new ArrayList<>();
        ArrayList<PkgInfo> installedPackagesList = getAllPackagesInfo(flag);
        for (PkgInfo pkgInfo : installedPackagesList) {
            switch (flag) {
                case APPLICATIONS:
                    String appname = pkgInfo.getAppName();
                    if (appname != null && appname.toLowerCase().contains(queryLowercase)) {
                        pkgInfoArrayList.add(pkgInfo);
                    }
                    break;
                case PACKAGES:
                    String packagename = pkgInfo.getPackageName();
                    if (packagename != null && packagename.toLowerCase().contains(queryLowercase)) {
                        pkgInfoArrayList.add(pkgInfo);
                    }
                    break;
                case PERMISSIONS: {
                    String[] permissions = getPermissionForPkg(pkgInfo.getPackageName());
                    filter(permissions, queryLowercase, pkgInfoArrayList, pkgInfo);
                    break;
                }
                case SERVICES: {
                    String[] services = getServicesForPkg(pkgInfo.getPackageName());
                    filter(services, queryLowercase, pkgInfoArrayList, pkgInfo);
                    break;
                }
                case ACTIVITIES: {
                    String[] abilities = getAbilitiesForPkg(pkgInfo.getPackageName());
                    filter(abilities, queryLowercase, pkgInfoArrayList, pkgInfo);
                    break;
                }
                default: {
                    String packageName1 = pkgInfo.getPackageName();
                    if (packageName1 != null && packageName1.toLowerCase().contains(queryLowercase)) {
                        pkgInfoArrayList.add(pkgInfo);
                    }
                    break;
                }
            }
        }
        return pkgInfoArrayList;
    }

    private void filter(String[] arrayData, String queryLowercase,
                        ArrayList<PkgInfo> pkgInfoArrayList, PkgInfo pkgInfo) {
        if (arrayData != null) {
            for (String ability : arrayData) {
                if (ability.toLowerCase().contains(queryLowercase)) {
                    pkgInfoArrayList.add(pkgInfo);
                    break;
                }
            }
        }
    }

    private ArrayList<PkgInfo> getAllPackagesInfo(int flag) {
        ArrayList<PkgInfo> pkgInfoArrayList = new ArrayList<>();
        List<BundleInfo> installedPackagesList = null;
        try {
            switch (flag) {
                case PACKAGES:
                    installedPackagesList = packageManager.getBundleInfos(0x00000000);
                    break;
                case PERMISSIONS:
                    installedPackagesList =
                            packageManager.getBundleInfos(IBundleManager.GET_APPLICATION_INFO_WITH_PERMISSION);
                    break;
                case SERVICES:
                    installedPackagesList = getAllServices();
                    break;
                case ACTIVITIES:
                    installedPackagesList = getAllUiApps();
                    break;
                default:
                    installedPackagesList = packageManager.getBundleInfos(0);
                    break;
            }

        } catch (RemoteException e) {
            LogUtils.debug(TAG, e.getMessage());
        }
        if (null != installedPackagesList) {
            // iterate over list of installed packages and check none belongs to system app category
            for (BundleInfo bundleInfo : installedPackagesList) {
                LogUtils.info(TAG, "bundleInfo = " + bundleInfo.toString());
                if (!bundleInfo.getAppInfo().getName().contains("ohos.")) { //ignore ohos packages
                    pkgInfoArrayList.add(getPkgInfoModel(bundleInfo));
                }
            }
        }
        return pkgInfoArrayList;
    }

    private List<BundleInfo> getAllServices() throws RemoteException {
        List<BundleInfo> servicesList = new ArrayList<>();
        for (BundleInfo bundleInfo : packageManager.getBundleInfos(0x00000000)) {
            List<AbilityInfo> abilityInfos = bundleInfo.getAbilityInfos();
            for (int count = 0; count < abilityInfos.size(); count++) {
                AbilityInfo.AbilityType abilityType = abilityInfos.get(count).getType();
                // if any ability if of non service type found, this cannot be a service app
                if (abilityType != AbilityInfo.AbilityType.SERVICE) {
                    LogUtils.debug(TAG, "getAllServices # Found non SERVICE Ability, so not a SERVICE app!");
                    break; //break this loop and start checking another package
                } else {
                    // count will be equal to list size -1 only if all abilities are of Service types
                    if (count == (abilityInfos.size() - 1)) {
                        servicesList.add(bundleInfo);
                    }
                }
            }
        }
        return servicesList;
    }

    private List<BundleInfo> getAllUiApps() throws RemoteException {
        List<BundleInfo> allInstalledBundleInfoList = packageManager.getBundleInfos(0x00000000);
        List<BundleInfo> uiAppsList = new ArrayList<>();
        for (BundleInfo bundleInfo : allInstalledBundleInfoList) {
            for (int count = 0; count < bundleInfo.getAbilityInfos().size(); count++) {
                AbilityInfo abilityInfo = bundleInfo.getAbilityInfos().get(count);
                AbilityInfo.AbilityType abilityType = abilityInfo.getType();
                if (abilityType == AbilityInfo.AbilityType.PAGE) {
                    uiAppsList.add(bundleInfo);
                    break;
                }
            }
        }
        return uiAppsList;
    }

    private BundleInfo getPkgInfo(String packageName, int flag) {
        final IBundleManager iBundleManager = context.getBundleManager();
        try {
            return iBundleManager.getBundleInfo(packageName, flag);
        } catch (final RemoteException ignored) {
            return new BundleInfo();
        }
    }

    private PkgInfo getPkgInfoModel(BundleInfo p) {
        // Always available
        PkgInfo pkgInfo = new PkgInfo();
        if (p != null) {
            pkgInfo.setAppName(p.appInfo.getLabel());
            pkgInfo.setPackageName(p.getName());
            pkgInfo.setVersionCode(p.getVersionCode());
            pkgInfo.setVersionName(p.getVersionName());
            pkgInfo.setLastUpdateTime(p.getUpdateTime());
            pkgInfo.setFirstInstallTime(p.getInstallTime());
        }

        return pkgInfo;
    }
}