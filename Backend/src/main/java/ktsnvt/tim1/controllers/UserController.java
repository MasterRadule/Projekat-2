package ktsnvt.tim1.controllers;

import ktsnvt.tim1.DTOs.UserDTO;
import ktsnvt.tim1.exceptions.EntityNotFoundException;
import ktsnvt.tim1.exceptions.EntityNotValidException;
import ktsnvt.tim1.model.User;
import ktsnvt.tim1.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

@RestController
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserService userService;

	@PutMapping()
	@Secured({"ROLE_ADMIN","ROLE_USER"})
    public ResponseEntity<Object> editUser(@Valid @RequestBody UserDTO user){
		try{
			return new ResponseEntity<>(userService.editUser(user), HttpStatus.OK);
		}catch (EntityNotValidException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
    }

    @GetMapping()
    public ResponseEntity<Object> getUser(){
	    try{
	    	User u = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			return new ResponseEntity<>(u, HttpStatus.OK);
		}catch(ClassCastException e){
	    	return new ResponseEntity<>("No User Logged In!", HttpStatus.BAD_REQUEST);
		}

    }

}
