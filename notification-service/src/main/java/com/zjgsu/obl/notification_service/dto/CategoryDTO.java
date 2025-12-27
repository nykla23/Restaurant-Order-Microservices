package com.zjgsu.obl.notification_service.dto;

import lombok.Data;

import java.util.Date;

@Data
public class CategoryDTO {
    private Integer id;
    private String name;
    private String description;
    private Integer sortOrder;
    private Boolean isActive;
    private Date createdAt;

}