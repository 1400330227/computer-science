package com.computerscience.hdfsapi.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum AnnotationFileFormat {
    // 文本格式
    TXT("Plain Text File", ".txt", "最基础的纯文本文件，无任何格式", "文本");

    private final String fullName;
    private final String extension;
    private final String description;
    private final String category;

    AnnotationFileFormat(String fullName, String extension, String description, String category) {
        this.fullName = fullName;
        this.extension = extension;
        this.description = description;
        this.category = category;
    }

    @JsonCreator
    public static DataFormat getEnum(String extension) {
        for (DataFormat format : DataFormat.values()) {
            if (format.getExtension().equals(extension)) {
                return format;
            }
        }
        return null;
    }

    @JsonValue
    public String getExtension() {
        return extension;
    }

    public String getFullName() {
        return fullName;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    /**
     * 根据文件扩展名获取数据格式
     *
     * @param fileName 文件名
     * @return 对应的数据格式，如果未找到返回null
     */
    public static DataFormat getByFileName(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return null;
        }

        String lowerFileName = fileName.toLowerCase();
        for (DataFormat format : DataFormat.values()) {
            String[] extensions = format.getExtension().split("/");
            for (String ext : extensions) {
                if (lowerFileName.endsWith(ext.toLowerCase())) {
                    return format;
                }
            }
        }
        return null;
    }

    /**
     * 根据类别获取所有格式
     *
     * @param category 类别（TEXT, VIDEO, AUDIO）
     * @return 该类别下的所有格式
     */
    public static DataFormat[] getByCategory(String category) {
        return java.util.Arrays.stream(DataFormat.values())
                .filter(format -> format.getCategory().equals(category))
                .toArray(DataFormat[]::new);
    }
}
