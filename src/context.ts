import { createContext, useContext } from 'react'
import { Animated } from 'react-native'

interface AnimatedInsets {
  top: Animated.Value
  left: Animated.Value
  right: Animated.Value
  bottom: Animated.Value
}

type KeyboardInsetsContext = {
  insets: AnimatedInsets
  keyboardHeight: number
}

const defaultContext: KeyboardInsetsContext = {
  insets: {
    top: new Animated.Value(0),
    left: new Animated.Value(0),
    right: new Animated.Value(0),
    bottom: new Animated.Value(0),
  },
  keyboardHeight: 0,
}

export const KeyboardInsetsContext = createContext<KeyboardInsetsContext>(defaultContext)

export function useKeyboardInsets() {
  return useContext(KeyboardInsetsContext).insets
}

export function useKeyboardHeight() {
  return useContext(KeyboardInsetsContext).keyboardHeight
}
