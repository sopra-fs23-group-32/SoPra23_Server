package ch.uzh.ifi.hase.soprafs23.entity;

import ch.uzh.ifi.hase.soprafs23.constant.UserStatus;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * Internal User Representation
 * This class composes the internal representation of the user and defines how
 * the user is stored in the database.
 * Every variable will be mapped into a database field with the @Column
 * annotation
 * - nullable = false -> this cannot be left empty
 * - unique = true -> this value must be unqiue across the database -> composes
 * the primary key
 */
@Entity
@Table(name = "USER")
public class User implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long userId;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private UserStatus status;

    @Column(nullable = false)
    private Date createDay;

    @Column()
    private Date birthDay;

    public Long getUserId() {return userId;}
    public void setUserId(Long userId) {this.userId = userId;}

    public String getUsername() {return username;}
    public void setUsername(String username) {this.username = username;}

    public String getPassword() {return password;}
    public void setPassword(String pwd) {this.password = pwd;}

    public UserStatus getStatus() {return status;}
    public void setStatus(UserStatus status) {this.status = status;}

    public Date getCreateDay() {return createDay;}
    public void setCreateDay(Date createDay) {this.createDay = createDay;}

    public Date getBirthDay() {return birthDay;}
    public void setBirthDay(Date birthDay) {this.birthDay = birthDay;}
}
