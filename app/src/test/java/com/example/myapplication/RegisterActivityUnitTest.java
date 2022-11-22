package com.example.myapplication;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class RegisterActivityUnitTest
{
    /* mockup of functionality */
    class User
    {
      String email, password, firstName, lastName, address;
      User(String email, String password, String firstName, String lastName, String address)
      {
          this.email = email; this.password = password; this.firstName = firstName; this.lastName = lastName; this.address = address;
      }
    };
    private List<String> users;
    private Boolean registerUser(String email, String password, String firstName, String lastName, String address)
    {
        // Validating all the input strings
        if(firstName.isEmpty()) {
            return false;
        }

        if(lastName.isEmpty()) {
            return false;
        }

        if(email.isEmpty()) {
            return false;
        }

        if(password.isEmpty()) {
            return false;
        }

        if(address.isEmpty()) {
            return false;
        }
        String emailRegex = "^(.+)@(\\S+)$";
        if(!Pattern.compile(emailRegex).matcher(email).matches())
        {
            return false;
        }
        // Password should be greater than 6 characters
        if(password.length() < 6) {
            return false;
        }
        if(users.contains(email))
            return false;
        users.add(email);
        return true;
    }
    private Boolean registerUser(User u)
    {
        return registerUser(u.email, u.password, u.firstName, u.lastName, u.address);
    }

    @Before
    public void setup()
    {
        users = new ArrayList<String>(Arrays.asList("g@v.com", "m@r.com", "j@s.com"));
    }

    @Test
    public void Try_InsertGoodUser()
    {
        User newUser = new User("jorge@jj.com", "patrickstar123", "Jorge", "Bald", "123 Bald Ave.");
        assertEquals(true, registerUser(newUser));
        assertEquals(true, users.contains(newUser.email));
    }

    @Test
    public void Try_InsertBadUsers()
    {
        User newUser = new User("", "patrickstar123", "Jorge", "Bald", "123 Bald Ave.");
        assertEquals(false, registerUser(newUser));
        assertEquals(false, users.contains(newUser.email));

        User newUser0 = new User("jorge", "patrickstar123", "Jorge", "Bald", "123 Bald Ave.");
        assertEquals(false, registerUser(newUser0));
        assertEquals(false, users.contains(newUser0.email));

        User newUser1 = new User("jorge@jj.com", "patr", "Jorge", "Bald", "123 Bald Ave.");
        assertEquals(false, registerUser(newUser1));
        assertEquals(false, users.contains(newUser1.email));

        User newUser2 = new User("jorge@jj.com", "patrickstar123", "", "Bald", "123 Bald Ave.");
        assertEquals(false, registerUser(newUser2));
        assertEquals(false, users.contains(newUser2.email));

        User newUser3 = new User("jorge@jj.com", "patrickstar123", "Jorge", "", "123 Bald Ave.");
        assertEquals(false, registerUser(newUser3));
        assertEquals(false, users.contains(newUser3.email));

        User newUser4 = new User("jorge@jj.com", "patrickstar123", "Jorge", "Bald", "");
        assertEquals(false, registerUser(newUser4));
        assertEquals(false, users.contains(newUser4.email));
    }

    @Test
    public void Try_InsertDupeUser()
    {
        users.clear();
        User newUser = new User("jorge@jj.com", "patrickstar123", "Jorge", "Bald", "123 Bald Ave.");
        assertEquals(true, registerUser(newUser));
        assertEquals(true, users.contains(newUser.email));

        User newUser1 = new User("jorge@jj.com", "patrickstar345", "Jorge", "Bald", "123 Bald Ave.");
        assertEquals(false, registerUser(newUser1));
        assertEquals(true, users.contains(newUser1.email));
    }
}
