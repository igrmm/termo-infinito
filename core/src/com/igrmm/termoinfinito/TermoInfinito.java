package com.igrmm.termoinfinito;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.*;
import java.util.List;

public class TermoInfinito extends ApplicationAdapter {
	public static final String[] KEYS = {"Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P", "A", "S", "D", "F", "G", "H",
			"J", "K", "L", "<=", "Z", "X", "C", "V", "B", "N", "M", "ENTER"};
	public static final int WORD_MAX = 6;
	public static final int LETTER_MAX = 5;

	private Stage stage;
	private BitmapFont font;
	private final Map<Integer, Map<Integer, TextButton>> attempts = new HashMap<>();
	private final List<String> wonWords = new ArrayList<>();
	private final List<String> newWords = new ArrayList<>();
	private int currentWordAttempt, currentLetterAttempt;
	private String currentWord;
	private Label wrongWordLabel;
	private float wrongWordTimer;

	@Override
	public void create() {
		//make wordlists
		String words;
		FileHandle newWordsFileHandle = Gdx.files.local("newWords");
		if (!newWordsFileHandle.exists()) {
			FileHandle wordListFileHandle = Gdx.files.internal("5wordlist");
			words = wordListFileHandle.readString("UTF-8");
			newWordsFileHandle.writeString(words, false);
		} else {
			words = newWordsFileHandle.readString("UTF-8");
		}
		Collections.addAll(newWords, words.split("\\r?\\n"));
		currentWord = newWords.get(new Random().nextInt(newWords.size()));

		//make cool font
		FreeTypeFontGenerator fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("font.ttf"));
		FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
		parameter.size = 70;
		font = fontGenerator.generateFont(parameter);
		fontGenerator.dispose();

		//make colors
		Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
		pixmap.setColor(68f / 255, 71f / 255, 90f / 255, 1f);
		pixmap.fill();
		TextureRegionDrawable backgroundColor = new TextureRegionDrawable(new Texture(pixmap));
		pixmap.setColor(189f / 255, 147f / 255, 249f / 255, 1f);
		pixmap.fill();
		final TextureRegionDrawable keyColor = new TextureRegionDrawable(new Texture(pixmap));
		pixmap.setColor(255f / 255, 121f / 255, 198f / 255, 1f);
		pixmap.fill();
		TextureRegionDrawable keyPressedColor = new TextureRegionDrawable(new Texture(pixmap));
		pixmap.setColor(40f / 255, 42f / 255, 54f / 255, 1f);
		pixmap.fill();
		TextureRegionDrawable wrongColor = new TextureRegionDrawable(new Texture(pixmap));
		pixmap.setColor(80f / 255, 250f / 255, 123f / 255, 1f);
		pixmap.fill();
		TextureRegionDrawable greenColor = new TextureRegionDrawable(new Texture(pixmap));
		pixmap.setColor(255f / 255, 184f / 255, 108f / 255, 1f);
		pixmap.fill();
		TextureRegionDrawable yellowColor = new TextureRegionDrawable(new Texture(pixmap));
		pixmap.setColor(139f / 255, 233f / 255, 253f / 255, 1f);
		pixmap.fill();
		final TextureRegionDrawable currentWordColor = new TextureRegionDrawable(new Texture(pixmap));
		pixmap.setColor(98f / 255, 114f / 255, 164f / 255, 1f);
		pixmap.fill();
		TextureRegionDrawable nextWordColor = new TextureRegionDrawable(new Texture(pixmap));
		pixmap.dispose();

		//make stage and root table
		stage = new Stage(new ScreenViewport());
		final Table root = new Table();
		root.setBackground(backgroundColor);
		root.setFillParent(true);
		stage.addActor(root);
		Gdx.input.setInputProcessor(stage);

		//make title and wrong word label
		final Label.LabelStyle labelStyle = new Label.LabelStyle();
		labelStyle.font = font;
		final Table titleTable = new Table();
		final Label titleLabel = new Label("Termo Inifinito", labelStyle);
		titleTable.add(titleLabel);
		root.add(titleTable).row();
		wrongWordLabel = new Label("Palavra inválida!", labelStyle);
		wrongWordLabel.setPosition(
				Gdx.graphics.getWidth() / 2f - wrongWordLabel.getWidth() / 2f,
				Gdx.graphics.getHeight() - wrongWordLabel.getHeight() - titleLabel.getHeight()
		);

		//make word attempts
		Table wordsTable = new Table();
		for (int i = 0; i < WORD_MAX; i++) {
			for (int j = 0; j < LETTER_MAX; j++) {
				TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
				textButtonStyle.font = font;

				//verify if is word or letter attempt
				if (i == 0) {
					if (j == 0) {
						textButtonStyle.up = keyColor;
					} else {
						textButtonStyle.up = currentWordColor;
					}
				} else {
					textButtonStyle.up = nextWordColor;
				}

				textButtonStyle.fontColor = new Color(40f / 255, 42f / 255, 54f / 255, 1f);
				float btnSize = 0.12f * Gdx.graphics.getWidth();
				float btnPad = 0.015f * Gdx.graphics.getWidth();
				TextButton textButton = new TextButton(" ", textButtonStyle);
				wordsTable.add(textButton).size(btnSize).pad(btnPad);
				if (j == 4) wordsTable.row();

				//add attempt buttons to hashmap
				Map<Integer, TextButton> wordAttempt;
				if (attempts.containsKey(i)) {
					wordAttempt = attempts.get(i);
					wordAttempt.put(j, textButton);
				} else {
					wordAttempt = new HashMap<>();
					wordAttempt.put(j, textButton);
					attempts.put(i, wordAttempt);
				}
			}
		}
		root.add(wordsTable).grow().row();

		//make keyboard
		Table keyboardTable = new Table();
		Table firstRow = new Table();
		Table secondRow = new Table();
		Table thirdRow = new Table();

		for (int key = 0; key < KEYS.length; key++) {
			TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
			textButtonStyle.font = font;
			textButtonStyle.up = keyColor;
			textButtonStyle.down = keyPressedColor;
			textButtonStyle.fontColor = new Color(40f / 255, 42f / 255, 54f / 255, 1f);

			final TextButton keyButton = new TextButton(KEYS[key], textButtonStyle);
			float btnWidth = 0.075f * Gdx.graphics.getWidth();
			float btnHeight = btnWidth * 1.5f;
			float btnPad = 0.005f * Gdx.graphics.getWidth();

			keyButton.addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					String key = String.valueOf(keyButton.getLabel().getText());
					TextButton textButton;

					//PRESS BACKSPACE
					if (key.equals("<=")) {
						if (currentLetterAttempt > 0) {
							if (currentLetterAttempt < LETTER_MAX) {
								textButton = attempts.get(currentWordAttempt).get(currentLetterAttempt);
								textButton.getStyle().up = currentWordColor;
							}
							currentLetterAttempt--;
							textButton = attempts.get(currentWordAttempt).get(currentLetterAttempt);
							textButton.setText(" ");
							textButton.getStyle().up = keyColor;
						}

						//PRESS ENTER
					} else if (key.equals("ENTER")) {
						//TO DO
						if (currentLetterAttempt >= LETTER_MAX) {
						} else {
							stage.addActor(wrongWordLabel);
							wrongWordTimer = 1.5f;
						}

						//PRESS OTHER KEYS
					} else {
						if (currentLetterAttempt < LETTER_MAX) {
							textButton = attempts.get(currentWordAttempt).get(currentLetterAttempt);
							textButton.setText(String.valueOf(keyButton.getLabel().getText()));
							textButton.getStyle().up = currentWordColor;
							currentLetterAttempt++;
							if (currentLetterAttempt < LETTER_MAX) {
								textButton = attempts.get(currentWordAttempt).get(currentLetterAttempt);
								textButton.getStyle().up = keyColor;
							}
						}
					}
				}
			});

			//to P
			if (key <= Arrays.asList(KEYS).indexOf("P")) {
				//add keys
				firstRow.add(keyButton).width(btnWidth).height(btnHeight).pad(btnPad);

				if (key == Arrays.asList(KEYS).indexOf("P")) {
					// expand space at the end of row
					firstRow.add(new Actor()).expandX();

					//add row
					keyboardTable.add(firstRow).grow().row();
				}
			}

			//to <=
			if (key > Arrays.asList(KEYS).indexOf("P") && key <= Arrays.asList(KEYS).indexOf("<=")) {
				if (key == Arrays.asList(KEYS).indexOf("A"))
					//add offset on second row
					secondRow.add(new Actor()).width(btnWidth / 2f).pad(btnPad);

				//add keys
				secondRow.add(keyButton).width(btnWidth).height(btnHeight).pad(btnPad);

				if (key == Arrays.asList(KEYS).indexOf("L"))
					//add space between L and <=
					secondRow.add(new Actor()).width(btnWidth).height(btnHeight).pad(btnPad);

				if (key == Arrays.asList(KEYS).indexOf("<="))
					//add row
					keyboardTable.add(secondRow).grow().row();
			}

			//to enter
			if (key > Arrays.asList(KEYS).indexOf("<=")) {
				if (key == Arrays.asList(KEYS).indexOf("ENTER")) {
					//add space between M and ENTER
					thirdRow.add(new Actor()).expandX();

					//add ENTER key
					thirdRow.add(keyButton).width(btnWidth * 2f).height(btnHeight).pad(btnPad);

					//add row
					keyboardTable.add(thirdRow).grow();

				} else {
					if (key == Arrays.asList(KEYS).indexOf("Z"))
						//add offset on third row
						thirdRow.add(new Actor()).width(btnWidth).pad(btnPad);

					//add keys
					thirdRow.add(keyButton).width(btnWidth).height(btnHeight).pad(btnPad);
				}
			}
		}
		root.add(keyboardTable);
	}

	@Override
	public void render() {
		//remove wrong word notification
		if (wrongWordTimer > 0f) {
			wrongWordTimer -= Gdx.graphics.getDeltaTime();
		} else {
			wrongWordLabel.remove();
			wrongWordTimer = 0f;
		}

		ScreenUtils.clear(Color.CLEAR);
		stage.getViewport().apply();
		stage.act();
		stage.draw();
	}

	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
	}

	@Override
	public void dispose() {
		stage.dispose();
		font.dispose();
	}
}
