package com.abechat.server.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import com.abechat.server.service.UserService;
import com.abechat.server.model.Request;
import com.abechat.server.model.CreateUserForm;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;

@Controller
@RequestMapping("/createUser")
public class CreateUserController {
    private static final Logger LOGGER = LoggerFactory.getLogger(CreateUserController.class);
    private final UserService userService;

    public CreateUserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(path = {"", "/"})
    public String createUserView(Model model) {
        model.addAttribute("createUserForm", new CreateUserForm());
        return "createUser"; 
    }

@PostMapping(path = {"", "/"}, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String createUser(@Valid @ModelAttribute("createUserForm") CreateUserForm createUserForm, 
                            BindingResult bindingResult,  
                            Model model) {
        
        if (bindingResult.hasErrors()) {
            LOGGER.info("Validation errors: {}", bindingResult.getAllErrors());
            return "createUser"; // Return to form with errors
        }

        //Check if passwords match
        if (!createUserForm.getPassword().equals(createUserForm.getConfirmPassword())) {
            bindingResult.rejectValue("confirmPassword", "error.confirmPassword", "Passwords do not match");
            LOGGER.info("Error: User input unmatched passwords");
            return "createUser";
        }

        try {
            // Takes the CreateUserForm dats and converts to a NewUser request object
            Request.NewUser newUser = new Request.NewUser(
                createUserForm.getUsername(),
                createUserForm.getPassword()
            );
            userService.createNew(newUser); 
        } catch (Exception e) {
            LOGGER.error("Error creating user: {}", e.getMessage());
            model.addAttribute("errorMessage", "Error creating user: " + e.getMessage());
            return "createUser";
        }
        LOGGER.info("Creating user account with username: " + createUserForm.getUsername());

        return "redirect:/";
    }
}
