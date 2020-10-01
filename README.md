# SMS my GPS

This Android app is designed to automatically send SMS with your location as a
reply to SMS with a specific prefix from a specific number. All you need is
just to add necessary numbers and prefixes to the app, and it will
automatically send a message with your coordinates after receiving an
appropriate SMS.

This fork is based on [Warren Bank's project](https://github.com/warren-bank/Android-SMS-Automatic-Reply-GPS).

## Screenshots

![SMS my GPS](https://user-images.githubusercontent.com/47552815/94821871-b94dcd80-040a-11eb-900f-cc3031b04abe.png)

## Changes in the fork

- App's interface has been completely redesigned to comply with the Material
design guidelines. Night theme is also supported now.
- A lot of changes in the app's architecture. For example, "inline" dialogues
have been replaced by special activities.
- Google Location services are now used instead of the system location
provider. The app now depends on Google Play Services but sends the location
faster.
- Ability to send last known location, which can be provided by the system
almost immediately, has been added.
- Contact picker feature has been added (to add the number by choosing a
contact through the system provider).
- The broadcast receiver is now registered at runtime, not in the manifest, to
meet Android Oreo (and later) requirements for implicit receivers.
- Some additional FAQs have been added to the app.
- Ability to send and receive raw data SMS has been removed.
- Minimum SDK level is now 23 (Android 6.0 Marshmallow). 
- A lot of refactoring changes.
