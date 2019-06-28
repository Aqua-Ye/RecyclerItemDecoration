/**
 * Copyright (C) 2015 magiepooh
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.magiepooh.recycleritemdecoration;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.Map;
import java.util.HashMap;

/**
 * Created by magiepooh on 2015/08/.
 */
public class VerticalItemDecoration extends RecyclerView.ItemDecoration {

    private static final int[] ATTRS = {android.R.attr.listDivider};

    private final Map<Integer, Drawable> mDividerViewTypeMap;
    private final Drawable mFirstDrawable;
    private final Drawable mLastDrawable;
    private boolean mDividerAfter = true; // always put a divider after, otherwise, divider only in between same type items

    public VerticalItemDecoration(Map<Integer, Drawable> dividerViewTypeMap,
            Drawable firstDrawable, Drawable lastDrawable, boolean dividerAfter) {
        mDividerViewTypeMap = dividerViewTypeMap;
        mFirstDrawable = firstDrawable;
        mLastDrawable = lastDrawable;
        mDividerAfter = dividerAfter;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
            RecyclerView.State state) {

        // specific view type
        int childType = parent.getLayoutManager().getItemViewType(view);
        Drawable drawable = mDividerViewTypeMap.get(childType);
        if (drawable != null) {
            outRect.bottom = drawable.getIntrinsicHeight();
        }

        // last position
        if (isLastPosition(view, parent) && mLastDrawable != null) {
            outRect.bottom = mLastDrawable.getIntrinsicHeight();
        }

        // first position
        if (isFirstPosition(view, parent) && mFirstDrawable != null) {
            outRect.top = mFirstDrawable.getIntrinsicHeight();
        }
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();

        int childCount = parent.getChildCount();
        for (int i = 0; i <= childCount - 1; i++) {
            View child = parent.getChildAt(i);
            int childViewType = parent.getLayoutManager().getItemViewType(child);
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

            boolean shouldDrawFirst = isFirstPosition(child, parent) && mFirstDrawable != null;
            boolean shouldDrawLast = isLastPosition(child, parent) && mLastDrawable != null;

            if (mDividerAfter) {
                // specific view type
                Drawable drawable = mDividerViewTypeMap.get(childViewType);
                if (drawable != null) {
                    if (i < childCount - 1 || !shouldDrawLast) {
                        int top = child.getBottom() + params.bottomMargin;
                        int bottom = top + drawable.getIntrinsicHeight();
                        drawable.setBounds(left, top, right, bottom);
                        drawable.draw(c);
                    }
                }
            } else if (i < childCount - 1) {
                View nextChild = parent.getChildAt(i + 1);
                int nextChildViewType = parent.getLayoutManager().getItemViewType(nextChild);
                if (nextChildViewType == childViewType) {
                    // specific view type
                    Drawable drawable = mDividerViewTypeMap.get(childViewType);
                    if (drawable != null) {
                        int top = child.getBottom() + params.bottomMargin;
                        int bottom = top + drawable.getIntrinsicHeight();
                        drawable.setBounds(left, top, right, bottom);
                        drawable.draw(c);
                    }
                }
            }

            // last position
            if (shouldDrawLast) {
                int top = child.getBottom() + params.bottomMargin;
                int bottom = top + mLastDrawable.getIntrinsicHeight();
                mLastDrawable.setBounds(left, top, right, bottom);
                mLastDrawable.draw(c);
            }

            // first position
            if (shouldDrawFirst) {
                int bottom = child.getTop() - params.topMargin;
                int top = bottom - mFirstDrawable.getIntrinsicHeight();
                mFirstDrawable.setBounds(left, top, right, bottom);
                mFirstDrawable.draw(c);
            }
        }
    }

    private boolean isFirstPosition(View view, RecyclerView parent) {
        return parent.getChildAdapterPosition(view) == 0;
    }

    private boolean isLastPosition(View view, RecyclerView parent) {
        return parent.getChildAdapterPosition(view) == parent.getAdapter().getItemCount() - 1;
    }

    public static class Builder {

        private final Context mContext;
        private final Map<Integer, Drawable> mDividerViewTypeMap = new HashMap<>();
        private Drawable mFirstDrawable;
        private Drawable mLastDrawable;
        private boolean mDividerAfter = true;

        Builder(Context context) {
            mContext = context;
        }

        public Builder type(int viewType) {
            final TypedArray a = mContext.obtainStyledAttributes(ATTRS);
            Drawable divider = a.getDrawable(0);
            type(viewType, divider);
            a.recycle();
            return this;
        }

        public Builder type(int viewType, @DrawableRes int drawableResId) {
            mDividerViewTypeMap.put(viewType, ContextCompat.getDrawable(mContext, drawableResId));
            return this;
        }

        public Builder type(int viewType, Drawable drawable) {
            mDividerViewTypeMap.put(viewType, drawable);
            return this;
        }

        public Builder first(@DrawableRes int drawableResId) {
            first(ContextCompat.getDrawable(mContext, drawableResId));
            return this;
        }

        public Builder first(Drawable drawable) {
            mFirstDrawable = drawable;
            return this;
        }

        public Builder last(@DrawableRes int drawableResId) {
            last(ContextCompat.getDrawable(mContext, drawableResId));
            return this;
        }

        public Builder last(Drawable drawable) {
            mLastDrawable = drawable;
            return this;
        }

        public Builder after(boolean after) {
            mDividerAfter = after;
            return this;
        }

        public VerticalItemDecoration create() {
            return new VerticalItemDecoration(mDividerViewTypeMap, mFirstDrawable, mLastDrawable, mDividerAfter);
        }

    }

}
