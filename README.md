# SMS my GPS

This Android app is designed to automatically send SMS with your location as a
reply to SMS with a specific prefix from a specific number. All you need is
just to add necessary numbers and prefixes to the app, and it will
automatically send a message with your coordinates after receiving an
appropriate SMS.

This fork is based on [Warren Bank's project](https://github.com/warren-bank/Android-SMS-Automatic-Reply-GPS).

## Screenshots

![SMS my GPS](https://user-images.githubusercontent.com/47552815/94429552-24916880-019b-11eb-917b-530ef14405de.png)

## Changes in the fork

- App's interface has been completely redesigned to comply with the Material
design guidelines. Night theme is also supported now.
- Google Location services are now used instead of system location providers.
The app now depends on Google Play Services but sends the location faster.
- Ability to send last known location, which can be provided by the system
almost immediately, has been added.
- The broadcast receiver is now registered at runtime, not in the manifest, to
meet Android Oreo (and later) requirements for implicit receivers.
- Some additional FAQs have been added to the app.
- Ability to send and receive raw data SMS has been removed.
- Minimum SDK level is now 23 (Android 6.0 Marshmallow). 
- A lot of refactoring changes.
