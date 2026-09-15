import auth from '@/plugins/auth'
import router, { constantRoutes, dynamicRoutes } from '@/router'
import cache from '@/plugins/cache'
import Layout from '@/layout/index'
import ParentView from '@/components/ParentView'
import InnerLink from '@/layout/components/InnerLink'

// 匹配views里面所有的.vue文件
const modules = import.meta.glob('./../../views/**/*.vue')

const usePermissionStore = defineStore(
  'permission',
  {
    state: () => ({
      routes: [],
      addRoutes: [],
      defaultRoutes: [],
      topbarRouters: [],
      sidebarRouters: constantRoutes
    }),
    actions: {
      setRoutes(routes) {
        this.addRoutes = routes
        this.routes = constantRoutes.concat(routes)
      },
      setDefaultRoutes(routes) {
        this.defaultRoutes = constantRoutes.concat(routes)
      },
      setTopbarRoutes(routes) {
        this.topbarRouters = routes
      },
      setSidebarRouters(routes) {
        this.sidebarRouters = routes
      },
generateRoutes(roles) {
  return new Promise(resolve => {
    const cachedMenus = cache.session.getJSON('loginMenus')
    if (cachedMenus && cachedMenus.length > 0) {
      const sdata = JSON.parse(JSON.stringify(cachedMenus))
      const rdata = JSON.parse(JSON.stringify(cachedMenus))
      const sidebarRoutes = filterAsyncRouter(sdata)
      const rewriteRoutes = filterAsyncRouter(rdata, false, true)
      const asyncRoutes = filterDynamicRoutes(dynamicRoutes)
      asyncRoutes.forEach(route => { router.addRoute(route) })
      this.setRoutes(rewriteRoutes)
      this.setSidebarRouters(constantRoutes.concat(sidebarRoutes))
      this.setDefaultRoutes(sidebarRoutes)
      this.setTopbarRoutes(sidebarRoutes)
      resolve(rewriteRoutes)
    } else {
      const asyncRoutes = filterDynamicRoutes(dynamicRoutes)
      asyncRoutes.forEach(route => { router.addRoute(route) })
      this.setRoutes(dynamicRoutes)
      this.setSidebarRouters(constantRoutes)
      resolve(dynamicRoutes)
    }
  })
},
    }
  })

// 遍历后台传来的路由字符串，转换为组件对象
function filterAsyncRouter(asyncRouterMap, lastRouter = false, type = false) {
  return asyncRouterMap.filter(route => {
    route.path = normalizeRoutePath(route.path)
    route.meta = route.meta || {}
    if (route.menuName !== undefined) {
      route.meta.title = route.menuName
    }
    if (route.icon !== undefined) {
      route.meta.icon = route.icon
    }
    if (route.visible === 0 || route.visible === '0') {
      route.hidden = true
    }
    if (type && route.children) {
      route.children = filterChildren(route.children)
    }
    if (route.component) {
      // Layout ParentView 组件特殊处理
      if (route.component === 'Layout') {
        route.component = Layout
      } else if (route.component === 'ParentView') {
        route.component = ParentView
      } else if (route.component === 'InnerLink') {
        route.component = InnerLink
      } else {
        route.component = loadView(route.component)
        if (!route.component) {
          return false
        }
      }
      // 自动生成路由name（用于TagsView全局缓存）
      if (route.path) {
        route.name = normalizeRouteName(route.name, route.path)
      }
    }
    if (route.children != null && route.children && route.children.length) {
      if (!route.component) {
        route.component = Layout
      }
      route.children.forEach(child => {
        const childPath = normalizeRoutePath(child.path)
        if (!/^https?:\/\//i.test(childPath) && !childPath.startsWith(`${route.path}/`)) {
          child.path = `${route.path.replace(/\/$/, '')}/${childPath.replace(/^\//, '')}`
        } else {
          child.path = childPath
        }
      })
      route.children = filterAsyncRouter(route.children, route, type)
      if (!route.children.length) {
        return false
      }
      const firstVisibleChild = route.children.find(child => !child.hidden && child.path)
      if (firstVisibleChild) {
        if (route.menuType === 1 || route.menuType === '1') {
          route.alwaysShow = true
        }
        if (!route.redirect) {
          const parentPath = (route.path || '').replace(/\/$/, '')
          route.redirect = firstVisibleChild.path.startsWith('/')
            ? firstVisibleChild.path
            : `${parentPath}/${firstVisibleChild.path}`
        }
      }
    } else {
      delete route['children']
      delete route['redirect']
    }
    return true
  })
}

function normalizeRoutePath(path) {
  const value = String(path || '').trim().replace(/\\/g, '/')
  if (!value) return '/'
  if (/^https?:\/\//i.test(value)) return value
  return value.startsWith('/') ? value : `/${value}`
}

function toPascalCaseRouteName(path) {
  return path
    .split('/')
    .filter(Boolean)
    .map(segment => segment.charAt(0).toUpperCase() + segment.slice(1))
    .join('')
    .replace(/[^A-Za-z0-9_$]/g, '')
}

function normalizeRouteName(name, path) {
  const value = String(name || '')
  if (value && /^[A-Z][A-Za-z0-9_$]*$/.test(value)) {
    return value
  }
  if (value) {
    const normalizedName = value
      .split(/[_\-/]+/)
      .filter(Boolean)
      .map(segment => segment.charAt(0).toUpperCase() + segment.slice(1))
      .join('')
      .replace(/[^A-Za-z0-9_$]/g, '')
    if (normalizedName) {
      return normalizedName
    }
  }
  return toPascalCaseRouteName(path)
}

function filterChildren(childrenMap, lastRouter = false) {
  var children = []
  childrenMap.forEach(el => {
    const childPath = normalizeRoutePath(el.path)
    el.path = lastRouter && !childPath.startsWith(`${lastRouter.path}/`)
      ? `${lastRouter.path.replace(/\/$/, '')}/${childPath.replace(/^\//, '')}`
      : childPath
    if (el.children && el.children.length && el.component === 'ParentView') {
      children = children.concat(filterChildren(el.children, el))
    } else {
      children.push(el)
    }
  })
  return children
}

// 动态路由遍历，验证是否具备权限
export function filterDynamicRoutes(routes) {
  const res = []
  routes.forEach(route => {
    if (route.permissions) {
      if (auth.hasPermiOr(route.permissions)) {
        res.push(route)
      }
    } else if (route.roles) {
      if (auth.hasRoleOr(route.roles)) {
        res.push(route)
      }
    }
  })
  return res
}

export const loadView = (view) => {
  const normalizedView = String(view || '')
    .replace(/^\/+/, '')
    .replace(/^views\//, '')
    .replace(/\\/g, '/')
    .replace(/\.vue$/, '')
  let res
  for (const path in modules) {
    const dir = path.split('views/')[1].split('.vue')[0].replace(/\\/g, '/')
    if (dir === normalizedView || dir === `${normalizedView}/index` || `${dir}/index` === normalizedView) {
      res = () => modules[path]()
      break
    }
  }
  return res
}

export default usePermissionStore