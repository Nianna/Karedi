package main.java.com.github.nianna.karedi.util;

import java.util.Comparator;
import java.util.Optional;

import org.controlsfx.validation.Severity;
import org.controlsfx.validation.ValidationMessage;
import org.controlsfx.validation.ValidationResult;

public final class ValidationUtils {
	private ValidationUtils() {
	}

	public static Comparator<Severity> severityComparator = (o1, o2) -> {
		return Integer.compare(severity(o1), severity(o2));
	};

	public static Comparator<ValidationMessage> messageComparator = (o1, o2) -> {
		return severityComparator.compare(o1.getSeverity(), o2.getSeverity());
	};

	public static Optional<ValidationMessage> getHighestPriorityMessage(ValidationResult result) {
		return result.getMessages().stream().max(messageComparator);
	}

	private static int severity(Severity severity) {
		switch (severity) {
		case ERROR:
			return 100;
		case WARNING:
			return 50;
		default:
			return 0;
		}
	}

}
