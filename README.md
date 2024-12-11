
# OutSystems Plugin Yubico

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Platform: Android](https://img.shields.io/badge/platform-android-green.svg)](https://cordova.apache.org/)

A Cordova plugin for integrating the Yubico SDK with OutSystems apps, enabling functionality such as OTP generation and NFC discovery on Android devices.

## Table of Contents
- [Installation](#installation)
- [Usage](#usage)
  - [Start NFC Discovery](#start-nfc-discovery)
  - [Stop NFC Discovery](#stop-nfc-discovery)
  - [Get OTP](#get-otp)
- [Platforms Supported](#platforms-supported)
- [Requirements](#requirements)
- [License](#license)
- [Contributing](#contributing)
- [Author](#author)
- [Links](#links)

## Installation

To install the plugin, use the Cordova CLI:

```bash
cordova plugin add https://github.com/andregrillo/outsystems-plugin-yubico.git
```

## Usage

This plugin exposes three main functionalities for integrating with Yubico devices. Below is the API and how to use it:

### Start NFC Discovery

Start the process to discover a Yubico device via NFC.

```javascript
cordova.plugins.yubico.StartNFCDiscovery(
  function(serialNumber) {
    console.log('YubiKey Serial Number:', serialNumber);
  },
  function(error) {
    console.error('Error during NFC Discovery:', error);
  }
);
```

### Stop NFC Discovery

Stop the ongoing NFC discovery process.

```javascript
cordova.plugins.yubico.StopNFCDiscovery(
  function(successMessage) {
    console.log(successMessage); // "NFC Discovery stopped"
  },
  function(error) {
    console.error('Error stopping NFC Discovery:', error);
  }
);
```

### Get OTP

Opens the OTP activity to retrieve a One-Time Password (OTP) from a YubiKey.

```javascript
cordova.plugins.yubico.GetOTP(
  function(otp) {
    console.log('YubiKey OTP:', otp);
  },
  function(error) {
    console.error('Error getting OTP:', error);
  }
);
```

## Platforms Supported

This plugin currently supports:

- **Android**

*Note: iOS support is not yet implemented.*

## Requirements

- Cordova CLI
- Android SDK (minimum SDK version: 21)
- YubiKit library version: `2.7.0`

## License

This project is licensed under the [MIT License](https://opensource.org/licenses/MIT).

## Contributing

Issues and pull requests are welcome! If you encounter any bugs or want to propose a feature, visit the [issues page](https://github.com/andregrillo/outsystems-plugin-yubico/issues).

## Author

Developed by **Andre Grillo**

## Links

- [GitHub Repository](https://github.com/andregrillo/outsystems-plugin-yubico.git)
- [Yubico SDK Documentation](https://developers.yubico.com/yubikit-android/)