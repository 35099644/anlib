package com.th.wx_xposed.server.hooks;

import android.view.animation.LayoutAnimationController;

import de.robv.android.xposed.XposedHelpers;

import static de.robv.android.xposed.XposedHelpers.findClass;

/**
 * Created by york on 16/06/2017.
 */

public class Classes {
    public static Class SubCoreNfc;

    public static Class WalletUserInfoManager;

    public static Class LauncherUI;

    public static Class NetSceneQueue;

    public static Class NetSceneBase;

    public static Class NetSceneGetA8Key;

    public static Class NetSceneGetA8Key$sb;

    public static Class WebViewIInterface;

    public static Class MMCore;

    public static Class QBarStringHandler;

    /**
     * 微信webview。对系统webview和自己的webview进行了代理，动态进行切换，本身是一个FrameLayout.
     */
    public static Class WebView;

    public static Class WebViewUtil;

    public static Class WebViewClient;

    public static Class CookieManager;

    public static Class WebResourceRequest;

    /**
     * 微信WebView界面
     */
    public static Class WebViewUI;

    public static void init(ClassLoader classLoader) {
        SubCoreNfc = findClass("com.tencent.mm.plugin.wallet_core.model.k", classLoader);
        WalletUserInfoManager = findClass("com.tencent.mm.plugin.wallet_core.model.ab", classLoader);

        LauncherUI = findClass("com.tencent.mm.ui.LauncherUI", classLoader);
        NetSceneQueue = findClass("com.tencent.mm.u.n", classLoader);
        NetSceneBase = findClass("com.tencent.mm.u.k", classLoader);
        NetSceneGetA8Key = findClass("com.tencent.mm.modelsimple.l", classLoader);
        WebViewIInterface = findClass("com.tencent.mm.plugin.webview.stub.c", classLoader);

        WebViewUI = findClass("com.tencent.mm.plugin.webview.ui.tools.WebViewUI", classLoader);
        WebView = findClass("com.tencent.smtt.sdk.WebView", classLoader);

        MMCore = findClass("com.tencent.mm.model.ak", classLoader);

        QBarStringHandler = findClass("com.tencent.mm.plugin.scanner.util.a", classLoader);

        WebViewUtil = findClass("com.tencent.mm.pluginsdk.ui.tools.s", classLoader);

        WebViewClient = findClass("com.tencent.smtt.sdk.WebViewClient", classLoader);

        CookieManager = findClass("com.tencent.smtt.sdk.CookieManager", classLoader);

        WebResourceRequest = findClass("com.tencent.smtt.export.external.interfaces.WebResourceRequest", classLoader);

    }

}
