import useDictStore from '@/store/modules/dict'
import { ref, toRefs } from 'vue'

export function useDict(...args) {
  const res = ref({})
  return (() => {
    args.forEach((dictType) => {
      res.value[dictType] = []
      const dicts = useDictStore().getDict(dictType)
      if (dicts) {
        res.value[dictType] = dicts
      }
    })
    return toRefs(res.value)
  })()
}