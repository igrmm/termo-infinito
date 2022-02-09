package com.igrmm.termoinfinito;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.Arrays;

public class TermoInfinito extends ApplicationAdapter {
	public static final String[] KEYS = {"Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P", "A", "S", "D", "F", "G", "H",
			"J", "K", "L", "<=", "Z", "X", "C", "V", "B", "N", "M", "ENTER"};
	private Stage stage;

	@Override
	public void create() {
		stage = new Stage(new ScreenViewport());
		Skin skin = new Skin(Gdx.files.internal("ui/clean-crispy-ui.json"));
		Table root = new Table();
		root.setFillParent(true);
		stage.addActor(root);
		Gdx.input.setInputProcessor(stage);

		//title
		Table titleTable = new Table();
		Label titleLabel = new Label("Termo Inifinito", skin);
		titleTable.add(titleLabel);
		root.add(titleTable).row();

		//words
		Table wordsTable = new Table();
		TextButton textButton = new TextButton(" ", skin, "default");
		TextButton textButton2 = new TextButton(" ", skin, "default");
		wordsTable.add(textButton2);
		wordsTable.add(textButton);
		root.add(wordsTable).grow().row();

		//keyboard
		Table keyboardTable = new Table();
		Table firstRow = new Table();
		Table secondRow = new Table();
		Table thirdRow = new Table();

		for (int key = 0; key < KEYS.length; key++) {
			final TextButton keyButton = new TextButton(KEYS[key], skin);
			keyButton.getLabel().setFontScale(3f);
			float btnSize = 0.08f * Gdx.graphics.getWidth();
			float btnPad = 0.008f * Gdx.graphics.getWidth();

			keyButton.addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					System.out.println(keyButton.getLabel().getText());
				}
			});

			//to P
			if (key <= Arrays.asList(KEYS).indexOf("P")) {
				firstRow.add(keyButton).width(btnSize).height(btnSize * 1.3f).pad(btnPad);
				if (key == Arrays.asList(KEYS).indexOf("P")) {
					keyboardTable.add(firstRow).grow().row();
				}
			}

			//to <=
			if (key > Arrays.asList(KEYS).indexOf("P") && key <= Arrays.asList(KEYS).indexOf("<=")) {
				secondRow.add(keyButton).width(btnSize).height(btnSize * 1.3f).pad(btnPad);
				if (key == Arrays.asList(KEYS).indexOf("<="))
					keyboardTable.add(secondRow).grow().row();
			}

			//to enter
			if (key > Arrays.asList(KEYS).indexOf("<=")) {
				if (key == Arrays.asList(KEYS).indexOf("ENTER")) {
					thirdRow.add(keyButton).width(2f * btnSize).height(btnSize * 1.3f).pad(btnPad);
					keyboardTable.add(thirdRow).grow();
				} else thirdRow.add(keyButton).width(btnSize).height(btnSize * 1.3f).pad(btnPad);
			}
		}
		root.add(keyboardTable);
	}

	@Override
	public void render() {
		ScreenUtils.clear(191f, 191f, 191f, 1);
		stage.getViewport().apply();
//		stage.setDebugAll(true);
		stage.act(Gdx.graphics.getDeltaTime());
		stage.draw();
	}

	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
	}

	@Override
	public void dispose() {
		stage.dispose();
	}
}
