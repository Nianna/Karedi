package com.github.nianna.karedi.control;

import com.github.nianna.karedi.util.ForbiddenCharacterRegex;

public class FilenameTextField extends RestrictedTextField {
	public FilenameTextField() {
		super(ForbiddenCharacterRegex.FOR_FILENAME);
	}
}
