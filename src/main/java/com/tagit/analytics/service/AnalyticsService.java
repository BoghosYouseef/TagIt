package com.tagit.analytics.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.tagit.file.domain.FileModel;
import com.tagit.file.service.FileService;
import com.tagit.tag.domain.TagModel;
import com.tagit.tag.service.TagService;

@Service
public class AnalyticsService {
    private final FileService fileService;
    private final TagService tagService;

    public AnalyticsService(FileService fileService, TagService tagService) {
        this.fileService = fileService;
        this.tagService = tagService;
    }

    public List<TagDistribution> getTagDistribution() {
        Map<Long, TagDistributionAccumulator> distributions = new LinkedHashMap<>();
        for (TagModel tag : tagService.getAllTags()) {
            distributions.put(tag.getId(), new TagDistributionAccumulator(tag));
        }

        for (FileModel file : fileService.retrieveAllFilesAsObjects()) {
            long fileSizeBytes = parseSizeInBytes(file.getSize());
            file.getTags().forEach(tag -> {
                TagDistributionAccumulator distribution = distributions.get(tag.getId());
                if (distribution != null) {
                    distribution.fileCount++;
                    distribution.totalSizeBytes += fileSizeBytes;
                }
            });
        }

        List<TagDistribution> results = new ArrayList<>();
        distributions.values().forEach(distribution -> results.add(
            new TagDistribution(distribution.tag, distribution.fileCount, distribution.totalSizeBytes)
        ));
        return results;
    }

    private long parseSizeInBytes(String formattedSize) {
        if (formattedSize == null) {
            return 0;
        }

        String[] parts = formattedSize.trim().split("\\s+");
        if (parts.length != 2) {
            return 0;
        }

        int power = switch (parts[1].toLowerCase()) {
            case "byte", "bytes" -> 0;
            case "kb" -> 1;
            case "mb" -> 2;
            case "gb" -> 3;
            case "tb" -> 4;
            case "pb" -> 5;
            default -> -1;
        };
        if (power < 0) {
            return 0;
        }

        try {
            return new BigDecimal(parts[0])
                .multiply(BigDecimal.valueOf(1024).pow(power))
                .setScale(0, RoundingMode.HALF_UP)
                .longValue();
        } catch (NumberFormatException exception) {
            return 0;
        }
    }

    public record TagDistribution(TagModel tag, long fileCount, long totalSizeBytes) {}

    private static class TagDistributionAccumulator {
        private final TagModel tag;
        private long fileCount;
        private long totalSizeBytes;

        private TagDistributionAccumulator(TagModel tag) {
            this.tag = tag;
        }
    }
}
