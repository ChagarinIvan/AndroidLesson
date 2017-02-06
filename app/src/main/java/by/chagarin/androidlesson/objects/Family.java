package by.chagarin.androidlesson.objects;


import java.util.ArrayList;
import java.util.List;

public class Family {
    public String name;
    public String password;
    public List<User> users = new ArrayList<>();

    public Family() {
    }

    public Family(String name, String password, User user) {
        this.name = name;
        this.password = password;
        this.users.add(user);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<User> getUsers() {
        return users;
    }

    public void addUsers(User user) {
        users.add(user);
    }
}
