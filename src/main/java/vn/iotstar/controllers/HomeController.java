package vn.iotstar.controllers;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class HomeController {


	@GetMapping("/home2")
	public String index(Model model, HttpSession session)
	{
		model.addAttribute("user", session.getAttribute("user0") );
		return "home";
	}

	@GetMapping("/hieu")
	public String haha()
	{
		return "test";
	}

}