package com.tagit.repository.interfaces;

import com.tagit.model.FileModel;
import com.tagit.model.FileTag;
import com.tagit.model.TagModel;

public interface FileTagRepositoryCustom {
    FileTag saveFileTagRelationship(FileModel file, TagModel tag);
}
