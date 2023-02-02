import {
  Insets,
  NativeModule,
  NativeModules,
  NativeSyntheticEvent,
  requireNativeComponent,
  ViewProps,
} from 'react-native'

export interface KeyboardHeight {
  height: number
}

interface NativeKeyboardInsetsViewProps {
  mode?: 'auto' | 'manual'
  extraHeight?: number
  onInsetsChanged?: (event: NativeSyntheticEvent<Insets>) => void
  onKeyboardHeightChanged?: (event: NativeSyntheticEvent<KeyboardHeight>) => void
}

export const NativeKeyboardInsetsView = requireNativeComponent<NativeKeyboardInsetsViewProps & ViewProps>(
  'KeyboardInsetsView',
)

interface KeyboardInsetsModuleInterface extends NativeModule {
  getEdgeInsetsForView(viewTag: number, callback: (insets: Insets) => void): void
}

const KeyboardInsetsModule: KeyboardInsetsModuleInterface = NativeModules.KeyboardInsetsModule

export function getEdgeInsetsForView(viewTag: number, callback: (insets: Insets) => void) {
  KeyboardInsetsModule.getEdgeInsetsForView(viewTag, callback)
}
