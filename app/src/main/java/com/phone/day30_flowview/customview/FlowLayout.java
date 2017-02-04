package com.phone.day30_flowview.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * 流式布局
 * 
 * @author apple
 * 
 */
public class FlowLayout extends ViewGroup {

	public FlowLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	/*
	 * 
	 * 给ViewGroup设置一个layoutParams
	 */
	@Override
	public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
		return new MarginLayoutParams(getContext(), attrs);
	}

	/**
	 * 计算测量View 的宽高 MeasureSpec.AT_MOST -- wrap_content -- 父布局所允许的最大尺寸
	 * MeasureSpec.EXACTLY -- match_parent 或者是 绝对固定的值 精确的值
	 * MeasureSpec.UNSPECIFIED-- 未指定的尺寸
	 * 
	 * 
	 * 
	 */

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
		int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);

		int modeWidth = MeasureSpec.getMode(widthMeasureSpec);
		int modeHeight = MeasureSpec.getMode(heightMeasureSpec);

		// 记录View的宽高
		int width = 0;
		int height = 0;

		// 记录每一行的宽高
		int lineWidth = 0;
		int lineHeight = 0;

		// 获取所有子view 的数目
		int cCount = getChildCount();
		for (int i = 0; i < cCount; i++) {
			// 获取每一个View对象
			View child = getChildAt(i);
			// 计算每一个View 的宽高
			measureChild(child, widthMeasureSpec, heightMeasureSpec);

			MarginLayoutParams lp = (MarginLayoutParams) child
					.getLayoutParams();

			// 获取View 的实际宽高
			int childWidth = child.getMeasuredWidth() + lp.leftMargin
					+ lp.rightMargin;

			int childHeight = child.getMeasuredHeight() + lp.topMargin
					+ lp.bottomMargin;

			/**
			 * 如果 加上子View 的宽度大于最大宽度
			 */
			if (lineWidth + childWidth > sizeWidth - getPaddingLeft()
					- getPaddingRight()) {

				width = Math.max(lineWidth, childWidth); // 取最大值给width
				lineWidth = childWidth;// 重新开启一行

				height += lineHeight; // 行高叠加
				lineHeight = childHeight; // 重新开启一行
			} else {
				/**
				 * 正常情况下 宽度叠加,高度取最大值
				 */
				lineWidth += childWidth;
				lineHeight = Math.max(lineHeight, childHeight);
			}

			/**
			 * 最后一行
			 */
			if (i == cCount - 1) {
				width = Math.max(width, lineWidth);
				height += lineHeight;
			}

		}
		/**
		 * 设置View 的 宽高
		 * 
		 * 根据mode 设置宽高
		 */
		setMeasuredDimension((modeWidth == MeasureSpec.EXACTLY) ? sizeWidth
				: width + getPaddingLeft() + getPaddingRight(),
				(modeHeight == MeasureSpec.EXACTLY) ? sizeHeight : height
						+ getPaddingBottom() + getPaddingTop());

	}

	/**
	 * 存放所有的View 以行为单位
	 */

	List<List<View>> mAllViews = new ArrayList<List<View>>();

	// 记录行高
	List<Integer> mLineHeight = new ArrayList<Integer>();

	/**
	 * 计算位置 第一个参数 是否对该View的位置或宽高进行了修改 后面的参数是相对于父布局的位置
	 */
	@Override
	protected void onLayout(boolean arg0, int arg1, int arg2, int arg3, int arg4) {
		// 获取View 的宽度
		mAllViews.clear();
		mLineHeight.clear();

		// 当前View 的宽度
		int width = getWidth();

		// 记录行高与行宽
		int lineWidth = 0;
		int lineHeight = 0;

		// 存放每一行的View
		List<View> lineViws = new ArrayList<View>();
		int cCount = getChildCount();
		// 遍历所有的ChildView
		for (int i = 0; i < cCount; i++) {
			View child = getChildAt(i);
			MarginLayoutParams lp = (MarginLayoutParams) child
					.getLayoutParams();
			/**
			 * 获取实际的 ChildView 宽高
			 */
			int childWidth = child.getMeasuredWidth() + lp.leftMargin
					+ lp.rightMargin;
			int childHeight = child.getMeasuredHeight() + lp.topMargin
					+ lp.bottomMargin;

			if (childWidth + lineWidth > width - getPaddingLeft()
					- getPaddingRight()) {

				mLineHeight.add(lineHeight); // 添加该行的 高度
				mAllViews.add(lineViws); // 添加该行的Views

				lineWidth = 0; // 换行清零
				/**
				 * 该list必须new 一个list.clear 会将之前的数据清空
				 */
				lineViws = new ArrayList<View>();
			}
			lineWidth += childWidth; // 每一行宽度叠加
			lineHeight = Math.max(childHeight, lineHeight);// 高度取最大值
			lineViws.add(child);// 添加到每一行的List 中

		}

		/*
		 * 添加最后一行
		 */
		mLineHeight.add(lineHeight);
		mAllViews.add(lineViws);

		// 父布局的
		int left = getPaddingLeft();
		int top = getPaddingTop();

		// 遍历每一个childView
		for (int i = 0; i < mAllViews.size(); i++) {

			// 拿到每一行的View
			lineViws = mAllViews.get(i);
			// 拿到当前行的高度
			lineHeight = mLineHeight.get(i);

			// 循环每一行里的View
			for (int j = 0; j < lineViws.size(); j++) {
				View child = lineViws.get(j);
				MarginLayoutParams lp = (MarginLayoutParams) child
						.getLayoutParams();

				// 计算每一个view的位置
				int l = left + lp.leftMargin;
				int t = top + lp.topMargin;

				int r = l + child.getMeasuredWidth();
				int b = t + child.getMeasuredHeight();

				// 设置该View 现对于父布局的位置
				child.layout(l, t, r, b);
				// 该行每个View 的 left 的修改
				left += child.getMeasuredWidth() + lp.leftMargin
						+ lp.rightMargin;

			}
			// 换行 left 初始化
			// 高度叠加
			left = getPaddingLeft();
			top += lineHeight;
		}

	}

}
