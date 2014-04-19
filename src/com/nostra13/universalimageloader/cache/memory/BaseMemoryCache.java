package com.nostra13.universalimageloader.cache.memory;

import java.lang.ref.Reference;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public abstract class BaseMemoryCache<K, V>
  implements MemoryCacheAware<K, V>
{
  private final Map<K, Reference<V>> softMap = Collections.synchronizedMap(new HashMap());
  
  public void clear()
  {
    this.softMap.clear();
  }
  
  protected abstract Reference<V> createReference(V paramV);
  
  public V get(K paramK)
  {
    Reference localReference = (Reference)this.softMap.get(paramK);
    Object localObject = null;
    if (localReference != null) {
      localObject = localReference.get();
    }
    return localObject;
  }
  
  public Collection<K> keys()
  {
    return this.softMap.keySet();
  }
  
  public boolean put(K paramK, V paramV)
  {
    this.softMap.put(paramK, createReference(paramV));
    return true;
  }
  
  public void remove(K paramK)
  {
    this.softMap.remove(paramK);
  }
}


/* Location:
 * Qualified Name:     com.nostra13.universalimageloader.cache.memory.BaseMemoryCache
 * JD-Core Version:    0.7.0.1
 */