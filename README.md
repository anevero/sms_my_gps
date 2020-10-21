# SMS my GPS

This Android app is designed to automatically send SMS with your location as a
reply to SMS with a specific prefix from a specific number. All you need is
just to add necessary numbers and prefixes to the app, and it will
automatically send a message with your coordinates after receiving an
appropriate SMS.

This fork is based on [Warren Bank's project](https://github.com/warren-bank/Android-SMS-Automatic-Reply-GPS).

## Screenshots

![SMS my GPS](https://user-images.githubusercontent.com/47552815/96753460-921a6880-13d8-11eb-84ad-274ce49988c6.png)

## Changes in the fork

- App's interface has been completely redesigned to comply with the Material
design guidelines. Night theme is also supported now.
- A lot of changes in the app's architecture. For example, "inline" dialogues
have been replaced by special activities.
- All the logic connected with listening to SMS broadcasts and sending the
location has been moved to the foreground service to ensure that the
application works properly in the background mode.
- Google Location services are now used instead of the system location
provider. The app now depends on Google Play Services but sends the location
faster. Nevertheless, using system GPS provider is also possible (the
corresponding option can be found in the settings).
- Ability to send last known location which can be provided by the system
almost immediately has been added.
- Contact picker feature has been added (to add the number by choosing a
contact through the system provider).
- The broadcast receiver is now registered at runtime, not in the manifest, to
meet Android Oreo (and later) requirements for implicit receivers.
- Some additional FAQs have been added to the app.
- Ability to send and receive raw data SMS has been removed (it didn't work
properly at least on some devices).
- Minimum SDK level is now 23 (Android 6.0 Marshmallow). 
- A lot of refactoring changes.
