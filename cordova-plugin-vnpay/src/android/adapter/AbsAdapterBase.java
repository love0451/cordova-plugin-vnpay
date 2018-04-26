package com.apvisa.pay.adapter;

import java.util.List;
import java.util.Stack;

public abstract class AbsAdapterBase<E> extends MyAbsAdapterBase
{
  protected List<E> mList = new Stack();
  protected byte[] sLock = new byte[0];
  private int mCount = Integer.MAX_VALUE;
  
  public AbsAdapterBase() {}
  
  public AbsAdapterBase(List<E> mList)
  {
    if (mList != null) {
      this.mList = mList;
    } else {
      this.mList.clear();
    }
  }
  
  public final void setMaxCount(int nMaxCount)
  {
    this.mCount = nMaxCount;
  }
  
  public final int getMaxCount()
  {
    return this.mCount;
  }
  
  public void remove(int position)
  {
    synchronized (this.sLock)
    {
      if ((position >= 0) && (position < this.mList.size()))
      {
        this.mList.remove(position);
        notifyDataSetChanged();
      }
    }
  }
  
  /* Error */
//  public int getCount()
//  {
//    // Byte code:
//    //   0: aload_0
//    //   1: getfield 23	cn/com/apvisa/pay/adapter/AbsAdapterBase:sLock	[B
//    //   4: dup
//    //   5: astore_1
//    //   6: monitorenter
//    //   7: aload_0
//    //   8: getfield 21	cn/com/apvisa/pay/adapter/AbsAdapterBase:mList	Ljava/util/List;
//    //   11: invokeinterface 48 1 0
//    //   16: aload_1
//    //   17: monitorexit
//    //   18: ireturn
//    //   19: aload_1
//    //   20: monitorexit
//    //   21: athrow
//    //   22: astore_1
//    //   23: aload_1
//    //   24: invokevirtual 62	java/lang/Exception:printStackTrace	()V
//    //   27: iconst_0
//    //   28: ireturn
//    // Line number table:
//    //   Java source line #36	-> byte code offset #0
//    //   Java source line #37	-> byte code offset #7
//    //   Java source line #36	-> byte code offset #19
//    //   Java source line #39	-> byte code offset #22
//    //   Java source line #40	-> byte code offset #23
//    //   Java source line #42	-> byte code offset #27
//    // Local variable table:
//    //   start	length	slot	name	signature
//    //   0	29	0	this	AbsAdapterBase<E>
//    //   5	15	1	Ljava/lang/Object;	Object
//    //   22	2	1	e	Exception
//    // Exception table:
//    //   from	to	target	type
//    //   7	18	19	finally
//    //   19	21	19	finally
//    //   0	18	22	java/lang/Exception
//    //   19	22	22	java/lang/Exception
//  }
  
  /* Error */
//  public E getItem(int position)
//  {
//    // Byte code:
//    //   0: aload_0
//    //   1: getfield 23	cn/com/apvisa/pay/adapter/AbsAdapterBase:sLock	[B
//    //   4: dup
//    //   5: astore_2
//    //   6: monitorenter
//    //   7: aload_0
//    //   8: getfield 21	cn/com/apvisa/pay/adapter/AbsAdapterBase:mList	Ljava/util/List;
//    //   11: iload_1
//    //   12: invokeinterface 71 2 0
//    //   17: aload_2
//    //   18: monitorexit
//    //   19: areturn
//    //   20: aload_2
//    //   21: monitorexit
//    //   22: athrow
//    // Line number table:
//    //   Java source line #46	-> byte code offset #0
//    //   Java source line #47	-> byte code offset #7
//    //   Java source line #46	-> byte code offset #20
//    // Local variable table:
//    //   start	length	slot	name	signature
//    //   0	23	0	this	AbsAdapterBase<E>
//    //   0	23	1	position	int
//    //   5	16	2	Ljava/lang/Object;	Object
//    // Exception table:
//    //   from	to	target	type
//    //   7	19	20	finally
//    //   20	22	20	finally
//  }
  
  public long getItemId(int position)
  {
    return position;
  }
  
  public void clear()
  {
    synchronized (this.sLock)
    {
      this.mList.clear();
      notifyDataSetChanged();
    }
  }
  
  public void replaceList(List<E> list)
  {
    synchronized (this.sLock)
    {
      if (list == null) {
        this.mList.clear();
      } else {
        this.mList = list;
      }
      notifyDataSetChanged();
    }
  }
  
  public void addToListHead(List<E> pItemList)
  {
    if ((pItemList == null) || (pItemList.size() <= 0)) {
      return;
    }
    synchronized (this.sLock)
    {
      for (int i = pItemList.size() - 1; i >= 0; i--) {
        this.mList.add(0, pItemList.get(i));
      }
      if (this.mCount != Integer.MAX_VALUE)
      {
        int n = this.mList.size() - this.mCount;
        while (n-- > 0) {
          this.mList.remove(this.mList.size() - 1);
        }
      }
      notifyDataSetChanged();
    }
  }
  
  public void addToListHead(E pItem)
  {
    synchronized (this.sLock)
    {
      this.mList.add(0, pItem);
      if (this.mCount != Integer.MAX_VALUE)
      {
        int n = this.mList.size() - this.mCount;
        while (n-- > 0) {
          this.mList.remove(this.mList.size() - 1);
        }
      }
      notifyDataSetChanged();
    }
  }
  
  public void addToListBack(List<E> list)
  {
    if ((list == null) || (list.size() <= 0)) {
      return;
    }
    synchronized (this.sLock)
    {
      this.mList.addAll(list);
      if (this.mCount != Integer.MAX_VALUE)
      {
        int n = this.mList.size() - this.mCount;
        while (n-- > 0) {
          this.mList.remove(0);
        }
      }
      notifyDataSetChanged();
    }
  }
  
  public void removeListBack(int nRemoveCount)
  {
    removeListBack(nRemoveCount, true);
  }
  
  private void removeListBack(int nRemoveCount, boolean bNotify)
  {
    synchronized (this.sLock)
    {
      int n = Math.min(this.mList.size(), nRemoveCount);
      while (n-- > 0) {
        this.mList.remove(0);
      }
      if (bNotify) {
        notifyDataSetChanged();
      }
    }
  }
  
  public void addToListBack(E e)
  {
    synchronized (this.sLock)
    {
      this.mList.add(e);
      if (this.mCount != Integer.MAX_VALUE)
      {
        int n = this.mList.size() - this.mCount;
        while (n-- > 0) {
          this.mList.remove(0);
        }
      }
      notifyDataSetChanged();
    }
  }
  
  public final List<E> getList()
  {
    synchronized (this.sLock)
    {
      List<E> mListRt = new Stack();
      mListRt.addAll(this.mList);
      return mListRt;
    }
  }
}

