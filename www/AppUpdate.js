var exec = require('cordova/exec');

/*
 * Interface for js to get location Address using Geocoder service;
 * @param: url => a string refer to url to check for update version;
 * @param: success => callback will fired if address is found;
 * @param: error => callback will fired if address is null or something is wrong;
 */
exports.checkForUpdate = function (url, success, error) {
  //check if typeof message is string
  if (typeof success !== "function") {
    //throw an error to user
    throw new Error("Location Provider need a onRequestLocation function");
  } else if (typeof error !== "function") {
    //throw an error to user
    throw new Error("Please define the an error callback as function");
  } else if (typeof url !== "string") {
    //throw an error to user
    throw new Error("Please define the latitude to get location updates as number");
  } else {
    //call native side to get current location
    exec(success, error, 'AppUpdate', 'checkForUpdate', [url]);
  }
};


