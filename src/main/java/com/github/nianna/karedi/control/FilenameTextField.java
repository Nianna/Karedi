package main.java.com.github.nianna.karedi.control;

import main.java.com.github.nianna.karedi.util.ForbiddenCharacterRegex;

public class FilenameTextField extends RestrictedTextField {
	public FilenameTextField() {
		super(ForbiddenCharacterRegex.FOR_FILENAME);
	}
}
