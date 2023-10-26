package com.example.mygallery.dragselection;

import com.michaelflisar.dragselectrecyclerview.DragSelectTouchListener;

public class DragSelection extends DragSelectTouchListener {

    private static final DragSelectTouchListener dragSelectTouchListener = new DragSelectTouchListener()
            .withMaxScrollDistance(20)
            .withTopOffset(0)
            .withBottomOffset(0)
            .withScrollAboveTopRegion(true)
            .withScrollBelowTopRegion(true);

}
