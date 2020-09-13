package ml.voidstar.nokeyguardshade10;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.setBooleanField;
import static de.robv.android.xposed.XposedHelpers.setFloatField;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class XposedHook implements IXposedHookLoadPackage{
    public void handleLoadPackage(final LoadPackageParam lpparam) {
        if (!lpparam.packageName.equals("com.android.systemui"))
            return;

        final Class scrimState = XposedHelpers.findClass("com.android.systemui.statusbar.phone.ScrimState", lpparam.classLoader);

        try {
            findAndHookMethod(scrimState, "setScrimBehindAlphaKeyguard", float.class, new XC_MethodReplacement() {
                @Override
                protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                    setFloatField(param.thisObject, "mScrimBehindAlphaKeyguard", 0.f);
                    return null;
                }
            });
        } catch (Throwable t) {
            XposedBridge.log(t);
        }

        try {
            Class bouncer = scrimState.getDeclaredField("BOUNCER").get(null).getClass();

            findAndHookMethod(bouncer, "prepare", scrimState, new XC_MethodReplacement() {
                @Override
                protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                    setFloatField(param.thisObject, "mCurrentBehindAlpha", 0.f);
                    setFloatField(param.thisObject, "mCurrentInFrontAlpha", 0.f);
                    return null;
                }
            });
        } catch (Throwable t) {
            XposedBridge.log(t);
        }

        try {
            Class bouncerScrimmed = scrimState.getDeclaredField("BOUNCER_SCRIMMED").get(null).getClass();

            findAndHookMethod(bouncerScrimmed, "prepare", scrimState, new XC_MethodReplacement() {
                @Override
                protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                    setFloatField(param.thisObject, "mCurrentBehindAlpha", 0.f);
                    setFloatField(param.thisObject, "mCurrentInFrontAlpha", 0.f);
                    return null;
                }
            });
        } catch (Throwable t) {
            XposedBridge.log(t);
        }

        final Class scrimController = XposedHelpers.findClass("com.android.systemui.statusbar.phone.ScrimController", lpparam.classLoader);

        try {
            findAndHookMethod(scrimController, "onTrackingStarted", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    setBooleanField(param.thisObject, "mDarkenWhileDragging", false);
                }
            });
        } catch (Throwable t) {
            XposedBridge.log(t);
        }

        try {
            findAndHookMethod(scrimController, "updateScrims", new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    setFloatField(param.thisObject,"mScrimBehindAlphaKeyguard",0.f);
                }
            });
        } catch (Throwable t) {
            XposedBridge.log(t);
        }

        try {
            XposedBridge.hookAllConstructors(scrimController, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    setFloatField(param.thisObject,"mScrimBehindAlphaKeyguard",0.f);
                }
            });
        } catch (Throwable t) {
            XposedBridge.log(t);
        }
        try {
            XposedHelpers.findAndHookMethod(scrimController, "scheduleUpdate", new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) {
                    Object[] states = (Object[]) XposedHelpers.callStaticMethod(XposedHelpers.findClass("com.android.systemui.statusbar.phone.ScrimState", lpparam.classLoader), "values");
                    for (Object state : states)
                        XposedHelpers.callMethod(state, "setScrimBehindAlphaKeyguard", 0.f);
                }
            });
        } catch (Throwable t) {
            XposedBridge.log(t);
        }
    }

    /*@Override
    public void initZygote(StartupParam startupParam) throws Throwable {
        XResources.setSystemWideReplacement("android","color","system_bar_background_semi_transparent", 0);
    }*/
}