package com.foodproject.api.response;

import java.util.List;

public class BasePageResponse<T> {
    private Long totalElements;
    private int totalPages;
    private int size;
    private List<T> content;
    private int number;
    private Sort sort;
    private boolean first;
    private boolean last;
    private int numberOfElements;
    private PageableObject pageable;
    private boolean empty;

    public class Sort {
        private boolean empty;
        private boolean sorted;
        private boolean unsorted;

        public Sort() {
        }

        public Sort(boolean empty, boolean sorted, boolean unsorted) {
            this.empty = empty;
            this.sorted = sorted;
            this.unsorted = unsorted;
        }

        public boolean isEmpty() {
            return empty;
        }

        public void setEmpty(boolean empty) {
            this.empty = empty;
        }

        public boolean isSorted() {
            return sorted;
        }

        public void setSorted(boolean sorted) {
            this.sorted = sorted;
        }

        public boolean isUnsorted() {
            return unsorted;
        }

        public void setUnsorted(boolean unsorted) {
            this.unsorted = unsorted;
        }
    }

    public class PageableObject {
        private Long offset;
        private Sort sort;
        private int pageNumber;
        private int pageSize;
        private boolean paged;
        private boolean unpaged;

        public PageableObject() {
        }

        public PageableObject(Long offset, Sort sort, int pageNumber, int pageSize, boolean paged, boolean unpaged) {
            this.offset = offset;
            this.sort = sort;
            this.pageNumber = pageNumber;
            this.pageSize = pageSize;
            this.paged = paged;
            this.unpaged = unpaged;
        }

        public Long getOffset() {
            return offset;
        }

        public void setOffset(Long offset) {
            this.offset = offset;
        }

        public Sort getSort() {
            return sort;
        }

        public void setSort(Sort sort) {
            this.sort = sort;
        }

        public int getPageNumber() {
            return pageNumber;
        }

        public void setPageNumber(int pageNumber) {
            this.pageNumber = pageNumber;
        }

        public int getPageSize() {
            return pageSize;
        }

        public void setPageSize(int pageSize) {
            this.pageSize = pageSize;
        }

        public boolean isPaged() {
            return paged;
        }

        public void setPaged(boolean paged) {
            this.paged = paged;
        }

        public boolean isUnpaged() {
            return unpaged;
        }

        public void setUnpaged(boolean unpaged) {
            this.unpaged = unpaged;
        }
    }

    public Long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(Long totalElements) {
        this.totalElements = totalElements;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public List<T> getContent() {
        return content;
    }

    public void setContent(List<T> content) {
        this.content = content;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public Sort getSort() {
        return sort;
    }

    public void setSort(Sort sort) {
        this.sort = sort;
    }

    public boolean isFirst() {
        return first;
    }

    public void setFirst(boolean first) {
        this.first = first;
    }

    public boolean isLast() {
        return last;
    }

    public void setLast(boolean last) {
        this.last = last;
    }

    public int getNumberOfElements() {
        return numberOfElements;
    }

    public void setNumberOfElements(int numberOfElements) {
        this.numberOfElements = numberOfElements;
    }

    public PageableObject getPageable() {
        return pageable;
    }

    public void setPageable(PageableObject pageable) {
        this.pageable = pageable;
    }

    public boolean isEmpty() {
        return empty;
    }

    public void setEmpty(boolean empty) {
        this.empty = empty;
    }
}
