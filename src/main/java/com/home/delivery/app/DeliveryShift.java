package com.home.delivery.app;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by evgeny on 08.05.15.
 */
public enum DeliveryShift {
    MORNING("M"),
    AFTERNOON("N"),
    EVENING("E");

    final String letter;

    DeliveryShift(@Nonnull String letter) {
        this.letter = letter;
    }

    @Nullable
    public static DeliveryShift fromLetter(@Nullable String letter) {
        for (DeliveryShift deliveryShift : DeliveryShift.values())
            if (deliveryShift.letter.equals(letter)) return deliveryShift;
        return null;
//            throw new IllegalStateException(String.format("No shift found for %s", letter));
    }
}
