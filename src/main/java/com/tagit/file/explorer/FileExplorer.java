package com.tagit.file.explorer;

import java.nio.file.Path;

public interface FileExplorer {
    void openAndSelectFile(Path fileAbsolutePath);
}
