package com.vispect.android.vispect_g2_app.bean;


/**
 * 用户信息
 *
 * Created by xu on 2015/12/26.
 */
public class UserInfo {

	private int id;
	private String avatar;
	private String device_id;
	private String car_id;
	private String phone;
	private String name;
	private String email;
	private int sex;   //0:man   1: woman

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getSex() {
		return sex;
	}

	public void setSex(int sex) {
		this.sex = sex;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}


	public String getDevice_id() {
		return device_id;
	}

	public void setDevice_id(String device_id) {
		this.device_id = device_id;
	}

	public String getCar_id() {
		return car_id;
	}

	public void setCar_id(String car_id) {
		this.car_id = car_id;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	@Override
	public String toString() {
		return "UserInfo{" +
				"id=" + id +
				", avatar='" + avatar + '\'' +
				", device_id='" + device_id + '\'' +
				", car_id='" + car_id + '\'' +
				", phone='" + phone + '\'' +
				", name='" + name + '\'' +
				", email='" + email + '\'' +
				", sex=" + sex +
				'}';
	}
}

