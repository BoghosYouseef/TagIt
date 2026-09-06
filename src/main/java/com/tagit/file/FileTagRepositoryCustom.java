package com.tagit.file;

import com.tagit.file.domain.FileModel;
import com.tagit.file.domain.FileTag;
import com.tagit.tag.TagModel;

public interface FileTagRepositoryCustom {
    FileTag saveFileTagRelationship(FileModel file, TagModel tag);
}
