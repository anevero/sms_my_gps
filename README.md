# SMS my GPS

[![GitHub Actions Status](https://github.com/anevero/sms_my_gps/workflows/build/badge.svg)](https://github.com/anevero/sms_my_gps/actions)

This Android app is designed to automatically send SMS with your location as a
reply to SMS with a specific prefix from a specific number. All you need is
just to add necessary numbers and prefixes to the app, and it will
automatically send a message with your coordinates after receiving an
appropriate SMS.

![SMS my GPS](https://user-images.githubusercontent.com/47552815/97208941-c3c27380-17cc-11eb-8185-6e24a9dc7643.png)

This fork is based on [Warren Bank's project](https://github.com/warren-bank/Android-SMS-Automatic-Reply-GPS).

## Features

- Add any items and corresponding prefixes to the app. You can edit already
added items later or delete them. You can add the number by choosing the
appropriate contact or manually. It's better to use full number format with
the country code (but the app still will work with short numbers, because
actually it checks number postfix match).
- Enable the foreground service. While working, it checks all the incoming SMS,
and if the match is found, sends an SMS as a reply. When service is running,
the corresponding notification is shown in the notification area.
- Set up location providers to use. Several ones are available; each can be
better than others in some situations (check their descriptions in the app).
Each provider initiates sending the corresponding message (so if you enable
several providers, the app will send several messages with the location info
from each of them).
- Set up location accuracy and maximum location requests number. When the app
needs to get the location from the provider, it will make requests to the
provider until the necessary accuracy or the maximum attempts number is
reached, and send the location only after that. One request to the provider
usually takes around 5 seconds. This feature allows getting a much more
accurate location because different device sensors (for example, GPS) often
need some time to determine the real location.

## Changes in the fork

- App's interface has been completely redesigned to comply with the Material
design guidelines. Night theme is also supported now.
- A lot of changes in the app's architecture. The overwhelming majority of the
code has been rewritten. New activities have been implemented with new
different features.
- Choosing the location provider (between Google Location Services, system GPS
provider, system network provider, last known location from them) feature has
been added.
- Choosing the location accuracy and maximum requests number features have been
added.
- All the logic connected with listening to SMS broadcasts and sending the
location has been moved to the foreground service to ensure that the
application works properly in the background mode.
- Different compatibility changes. For example, the broadcast receiver is now
registered at runtime, not in the manifest, to meet Android Oreo (and later)
requirements for implicit receivers.
- Ability to send and receive raw data SMS has been removed (it didn't work
correctly at least on some devices).
- Minimum SDK level is now 23 (Android 6.0 Marshmallow). 
- A lot of refactoring changes.
