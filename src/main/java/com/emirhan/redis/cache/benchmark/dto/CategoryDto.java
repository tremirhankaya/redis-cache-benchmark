package com.emirhan.redis.cache.benchmark.dto;

import com.emirhan.redis.cache.benchmark.entity.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDto implements Serializable {

    private Long id;
    private String name;
    private Long parentId;

    public static CategoryDto from(Category category) {
        if (category == null) return null;
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .parentId(category.getParent() != null ? category.getParent().getId() : null)
                .build();
    }
}
