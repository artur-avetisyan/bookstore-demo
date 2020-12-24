package dev.avetisyan.egs.bookstore.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;

@ApiIgnore
@RestController
public class HomeController {

    @GetMapping
    // In production I'll use this to check if the user is logged in
    // and perform a silent login or request a new login.
    // Here I just redirect to swagger-ui for the convenience
    public void home(HttpServletResponse httpServletResponse) {
        httpServletResponse.setHeader("Location", "/swagger-ui/index.html");
        httpServletResponse.setStatus(302);
    }

}
