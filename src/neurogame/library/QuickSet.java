package neurogame.library;


public class QuickSet<T>
{
  private T[] list;
  private int last = -1;
  
  
  public QuickSet(int maxElements)
  {
    list = (T[]) new Object[maxElements];
  }
  
  public T get(int idx) 
  { if (idx > last) throw new IllegalArgumentException("idx > last: idx="+idx+", last="+last);
    return list[idx];
  }
  
  public boolean add(T obj)
  {
    if (obj == null) return false;
    if (last + 1  == list.length) return false;
    last++;
    list[last] = obj;
    return true;
  }
  
  public void remove(int idx)
  {
    if (idx < last) 
    {
      list[idx] = list[last];
      last--;
    }
    else if (idx == last) last--;
  }
  
  public int size() {return last+1;}
}
