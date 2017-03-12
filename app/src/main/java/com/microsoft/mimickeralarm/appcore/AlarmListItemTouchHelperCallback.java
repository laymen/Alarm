/*
 *
 * Copyright (c) Microsoft. All rights reserved.
 * Licensed under the MIT license.
 *
 * Project Oxford: http://ProjectOxford.ai
 *
 * Project Oxford Mimicker Alarm Github:
 * https://github.com/Microsoft/ProjectOxford-Apps-MimickerAlarm
 *
 * Copyright (c) Microsoft Corporation
 * All rights reserved.
 *
 * MIT License:
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED ""AS IS"", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package com.microsoft.mimickeralarm.appcore;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import com.microsoft.mimickeralarm.R;

/**
 * This class implements the swipe UI experience for the items in the alarm list. This callback
 * class is attached to the RecyclerView upon initialization. The onChildDraw method takes care of
 * drawing the swipe visuals in the item that is being manipulated.
 *
 * When the swipe action fires in onSwiped, we call back into the adaptor with the index of the
 * item that needs to be removed.
 */
public class AlarmListItemTouchHelperCallback extends ItemTouchHelper.Callback {

    private final ItemTouchHelperAdapter mAdapter;
    private boolean mCanDismiss;
    public AlarmListItemTouchHelperCallback(ItemTouchHelperAdapter adapter) {
        mAdapter = adapter;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        return makeMovementFlags(0, ItemTouchHelper.START | ItemTouchHelper.END);
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        if (mCanDismiss) {
            // Remove the item from the view
            mAdapter.onItemDismiss(viewHolder.getAdapterPosition());
        } else {
            // Reset the view back to its default visual state
            mAdapter.onItemDismissCancel(viewHolder.getAdapterPosition());
        }
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onChildDraw(Canvas canvas, RecyclerView recyclerView,
                            RecyclerView.ViewHolder viewHolder, float dX, float dY,
                            int actionState, boolean isCurrentlyActive) {

        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {

            View itemView = viewHolder.itemView;
            Resources resources = AlarmApplication.getAppContext().getResources();
            Bitmap icon = BitmapFactory.decodeResource(resources, R.drawable.delete_trash_can);
            int iconPadding = resources.getDimensionPixelOffset(R.dimen.alarm_list_delete_icon_padding);
            int maxDrawWidth = (iconPadding * 2) + icon.getWidth();

            Paint paint = new Paint();
            paint.setColor(ContextCompat.getColor(AlarmApplication.getAppContext(), R.color.red));

            int x = Math.round(Math.abs(dX));

            // Reset the dismiss flag if the view resets to its default position
            if (x == 0) {
                mCanDismiss = false;
            }

            // If we have travelled beyond the icon area via direct user interaction
            // we will dismiss when we get a swipe callback.  We do this to try to avoid
            // unwanted swipe dismissal
            if ((x > maxDrawWidth) && isCurrentlyActive) {
                mCanDismiss = true;
            }

            int drawWidth  = Math.min(x, maxDrawWidth);
            // Cap the height of the drawable area to the selectable area - this improves the visual
            // for the first taller item in the alarm list
            int itemTop = itemView.getBottom() - resources.getDimensionPixelSize(R.dimen.alarm_list_item_height);

            if (dX > 0) {
                // Handle swiping to the right
                // Draw red background in area that we vacate up to maxDrawWidth
                canvas.drawRect((float) itemView.getLeft(),
                        (float) itemTop,
                        drawWidth,
                        (float) itemView.getBottom(),
                        paint);

                // Only draw icon when we've past the padding threshold
                if (x > iconPadding) {

                    Rect destRect = new Rect();
                    destRect.left = itemView.getLeft() + iconPadding;
                    destRect.top = itemTop + (itemView.getBottom() - itemTop - icon.getHeight()) / 2;
                    int maxRight = destRect.left + icon.getWidth();
                    destRect.right = Math.min(x, maxRight);
                    destRect.bottom = destRect.top + icon.getHeight();

                    // Only draw the appropriate parts of the bitmap as it is revealed
                    Rect srcRect = null;
                    if (x < maxRight) {
                        srcRect = new Rect();
                        srcRect.top = 0;
                        srcRect.left = 0;
                        srcRect.bottom = icon.getHeight();
                        srcRect.right = x - iconPadding;
                    }

                    canvas.drawBitmap(icon,
                            srcRect,
                            destRect,
                            paint);
                }

            } else {
                // Handle swiping to the left
                // Draw red background in area that we vacate  up to maxDrawWidth
                canvas.drawRect((float) itemView.getRight() - drawWidth,
                        (float) itemTop,
                        (float) itemView.getRight(),
                        (float) itemView.getBottom(), paint);

                // Only draw icon when we've past the padding threshold
                if (x > iconPadding) {
                    int fromLeftX = itemView.getRight() - x;
                    Rect destRect = new Rect();
                    destRect.right = itemView.getRight() - iconPadding;
                    destRect.top = itemTop + (itemView.getBottom() - itemTop - icon.getHeight()) / 2;
                    int maxFromLeft = destRect.right - icon.getWidth();
                    destRect.left = Math.max(fromLeftX, maxFromLeft);
                    destRect.bottom = destRect.top + icon.getHeight();

                    // Only draw the appropriate parts of the bitmap as it is revealed
                    Rect srcRect = null;
                    if (fromLeftX > maxFromLeft) {
                        srcRect = new Rect();
                        srcRect.top = 0;
                        srcRect.right = icon.getWidth();
                        srcRect.bottom = icon.getHeight();
                        srcRect.left = srcRect.right - (x - iconPadding);
                    }

                    canvas.drawBitmap(icon,
                            srcRect,
                            destRect,
                            paint);
                }
            }

            // Fade out the item as we swipe it
            float alpha = 1.0f - Math.abs(dX) / (float) itemView.getWidth();
            itemView.setAlpha(alpha);
            itemView.setTranslationX(dX);
        } else {
            super.onChildDraw(canvas, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    }

    public interface ItemTouchHelperAdapter {
        void onItemDismiss(int position);

        void onItemDismissCancel(int position);
    }
}