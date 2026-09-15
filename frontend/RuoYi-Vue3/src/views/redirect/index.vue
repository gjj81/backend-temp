<template>
  <div></div>
</template>

<script setup>
import { useRoute, useRouter } from 'vue-router'
import { isHttp } from '@/utils/validate'
import usePermissionStore from '@/store/modules/permission'

const route = useRoute()
const router = useRouter()
const permissionStore = usePermissionStore()
const { params, query } = route
const { path } = params

async function redirectToTarget() {
  const targetPath = '/' + String(path || '').replace(/^\/+/, '')
  const targetRoute = router.resolve(targetPath)
  const isFallbackRoute = targetRoute.matched.some(item => item.path === '/:pathMatch(.*)*')

  if (isFallbackRoute) {
    const accessRoutes = await permissionStore.generateRoutes()
    accessRoutes.forEach(accessRoute => {
      if (!isHttp(accessRoute.path) && accessRoute.name && !router.hasRoute(accessRoute.name)) {
        router.addRoute(accessRoute)
      }
    })
  }

  await router.replace({ path: targetPath, query })
}

redirectToTarget()
</script>