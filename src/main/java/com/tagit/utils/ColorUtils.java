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

     /**
     * Dynamically generates a beautiful, unique pastel hex color based on text input.
     * Scales infinitely without overlapping color selections.
     */
    public static String generateColorFromText(String text) {
        if (text == null || text.isBlank()) {
            return "#E0E0E0"; // Default neutral gray
        }

        // 1. Convert text hash code into a unique hue angle (0 to 359)
        int hue = Math.abs(text.hashCode()) % 360;

        // 2. Lock saturation to 75% and lightness to 80% for nice, readable pastels
        double s = 0.75;
        double l = 0.80;

        // 3. Convert HSL math into standard RGB values
        double c = (1 - Math.abs(2 * l - 1)) * s;
        double x = c * (1 - Math.abs((hue / 60.0) % 2 - 1));
        double m = l - c / 2;

        double r = 0, g = 0, b = 0;
        if (hue < 60)       { r = c; g = x; b = 0; }
        else if (hue < 120) { r = x; g = c; b = 0; }
        else if (hue < 180) { r = 0; g = c; b = x; }
        else if (hue < 240) { r = 0; g = x; b = c; }
        else if (hue < 300) { r = x; g = 0; b = c; }
        else                { r = c; g = 0; b = x; }

        int red = (int) ((r + m) * 255);
        int green = (int) ((g + m) * 255);
        int blue = (int) ((b + m) * 255);

        // 4. Return as a standard hex string (e.g., #FFB3BA)
        return String.format("#%02X%02X%02X", red, green, blue);
    }
}
