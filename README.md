# react-native-keyboard-insets

A powerful Keyboard Aware View for React Native.

![README-2023-02-02-15-56-36](https://todoit.oss-cn-shanghai.aliyuncs.com/assets/README-2023-02-02-15-56-36.gif)

## Installation

```bash
yarn add react-native-keyboard-insets
```

## Usage

Just Wrap Your View or ScrollView with KeyboardInsetsView. It will automatically adjust the height of the view when the keyboard is shown or hidden.

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
