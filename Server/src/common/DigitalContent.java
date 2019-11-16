
package common;
import java.io.Serializable;
import java.util.Objects;

public class DigitalContent implements Serializable {

	private String key;
	private String title;
	private String description;
	private String password;
	private byte[] content;

	public DigitalContent(byte[] content, String key, String title, String description, String password) {
		this.content = content;
		this.key = key;
		this.title = title;
		this.description = description;
		this.password = password;
	}

	public DigitalContent(byte[] content, String key, String title, String description) {
		this.content = content;
		this.key = key;
		this.title = title;
		this.description = description;
		this.password = null;
	}

	public boolean isPasswordCorrect(String[] password) {
		return Objects.equals(this.password, password);
	}

	public String getTitle(){
		return this.title;
	}

	public byte[] getContent(){ return this.content;}

	public String getDescription(){ return this.description; }

	public String getKey(){ return this.key; }

}
