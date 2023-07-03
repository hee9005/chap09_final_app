package org.edupoll.model.dto;

import java.util.List;

import org.edupoll.model.entity.Feed;

import lombok.Data;


@Data

public class FeedWrapper {
	public FeedWrapper(Feed t) {
		this.id = t.getId();
		this.description  = t.getDescription();
		this.witer = new UserWrapper(t.getWiter());
		this.viewCount = t.getViewCount();
		
		this.feedAttachs = t.getAttaches().stream().map(e -> new FeedAttachWrapper(e)).toList();
	}

	private Long id;
	
	private UserWrapper witer;
	
	private String description;
	
	private Long viewCount;
	
	private List<FeedAttachWrapper> feedAttachs;
}
