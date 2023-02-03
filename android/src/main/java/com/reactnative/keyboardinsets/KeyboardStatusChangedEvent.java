package com.reactnative.keyboardinsets;

import androidx.annotation.Nullable;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.PixelUtil;
import com.facebook.react.uimanager.events.Event;

public class KeyboardStatusChangedEvent extends Event<KeyboardStatusChangedEvent> {

    private final int height;
    private final boolean hidden;
    private final boolean transitioning;

    public KeyboardStatusChangedEvent(int viewTag, int height, boolean hidden, boolean transitioning) {
        super(viewTag);
        this.height = height;
        this.hidden = hidden;
        this.transitioning = transitioning;
    }

    @Override
    public String getEventName() {
        return "topStatusChanged";
    }

    @Nullable
    @Override
    protected WritableMap getEventData() {
        WritableMap map = Arguments.createMap();
        map.putDouble("height", PixelUtil.toDIPFromPixel(height));
        map.putBoolean("transitioning", transitioning);
        map.putBoolean("hidden", hidden);
        return map;
    }
}
