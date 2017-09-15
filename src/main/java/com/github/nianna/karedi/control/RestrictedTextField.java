package main.java.com.github.nianna.karedi.control;

import javafx.scene.control.TextField;

public class RestrictedTextField extends TextField {
	private String forbiddenCharacterRegex;

	public RestrictedTextField() {
	}

	public RestrictedTextField(String forbiddenCharacterRegex) {
		setForbiddenCharacterRegex(forbiddenCharacterRegex);
	}

	@Override
	public void replaceText(int start, int end, String text) {
		super.replaceText(start, end, filter(text));
	}

	@Override
	public void replaceSelection(String text) {
		super.replaceSelection(filter(text));
	}

	public String getForbiddenCharacterRegex() {
		return forbiddenCharacterRegex;
	}

	public String filter(String string) {
		if (forbiddenCharacterRegex != null) {
			return string.replaceAll(forbiddenCharacterRegex, "");
		}
		return string;
	}

	public void setForbiddenCharacterRegex(String forbiddenCharacterRegex) {
		this.forbiddenCharacterRegex = forbiddenCharacterRegex;
	}
}
