<template>
    <div class="breadcrumb">
        <el-breadcrumb :separator-icon="ArrowRight">
            <el-breadcrumb-item v-for="(item, index) in breadcrumbItems" :key="index" :to="item.path">
                {{ item.title }}
            </el-breadcrumb-item>
        </el-breadcrumb>
    </div>
</template>

<script setup>
import { computed, inject } from 'vue'
import { useRoute } from 'vue-router'
import { ArrowRight } from '@element-plus/icons-vue'

// 注入全局面包屑状态
const breadcrumbState = inject('breadcrumb', { items: [] })
const route = useRoute()

// 根据当前路由生成面包屑数据
const breadcrumbItems = computed(() => {
    // 如果有手动设置的面包屑，优先使用
    if (breadcrumbState.items && breadcrumbState.items.length > 0) {
        return breadcrumbState.items
    }

    // 否则根据路由自动生成
    const routePath = route.path
    const baseCrumb = [{ title: '首页', path: '/' }]

    if (routePath === '/') {
        return baseCrumb
    }

    // 路由映射到面包屑
    const pathMap = {
        '/file-list': [{ title: '语料清单', path: '/file-list' }],
        '/upload': [{ title: '语料上传', path: '/upload' }],
        '/about': [{ title: '关于', path: '/about' }],
    }

    return [...baseCrumb, ...(pathMap[routePath] || [])]
})
</script>

<style scoped>
.breadcrumb {
    padding: 16px 0;
    color: #666;
    font-size: 14px;
    display: flex;
    justify-content: left;
    max-width: 1240px;
    margin: 0 auto;
    background: transparent;
}

.breadcrumb .el-breadcrumb {
    width: 100%;
    padding: 0 24px;
}

:deep(.el-breadcrumb__item) {
    color: #64748b;
    font-weight: 500;
}

:deep(.el-breadcrumb__inner.is-link) {
    color: #667eea;
    transition: all 0.3s ease;
    padding: 4px 8px;
    border-radius: 6px;

    &:hover {
        background: rgba(102, 126, 234, 0.08);
        color: #5a67d8;
    }
}

:deep(.el-breadcrumb__inner) {
    font-weight: 500;
}

:deep(.el-breadcrumb__item:last-child .el-breadcrumb__inner) {
    color: #5a67d8;
    font-weight: 600;
    background: rgba(102, 126, 234, 0.1);
    padding: 4px 12px;
    border-radius: 6px;
}

:deep(.el-breadcrumb__separator) {
    color: #cbd5e1;
    font-weight: 600;
}
</style>