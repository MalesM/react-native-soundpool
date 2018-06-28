package com.malesevicmarko.rnsoundpool;

import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.util.SparseArray;
import android.util.SparseIntArray;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RNSoundPoolModule extends ReactContextBaseJavaModule {

    private SoundPool sp;
    private ReactApplicationContext context;
    private HashMap<String, Integer> soundMap = new HashMap<>();
    private SparseArray<Promise> promises = new SparseArray<>();
    private ArrayList<Integer> soundsInPool = new ArrayList<>();

    public RNSoundPoolModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.context = reactContext;
    }

    @Override
    public String getName() {
        return "RNSoundPool";
    }


    @ReactMethod
    public void createPool(int maxStreams){
        if(sp != null) release();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            sp = new SoundPool.Builder().setMaxStreams(maxStreams).build();
        } else {
            sp = new SoundPool(maxStreams, AudioManager.STREAM_MUSIC, 0);
        }

        sp.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                if(!soundsInPool.contains(sampleId) && status == 0) {
                    soundsInPool.add(sampleId);
                    promises.get(sampleId).resolve(null);
                    promises.delete(sampleId);
                }else if(status != 0){
                    String key = getKeyFromValue(soundMap, sampleId);
                    soundMap.remove(key);
                    promises.get(sampleId).reject("NOT_LOADED", "LOAD_ERROR");
                    promises.delete(sampleId);
                }
            }
        });
    }

    private void release(){
        soundMap.clear();
        soundsInPool.clear();
        sp.release();
    }

    private static String getKeyFromValue(Map hm, int value) {
        for (Object o : hm.keySet()) {
            if (hm.get(o).equals(value)) {
                return o.toString();
            }
        }
        return null;
    }

    @ReactMethod
    public void addSound(String name, Promise promise){
        if(!soundMap.containsKey(name)) {
            int resourceID = this.context.getResources().getIdentifier(
                    name,
                    "raw",
                    this.context.getPackageName()
            );
            if(resourceID!=0) {
                int i = sp.load(this.context, resourceID, 1);
                promises.put(i, promise);
                soundMap.put(name, i);
            }
        }
    }

    @ReactMethod
    public void play(String name, Promise promise){
        if(soundsInPool.contains(soundMap.get(name))) promise.resolve(sp.play(soundMap.get(name), 1, 1, 1, 0, 1.0f));
        else promise.reject("NOT_IN_POOL", "-1");
    }

    @ReactMethod
    public void playCustom(String name, int loop, float rate, float volume, Promise promise){
        if(soundsInPool.contains(soundMap.get(name))) promise.resolve(sp.play(soundMap.get(name), volume, volume, 1, loop, rate));
        else promise.reject("NOT_IN_POOL", "-1");
    }

    @ReactMethod
    public void setRate(int streamID, float rate){
        sp.setRate(streamID, rate);
    }

    @ReactMethod
    public void releasePool(){
        release();
    }

    @ReactMethod
    public void unload(String name){
        sp.unload(soundMap.get(name));
        soundsInPool.remove(soundMap.get(name));
        soundMap.remove(name);
    }

    @ReactMethod
    public void pause(int streamID){
        sp.pause(streamID);
    }

    @ReactMethod
    public void resume(int streamID){
        sp.resume(streamID);
    }

    @ReactMethod
    public void stop(int streamID){
        sp.stop(streamID);
    }

    @ReactMethod
    public void setVolume(int streamID, float volume){
        sp.setVolume(streamID, volume, volume);
    }

    @ReactMethod
    public void autoPause(){
        sp.autoPause();
    }

    @ReactMethod
    public void autoResume(){
        sp.autoResume();
    }
}
