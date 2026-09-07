package com.tagit.file.explorer;

import java.io.IOException;
import java.nio.file.Path;

public class MacFileExplorer implements FileExplorer {

    @Override
    public void openAndSelectFile(Path file) {
        try {
            String absolutePath = file.toAbsolutePath().toString();
            
            // Runs: open -R /path/to/file.txt
            new ProcessBuilder("open", "-R", absolutePath).start();
        } catch (IOException e) {
            throw new RuntimeException("Failed to open Finder and select file", e);
        }
    }
}