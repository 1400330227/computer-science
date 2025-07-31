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
        '/file-manager': [{ title: '文件管理', path: '/file-manager' }],
    }

    return [...baseCrumb, ...(pathMap[routePath] || [])]
})
</script>

<style scoped>
.breadcrumb {
    padding: 10px 0;
    color: #666;
    font-size: 12px;
    display: flex;
    justify-content: left;
    max-width: 1240px;
    margin: 10px auto;
}

.breadcrumb .el-breadcrumb {
    width: 100%;
    padding: 0 20px;
}

:deep(.el-breadcrumb__item) {
    color: #333;
}

:deep(.el-breadcrumb__inner.is-link) {
    color: #4B70BD;
}

:deep(.el-breadcrumb__inner) {
    font-weight: normal;
}

:deep(.el-breadcrumb__item:last-child .el-breadcrumb__inner) {
    color: #333;
    font-weight: 500;
}
</style>