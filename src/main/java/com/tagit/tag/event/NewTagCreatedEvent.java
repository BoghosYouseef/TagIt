package com.tagit.tag.event;

import com.tagit.tag.domain.TagModel;

/**
 * Domain event published whenever a new tag is successfully persisted.
 */
public record NewTagCreatedEvent(TagModel tag) {
    
}