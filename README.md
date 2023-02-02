# react-native-keyboard-insets

A powerful Keyboard Aware View for React Native.

使用简单，自动模式下不需要额外代码来处理键盘。

![README-2023-02-02-15-56-36](https://todoit.oss-cn-shanghai.aliyuncs.com/assets/README-2023-02-02-15-56-36.gif)

本库主要依据 Google 官方指南 [Synchronize animation with the software keyboard](https://developer.android.com/develop/ui/views/layout/sw-keyboard#synchronize-animation) 来实现，同时参考了 [react-native-keyboard-controller](https://github.com/kirillzyusko/react-native-keyboard-controller)。因为该库不是很符合我的需求，所以我自己写了一个。

## Installation

```bash
yarn add react-native-keyboard-insets
```

### iOS

不需要额外配置

### Android

Before setting up control and animation for the software keyboard, configure your app to [display edge-to-edge](https://developer.android.com/develop/ui/views/layout/edge-to-edge). This allows it to handle system window insets such as the system bars and the on-screen keyboard.

```java
// MainActivity.java
import androidx.core.view.WindowCompat;

public class MainActivity extends ReactActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(null);
        // enable Edge-to-Edge
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
    }
}
```

To achieve the best backward compatibility with this AndroidX implementation, set android:windowSoftInputMode="adjustResize" to the activity in AndroidManifest.xml.

```xml
<!-- AndroidManifest.xml -->
<activity
  android:name=".MainActivity"
    ...
  android:windowSoftInputMode="adjustResize">
  <intent-filter>
    ...
  </intent-filter>
</activity>
```

开启 Edge-to-Edge 后，你的 UI 会撑满整个屏幕，你需要使用 [react-native-safe-area-context](https://github.com/th3rdwave/react-native-safe-area-context) 来处理和系统 UI (譬如虚拟导航键) 重叠的部分。

可参考以下代码进行全局处理，也可以每个页面单独处理，以实现更美观的 UI 效果。

```tsx
import { Platform } from 'react-native'
import { SafeAreaProvider, SafeAreaView } from 'react-native-safe-area-context'

function App() {
  return (
    <SafeAreaProvider>
      <NavigationContainer>...</NavigationContainer>
      {Platform.OS === 'android' && <SafeAreaView mode="margin" edges={['bottom']} />}
    </SafeAreaProvider>
  )
}
```

> 如果使用 [hybrid-navigation](https://github.com/listenzz/hybrid-navigation) 作为导航组件，则不需要做任何事情，因为它已经帮你处理好了。

## Usage

Just wrap your `View` or `ScrollView` with `KeyboardInsetsView`. It will automatically adjust the height of the view when the keyboard is shown or hidden.

```tsx
<KeyboardInsetsView extraHeight={16} style={{ flex: 1 }}>
  <ScrollView>
    ...
    <TextInput />
    ...
  </ScrollView>
</KeyboardInsetsView>
```

Support Nested.

```tsx
<KeyboardInsetsView extraHeight={16} style={{ flex: 1 }}>
  ...
  <KeyboardInsetsView extraHeight={8}>
    <TextInput />
  </KeyboardInsetsView>
  ...
</KeyboardInsetsView>
```

**KeyboardInsetsView** 本质上是个 `View`，所以你可以使用 `View` 的所有属性，也可以和 `View` 互相替换。

## 运行 example 项目

首先 clone 本项目

```shell
git clone git@github.com:listenzz/react-native-keyboard-insets.git
cd react-native-keyboard-insets
```

然后在项目根目录下运行如下命令：

```shell
yarn install
# &
yarn start
```

### 在 Android 上运行

首先，确保你有一个模拟器或设备

如果熟悉原生开发，使用 Android Studio 打开 example/android，像运行原生应用那样运行它，也可以使用命令行：

```sh
# 在项目根目录下运行
yarn android
```

你可能需要运行如下命令，才可以使用 Hot Reload 功能

```sh
adb reverse tcp:8081 tcp:8081
```

### 在 iOS 上运行

首先安装 cocoapods 依赖，在项目根目录下运行如下命令：

```sh
cd example/ios && pod install
# 成功安装依赖后，回到根目录
cd -
```

如果熟悉原生开发，使用 Xcode 打开 example/ios，像运行原生应用那样运行它，或者使用命令行：

```sh
# 在项目根目录下运行
yarn ios
```
