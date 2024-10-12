package com.github.nianna.karedi.control;

import javafx.util.Callback;
import org.controlsfx.control.textfield.AutoCompletionBinding;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class MultiValuedSuggestionsProvider<T> implements Callback<AutoCompletionBinding.ISuggestionRequest, Collection<String>> {

    private static final String SEPARATOR = ",";

    private static final String SPACED_SEPARATOR = SEPARATOR + " ";

    private final Collection<String> suggestions;

    public MultiValuedSuggestionsProvider(Collection<T> suggestions) {
        this.suggestions = suggestions.stream().map(Objects::toString).toList();
    }

    @Override
    public Collection<String> call(AutoCompletionBinding.ISuggestionRequest request) {
        String input = request.getUserText().strip();

        List<String> inputtedValues = Arrays.stream(input.split(SEPARATOR))
                .map(String::strip)
                .toList();

        String previousInput = String.join(SPACED_SEPARATOR, inputtedValues.subList(0, inputtedValues.size() - 1));
        String currentInput = inputtedValues.get(inputtedValues.size() - 1);

        return suggestions.stream()
                .filter(suggestion -> !inputtedValues.contains(suggestion))
                .filter(suggestion -> suggestion.toLowerCase().contains(currentInput.toLowerCase()))
                .map(suggestion -> previousInput.isEmpty() ? suggestion : previousInput + SPACED_SEPARATOR + suggestion)
                .toList();
    }

}
