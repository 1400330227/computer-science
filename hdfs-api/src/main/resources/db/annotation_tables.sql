-- 标注功能数据库表结构
-- 执行此SQL脚本创建标注相关的数据库表

-- 1. 标注表
CREATE TABLE IF NOT EXISTS `annotations` (
  `annotation_id` INT AUTO_INCREMENT PRIMARY KEY COMMENT '标注ID',
  `corpus_id` INT NOT NULL COMMENT '关联的语料ID',
  `file_id` INT DEFAULT NULL COMMENT '关联的文件ID（可选）',
  `title` VARCHAR(255) NOT NULL COMMENT '标注标题',
  `content` TEXT NOT NULL COMMENT '标注内容/注释',
  `annotation_type` VARCHAR(100) DEFAULT NULL COMMENT '标注类型/标签',
  `status` VARCHAR(50) DEFAULT '待审核' COMMENT '标注状态（待审核、已审核、已发布等）',
  `text_segment` TEXT DEFAULT NULL COMMENT '标注的文本片段',
  `start_position` INT DEFAULT NULL COMMENT '文本片段起始位置',
  `end_position` INT DEFAULT NULL COMMENT '文本片段结束位置',
  `creator_id` INT NOT NULL COMMENT '标注创建者ID',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remarks` TEXT DEFAULT NULL COMMENT '备注说明',
  INDEX `idx_corpus_id` (`corpus_id`),
  INDEX `idx_file_id` (`file_id`),
  INDEX `idx_creator_id` (`creator_id`),
  INDEX `idx_annotation_type` (`annotation_type`),
  INDEX `idx_status` (`status`),
  FOREIGN KEY (`corpus_id`) REFERENCES `corpus`(`corpus_id`) ON DELETE CASCADE,
  FOREIGN KEY (`file_id`) REFERENCES `files`(`file_id`) ON DELETE SET NULL,
  FOREIGN KEY (`creator_id`) REFERENCES `users`(`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='标注表';

-- 2. 标注标签表
CREATE TABLE IF NOT EXISTS `annotation_labels` (
  `label_id` INT AUTO_INCREMENT PRIMARY KEY COMMENT '标签ID',
  `label_name` VARCHAR(100) NOT NULL COMMENT '标签名称',
  `description` VARCHAR(500) DEFAULT NULL COMMENT '标签描述',
  `color` VARCHAR(20) DEFAULT NULL COMMENT '标签颜色（用于前端显示）',
  `category` VARCHAR(100) DEFAULT NULL COMMENT '标签分类（如：情感、实体、语法等）',
  `creator_id` INT DEFAULT NULL COMMENT '创建者ID',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `is_system` TINYINT(1) DEFAULT 0 COMMENT '是否系统默认标签（0-否，1-是）',
  INDEX `idx_category` (`category`),
  INDEX `idx_creator_id` (`creator_id`),
  UNIQUE KEY `uk_label_name` (`label_name`),
  FOREIGN KEY (`creator_id`) REFERENCES `users`(`user_id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='标注标签表';

-- 3. 插入一些默认的标注标签
INSERT INTO `annotation_labels` (`label_name`, `description`, `color`, `category`, `is_system`) VALUES
('情感分析', '对文本情感进行标注', '#FF6B6B', '情感', 1),
('实体识别', '识别文本中的实体', '#4ECDC4', '实体', 1),
('语法标注', '对语法结构进行标注', '#45B7D1', '语法', 1),
('语义标注', '对语义进行标注', '#96CEB4', '语义', 1),
('词性标注', '对词性进行标注', '#FFEAA7', '词性', 1),
('命名实体', '识别命名实体', '#DDA0DD', '实体', 1),
('关键词提取', '提取关键词', '#98D8C8', '关键词', 1),
('主题分类', '对主题进行分类', '#F7DC6F', '分类', 1)
ON DUPLICATE KEY UPDATE `label_name`=`label_name`;

