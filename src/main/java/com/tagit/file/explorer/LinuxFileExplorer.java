package com.tagit.file.explorer;

import java.io.IOException;
import java.nio.file.Path;

public class LinuxFileExplorer implements FileExplorer {

    @Override
    public void openAndSelectFile(Path file) {
        String absolutePath = file.toAbsolutePath().toString();

        try {
            // Modern Linux D-Bus command to highlight a file in the default file manager
            new ProcessBuilder(
                "dbus-send",
                "--session",
                "--print-reply",
                "--dest=org.freedesktop.FileManager1",
                "/org/freedesktop/FileManager1",
                "org.freedesktop.FileManager1.ShowItems",
                "array:string:file://" + absolutePath,
                "string:\"\""
            ).start();
        } catch (IOException e) {
            // Fallback: If D-Bus isn't working, just open the parent directory normally
            try {
                Path parentDir = file.getParent();
                if (parentDir != null) {
                    new ProcessBuilder("xdg-open", parentDir.toAbsolutePath().toString()).start();
                }
            } catch (IOException ex) {
                throw new RuntimeException("Failed to open Linux File Manager", ex);
            }
        }
    }
}