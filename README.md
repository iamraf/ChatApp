# ChatApp v1.1

ChatApp is a project that I started to improve my Android Development knowledge. Later on I have decided to make it public, open source and publish it on Play Store.

It uses [Firebase's](https://firebase.google.com) Authentication/Database/Storage/Messaging/CrashReporting libraries for it's implementation and several other libraries that they are listed on [Third Party Notices](THIRD_PARTY_NOTICES.md).

Make sure you read App's [Privacy Policy](PRIVACY_POLICY.md) and [Terms and Conditions](TERMS_AND_CONDITIONS.md) before using it.

_Based on [Lapit Chat](https://github.com/akshayejh/Lapit---Android-Firebase-Chat-App) Youtube Series which can be found [here](https://www.youtube.com/playlist?list=PLGCjwl1RrtcQ3o2jmZtwu2wXEA4OIIq53)._

**DOWNLOAD LINK:** [Play Store](https://play.google.com/store/apps/details?id=com.github.h01d.chatapp)

## Preview

![login](https://i.imgur.com/eXlXGiX.png)

_Browse all preview pictures [here](https://imgur.com/a/HQhKw)._

## Features 

- Messaging
  - Send and Receive messages with users
  - Send pictures
- Lists
  - List with your Requests
  - List with your Messages
  - List with your Friends
  - List with all Users
- Friends
  - Accept or Remove Friends
- Requests
  - Send or Cancel Friend Request to users
- Profile
  - Update your Profile Picture
  - Update your Status
  - Update your Cover Picture
  - View other users profile
- Notifications
  - Notification when you have a new message
  - Notification when you have a new friend request
  - Notification when someone accepts your request

**Upcoming**

- Blocking
  - Block user from sending messages

## Changelog

Read all Changelog [HERE](CHANGELOG.md).

## Installation

*Setting up project*

- Download Project
- Create a new [Firebase](https://firebase.google.com) Project in console
- Connect project with Firebase `(Tools/Firebase)` in Android Studio
- Generate, download, paste `google-services.json` into the project

*Setting up notifications back-end*

- Create a folder on your Desktop and open it
- Start CMD (for Windows) or Terminal (for MacOS/Linux)
- Login on Firebase CLI using `firebase login`
- Type `firebase init`, select `Functions` using the `Space` key and hit `Enter`
- Select your App, then `javascript`, `N` on ESLint, and `Y`on dependendcies with npm.
- Navigate `functions` folder and replace `index.js` with [this](note.js)
- Type `firebase deploy` and you are all set

**NOTE:** make sure you read project's [LICENSE](LICENSE) before start playing with it.

## License

```
Copyright 2018 Raf.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
