package com.demo.bank.model.response;

import java.util.List;

public class GetAllTransactionPageResponse {
    private Integer totalPage;
    private Integer totalItem;
    private Integer currentPage;
    private List<GetAllTransactionContentsResponse> contents;

    public Integer getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(Integer totalPage) {
        this.totalPage = totalPage;
    }

    public Integer getTotalItem() {
        return totalItem;
    }

    public void setTotalItem(Integer totalItem) {
        this.totalItem = totalItem;
    }

    public Integer getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    public List<GetAllTransactionContentsResponse> getContents() {
        return contents;
    }

    public void setContents(List<GetAllTransactionContentsResponse> contents) {
        this.contents = contents;
    }

    @Override
    public String toString() {
        return "GetAllTransactionPageResponse{" +
                "totalPage=" + totalPage +
                ", totalItem=" + totalItem +
                ", currentPage=" + currentPage +
                ", contents=" + contents +
                '}';
    }

}
