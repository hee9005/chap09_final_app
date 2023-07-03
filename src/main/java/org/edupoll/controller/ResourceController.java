package org.edupoll.controller;

import java.net.MalformedURLException;

import org.edupoll.exception.NotExistUserException;
import org.edupoll.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class ResourceController {

	@Autowired
	UserService userService;

	// 특정경로로 왔을때 이미지를 보내주는API
	public ResponseEntity<?> getResourceHandle(HttpServletRequest request)
			throws MalformedURLException, NotExistUserException {

//		log.info("uri : {}",request.getRequestURI().toString());
//		log.info("url : {}",request.getRequestURL().toString());

		MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
		headers.add("content-type", "image/png");
		Resource resource = userService.loadResource(request.getRequestURL().toString());
		ResponseEntity<Resource> reponse = new ResponseEntity<Resource>(resource, headers, HttpStatus.OK);

		return reponse;
	}
}
