package com.raii.devops.controller;

import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/hello")
	public String hello() {
		return "Hello Java DevOps!";
	}

}