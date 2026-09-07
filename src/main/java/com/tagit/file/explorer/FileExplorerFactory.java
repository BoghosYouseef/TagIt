package com.tagit.file.explorer;

public class FileExplorerFactory {
    
    public static FileExplorer create() {
        String os = System.getProperty("os.name").toLowerCase();
        
        if (os.contains("win")) {
            return new WindowsFileExplorer();
        } else if (os.contains("mac")) {
            return new MacFileExplorer();
        } else if (os.contains("nix") || os.contains("nux") || os.contains("aix")) {
            return new LinuxFileExplorer();
        } else {
            throw new UnsupportedOperationException("Your Operating System is not supported yet.");
        }
    }
}