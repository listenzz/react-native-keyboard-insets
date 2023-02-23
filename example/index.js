import { Platform } from 'react-native'
import Navigation, { BarStyleDarkContent } from 'hybrid-navigation'
import App from './App'
import KeyboardChat from './KeyboardChat'

Navigation.setDefaultOptions({
  screenBackgroundColor: '#F8F8F8',
  topBarStyle: BarStyleDarkContent,
  statusBarColorAndroid: Platform.Version > 21 ? undefined : '#4A4A4A',
  navigationBarColorAndroid: '#FFFFFF',
})

Navigation.startRegisterComponent()
Navigation.registerComponent('KeyboardInsets', () => App)
Navigation.registerComponent('Chat', () => KeyboardChat)
Navigation.endRegisterComponent()

Navigation.setRoot({
  stack: {
    children: [
      {
        screen: {
          moduleName: 'KeyboardInsets',
        },
      },
    ],
  },
})
