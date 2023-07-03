package org.edupoll.service;

import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import org.edupoll.exception.NotExistUserException;
import org.edupoll.model.dto.FeedAttachWrapper;
import org.edupoll.model.dto.FeedWrapper;
import org.edupoll.model.dto.UserWrapper;
import org.edupoll.model.dto.request.CreateFeedRequest;
import org.edupoll.model.dto.response.FeedListResponse;
import org.edupoll.model.entity.Feed;
import org.edupoll.model.entity.FeedAttach;
import org.edupoll.model.entity.User;
import org.edupoll.repository.FeedAttachRepository;
import org.edupoll.repository.FeedRepository;
import org.edupoll.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class FeedService {
	@Value("${upload.basedir}")
	String baseDir;
	@Value("${upload.server}")
	String uploadServer;
	@Autowired
	FeedRepository feedRepository;
	@Autowired
	UserRepository userRepository;
	@Autowired
	FeedAttachRepository feedAttachRepository;
	
	@Transactional
	public boolean newFeedCreate(String principal, CreateFeedRequest cfr)
			throws NotExistUserException, IllegalStateException, IOException {
		var user = userRepository.findByEmail(principal).orElseThrow(() -> new NotExistUserException());
		var feed = new Feed();
		feed.setWiter(user);
		feed.setDescription(cfr.getDescription());
		feed.setViewCount(0L);
		var saved = feedRepository.save(feed);
		log.info("attaches is exist? {}", cfr.getArraches() != null);
		if (cfr.getArraches() != null) {
			String emailEncoded = new String(Base64.getEncoder().encode(principal.getBytes()));
			for (MultipartFile multi : cfr.getArraches()) { // 하나씩 반복문 돌면서
				FeedAttach feedAttached = new FeedAttach();
				// 어디다가 file 옮겨둘껀지 File 객체를 정의하고
				File saveDir = new File(baseDir + "/feed/" + saved.getId());
				saveDir.mkdirs();
				String filename = System.currentTimeMillis()
						+ multi.getOriginalFilename().substring(multi.getOriginalFilename().lastIndexOf("."));
				File dest = new File(saveDir, filename);
				multi.transferTo(dest); // 옮기는거 진행
				// 업로드가 끝나면 DB에 기록
				feedAttached.setType(multi.getContentType());
				feedAttached.setMediaUrl(uploadServer + "/resource/feed/" + saved.getId() + "/" + filename); // 업로드를 한
																												// 곳이
				// 어디냐에 따라서 // 결정되는 값
				feedAttached.setFeed(saved);
				feedAttachRepository.save(feedAttached);
			}
		}
		return true;
	}
	
	@Transactional
	public Long totalCount() {
		Long total =feedRepository.count();
		return total;
	}
	@Transactional
	public List<FeedWrapper> allItems(int page) {
		List<Feed> entityList = feedRepository.findAllByOrderByIdDesc(PageRequest.of(page-1, 10, Sort.by("id").descending()));
		
		return entityList.stream().map(t -> new FeedWrapper(t)).toList();
		
		
	}

}
