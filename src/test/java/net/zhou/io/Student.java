package net.zhou.io;

import net.zhou.protobuf.ProtobufField;

/**
 * 
 * @author zhou
 * 
 */
public class Student {

	@ProtobufField(index = 1)
	private long id;

	@ProtobufField(index = 2)
	private String name;

	@ProtobufField(index = 3)
	private short rank;
	@ProtobufField(index = 5)
	private int age;
	@ProtobufField(index = 6)
	private double height;
	@ProtobufField(index = 4)
	private float weight;
	@ProtobufField(index = 7)
	private boolean male;
	@ProtobufField(index = 11)
	private String tag;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public double getHeight() {
		return height;
	}

	public void setHeight(double height) {
		this.height = height;
	}

	public float getWeight() {
		return weight;
	}

	public void setWeight(float weight) {
		this.weight = weight;
	}

	public short getRank() {
		return rank;
	}

	public void setRank(short rank) {
		this.rank = rank;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + age;
		long temp;
		temp = Double.doubleToLongBits(height);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + rank;
		result = prime * result + Float.floatToIntBits(weight);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Student other = (Student) obj;
		if (age != other.age)
			return false;
		if (Double.doubleToLongBits(height) != Double.doubleToLongBits(other.height))
			return false;
		if (id != other.id)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (rank != other.rank)
			return false;
		if (Float.floatToIntBits(weight) != Float.floatToIntBits(other.weight))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Student [id=" + id + ", name=" + name + ", rank=" + rank + ", age=" + age + ", height=" + height + ", weight=" + weight + "]";
	}

	public Student() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Student(long id, String name, short rank, int age, double height, float weight) {
		super();
		this.id = id;
		this.name = name;
		this.rank = rank;
		this.age = age;
		this.height = height;
		this.weight = weight;
	}

	public boolean isMale() {
		return male;
	}

	public void setMale(boolean male) {
		this.male = male;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

}
