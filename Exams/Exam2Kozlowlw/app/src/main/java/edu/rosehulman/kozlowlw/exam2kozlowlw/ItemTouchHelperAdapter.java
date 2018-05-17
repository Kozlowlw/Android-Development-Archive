package edu.rosehulman.kozlowlw.exam2kozlowlw;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

public class ItemTouchHelperAdapter extends ItemTouchHelper.SimpleCallback {

    TerritoryAdapter mAdapter;

    public ItemTouchHelperAdapter(int dragDirs, int swipeDirs) {
        super(dragDirs, swipeDirs);
    }

    public ItemTouchHelperAdapter(TerritoryAdapter adapter) {
        super(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT);
        this.mAdapter = adapter;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        if (!mAdapter.isAnyHighlighted()) {
            if (direction == ItemTouchHelper.RIGHT) {
                mAdapter.setScore(mAdapter.getScore() + 2);
            } else if (direction == ItemTouchHelper.LEFT) {
                mAdapter.setScore(mAdapter.getScore() - 1);
            }
            mAdapter.removeTerritory(viewHolder.getAdapterPosition());
        }
    }
}
