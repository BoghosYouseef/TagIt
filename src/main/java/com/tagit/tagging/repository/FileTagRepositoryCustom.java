package com.tagit.tagging.repository;

import com.tagit.file.domain.FileModel;
import com.tagit.file.domain.FileTag;
import com.tagit.tag.domain.TagModel;

public interface FileTagRepositoryCustom {
    FileTag saveFileTagRelationship(FileModel file, TagModel tag);
}
