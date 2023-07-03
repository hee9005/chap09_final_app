package org.edupoll.controller;

import java.io.IOException;
import java.util.List;

import org.edupoll.exception.NotExistUserException;
import org.edupoll.model.dto.FeedWrapper;
import org.edupoll.model.dto.request.CreateFeedRequest;
import org.edupoll.model.dto.response.FeedListResponse;
import org.edupoll.model.entity.Feed;
import org.edupoll.model.entity.FeedAttach;
import org.edupoll.service.FeedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/feed")
@Slf4j
@CrossOrigin
public class FeedController {
	
	@Autowired
	FeedService feedService;
	//전체 글 목록 제공해주는 api
	@GetMapping("/readAll")
	public ResponseEntity<FeedListResponse> readAllFeedHandle(@RequestParam(defaultValue = "1") int page) {
		Long total = feedService.totalCount();
		List<FeedWrapper> feeds = feedService.allItems(page);
		FeedListResponse response = new FeedListResponse(total, feeds);
		
		return new ResponseEntity<>(response,HttpStatus.OK);
	}
	
	// 특정 글 제공해주는 api
	public ResponseEntity<?> readSpecificFeedHandle(@PathVariable String id) {
		return null;
	}
	
	// 신규글 등록해주는 api
	@PostMapping("/new-create")
	public ResponseEntity<?> createNewFeedHandle(@AuthenticationPrincipal String principal,CreateFeedRequest cfr ) throws NotExistUserException, IllegalStateException, IOException {
		log.info("feed {}",cfr);
		boolean ref =feedService.newFeedCreate(principal,cfr);
		return new ResponseEntity<>(HttpStatus.CREATED);
	}
	
	// 특정글 삭제해주는 api
	public ResponseEntity<?> createdeleteSpecificFeedHandle(@AuthenticationPrincipal String principal,Feed feed) {
		return null;
	}

}
