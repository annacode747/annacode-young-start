package com.cloudwebsoft.framework.base;

import org.apache.log4j.Logger;

import java.util.Iterator;

public class QObjectBlockIterator implements Iterator {
    public Logger logger = Logger.getLogger(this.getClass().getName());

    QObjectDb qObject;

    private Object[] infoBlock;
    private int blockID;
    private int blockStart;
    private String query;
    private int startIndex;
    private int currentIndex;
    private int endIndex;

    private String groupName;

    private Object previousElement = null;
    private Object nextElement = null;

    /**
     * Constructs a new ForumThreadBlockIterator.
     *
     * @param threadBlock the starting threadBlock of elements to iterate
     *                    through.
     * @param query       the SQL query corresponding to this iteration.
     * @param startIndex  the starting index of the iteration.
     * @param endIndex    the ending index of the iteration.
     * @param forumID     the forumID the threads are a part of.
     * @param factory     a ForumFactory to load data from.
     */
    public QObjectBlockIterator(QObjectDb qObject, Object[] block, String query,
                                String groupName,
                                int startIndex, int endIndex) {
        this.qObject = qObject;
        this.infoBlock = block;
        this.blockID = startIndex / qObject.getTable().getBlockSize();
        this.blockStart = blockID * qObject.getTable().getBlockSize();
        this.query = query;
        this.currentIndex = startIndex - 1;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.groupName = groupName;
    }

    public void remove() {
    }

    public boolean hasNext() {
        // If we are at the end of the list there are no more elements.
        if (currentIndex == endIndex) {
            return false;
        }
        // Otherwise, see if nextElement is null. If so, try to load the next
        // element to make sure it exists. If nextElement isn't null, that
        // means we've already checked to make sure it exists.
        if (nextElement == null) {
            nextElement = getNextElement();
            // If getting the next element failed, return false.
            if (nextElement == null) {
                return false;
            }
        }
        return true;
    }

    public boolean hasPrevious() {
        // If we are at the start of the list there are no previous elements.
        if (currentIndex == startIndex) {
            return false;
        }
        // Otherwise, see if previousElement is null. If so, try to load the
        // previous element to make sure it exists.
        if (previousElement == null) {
            previousElement = getPreviousElement();
            // If getting the previous element failed, return false.
            if (previousElement == null) {
                return false;
            }
        }
        return true;
    }

    public Object next() throws java.util.NoSuchElementException {
        Object element = null;
        if (nextElement != null) {
            element = nextElement;
            this.nextElement = null;
        } else {
            element = getNextElement();
            if (element == null) {
                throw new java.util.NoSuchElementException();
            }
        }
        // logger.info("next:" + element);
        return element;
    }

    public Object previous() {
        Object element = null;
        if (previousElement != null) {
            element = previousElement;
            previousElement = null;
        } else {
            element = getPreviousElement();
            if (element == null) {
                throw new java.util.NoSuchElementException();
            }
        }
        return element;
    }

    /**
     * Returns the next element, or null if there are no more
     * elements to return.
     *
     * @return the next element.
     */
    private Object getNextElement() {
        previousElement = null;
        Object element = null;
        if (endIndex != Integer.MAX_VALUE) { // ?????????max_value?????????????????????????????????CPU 100%
            while (currentIndex + 1 < endIndex && element == null) {
                currentIndex++;
                element = getElement(currentIndex);
                // logger.info("getNextElement:currentIndex=" + currentIndex);
            }
        } else { // ??????next???????????????????????????
            if (currentIndex + 1 < endIndex) {
                currentIndex++;
                element = getElement(currentIndex);
            }
        }
        // logger.info("getNextElement:" + element + " currentIndex=" + currentIndex);
        return element;
    }

    /**
     * Returns the previous element, or null if there are no more elements
     * to return.
     *
     * @return the previous element.
     */
    private Object getPreviousElement() {
        nextElement = null;

        Object element = null;
        while (currentIndex >= startIndex && element == null) {
            currentIndex--;
            element = getElement(currentIndex);
        }
        return element;
    }

    /**
     * @param index int ????????????????????????
     * @return Object
     */
    private Object getElement(int index) {
        // logger.info("getElement index=" + index);

        if (index < 0) {
            return null;
        }
        // See if element isn't in the current info block
        // ??????endIndex??????????????????block?????????????????????
        if (index < blockStart ||
                index >= blockStart + qObject.getTable().getBlockSize()) {
            // Then load up the appropriate block
            this.infoBlock = qObject.getQObjectBlock(query, groupName, index);
            this.blockID = index / qObject.getTable().getBlockSize();
            this.blockStart = blockID * qObject.getTable().getBlockSize();
        }
        Object element = null;
        // Compute the relative index of the element, which is the index in the
        // current thread block.
        int relativeIndex = index % qObject.getTable().getBlockSize();
        // Make sure index isn't too large
        if (relativeIndex < infoBlock.length) {
            element = qObject.getQObjectDb(infoBlock[relativeIndex]);
            // logger.info("getElement:infoBlock=" + infoBlock[relativeIndex]);
        }
        return element;
    }

    /**
     * ?????????????????????????????????????????????????????????????????????????????????
     */
    public void beforeFirst() {
        currentIndex = startIndex - 1;
        previousElement = null;
        nextElement = null;
    }
}
