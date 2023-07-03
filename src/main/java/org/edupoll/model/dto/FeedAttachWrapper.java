package org.edupoll.model.dto;

import org.edupoll.model.entity.FeedAttach;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FeedAttachWrapper {
	public FeedAttachWrapper(FeedAttach t) {
		this.type = t.getType();
		this.mediaUrl = t.getMediaUrl();
	}

	private Long id;
	
	private String type;
	
	private String mediaUrl;
}
