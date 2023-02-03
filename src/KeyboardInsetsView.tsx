import React, { useCallback, useMemo, useRef } from 'react'
import { Animated, NativeSyntheticEvent, ViewProps } from 'react-native'
import { KeyboardStatus, NativeKeyboardInsetsView } from './native'

interface KeyboardState {
  height: number
  hidden: boolean
  transitioning: boolean
  position: Animated.Value
}

interface KeyboardInsetsViewProps extends ViewProps {
  extraHeight?: number
  onKeyboard?: (status: KeyboardState) => void
}

const NativeKeyboardInsetsViewAnimated = Animated.createAnimatedComponent(NativeKeyboardInsetsView)

export function KeyboardInsetsView(props: KeyboardInsetsViewProps) {
  const { children, onKeyboard, ...rest } = props

  const position = useRef(new Animated.Value(0)).current

  const onPositionChanged = useMemo(
    () =>
      Animated.event(
        [
          {
            nativeEvent: {
              position,
            },
          },
        ],
        {
          useNativeDriver: true,
        },
      ),
    [position],
  )

  const onStatusChanaged = useCallback(
    (event: NativeSyntheticEvent<KeyboardStatus>) => {
      onKeyboard?.({ ...event.nativeEvent, position })
    },
    [position, onKeyboard],
  )

  if (onKeyboard) {
    return (
      <NativeKeyboardInsetsViewAnimated
        mode="manual"
        onStatusChanged={onStatusChanaged}
        onPositionChanged={onPositionChanged}
        {...rest}>
        {children}
      </NativeKeyboardInsetsViewAnimated>
    )
  }

  return <NativeKeyboardInsetsView {...rest}>{children}</NativeKeyboardInsetsView>
}
