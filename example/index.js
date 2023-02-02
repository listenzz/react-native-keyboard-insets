import { Platform } from 'react-native'
import { ReactRegistry, Navigator, Garden, BarStyleDarkContent } from 'hybrid-navigation'
import App from './App'

Garden.setStyle({
  screenBackgroundColor: '#F8F8F8',
  topBarStyle: BarStyleDarkContent,
  statusBarColorAndroid: Platform.Version > 21 ? undefined : '#4A4A4A',
})

ReactRegistry.startRegisterComponent()
ReactRegistry.registerComponent('KeyboardInsets', () => App)
ReactRegistry.endRegisterComponent()

Navigator.setRoot({
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
