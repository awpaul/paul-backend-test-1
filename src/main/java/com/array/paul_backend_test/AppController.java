package com.array.paul_backend_test;

import com.array.paul_backend_test.db.User;
import com.array.paul_backend_test.db.UserRepository;
import com.array.paul_backend_test.rest.BackendAuthProvider;
import com.array.paul_backend_test.rest.JsonResponse;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

@RestController
public class AppController {
    @Autowired
    BackendAuthProvider backendAuthProvider;
    @Autowired
    private UserRepository userRepo;


    @PostMapping("/rest/users/register")
    public JsonResponse registerUser(@RequestBody User user) {
        JsonResponse response = new JsonResponse();
        String password = new String(Base64.decodeBase64(user.getPassword().getBytes()));
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(password);
        user.setPassword(encodedPassword);
        userRepo.save(user);
        response.setSuccess(true);
        return response;
    }

    @PostMapping("/rest/user/login")
    public JsonResponse login(@RequestBody User user) {
        JsonResponse response = new JsonResponse();
        String decodedPassword = new String(Base64.decodeBase64(user.getPassword().getBytes()));
        User dbUser = userRepo.findByEmail(user.getEmail());
        if (dbUser!=null) {
            if (BCrypt.checkpw(decodedPassword, dbUser.getPassword())) {
                response.setSuccess(true);
                response.addData("creds",dbUser.getPassword());
                dbUser.setPassword("");
                response.addData("user",dbUser);
            } else {
                response.setError("Incorrect email or password!");
            }
        } else {
            response.setError("Incorrect email or password!");
        }
        return response;
    }

    @GetMapping("/rest/allUsers")
    public JsonResponse getAllUsers(HttpServletRequest request) {
        JsonResponse response = new JsonResponse();
        List<User> users = userRepo.findAll();
        //remove all passwords being set to frontend
        users.forEach((u)->u.setPassword(""));
        if (users!=null) {
            response.setSuccess(true);
            response.addData("users",users);
        }
        return response;
    }
    @GetMapping("/rest/getUserById/{id}")
    public JsonResponse getUserById(HttpServletRequest request,@PathVariable("id") Long id) {
        JsonResponse response = new JsonResponse();
        if (authenticate(request)) {
            Optional<User> dbUser = userRepo.findById(id);
            if (dbUser.isPresent()) {
                User user = dbUser.get();
                user.setPassword("");
                response.setSuccess(true);
                response.addData("user",user);
            }
        } else {
            response.setError("Unauthorized!");
        }
        return response;
    }

    @PostMapping("/rest/updateUser/{id}")
    public JsonResponse updateUser(HttpServletRequest request,@PathVariable("id") Long id, @RequestBody User user) {
        JsonResponse response = new JsonResponse();
        if (authenticate(request)) {
            Optional<User> dbUser = userRepo.findById(id);
            if (dbUser.isPresent()) {
                User userToUpdate = dbUser.get();
                if (user.getPassword().length()>1) {
                    //decode and recode password
                    String decodedPassword = new String(Base64.decodeBase64(user.getPassword().getBytes()));
                    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
                    userToUpdate.setPassword(passwordEncoder.encode(decodedPassword));
                }
                userToUpdate.setFirstName(user.getFirstName());
                userToUpdate.setLastName(user.getLastName());
                userToUpdate.setEmail(user.getEmail());
                userRepo.save(userToUpdate);
                response.setSuccess(true);
                response.addData("user",userToUpdate);
            }
        } else {
            response.setError("Unauthorized!");
        }
        return response;
    }


    @DeleteMapping("/rest/deleteUser/{id}")
    public JsonResponse deleteUser(HttpServletRequest request,@PathVariable("id") Long id) {
        JsonResponse response = new JsonResponse();
        if (authenticate(request)) {
            userRepo.deleteById(id);
            response.addData("userId",id);
            if (!userRepo.findById(id).isPresent()) {
                response.setSuccess(true);
            }
        } else {
            response.setError("Unauthorized!");
        }
        return response;
    }

    public boolean authenticate(HttpServletRequest request) {
        return backendAuthProvider.authenticateUser(request.getHeader("app_uid"),request.getHeader("Authorization"));
    }


}
