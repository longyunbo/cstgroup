package com.drag.cstgroup.user.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.drag.cstgroup.user.service.ScoreService;
import com.drag.cstgroup.user.vo.ScoreRecordVo;


@RestController
@RequestMapping(value = "/cstgroup/score")
public class ScoreController {
	
	private final static Logger log = LoggerFactory.getLogger(ScoreController.class);

	@Autowired
	private ScoreService scoreService;
	
	
	/**
	 * 查询职业信息
	 * @return
	 */
	@RequestMapping(value = "/scorerecord", method = {RequestMethod.POST,RequestMethod.GET})
	public @ResponseBody ResponseEntity<List<ScoreRecordVo>> queryScore(@RequestParam String openid) {
		List<ScoreRecordVo> br = scoreService.queryScore(openid);
		return new ResponseEntity<List<ScoreRecordVo>>(br, HttpStatus.OK);
	}
	
	
	
}
