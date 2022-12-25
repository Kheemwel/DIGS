package com.amogus.digs.managers;

public class SoundManager {
    private static SoundManager soundManagerInstance;

    private SoundManager() {
    }

    public static SoundManager getInstance() {
        if (soundManagerInstance == null) {
            soundManagerInstance = new SoundManager();
        }
        return soundManagerInstance;
    }
}
