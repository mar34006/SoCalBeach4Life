package com.example.myapplication;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import android.util.Pair;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public class LoginLogoutUnitTest {
    class User
    {
        String email, password;
        User(String em, String ps)
        {
            this.email = em;
            this.password = ps;
        }
    }

    Map<String, Boolean> loggedIn;

    Boolean login(String email, String password)
    {
        if(email.isEmpty() || password.isEmpty())
            return false;
        if(password.length() < 6)
            return false;
        String emailRegex = "^(.+)@(\\S+)$";
        if(!Pattern.compile(emailRegex).matcher(email).matches())
        {
            return false;
        }
        Set<String> users = loggedIn.keySet();
        Boolean found = false;
        String key = null;
        for(String loginInfo: users)
        {
            // obviously more complicated in actuality, but this gives the gist of it
            String infos[] = loginInfo.split(" ", 2);
            if(infos[0].equals(email) && infos[1].equals(password))
            {
                found = true;
                key = loginInfo;
                break;
            }
        }
        if(!found)
        {
            return false;
        }
        loggedIn.put(key, true);
        return true;
    }

    Boolean logout(User u)
    {
        Set<String> users = loggedIn.keySet();
        Boolean found = false;
        String key = null;
        for(String loginInfo: users)
        {
            String infos[] = loginInfo.split(" ", 2);
            if(infos[0].equals(u.email) && infos[1].equals(u.password))
            {
                found = true;
                key = loginInfo;
                break;
            }
        }
        if(!found)
        {
            return false;
        }
        loggedIn.put(key, false);
        return true;
    }

    @Before
    public void setup()
    {
        loggedIn = new HashMap<>();
        loggedIn.put("g@v.com pat12345", false);
        loggedIn.put("m@r.com poko4345", false);
        loggedIn.put("j@s.com wowe3445", false);
    }

    @Test
    public void Try_GoodLogins()
    {
        assertEquals(true, login("g@v.com", "pat12345"));
        assertEquals(true, login("m@r.com", "poko4345"));
        // should handle logged in users fine
        assertEquals(true, login("j@s.com", "wowe3445"));
        assertEquals(true, login("j@s.com", "wowe3445"));
    }

    @Test
    public void Try_BadLogins()
    {
        assertEquals(false, login("", "pat12345"));
        assertEquals(false, login("g@v.com", ""));
        assertEquals(false, login("g@v.com", "pat12335"));
    }

    @Test
    public void Try_Logout()
    {
        // login first
        assertEquals(true, login("g@v.com", "pat12345"));
        assertEquals(true, login("m@r.com", "poko4345"));
        User g = new User("g@v.com", "pat12345");
        User m = new User("m@r.com", "poko4345");
        User j = new User("j@s.com", "wowe3445");
        assertEquals(true, logout(g));
        assertEquals(true, logout(m));
        assertEquals(true, logout(j));
        // a second log out is ok, not really a problem
        assertEquals(true, logout(g));
    }
}
