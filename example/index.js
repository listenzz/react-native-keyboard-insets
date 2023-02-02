import { Platform } from 'react-native'
import Navigation, { BarStyleDarkContent } from 'hybrid-navigation'
import App from './App'

Navigation.setDefaultOptions({
  screenBackgroundColor: '#F8F8F8',
  topBarStyle: BarStyleDarkContent,
  statusBarColorAndroid: Platform.Version > 21 ? undefined : '#4A4A4A',
})

Navigation.startRegisterComponent()
Navigation.registerComponent('KeyboardInsets', () => App)
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
