import React, { useCallback, useMemo, useState } from 'react'
import { Animated, Insets, NativeSyntheticEvent, ViewProps } from 'react-native'
import { KeyboardInsetsContext } from './context'
import { KeyboardHeight, NativeKeyboardInsetsView } from './native'

interface KeyboardInsetsViewProps extends ViewProps {
  mode?: 'auto' | 'manual'
  extraHeight?: number
}

const NativeKeyboardInsetsViewAnimated = Animated.createAnimatedComponent(NativeKeyboardInsetsView)

export function KeyboardInsetsView(props: KeyboardInsetsViewProps) {
  const { children, ...rest } = props
  const insets = useMemo(
    () => ({
      top: new Animated.Value(0),
      left: new Animated.Value(0),
      right: new Animated.Value(0),
      bottom: new Animated.Value(0),
    }),
    [],
  )

  const [keyboardHeight, setKeyboardHeight] = useState(0)

  const onInsetsChanged = useMemo(
    () =>
      Animated.event<Insets>(
        [
          {
            nativeEvent: {
              top: insets.top,
              left: insets.left,
              right: insets.right,
              bottom: insets.bottom,
            },
          },
        ],
        {
          useNativeDriver: true,
          listener: event => {
            console.log(event.nativeEvent)
          },
        },
      ),
    [insets],
  )

  const onKeyboardHeightChanged = useCallback((event: NativeSyntheticEvent<KeyboardHeight>) => {
    setKeyboardHeight(event.nativeEvent.height)
  }, [])

  return (
    <KeyboardInsetsContext.Provider value={{ insets, keyboardHeight }}>
      <NativeKeyboardInsetsViewAnimated
        onInsetsChanged={onInsetsChanged}
        onKeyboardHeightChanged={onKeyboardHeightChanged}
        {...rest}>
        {children}
      </NativeKeyboardInsetsViewAnimated>
    </KeyboardInsetsContext.Provider>
  )
}
