package main.java.com.github.nianna.karedi.dialog;

import java.util.Optional;

import org.controlsfx.validation.ValidationResult;

import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import main.java.com.github.nianna.karedi.control.NonNegativeDoubleTextField;
import main.java.com.github.nianna.karedi.dialog.ModifyBpmDialog.BpmEditResult;
import main.java.com.github.nianna.karedi.song.tag.TagKey;
import main.java.com.github.nianna.karedi.song.tag.TagValidators;
import main.java.com.github.nianna.karedi.util.NumericNodeUtils;

public abstract class ModifyBpmDialog extends ValidatedDialog<BpmEditResult> {
	@FXML
	protected NonNegativeDoubleTextField bpmField;
	@FXML
	private Button halveButton;
	@FXML
	private Button doubleButton;

	public ModifyBpmDialog() {
		DialogUtils.loadPane(this, getClass().getResource("/fxml/ModifyBpmDialogPaneLayout.fxml"));
	}

	@FXML
	private void initialize() {
		bpmField.setOnScroll(NumericNodeUtils.createUpdateDoubleValueOnScrollHandler(
				bpmField::getValue, bpmField::setValueIfLegal));

		validationSupport.validationResultProperty()
				.addListener(this::refreshScalingButtonsDisable);

		Platform.runLater(() -> {
			validationSupport.registerValidator(bpmField, TagValidators.forKey(TagKey.BPM));
			validationSupport.initInitialDecoration();
			bpmField.requestFocus();
		});
	}

	private void refreshScalingButtonsDisable(Observable obs, ValidationResult oldResult,
			ValidationResult newResult) {
		if (newResult.getErrors().size() > 0) {
			halveButton.setDisable(true);
			doubleButton.setDisable(true);
		} else {
			halveButton.setDisable(false);
			doubleButton.setDisable(false);
		}
	}

	@FXML
	private void halveBpm() {
		bpmField.getValue().ifPresent(bpm -> bpmField.setValue(bpm / 2));
	}

	@FXML
	private void doubleBpm() {
		bpmField.getValue().ifPresent(bpm -> bpmField.setValue(bpm * 2));
	}

	public void setBpm(double value) {
		bpmField.setValue(value);
	}

	public void setBpmFieldText(String value) {
		bpmField.setText(value);
	}

	public class BpmEditResult {
		private boolean rescale = false;
		private double bpm = 0;

		protected BpmEditResult(boolean rescale, Optional<Double> optionalBpm) {
			this.rescale = rescale;
			optionalBpm.ifPresent(newBpm -> bpm = newBpm);
		}

		public boolean shouldRescale() {
			return rescale;
		}

		public double getBpm() {
			return bpm;
		}
	}
}
