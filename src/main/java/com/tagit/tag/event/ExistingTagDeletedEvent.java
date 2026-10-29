package com.tagit.tag.event;

import com.tagit.tag.domain.TagModel;

/**
 * Domain event published whenever an existing tag is successfully deleted.
 */
public record ExistingTagDeletedEvent(TagModel tag) {
    
}