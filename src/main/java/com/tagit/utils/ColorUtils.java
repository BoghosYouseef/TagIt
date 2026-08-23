package com.tagit.utils;

import javafx.scene.paint.Color;

public class ColorUtils {
        /**
     * Determines whether black or white text has better contrast against a given background.
     * @param hexColor The background color string (supports "0x00ff00ff", "#00ff00", etc.)
     * @return The optimal text color hex string ("#ffffff" or "#000000")
     */
    public static String getContrastTextColor(String hexColor) {
        // 1. Convert the user's string safely into a JavaFX Color object
        Color bg = Color.web(hexColor);

        // 2. Extract sRGB components (0.0 to 1.0)
        double r = bg.getRed();
        double g = bg.getGreen();
        double b = bg.getBlue();

        // 3. Convert sRGB to linear RGB based on WCAG formulas
        r = (r <= 0.04045) ? (r / 12.92) : Math.pow((r + 0.055) / 1.055, 2.4);
        g = (g <= 0.04045) ? (g / 12.92) : Math.pow((g + 0.055) / 1.055, 2.4);
        b = (b <= 0.04045) ? (b / 12.92) : Math.pow((b + 0.055) / 1.055, 2.4);

        // 4. Calculate relative luminance
        double luminance = 0.2126 * r + 0.7152 * g + 0.0722 * b;

        // 5. Use a midpoint threshold (0.179) to decide text color
        // If the background is bright, use black text. If dark, use white text.
        return (luminance > 0.179) ? "#000000" : "#ffffff";
    }

    public static String toCssColor(String color) {
        if (color == null) {
            return null;
        }

        return color.startsWith("0x")
                ? "#" + color.substring(2)
                : color;
    }
}
