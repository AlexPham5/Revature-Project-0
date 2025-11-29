package com.revature.project0.model;

public class Approval {
    private int id;
    private int expense_id;
    private String status;
    private int reviewer;
    private String comment;
    private String review_date;

    public Approval(){
    }

    public Approval(String status, int reviewer, String comment, String review_date) {
        this.status = status;
        this.reviewer = reviewer;
        this.comment = comment;
        this.review_date = review_date;
    }

    public Approval(int id, int expense_id, String status, int reviewer, String comment, String review_date) {
        this.id = id;
        this.expense_id = expense_id;
        this.status = status;
        this.reviewer = reviewer;
        this.comment = comment;
        this.review_date = review_date;
    }

    @Override
    public String toString() {
        return "Approval{" +
                "id=" + id +
                ", expense_id=" + expense_id +
                ", status='" + status + '\'' +
                ", reviewer=" + reviewer +
                ", comment='" + comment + '\'' +
                ", review_date='" + review_date + '\'' +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getExpense_id() {
        return expense_id;
    }

    public void setExpense_id(int expense_id) {
        this.expense_id = expense_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getReviewer() {
        return reviewer;
    }

    public void setReviewer(int reviewer) {
        this.reviewer = reviewer;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getReview_date() {
        return review_date;
    }

    public void setReview_date(String review_date) {
        this.review_date = review_date;
    }
}
