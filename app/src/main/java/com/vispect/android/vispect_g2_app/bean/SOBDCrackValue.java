package com.vispect.android.vispect_g2_app.bean;

/**
 * S款的OBD破解数据
 *
 * Created by xu on 2015/12/26.
 */
public class SOBDCrackValue {
	private int id;
	private int uid;
	private String did;
	private String time;
	private String brand;
	private String year;
	private String model;
	private String value;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getUid() {
		return uid;
	}
	public void setUid(int uid) {
		this.uid = uid;
	}
	public String getDid() {
		return did;
	}
	public void setDid(String did) {
		this.did = did;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getBrand() {
		return brand;
	}
	public void setBrand(String brand) {
		this.brand = brand;
	}
	public String getYear() {
		return year;
	}
	public void setYear(String year) {
		this.year = year;
	}
	public String getModel() {
		return model;
	}
	public void setModel(String model) {
		this.model = model;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "SOBDCrackValue{" +
				"id=" + id +
				", uid=" + uid +
				", did='" + did + '\'' +
				", time='" + time + '\'' +
				", brand='" + brand + '\'' +
				", year='" + year + '\'' +
				", model='" + model + '\'' +
				", value='" + value + '\'' +
				'}';
	}
}
