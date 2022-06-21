var exec = require('cordova/exec');

exports.StartNFCDiscovery = function (success, error) {
    exec(success, error, 'yubico', 'startNFCDiscovery');
};

exports.StopNFCDiscovery = function (success, error) {
    exec(success, error, 'yubico', 'stopNFCDiscovery');
};