package com.igrmm.termoinfinito;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
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
//			keyButton.getLabel().setFontScale(3f);
			float btnWidth = 0.08f * Gdx.graphics.getWidth();
			float btnHeight = btnWidth * 1.4f;
			float btnPad = 0.002f * Gdx.graphics.getWidth();

			keyButton.addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					System.out.println(keyButton.getLabel().getText());
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
