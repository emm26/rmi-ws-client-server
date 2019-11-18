
package common;
import java.io.Serializable;
import java.util.Objects;

public class DigitalContent implements Serializable {

	private int key;
	private String title;
	private String description;
	private String password;

	public DigitalContent(int key, String title, String description, String password) {
		this.key = key;
		this.title = title;
		this.description = description;
		this.password = password;
	}


	public boolean isPasswordCorrect(String[] password) {
		return Objects.equals(this.password, password);
	}

	public String getTitle() {
		return this.title;
	}

	public String getDescription() {
		return this.description;
	}

	public int getKey() {
		return this.key;
	}

	public String getPassword() {
		return this.password;
	}

	public String toString(){
		return ("-------------------------------------------------" +
			   "\n\t KEY: " + key +
			   "\n\t TITLE: " + title +
			   "\n\t DESCRIPTION: " + description +
			   "\n-------------------------------------------------");
	}
}
