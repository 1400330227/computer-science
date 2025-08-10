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
import { useRoute, useRouter } from 'vue-router'
import { ArrowRight } from '@element-plus/icons-vue'

// 注入全局面包屑状态
const breadcrumbState = inject('breadcrumb', { items: [] })
const route = useRoute()
const router = useRouter()

// 从路由表生成 path->title 映射
const routeTitleMap = computed(() => {
    const map = {}
    router.getRoutes().forEach(r => {
        if (r.path) {
            const title = r.meta && r.meta.title ? r.meta.title : r.name || r.path
            map[r.path.startsWith('/') ? r.path : `/${r.path}`] = title
        }
    })
    return map
})

function buildMatchedBreadcrumb() {
    const items = []
    const baseCrumb = { title: '首页', path: '/' }

    // 首页特殊处理
    if (route.path === '/') {
        items.push(baseCrumb)
        return items
    }

    items.push(baseCrumb)

    // 使用 route.matched 构建层级
    route.matched.forEach((rec) => {
        if (!rec.path) return
        // 规范化路径
        let fullPath = rec.path
        if (!fullPath.startsWith('/')) fullPath = `/${fullPath}`

        // 处理动态参数占位符，尽可能用当前参数替换
        Object.entries(route.params || {}).forEach(([key, val]) => {
            const value = Array.isArray(val) ? val[0] : val
            fullPath = fullPath.replace(`:${key}`, String(value))
        })

        // 标题优先使用 meta.title
        const title = rec.meta && rec.meta.title ? rec.meta.title : (rec.name || routeTitleMap.value[fullPath] || fullPath)

        // 避免重复首页
        if (fullPath !== '/' && title) {
            items.push({ title, path: fullPath })
        }
    })

    // 若 matched 为空，尝试使用完整 path 的映射
    if (items.length === 1) {
        const title = routeTitleMap.value[route.path]
        if (title) items.push({ title, path: route.path })
    }

    return items
}

// 根据当前路由生成面包屑数据
const breadcrumbItems = computed(() => {
    // 如果有手动设置的面包屑，优先使用
    if (breadcrumbState.items && breadcrumbState.items.length > 0) {
        return breadcrumbState.items
    }

    return buildMatchedBreadcrumb()
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