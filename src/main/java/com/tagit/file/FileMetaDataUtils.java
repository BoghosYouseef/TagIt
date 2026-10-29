package com.tagit.file;

import java.util.Locale;

public final class FileMetaDataUtils {

    private FileMetaDataUtils() {
        // Utility class
    }

    public static String getExtensionOf(String fileName) {
        int lastDot = fileName.lastIndexOf('.');

        if (lastDot <= 0 || lastDot == fileName.length() - 1) {
            return null;
        }

        return fileName.substring(lastDot + 1)
                .toLowerCase(Locale.ROOT);
    }

    public static String formatSize(long bytes) {
        if (bytes < 0) {
            throw new IllegalArgumentException(
                    "Byte value cannot be negative"
            );
        }

        if (bytes < 1024) {
            return bytes + " bytes";
        }

        String[] units = {
                "bytes",
                "KB",
                "MB",
                "GB",
                "TB",
                "PB"
        };

        int unitIndex =
                (int) (Math.log(bytes) / Math.log(1024));

        double value =
                bytes / Math.pow(1024, unitIndex);

        return String.format(
                Locale.ROOT,
                "%.2f %s",
                value,
                units[unitIndex]
        );
    }
}

