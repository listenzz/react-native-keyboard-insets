package com.reactnative.keyboardinsets;

import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import android.view.ViewParent;

import androidx.annotation.NonNull;
import androidx.core.graphics.Insets;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.WindowInsetsAnimationCompat;
import androidx.core.view.WindowInsetsCompat;

import com.facebook.common.logging.FLog;
import com.facebook.react.uimanager.PixelUtil;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.UIManagerHelper;
import com.facebook.react.uimanager.events.Event;
import com.facebook.react.uimanager.events.EventDispatcher;
import com.facebook.react.views.scroll.ReactScrollView;

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
    private int keyboardHeight;

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

        FLog.i("KeyboardInsets", "WindowInsetsAnimation.Callback onStart");
        if (view.isAutoMode()) {
            adjustScrollViewOffsetIfNeeded();
        }

        if (SystemUI.isImeVisible(view)) {
            keyboardHeight = SystemUI.imeHeight(view);
        }

        if (!view.isAutoMode()) {
            sendEvent(new KeyboardStatusChangedEvent(view.getId(), keyboardHeight, SystemUI.isImeVisible(view), transitioning));
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

        FLog.i("KeyboardInsets", "WindowInsetsAnimation.Callback onEnd");

        if (!SystemUI.isImeVisible(view)) {
            focusView = null;
        }

        if (!view.isAutoMode()) {
            sendEvent(new KeyboardStatusChangedEvent(view.getId(), keyboardHeight, SystemUI.isImeVisible(view), transitioning));
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
        if (transitioning) {
            return insets;
        }

        if (focusView == null) {
            // Android 10 以下，首次弹出键盘时，不会触发 WindowInsetsAnimationCompat.Callback
            focusView = view.findFocus();
        } else {
            View currentFocus = view.findFocus();
            if ((currentFocus != null && focusView != currentFocus)) {
                KeyboardInsetsView keyboardInsetsView = findClosestKeyboardInsetsView(focusView);
                if (keyboardInsetsView != null && keyboardInsetsView.isAutoMode()) {
                    keyboardInsetsView.setTranslationY(0);
                }
                focusView = currentFocus;
            }
        }

        if (shouldHandleKeyboardTransition(focusView)) {
            Insets imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime());

            FLog.i("KeyboardInsets", "onApplyWindowInsets imeInsets" + imeInsets);

            keyboardHeight = SystemUI.imeHeight(view);

            if (!view.isAutoMode()) {
                sendEvent(new KeyboardStatusChangedEvent(view.getId(), keyboardHeight, SystemUI.isImeVisible(view), true));
            }

            if (view.isAutoMode()) {
                adjustScrollViewOffsetIfNeeded();
            }

            handleKeyboardTransition(insets);

            if (!view.isAutoMode()) {
                sendEvent(new KeyboardStatusChangedEvent(view.getId(), keyboardHeight, SystemUI.isImeVisible(view), false));
            }

            return WindowInsetsCompat.CONSUMED;
        }

        return insets;
    }

    private void adjustScrollViewOffsetIfNeeded() {
        ReactScrollView scrollView = findClosestScrollView(focusView);
        if (scrollView != null) {
            Rect offset = new Rect();
            focusView.getDrawingRect(offset);
            scrollView.offsetDescendantRectToMyCoords(focusView, offset);
            float extraHeight = PixelUtil.toPixelFromDIP(view.getExtraHeight());
            float dy = scrollView.getHeight() + scrollView.getScrollY() - offset.bottom - extraHeight;
            if (dy < 0) {
                Log.d("KeyboardInsets", "adjustScrollViewOffset");
                scrollView.scrollTo(0, (int) (scrollView.getScrollY() - dy));
                scrollView.requestLayout();
            }
        }
    }

    private void handleKeyboardTransition(WindowInsetsCompat insets) {
        Insets imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime());
        if (view.isAutoMode()) {
            EdgeInsets edgeInsets = SystemUI.getEdgeInsetsForView(focusView);
            float extraHeight = PixelUtil.toPixelFromDIP(view.getExtraHeight());
            Log.d("KeyboardInsets", "edgeInsets.bottom:" + edgeInsets.bottom + " imeInsets.bottom:" + imeInsets.bottom);
            float translationY = 0;
            if (imeInsets.bottom > 0) {
                float actualBottomInset = Math.max(edgeInsets.bottom - extraHeight, 0);
                translationY = -Math.max(imeInsets.bottom - actualBottomInset, 0);
            }
            view.setTranslationY(translationY);
        } else {
            Log.d("KeyboardInsets", "imeInsets.bottom:" + imeInsets.bottom);
            sendEvent(new KeyboardPositionChangedEvent(view.getId(), imeInsets.bottom));
        }
    }

    private void sendEvent(Event<?> event) {
        EventDispatcher eventDispatcher = UIManagerHelper.getEventDispatcherForReactTag(reactContext, view.getId());
        eventDispatcher.dispatchEvent(event);
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

    private static ReactScrollView findClosestScrollView(View view) {
        ViewParent viewParent = view.getParent();
        if (viewParent instanceof ReactScrollView) {
            return (ReactScrollView) viewParent;
        }

        if (viewParent instanceof View) {
            return findClosestScrollView((View) viewParent);
        }

        return null;
    }

}
