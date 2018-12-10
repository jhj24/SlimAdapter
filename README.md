# SlimAdapter

### 优化的RecyclerView.Adapter可实现：
- 普通单一数据类型的显示
- 多种数据类型显示
- 单种数据类型设置多种显示样式
- 添加标题和结尾
- 设置加载更多
- 可以实现拖拽和滑动删除
- 左右滑动出现滑动菜单

SlimAdapter

- creator(RecyclerView.LayoutManager manager)
- register(final int layoutRes, final ItemViewCallback<D> callback)
- register(final int viewType, final int layoutRes, final ItemViewCallback<D> callback)

- addItemDecoration(RecyclerView.ItemDecoration itemDecoration)
- attachTo(RecyclerView view)
- updateData(List<?> dataList)
- remove(int index)
- getDataList()
- getRecyclerView()


- addHeader(Context context, int layoutRes, OnCustomLayoutListener listener)
- addHeader(Context context, int layoutRes)
- addHeader(View view)
- removeHeader(int index)
- setHeaderWholeLine(boolean headerWholeLine)

- addFooter(Context context, int layoutRes, OnCustomLayoutListener listener)
- addFooter(Context context, int layoutRes)
- addFooter(View view)
- removeFooter(int index)
- setFooterWholeLine(boolean footerWholeLine)

- setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener)
- setLoadMoreView(LoadMoreView loadingView)
- loadMoreEnd()
- loadMoreFail()

- setEmptyView(Context context, int layoutRes, OnCustomLayoutListener listener)
- setEmptyView(Context context, int layoutRes)
- setEmptyView(View emptyView)

- setOnItemClickListener(OnItemClickListener listener)
- setOnItemLongClickListener(OnItemLongClickListener listener)

DraggableAdapter
- setItemTouchHelper()

- setDragItem(boolean isDrag)
- setDragFlags(int dragFlags)
- setOnItemDragListener(OnItemDragListener onItemDragListener)
- setMoveThreshold(float moveThreshold)
- isLongPressDragEnable()

- setSwipeItem(boolean isSwipe)
- setSwipeFlags(int swipeFlags) 
- setOnItemSwipeListener(OnItemSwipeListener listener)
- setSwipeThreshold(float swipeThreshold)
- setSwipeFadeOutAnim(boolean swipeFadeOutAnim)
- isItemSwipeEnable()

方法冲突


SwipeMenuLayout
- setSwipeEnable(boolean swipeEnable) 
- setIos(boolean ios)
- setLeftSwipe(boolean leftSwipe)
- quickClose()

侧滑菜单，在RecyclerView的子布局使用该布局时，不能同时设置DraggableAdapter的setSwipeItem(boolean isSwipe)为true,会造成侧滑菜单和侧滑删除冲突，不知道使用哪种方法。

### Adapter二次封装
实际应用中可能对SlimAdapter进行二次封装，但是泛型二次封装会解析失败。可以通过 setGenericActualType(Type genericActualType)设置泛型的实际类型。

