package com.mingchao.ycj.mining2.model;

public class SourceClass extends Source {
	private static final long serialVersionUID = 1902696091520318813L;
	private Integer forwardCount;
	private Integer commentCount;
	private Integer likeCount;
	private Integer forwardClass;
	private Integer commentClass;
	private Integer likeClass;

	public Integer getForwardCount() {
		return forwardCount;
	}

	public void setForwardCount(Integer forwardCount) {
		this.forwardCount = forwardCount;
	}

	public Integer getCommentCount() {
		return commentCount;
	}

	public void setCommentCount(Integer commentCount) {
		this.commentCount = commentCount;
	}

	public Integer getLikeCount() {
		return likeCount;
	}

	public void setLikeCount(Integer likeCount) {
		this.likeCount = likeCount;
	}

	public Integer getForwardClass() {
		return forwardClass;
	}

	public void setForwardClass(Integer forwardClass) {
		this.forwardClass = forwardClass;
	}

	public Integer getCommentClass() {
		return commentClass;
	}

	public void setCommentClass(Integer commentClass) {
		this.commentClass = commentClass;
	}

	public Integer getLikeClass() {
		return likeClass;
	}

	public void setLikeClass(Integer likeClass) {
		this.likeClass = likeClass;
	}
}