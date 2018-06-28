# react-native-soundpool

React Native module for Android for playing sound clips using Android class [SoundPool](https://developer.android.com/reference/android/media/SoundPool).
This module is intended for playing multiple shorter clips that often change.


## Installation

First, install the npm package from your app directory:

```javascript
npm install react-native-soundpool --save
```

Then link it automatically using:

```javascript
react-native link react-native-soundpool
```
Edit `android/app/build.gradle` to declare the project dependency:

```
dependencies {
  ...
  compile project(':react-native-soundpool')
}
```

## Basic usage

 Save your sound clip files under the directory `android/app/src/main/res/raw`. Note that files in this directory must be lowercase and underscored (e.g. my_file_name.mp3) and that subdirectories are not supported by Android.

```js
// Import the react-native-soundpool module
import SoundPool from 'react-native-soundpool';

// Create SoundPool with max streams 
SoundPool.createPool( 2 );  // Maximum two clips can play at the same time

// Load the sound file 'beep.mp3' from the app bundle
SoundPool
    .addSound('beep')
    .then( () => {
        // Successfully loaded
    })
    .catch( error => {
        // Error occurred
    })
  

// Play the sound 'beep.mp3' which is loaded to SoundPool
SoundPool
    .play('beep')
    .then( streamID => {
        // If sound exists in SoundPool returns stream ID
        // streamID is used for later manipulation and should be saved somewhere
    })
    .catch( error => {
        // Sound is not in SoundPool and returns '-1'
    });

// Play the sound 'beep.mp3' with custom parameters (name, loop, rate, volume)
SoundPool
    .playCustom('beep', -1, 1, 0.5)  // Infinitive looping and reduced the volume by half
    .then( streamID => {
        // Save streamID
    })
    .catch( error => {
        // Sound is not in SoundPool and returns '-1'
    });

// Change playback rate (1.0 = normal playback, range 0.5 to 2.0) 
SoundPool.setRate( streamID, rate); // streamID is returned from play function

// Pause a playback stream
SoundPool.pause( streamID );

// Resume a playback stream
SoundPool.resume( streamID );

// Stop a playback stream
SoundPool.stop( streamID );

// Set stream volume (range = 0.0 to 1.0)
SoundPool.setVolume( streamID, volume );

// Pause all active streams
SoundPool.autoPause();

// Resume all previously active streams
SoundPool.autoResume();

// Unload 'beep.mp3' from a SoundPool
SoundPool.unload( 'beep' )

// Release SoundPool resource
SoundPool.releasePool();
```

## Notes
- This module works only for Android
- streamID is returned from play and playCustom functions
- After release, you must again call createPool before adding new sounds

