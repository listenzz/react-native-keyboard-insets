package com.reactnative.keyboardinsets;

import androidx.core.graphics.Insets;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.PixelUtil;
import com.facebook.react.uimanager.events.Event;
import com.facebook.react.uimanager.events.RCTEventEmitter;

public class KeyboardInsetsChangeEvent extends Event<KeyboardInsetsChangeEvent> {

    private final Insets insets;

    public KeyboardInsetsChangeEvent(int viewId, Insets insets) {
        super(viewId);
        this.insets = insets;
    }

    @Override
    public String getEventName() {
        return "topInsetsChanged";
    }

    @Override
    public void dispatch(RCTEventEmitter rctEventEmitter) {
        WritableMap map = Arguments.createMap();
        map.putDouble("top", PixelUtil.toDIPFromPixel(insets.top));
        map.putDouble("left", PixelUtil.toDIPFromPixel(insets.left));
        map.putDouble("right", PixelUtil.toDIPFromPixel(insets.right));
        map.putDouble("bottom", PixelUtil.toDIPFromPixel(insets.bottom));
        rctEventEmitter.receiveEvent(getViewTag(), getEventName(), map);
    }
}
