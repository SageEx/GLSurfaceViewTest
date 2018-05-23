package com.example.arbane.surfaceviewtest;

/**
 * Created by arbane on 5/21/2018.
 */
public enum ImageEffect {

    EFFECT_ORIGINAL("ORIGINAL", 0),
    EFFECT_MONO("MONO", 1),
    EFFECT_LOMOISH("LOMOISH", 2),
    EFFECT_SEPIA("SEPIA", 3),
    EFFECT_POSTER("POSTER", 4),
    EFFECT_ROTATE("ROTATE", 5);

    String mEffectName;
    int mValue;

    ImageEffect(String original, int i) {
        this.mEffectName = original;
        this.mValue = i;
    }

    public int getValue() {
        return mValue;
    }

    public static ImageEffect getEffect(int value){
        switch (value) {
            case 0:
                return EFFECT_ORIGINAL;
            case 1:
                return EFFECT_MONO;
            case 2:
                return EFFECT_LOMOISH;
            case 3:
                return EFFECT_SEPIA;
            case 4:
                return EFFECT_POSTER;
            case 5:
                return EFFECT_ROTATE;
        }
        return EFFECT_ORIGINAL;
    }

    public static String getEffectName(int value) {
        return getEffect(value).mEffectName;
    }

    public static final int EFFECT_LIST_SIZE = ImageEffect.values().length;

}

