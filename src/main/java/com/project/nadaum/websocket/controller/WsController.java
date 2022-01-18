package com.project.nadaum.websocket.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.project.nadaum.member.model.vo.Member;
import com.project.nadaum.websocket.model.service.WebsocketService;

import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class WsController {
	
	@Autowired
	private WebsocketService websocketService;

	@GetMapping("/member/mypage/chat.do")
	public void chat(Model model) {
		log.debug("principal = {}",SecurityContextHolder.getContext().getAuthentication().getPrincipal());
		Member user = (Member)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		log.debug("=================================");
		log.debug("user = {}", user);
		log.debug("@WsController, GetChat / Username : {}", user.getName());
		
		model.addAttribute("userid", user.getId());
	}
	
	@GetMapping("/websocket/wsCountAlarm.do")
	public ResponseEntity<?> wsCountAlarm(){
		
		return ResponseEntity.ok().build();
	}
	
}
