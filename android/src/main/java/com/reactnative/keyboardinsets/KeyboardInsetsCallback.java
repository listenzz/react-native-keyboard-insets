package com.reactnative.keyboardinsets;

import android.util.Log;
import android.view.View;
import android.view.ViewParent;

import androidx.annotation.NonNull;
import androidx.core.graphics.Insets;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.WindowInsetsAnimationCompat;
import androidx.core.view.WindowInsetsCompat;

import com.facebook.common.logging.FLog;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.PixelUtil;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.UIManagerHelper;
import com.facebook.react.uimanager.events.EventDispatcher;
import com.facebook.react.uimanager.events.RCTEventEmitter;

import java.util.List;

public class KeyboardInsetsCallback extends WindowInsetsAnimationCompat.Callback implements OnApplyWindowInsetsListener {
    
    private final KeyboardInsetsView view;
    private final ThemedReactContext reactContext;
    
    public KeyboardInsetsCallback(KeyboardInsetsView view, ThemedReactContext reactContext) {
        super(WindowInsetsAnimationCompat.Callback.DISPATCH_MODE_CONTINUE_ON_SUBTREE);
        this.view = view;
        this.reactContext = reactContext;
    }

    private View focusView;
    private boolean transitioning;

    @Override
    public void onPrepare(@NonNull WindowInsetsAnimationCompat animation) {
        transitioning = true;
    }

    @NonNull
    @Override
    public WindowInsetsAnimationCompat.BoundsCompat onStart(@NonNull WindowInsetsAnimationCompat animation, @NonNull WindowInsetsAnimationCompat.BoundsCompat bounds) {
        if (SystemUI.isImeVisible(view)) {
            focusView = view.findFocus();
        }
        
        if (!shouldHandleKeyboardTransition(focusView)) {
            return super.onStart(animation, bounds);
        }

        FLog.w("KeyboardInsets", "WindowInsetsAnimation.Callback onStart");
        
        if (SystemUI.isImeVisible(view)) {
            int keyboardHeight = SystemUI.imeHeight(view);
            WritableMap map = Arguments.createMap();
            map.putDouble("height", PixelUtil.toDIPFromPixel(keyboardHeight));
            reactContext.getJSModule(RCTEventEmitter.class).receiveEvent(
                view.getId(),
                "topKeyboardHeightChanged",
                map);
        }
        
        return super.onStart(animation, bounds);
    }

    @Override
    public void onEnd(@NonNull WindowInsetsAnimationCompat animation) {
        super.onEnd(animation);
        transitioning = false;
        
        if (!shouldHandleKeyboardTransition(focusView)) {
            return;
        }
        
        FLog.w("KeyboardInsets", "WindowInsetsAnimation.Callback onEnd");
        if (!SystemUI.isImeVisible(view)) {
            focusView = null;
        }
    }

    @NonNull
    @Override
    public WindowInsetsCompat onProgress(@NonNull WindowInsetsCompat insets, @NonNull List<WindowInsetsAnimationCompat> runningAnimations) {
        if (!shouldHandleKeyboardTransition(focusView)) {
            return insets;
        }
        
        handleKeyboardTransition(insets);
        
        return WindowInsetsCompat.CONSUMED;
    }

    @Override
    public WindowInsetsCompat onApplyWindowInsets(View v, WindowInsetsCompat insets) {
        if (shouldHandleKeyboardTransition(focusView) && !transitioning) {
            FLog.w("KeyboardInsets", "onApplyWindowInsets " + insets);
            handleKeyboardTransition(insets);
        }
        return insets;
    }
    
    private void handleKeyboardTransition(WindowInsetsCompat insets) {
        Insets imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime());
        if (view.isAutoMode()) {
            EdgeInsets edgeInsets = SystemUI.getEdgeInsetsForView(focusView);
            float extraHeight = PixelUtil.toPixelFromDIP(view.getExtraHeight());
            Log.d("KeyboardInsets", "edgeInsets.bottom:" + edgeInsets.bottom + " imeInsets.bottom:" + imeInsets.bottom);
            view.setTranslationY(-Math.max(imeInsets.bottom - edgeInsets.bottom + extraHeight, 0));

        } else {
            EventDispatcher eventDispatcher = UIManagerHelper.getEventDispatcherForReactTag(reactContext, view.getId());
            eventDispatcher.dispatchEvent(new KeyboardInsetsChangeEvent(view.getId(), imeInsets));
        }
    }
    
    private boolean shouldHandleKeyboardTransition(View focus) {
        if (focus != null) {
            KeyboardInsetsView insetsView = findClosestKeyboardInsetsView(focus);
            return insetsView == view;
        }
        return false;
    }

    private static KeyboardInsetsView findClosestKeyboardInsetsView(View focus) {
        ViewParent viewParent = focus.getParent();
        if (viewParent instanceof KeyboardInsetsView) {
            return (KeyboardInsetsView) viewParent;
        }

        if (viewParent instanceof View) {
            return findClosestKeyboardInsetsView((View) viewParent);
        }

        return null;
    }
    
}
