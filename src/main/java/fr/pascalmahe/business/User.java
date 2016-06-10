package fr.pascalmahe.business;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;


/**
 * Class representing a budget
 * @author Pascal
 */
@Entity
@Table(name="UserSB")
public class User implements Serializable {

	private static final long serialVersionUID = -8362985702527336043L;

	@Id
	@GeneratedValue
	private Integer id;
	
	private String login;
	
	private String password;
	
	private Boolean dailyNotification;
	
	public User() {}

	public User(Integer id, String login, String password, Boolean dailyNotification) {
		super();
		this.id = id;
		this.login = login;
		this.password = password;
		this.dailyNotification = dailyNotification;
	}

	public User(String login, String password, Boolean dailyNotification) {
		super();
		this.login = login;
		this.password = password;
		this.dailyNotification = dailyNotification;
	}

	@Override
	public String toString() {
		return "User [id=" + id + 
				", login=" + login + 
				", password=" + password + 
				", dailyNotification=" + dailyNotification + "]";
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Boolean getDailyNotification() {
		return dailyNotification;
	}

	public void setDailyNotification(Boolean dailyNotification) {
		this.dailyNotification = dailyNotification;
	}
	
}
