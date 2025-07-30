<script setup>
import { ref, inject, onMounted } from 'vue'

// 获取全局面包屑管理工具
const breadcrumb = inject('breadcrumb')

onMounted(() => {
  // 设置首页的面包屑
  breadcrumb.setBreadcrumb([
    { title: '首页', path: '/' }
  ])
})

let dataSource = ref([
  {
    key: '1',
    name: '胡彦斌',
    age: 32,
    address: '西湖区湖底公园1号',
  },
  {
    key: '2',
    name: '胡彦祖',
    age: 42,
    address: '西湖区湖底公园1号',
  },
])

let columns = ref([
  {
    title: '姓名',
    dataIndex: 'name',
    key: 'name',
  },
  {
    title: '年龄',
    dataIndex: 'age',
    key: 'age',
  },
  {
    title: '住址',
    dataIndex: 'address',
    key: 'address',
  },
])

</script>

<template>
  <main>
    <a-table :dataSource="dataSource" :columns="columns" />
  </main>
</template>
