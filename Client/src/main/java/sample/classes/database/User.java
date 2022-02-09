package sample.classes.database;

public class User
{
    private String nickname;
    private String email;
    private String phone;
    private String password;
    private String passwordRepeated;

    public User(String nickname, String email, String phone, String password, String passwordRepeated)
    {
        this.nickname = nickname;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.passwordRepeated = passwordRepeated;
    }

    public String getNickname()
    {
        return nickname;
    }

    public void setNickname(String nickname)
    {
        this.nickname = nickname;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getPhone()
    {
        return phone;
    }

    public void setPhone(String phone)
    {
        this.phone = phone;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public String getPasswordRepeated()
    {
        return passwordRepeated;
    }

    public void setPasswordRepeated(String passwordRepeated)
    {
        this.passwordRepeated = passwordRepeated;
    }
}
