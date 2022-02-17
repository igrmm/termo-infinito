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
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.*;
import java.util.List;

public class TermoInfinito extends ApplicationAdapter {
	public static final String[] KEYS = {"Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P", "A", "S", "D", "F", "G", "H",
			"J", "K", "L", "<=", "Z", "X", "C", "V", "B", "N", "M", "ENTER"};
	public static final int WORD_MAX = 6;
	public static final int LETTER_MAX = 5;
	public static final String URL = "bit.ly/termo-infinito";
	public static final String GREEN_SQUARE = "\uD83D\uDFE9", YELLOW_SQUARE = "\uD83D\uDFE8", BLACK_SQUARE = "\u2B1B";

	private final Color labelFontColor = new Color(248f / 255, 248f / 255, 242f / 255, 1f);
	private final Color keyFontColor = new Color(40f / 255, 42f / 255, 54f / 255, 1f);
	private final Color wrongKeyFontColor = new Color(68f / 255, 71f / 255, 90f / 255, 1f);

	private TextureRegionDrawable backgroundDrawableColor;
	private TextureRegionDrawable keyUpDrawableColor;
	private TextureRegionDrawable keyDownDrawableColor;
	private TextureRegionDrawable wrongKeyDrawableColor;
	private TextureRegionDrawable statisticsBackgroundDrawableColor;
	private TextureRegionDrawable greenKeyDrawableColor;
	private TextureRegionDrawable yellowKeyDrawableColor;
	private TextureRegionDrawable currentWordDrawableColor;
	private TextureRegionDrawable nextWordDrawableColor;

	private Stage stage;
	private Table statisticsTable;
	private Label victoryMaybeLabel;
	private TextButton playAgainButton;
	private TextButton shareButton;
	private BitmapFont font;
	private final Map<Integer, Map<Integer, TextButton>> attempts = new HashMap<>();
	private final Map<String, TextButton> keys = new HashMap<>();
	private final List<String> wonWords = new ArrayList<>();
	private final List<String> newWords = new ArrayList<>();
	private int currentWordAttemptIndex, currentLetterAttemptIndex;
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
		System.out.println(currentWord);

		//make cool font
		FreeTypeFontGenerator fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("font.ttf"));
		FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
		parameter.size = 70;
		font = fontGenerator.generateFont(parameter);
		fontGenerator.dispose();

		makeDrawables();

		//make generic label style
		final Label.LabelStyle labelStyle = new Label.LabelStyle();
		labelStyle.font = font;
		labelStyle.fontColor = labelFontColor;

		//make generic button style
		TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
		buttonStyle.font = font;
		buttonStyle.up = keyUpDrawableColor;
		buttonStyle.down = keyDownDrawableColor;
		buttonStyle.fontColor = keyFontColor;

		//make statistics table
		statisticsTable = new Table();
		statisticsTable.setFillParent(true);
		Table statisticsEmptyRow1 = new Table();
		statisticsTable.add(statisticsEmptyRow1).row();
		Table statisticsRow = new Table();
		statisticsTable.add(statisticsRow).row();
		statisticsRow.setBackground(statisticsBackgroundDrawableColor);
		victoryMaybeLabel = new Label("", labelStyle);
		victoryMaybeLabel.setAlignment(Align.center);
		statisticsRow.add(victoryMaybeLabel).pad(Gdx.graphics.getWidth() * 0.05f).row();
		playAgainButton = new TextButton("JOGAR NOVAMENTE", buttonStyle);
		playAgainButton.pad(Gdx.graphics.getWidth() * 0.02f);
		statisticsRow.add(playAgainButton).pad(Gdx.graphics.getWidth() * 0.05f).row();
		shareButton = new TextButton("COMPARTILHAR", buttonStyle);
		shareButton.pad(Gdx.graphics.getWidth() * 0.02f);
		statisticsRow.add(shareButton).pad(Gdx.graphics.getWidth() * 0.05f).row();
		Table statisticsEmptyRow2 = new Table();
		statisticsTable.add(statisticsEmptyRow2);

		//make stage and gameTable table
		stage = new Stage(new ScreenViewport());
		final Table gameTable = new Table();
		gameTable.setBackground(backgroundDrawableColor);
		gameTable.setFillParent(true);
		stage.addActor(gameTable);
		Gdx.input.setInputProcessor(stage);

		//make title and wrong word label
		final Table titleTable = new Table();
		final Label titleLabel = new Label("Termo Inifinito", labelStyle);
		titleTable.add(titleLabel);
		gameTable.add(titleTable).row();
		wrongWordLabel = new Label("Palavra inválida!", labelStyle);
		wrongWordLabel.setPosition(
				Gdx.graphics.getWidth() / 2f - wrongWordLabel.getWidth() / 2f,
				Gdx.graphics.getHeight() - wrongWordLabel.getHeight() - titleLabel.getHeight()
		);

		//make word attempts
		Table wordsTable = new Table();
		for (int i = 0; i < WORD_MAX; i++) {
			for (int j = 0; j < LETTER_MAX; j++) {
				TextButton.TextButtonStyle attemptButtonStyle = new TextButton.TextButtonStyle();
				attemptButtonStyle.font = font;
				attemptButtonStyle.fontColor = keyFontColor;

				//verify if is word or letter attempt
				if (i == 0) {
					if (j == 0) {
						attemptButtonStyle.up = keyUpDrawableColor;
					} else {
						attemptButtonStyle.up = currentWordDrawableColor;
					}
				} else {
					attemptButtonStyle.up = nextWordDrawableColor;
				}

				float btnSize = 0.12f * Gdx.graphics.getWidth();
				float btnPad = 0.015f * Gdx.graphics.getWidth();
				TextButton textButton = new TextButton(" ", attemptButtonStyle);
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
		gameTable.add(wordsTable).grow().row();

		//make keyboard
		Table keyboardTable = new Table();
		Table firstRow = new Table();
		Table secondRow = new Table();
		Table thirdRow = new Table();

		for (int key = 0; key < KEYS.length; key++) {
			final TextButton.TextButtonStyle keyButtonStyle = new TextButton.TextButtonStyle();
			keyButtonStyle.font = buttonStyle.font;
			keyButtonStyle.up = buttonStyle.up;
			keyButtonStyle.down = buttonStyle.down;
			keyButtonStyle.fontColor = buttonStyle.fontColor;
			final TextButton keyButton = new TextButton(KEYS[key], keyButtonStyle);
			float btnWidth = 0.075f * Gdx.graphics.getWidth();
			float btnHeight = btnWidth * 1.5f;
			float btnPad = 0.005f * Gdx.graphics.getWidth();

			keys.put(KEYS[key], keyButton);

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
		gameTable.add(keyboardTable);
		handleInput();
	}

	public void handleInput() {
		shareButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				shareButton.setText("COPIADO");
			}
		});

		playAgainButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Map<Integer, TextButton> wordAttempt;
				for (int wordAttemptIndex : attempts.keySet()) {
					wordAttempt = attempts.get(wordAttemptIndex);

					for (int letterAttemptIndex : wordAttempt.keySet()) {
						wordAttempt.get(letterAttemptIndex).setText(" ");
						TextButton.TextButtonStyle buttonStyle = wordAttempt.get(letterAttemptIndex).getStyle();
						buttonStyle.fontColor = keyFontColor;

						//first row
						if (wordAttemptIndex == 0) {
							if (letterAttemptIndex == 0)
								buttonStyle.up = keyUpDrawableColor;
							else
								buttonStyle.up = currentWordDrawableColor;

							//other rows
						} else buttonStyle.up = nextWordDrawableColor;
					}
				}

				for (TextButton keyButton : keys.values()) {
					keyButton.getStyle().up = keyUpDrawableColor;
					keyButton.getStyle().fontColor = keyFontColor;
				}

				currentWord = newWords.get(new Random().nextInt(newWords.size()));
				currentWordAttemptIndex = currentLetterAttemptIndex = 0;
				statisticsTable.remove();
			}
		});

		//HEADS UP: A BUNCH OF NASTY IF STATEMENTS
		for (final String key : KEYS) {
			final TextButton keyButton = keys.get(key);
			keyButton.addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					if (statisticsTable.getStage() != stage) {
						TextButton letterAttemptButton;

						//PRESS BACKSPACE
						if (key.equals("<=")) {
							if (currentLetterAttemptIndex > 0) {
								if (currentLetterAttemptIndex < LETTER_MAX) {
									letterAttemptButton = attempts.get(currentWordAttemptIndex).get(currentLetterAttemptIndex);
									letterAttemptButton.getStyle().up = currentWordDrawableColor;
								}
								currentLetterAttemptIndex--;
								letterAttemptButton = attempts.get(currentWordAttemptIndex).get(currentLetterAttemptIndex);
								letterAttemptButton.setText(" ");
								letterAttemptButton.getStyle().up = keyUpDrawableColor;
							}

							//PRESS ENTER
						} else if (key.equals("ENTER")) {
							if (currentLetterAttemptIndex >= LETTER_MAX) {
								String wordAttempt = "";
								Map<Integer, TextButton> letterAttemptButtons = attempts.get(currentWordAttemptIndex);
								for (TextButton letterButton : letterAttemptButtons.values()) {
									wordAttempt = wordAttempt.concat(String.valueOf(letterButton.getLabel().getText()));
								}
								wordAttempt = wordAttempt.toLowerCase();

								if (newWords.contains(wordAttempt)) {
									String currentWordWithoutLatinChar = getCurrentWordWithoutLatinChar(currentWord);

									//paint buttons with colors
									for (int i = 0; i < LETTER_MAX; i++) {
										for (int j = 0; j < LETTER_MAX; j++) {
											Drawable buttonColor = letterAttemptButtons.get(i).getStyle().up;
											String letterAttempt = String.valueOf(letterAttemptButtons.get(i).getText());

											if (buttonColor == currentWordDrawableColor) {
												letterAttemptButtons.get(i).getStyle().up = wrongKeyDrawableColor;
												letterAttemptButtons.get(i).getStyle().fontColor = wrongKeyFontColor;
												keys.get(letterAttempt).getStyle().up = wrongKeyDrawableColor;
												keys.get(letterAttempt).getStyle().fontColor = wrongKeyFontColor;
											}

											if (wordAttempt.charAt(i) == currentWordWithoutLatinChar.charAt(j)) {
												letterAttemptButtons.get(i).getStyle().fontColor = keyFontColor;
												keys.get(letterAttempt).getStyle().fontColor = keyFontColor;
												if (i == j) {
													letterAttemptButtons.get(i).getStyle().up = greenKeyDrawableColor;
													keys.get(letterAttempt).getStyle().up = greenKeyDrawableColor;
												} else if (buttonColor != greenKeyDrawableColor) {
													letterAttemptButtons.get(i).getStyle().up = yellowKeyDrawableColor;
													keys.get(letterAttempt).getStyle().up = yellowKeyDrawableColor;
												}
											}
										}
									}

									//WIN CONDITION
									if (currentWordWithoutLatinChar.equals(wordAttempt)) {
										spawnStatistics(true);

										//TRY AGAIN CONDITION
									} else {
										currentWordAttemptIndex++;

										//GAME OVER CONDITION
										if (currentWordAttemptIndex >= WORD_MAX) {
											spawnStatistics(false);
											return;
										}

										currentLetterAttemptIndex = 0;
										letterAttemptButtons = attempts.get(currentWordAttemptIndex);
										for (int i = 0; i < letterAttemptButtons.keySet().size(); i++) {
											letterAttemptButton = attempts.get(currentWordAttemptIndex).get(i);
											if (i == 0) {
												letterAttemptButton.getStyle().up = keyUpDrawableColor;
											} else {
												letterAttemptButton.getStyle().up = currentWordDrawableColor;
											}
										}
									}

									//WORD IS NOT VALID (WORD IS NOT IN WORDLIST)
								} else {
									stage.addActor(wrongWordLabel);
									wrongWordTimer = 1.5f;
								}

							} else {
								//WORD IS NOT VALID (ONLY WORDS WITH 5 LETTERS ARE VALID)
								stage.addActor(wrongWordLabel);
								wrongWordTimer = 1.5f;
							}

							//PRESS OTHER KEYS
						} else {
							if (currentLetterAttemptIndex < LETTER_MAX) {
								letterAttemptButton = attempts.get(currentWordAttemptIndex).get(currentLetterAttemptIndex);
								letterAttemptButton.setText(key);
								letterAttemptButton.getStyle().up = currentWordDrawableColor;
								currentLetterAttemptIndex++;
								if (currentLetterAttemptIndex < LETTER_MAX) {
									letterAttemptButton = attempts.get(currentWordAttemptIndex).get(currentLetterAttemptIndex);
									letterAttemptButton.getStyle().up = keyUpDrawableColor;
								}
							}
						}
					}
				}
			});
		}
	}

	public void spawnStatistics(boolean win) {
		if (win)
			victoryMaybeLabel.setText("Você acertou!");
		else
			victoryMaybeLabel.setText("Você errou!\nA palavra era:\n" + currentWord);

		stage.addActor(statisticsTable);
	}

	public String getCurrentWordWithoutLatinChar(String currentWord) {
		String currentWordWithoutLatinChar = currentWord.replaceAll("[áâã]", "a");
		currentWordWithoutLatinChar = currentWordWithoutLatinChar.replaceAll("[éê]", "e");
		currentWordWithoutLatinChar = currentWordWithoutLatinChar.replace("í", "i");
		currentWordWithoutLatinChar = currentWordWithoutLatinChar.replaceAll("[óô]", "o");
		currentWordWithoutLatinChar = currentWordWithoutLatinChar.replace("ú", "u");
		currentWordWithoutLatinChar = currentWordWithoutLatinChar.replace("ç", "c");

		return currentWordWithoutLatinChar;
	}

	public void makeDrawables() {
		Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
		pixmap.setColor(68f / 255, 71f / 255, 90f / 255, 1f);
		pixmap.fill();
		backgroundDrawableColor = new TextureRegionDrawable(new Texture(pixmap));
		pixmap.setColor(189f / 255, 147f / 255, 249f / 255, 1f);
		pixmap.fill();
		keyUpDrawableColor = new TextureRegionDrawable(new Texture(pixmap));
		pixmap.setColor(255f / 255, 121f / 255, 198f / 255, 1f);
		pixmap.fill();
		keyDownDrawableColor = new TextureRegionDrawable(new Texture(pixmap));
		pixmap.setColor(40f / 255, 42f / 255, 54f / 255, 1f);
		pixmap.fill();
		wrongKeyDrawableColor = new TextureRegionDrawable(new Texture(pixmap));
		pixmap.setColor(80f / 255, 250f / 255, 123f / 255, 1f);
		pixmap.fill();
		greenKeyDrawableColor = new TextureRegionDrawable(new Texture(pixmap));
		pixmap.setColor(255f / 255, 184f / 255, 108f / 255, 1f);
		pixmap.fill();
		yellowKeyDrawableColor = new TextureRegionDrawable(new Texture(pixmap));
		pixmap.setColor(139f / 255, 233f / 255, 253f / 255, 1f);
		pixmap.fill();
		currentWordDrawableColor = new TextureRegionDrawable(new Texture(pixmap));
		pixmap.setColor(98f / 255, 114f / 255, 164f / 255, 1f);
		pixmap.fill();
		nextWordDrawableColor = new TextureRegionDrawable(new Texture(pixmap));
		statisticsBackgroundDrawableColor = wrongKeyDrawableColor;
		pixmap.dispose();
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
