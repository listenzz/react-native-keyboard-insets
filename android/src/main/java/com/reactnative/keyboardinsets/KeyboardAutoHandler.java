package com.reactnative.keyboardinsets;

import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import android.view.ViewParent;

import androidx.core.graphics.Insets;
import androidx.core.view.WindowInsetsCompat;

import com.facebook.react.uimanager.PixelUtil;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.views.scroll.ReactScrollView;

public class KeyboardAutoHandler {

    private final KeyboardInsetsView view;
    private final ThemedReactContext reactContext;

    public KeyboardAutoHandler(KeyboardInsetsView view, ThemedReactContext reactContext) {
        this.view = view;
        this.reactContext = reactContext;
    }

    private int keyboardHeight;
    private boolean forceUpdated = false;

    void onStart(View focusView, int keyboardHeight) {
        adjustScrollViewOffsetIfNeeded(focusView);

        if (keyboardHeight != this.keyboardHeight) {
            forceUpdated = true;
        }
        this.keyboardHeight = keyboardHeight;
    }

    void onEnd(View focusView, int keyboardHeight) {

    }

    void onApplyWindowInsets(WindowInsetsCompat insets, View focusView, int keyboardHeight) {
        if (focusView == null) {
            view.setTranslationY(0);
            return;
        }

        adjustScrollViewOffsetIfNeeded(focusView);

        if (keyboardHeight != this.keyboardHeight) {
            forceUpdated = true;
        }
        this.keyboardHeight = keyboardHeight;

        handleKeyboardTransition(insets, focusView);
    }

    void handleKeyboardTransition(WindowInsetsCompat insets, View focusView) {
        Insets imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime());
        EdgeInsets edge = SystemUI.getEdgeInsetsForView(focusView);
        float extraHeight = PixelUtil.toPixelFromDIP(view.getExtraHeight());
        Log.d("KeyboardInsets", "edge.bottom:" + edge.bottom + " imeInsets.bottom:" + imeInsets.bottom);
        float translationY = 0;
        if (imeInsets.bottom > 0) {
            if (SystemUI.isGestureNavigationEnabled(reactContext.getContentResolver())) {
                float actualBottomInset = Math.max(edge.bottom - extraHeight, 0);
                translationY = -Math.max(imeInsets.bottom - actualBottomInset, 0);
            } else {
                Insets navInsets = insets.getInsets(WindowInsetsCompat.Type.navigationBars());
                float actualBottomInset = Math.max(edge.bottom - navInsets.bottom - extraHeight, 0);
                translationY = -Math.max(imeInsets.bottom - navInsets.bottom - actualBottomInset, 0);
            }
        }

        if (forceUpdated) {
            forceUpdated = false;
            view.setTranslationY(translationY);
        }

        if (SystemUI.isImeVisible(view) && view.getTranslationY() < translationY) {
            return;
        }

        view.setTranslationY(translationY);
    }

    private void adjustScrollViewOffsetIfNeeded(View focusView) {
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
