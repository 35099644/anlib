package com.th.wx_xposed.server.net;

import android.util.Log;

import com.base.xp.IHook;
import com.th.wx_xposed.base.Config;

import java.util.HashMap;
import java.util.Map;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created by th on 20/9/2017.
 */

public class NetSceneQueue extends BaseMock {

    private NetSceneQueue(Object real) {
        super(real);
    }

    private static NetSceneQueue instance;

    public static NetSceneQueue instance() {
        if (instance == null) {
            synchronized (NetSceneQueue.class) {
                if (instance == null) {
                    instance = new NetSceneQueue(XposedHelpers.callStaticMethod(Classes.MMCore, "vy"));
                }
            }
        }
        return instance;
    }

    /**
     * 提交一个任务，并传入一个回调
     */
    public void enqueueTask(NetSceneBase netSceneBase, Callback callback) {
        // 这里我们使用QBarStringHandler是因为我们不能new一个接口，只能通过hook方式绕个弯实现回调
        Log.i(Config.TAG, "enqueue task type: " + netSceneBase.getType());
        Object wxCallback = XposedHelpers.newInstance(Classes.QBarStringHandler);
        Log.i(Config.TAG, "create callback: " + wxCallback);

        addCallback(wxCallback, netSceneBase.getType());
        Helper.callbackMap.put(wxCallback, callback);

        XposedHelpers.callMethod(real, "a", netSceneBase.real, 0);      // 0 是默认，因为我看到大多数都是传0

        Log.i(Config.TAG, "enqueu taskk: " + netSceneBase.real);
    }

    /**
     * 增加Callback
     */
    private void addCallback(Object wxCallback, int type) {
        XposedHelpers.callMethod(real, "a", type, wxCallback);
    }

    public void removeCallback(Object wxCallback, int type) {
        XposedHelpers.callMethod(real, "b", type, wxCallback);
    }

    public interface Callback {
        /**
         * 注意这里的netScene进行了一个重新初始化，里面的real是微信给的，但是实例可能是重新初始化包装了的
         */
        void onFinish(int errType, int errCode, String errMsg, Object netSceneBaseReal);
    }

    /**
     * 目前为了xposed特殊机制下，用于绕一个弯实现回调的功能
     */
    public static class Helper {
        private static Map<Object, Callback> callbackMap = new HashMap<>();

        public static IHook xpHook = new IHook() {
            @Override
            public void handle(XC_LoadPackage.LoadPackageParam param) throws ClassNotFoundException {
                XposedHelpers.findAndHookMethod(Classes.QBarStringHandler, "a", int.class, int.class, String.class, Classes.NetSceneBase, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        Log.i(Config.TAG, "GetA8KeyCallback");
                        if (callbackMap.containsKey(param.thisObject)) {
                            param.setResult(null);
                            NetSceneBase netSceneBase = new NetSceneBase(param.args[3]);
                            Log.i(Config.TAG, "callback class: " + param.args[3].getClass().getSimpleName());

                            callbackMap.get(param.thisObject).onFinish((int)param.args[0], (int)param.args[1], (String)param.args[2], param.args[3]);

                            NetSceneQueue.instance().removeCallback(param.thisObject, netSceneBase.getType());
                        } else {
                            // do nothing just go on
                        }
                    }

                });
            }
        };

    }

}
