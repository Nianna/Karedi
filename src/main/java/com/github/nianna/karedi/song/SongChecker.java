package com.github.nianna.karedi.song;

import com.github.nianna.karedi.problem.DuetTagNotForDuetProblem;
import com.github.nianna.karedi.problem.InvalidMedleyBeatRangeProblem;
import com.github.nianna.karedi.problem.InvalidMedleyLengthProblem;
import com.github.nianna.karedi.problem.MandatoryTagMissingProblem;
import com.github.nianna.karedi.problem.MedleyMissingProblem;
import com.github.nianna.karedi.problem.NonZeroFirstBeatProblem;
import com.github.nianna.karedi.problem.NotesAfterEndProblem;
import com.github.nianna.karedi.problem.NotesBeforeStartProblem;
import com.github.nianna.karedi.problem.Problem;
import com.github.nianna.karedi.problem.Problem.Severity;
import com.github.nianna.karedi.problem.Problematic;
import com.github.nianna.karedi.problem.ProblemsCombiner;
import com.github.nianna.karedi.problem.TagForNonexistentTrackProblem;
import com.github.nianna.karedi.problem.TagValidationErrorProblem;
import com.github.nianna.karedi.problem.UnsupportedTagProblem;
import com.github.nianna.karedi.song.tag.FormatSpecification;
import com.github.nianna.karedi.song.tag.MultiplayerTags;
import com.github.nianna.karedi.song.tag.Tag;
import com.github.nianna.karedi.song.tag.TagKey;
import com.github.nianna.karedi.song.tag.TagValueValidators;
import com.github.nianna.karedi.util.Converter;
import com.github.nianna.karedi.util.ListenersUtils;
import com.github.nianna.karedi.util.MathUtils;
import com.github.nianna.karedi.util.ValidationUtils;
import javafx.collections.ObservableList;
import org.controlsfx.validation.ValidationResult;

import java.util.Comparator;
import java.util.Optional;

public class SongChecker implements Problematic {
	private Song song;
	private ProblemsCombiner combiner;
	private MedleyChecker medleyChecker = new MedleyChecker();

	public SongChecker(Song song) {
		this.song = song;
		combiner = new ProblemsCombiner(song.getTracks());
		song.getTracks().addListener(ListenersUtils.createListChangeListener(this::refreshTrack,
				this::refreshTrack, this::onTrackAdded, this::onTrackRemoved));
		song.getTags().addListener(ListenersUtils.createListChangeListener(ListenersUtils::pass,
				this::refreshTag, this::onTagAdded, this::onTagRemoved));

		song.getBeatMillisConverter().addListener(obs -> {
			checkStart();
			checkEnd();
			medleyChecker.check();
		});
		song.lowerXBoundProperty().addListener(obs -> checkStart());
		song.upperXBoundProperty().addListener(obs -> checkEnd());
	}

	private void onTrackRemoved(SongTrack track) {
		removeTrackProblems(track);
		if (song.getTrackCount() > 0) {
			refreshTrack(song.getTrack(0));
			revalidateAllMultiplayerTags();
		} else {
			// should never happen
		}
	}

	private void onTrackAdded(SongTrack track) {
		refreshTrack(track);
		revalidateAllMultiplayerTags();
	}

	private void removeTrackProblems(SongTrack track) {
		combiner.removeIf(problem -> {
			return problem.getElements().contains(track);
		});
	}

	private void removeTagProblems(Tag tag) {
		combiner.removeIf(problem -> {
			return problem.getElements().contains(tag.getKey());
		});
	}

	private void refreshTrack(SongTrack track) {
		removeTrackProblems(track);
		if (song.indexOf(track) == 0) {
			if (track.isValid() && track.getLowerXBound() != 0) {
				combiner.add(new NonZeroFirstBeatProblem(track));
			}
		}
	}

	@Override
	public ObservableList<Problem> getProblems() {
		return combiner.getProblems();
	}

	private void refreshTag(Tag tag) {
		removeTagProblems(tag);
		if (FormatSpecification.supports(song.getFormatSpecificationVersion(), tag.getKey())) {
			doValidateTag(tag);
		} else {
			combiner.add(new UnsupportedTagProblem(tag.getKey()));
		}
	}

	private void doValidateTag(Tag tag) {
		TagKey.optionalValueOf(tag.getKey()).ifPresent(tagKey -> {
			validateValue(tagKey, tag.getValue());
			performAdditionalValueValidation(tagKey);
			DuplicatedTagsConsistencyValidator.validate(song, tagKey).ifPresent(combiner::add);
		});
		validateTagKey(tag.getKey());
	}

	private void onTagRemoved(Tag tag) {
		removeTagProblems(tag);
		revalidateMandatoryTags();
	}

	private void onTagAdded(Tag tag) {
		refreshTag(tag);
		revalidateMandatoryTags();
	}

	private void validateTagKey(String tagKey) {
		if (MultiplayerTags.isANameTagKey(tagKey)) {
			MultiplayerTags.getTrackNumber(tagKey)
					.filter(trackIndex -> trackIndex >= song.getTrackCount())
					.ifPresent(ignored -> combiner.add(new TagForNonexistentTrackProblem(tagKey)));
		}

		if (isADuetSingerTagForExistingTrackInNotDuetSong(tagKey)) {
			combiner.add(new DuetTagNotForDuetProblem(tagKey));
		}

	}

	private boolean isADuetSingerTagForExistingTrackInNotDuetSong(String tagKey) {
		return TagKey.isKey(tagKey, TagKey.DUETSINGERP1) && song.getTrackCount() != 2
				|| TagKey.isKey(tagKey, TagKey.DUETSINGERP2) && song.getTrackCount() > 2;
	}

	private void performAdditionalValueValidation(TagKey key) {
		switch (key) {
			case MEDLEYSTARTBEAT, MEDLEYENDBEAT:
				medleyChecker.check();
				break;
			case START:
				checkStart();
				break;
			case END:
				checkEnd();
				break;
			case VERSION:
				revalidateAllTagsOnVersionChange();
				break;
			default:
		}
	}

	private void revalidateAllTagsOnVersionChange() {
		song.getTags().stream()
				.filter(tag -> !TagKey.isKey(tag.getKey(), TagKey.VERSION))
				.forEach(this::refreshTag);
		revalidateMandatoryTags();
	}

	private void revalidateAllMultiplayerTags() {
		song.getTags().stream()
				.filter(MultiplayerTags::isANameTag)
				.forEach(this::refreshTag);
	}

	private void revalidateMandatoryTags() {
		combiner.removeIf(problem -> problem instanceof MandatoryTagMissingProblem);

		FormatSpecification.mandatoryTags(song.getFormatSpecificationVersion()).stream()
				.sorted(Comparator.comparing(Enum::toString))
				.filter(key -> !song.hasTag(key))
				.map(MandatoryTagMissingProblem::new)
				.forEach(combiner::add);
	}

	private void validateValue(TagKey key, String value) {
		ValidationResult result = TagValueValidators.validate(key, value);
		if (!result.getErrors().isEmpty()) {
			addTagValidationProblem(key, Severity.ERROR, result);
		} else {
			if (!result.getWarnings().isEmpty()) {
				addTagValidationProblem(key, Severity.WARNING, result);
			}
		}
	}

	private void addTagValidationProblem(TagKey key, Severity severity, ValidationResult result) {
		combiner.add(new TagValidationErrorProblem(key, severity,
				ValidationUtils.getHighestPriorityMessage(result).get().getText()));
	}

	private void checkStart() {
		combiner.remove(NotesBeforeStartProblem.TITLE);
		song.getTagValue(TagKey.START).ifPresent(value -> {
			Converter.toDouble(value).ifPresent(startSec -> {
				if (song.getLowerXBound() != null) {
					Double startMillis = 1000 * startSec;
					if (startMillis > song.beatToMillis(song.getLowerXBound())) {
						combiner.add(new NotesBeforeStartProblem(song, startMillis.longValue()));
					}
				}
			});
		});
	}

	private void checkEnd() {
		combiner.remove(NotesAfterEndProblem.TITLE);
		song.getTagValue(TagKey.END).ifPresent(value -> {
			Converter.toDouble(value).ifPresent(endMillis -> {
				if (song.getUpperXBound() != null) {
					if (endMillis < song.beatToMillis(song.getUpperXBound())) {
						combiner.add(new NotesAfterEndProblem(song, endMillis.longValue()));
					}
				}
			});
		});
	}

	private class MedleyChecker {
		private void check() {
			removeProblems();
			Optional<Integer> startBeat = song.getTagValue(TagKey.MEDLEYSTARTBEAT)
					.flatMap(Converter::toInteger);
			Optional<Integer> endBeat = song.getTagValue(TagKey.MEDLEYENDBEAT)
					.flatMap(Converter::toInteger);
			if (!startBeat.isPresent() && !endBeat.isPresent()) {
				combiner.add(new MedleyMissingProblem());
				return;
			}
			if (!startBeat.isPresent() || !endBeat.isPresent()) {
				combiner.add(new InvalidMedleyBeatRangeProblem());
				return;
			}
			int size = endBeat.get() - startBeat.get();
			if (size < 0) {
				combiner.add(new InvalidMedleyBeatRangeProblem());
				return;
			}
			checkMedleyDuration(size);
		}

		private void checkMedleyDuration(int beatLength) {
			double seconds = MathUtils.msToSeconds((long) (beatLength * song.getBeatDuration()));
			if (!MathUtils.inRange(seconds, InvalidMedleyLengthProblem.MIN_LENGTH,
					InvalidMedleyLengthProblem.MAX_LENGTH)) {
				combiner.add(new InvalidMedleyLengthProblem(seconds));
			}
		}

		private void removeProblems() {
			combiner.removeIf(problem -> problem.getElements().contains(TagKey.MEDLEYENDBEAT.toString()));
			combiner.removeIf(problem -> problem.getElements().contains(TagKey.MEDLEYSTARTBEAT.toString()));
		}
	}
}
