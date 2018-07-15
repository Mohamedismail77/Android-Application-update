# Android Application Update for Cordova r1.0.0
###### Android Application Update is a cordova plugin allow users to check for updates on playstore


### Using with cordova:

```javascript
    Appupdate.< featuer >(url, callback_handler, error_handler);
    callback_handler = function(args){
        //your code goes here
    }
    error_handler = function(){
        //your error handler goes here
    }
```
### Using with ionic:

```javascript
    (<any>window).Appupdate.< featuer >(url, callback_handler, error_handler);
    function callback_handler(callback_args){
        //your code goes here
    }
    function error_handler {
        //your error handler goes here
    }
```
## Features:
#### Checking for update

```javascript
    checkForUpdate(url, callback_handler, error_handler);

    callback_handler = function(update_state){
        //your code goes here
        
    }
    onError = function(){
        //your error handler goes here
    }
```
## Server side implementation:
1- Response body:
```json
    {
        "version": "Integer / version code"
        "update-flag":  "<boolean ? forcedupdate : optional>"
    }
```
2- PHP Example:
```PHP
    <?PHP
        $app_data = ["version"=>2, "update-flag"=>false];
        echo json_encode($app_data);
    ?> 
```
#### Exmaple
```javascript
    var update_state = false;
    //Application initialization
    onDeviceReady: function(event) {
       if(update_state == true){   
            AppUpdate.checkForUpdate("http:update.server.url",onsuccess,onError);
        }
    }
    //Application back to foreground
    onResume: function(event) {
       if(update_state == true){   
            AppUpdate.checkForUpdate("http://update.server.url",onsuccess,onError);
        }
    }
onsuccess = function(state){
       //Save update state
       update_state = state;
    };
    onError = function(){
        //your error handler goes here
    };
```

## How to install
```bash
    cordova plugin add android-application-update
```


