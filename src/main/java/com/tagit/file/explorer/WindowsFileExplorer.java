package com.tagit.file.explorer;

import java.io.IOException;
import java.nio.file.Path;

public class WindowsFileExplorer implements FileExplorer {

    @Override
    public void openAndSelectFile(Path fileAbsolutePath) {
        try {
            // Converts path to absolute string, e.g., C:\Users\Name\Documents\image.png
            String absolutePath = fileAbsolutePath.toAbsolutePath().toString();
            
            // Runs: explorer.exe /select,"C:\path\to\file.txt"
            new ProcessBuilder("explorer.exe", "/select,", absolutePath).start();
        } catch (IOException e) {
            throw new RuntimeException("Failed to open Windows Explorer and select file", e);
        }
    }
}